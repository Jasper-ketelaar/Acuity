package com.acuitybotting.path_finding.web_processing;

import com.acuitybotting.db.arango.path_finding.domain.hpa.SavedEdge;
import com.acuitybotting.db.arango.path_finding.domain.hpa.SavedNode;
import com.acuitybotting.db.arango.path_finding.domain.hpa.SavedPath;
import com.acuitybotting.db.arango.path_finding.domain.hpa.SavedRegion;
import com.acuitybotting.db.arango.path_finding.repositories.hpa.EdgeRepository;
import com.acuitybotting.db.arango.path_finding.repositories.hpa.NodeRepository;
import com.acuitybotting.db.arango.path_finding.repositories.hpa.PathRepository;
import com.acuitybotting.db.arango.path_finding.repositories.hpa.RegionRepository;
import com.acuitybotting.db.arango.utils.ArangoUtils;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.HPAGraph;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPAEdge;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPANode;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPARegion;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.utils.EdgeType;
import com.acuitybotting.path_finding.rs.utils.RsEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Created by Zachary Herridge on 6/21/2018.
 */
@Slf4j
@Service
public class HpaWebService {

    private final RegionRepository regionRepository;
    private final NodeRepository nodeRepository;
    private final EdgeRepository edgeRepository;
    private final PathRepository pathRepository;

    @Autowired
    public HpaWebService(RegionRepository regionRepository, NodeRepository nodeRepository, EdgeRepository edgeRepository, PathRepository pathRepository) {
        this.regionRepository = regionRepository;
        this.nodeRepository = nodeRepository;
        this.edgeRepository = edgeRepository;
        this.pathRepository = pathRepository;
    }

    public void deleteVersion(int version) {
        log.info("Starting to delete graph {}, initial sizes {}, {}, {}, {}.", version, regionRepository.count(), nodeRepository.count(), edgeRepository.count(), pathRepository.count());

        regionRepository.deleteAllByWebVersion(version);
        nodeRepository.deleteAllByWebVersion(version);
        edgeRepository.deleteAllByWebVersion(version);
        pathRepository.deleteAllByWebVersion(version);

        log.info("Finished deleting graph {}, new sizes {}, {}, {}, {}.", version, regionRepository.count(), nodeRepository.count(), edgeRepository.count(), pathRepository.count());
    }


    public HPAGraph loadInto(HPAGraph graph, int version, boolean loadPaths){
        log.info("Starting load of HPA graph version {} into {}.", version, graph);

        for (SavedRegion savedRegion : regionRepository.findAllByWebVersion(version)) {
            HPARegion region = new HPARegion(graph, savedRegion.getRoot(), savedRegion.getWidth(), savedRegion.getHeight());
            graph.getRegions().put(region.getKey(), region);
        }

        log.info("Loaded {} SavedRegion(s).", graph.getRegions().size());

        Map<String, SavedNode> nodeMap = new HashMap<>();
        for (SavedNode savedNode : nodeRepository.findAllByWebVersion(version)) {
            nodeMap.put(savedNode.getKey(), savedNode);
        }

        log.info("Loaded {} SavedNode(s).", nodeMap.size());

        if (loadPaths){
            for (SavedPath savedPath : pathRepository.findAllByWebVersion(version)) {
                RsEnvironment.getRsMap().getPathMap().put(savedPath.getKey(), savedPath.getPath());
            }
            log.info("Loaded {} SavedPath(s).", RsEnvironment.getRsMap().getPathMap().size());
        }

        int edgeCount = 0;
        for (SavedEdge savedEdge : edgeRepository.findAllByWebVersion(version)) {
            SavedNode startSavedNode = nodeMap.get(savedEdge.getStartKey());
            SavedNode endSavedNode = nodeMap.get(savedEdge.getEndKey());

            HPARegion startRegion = graph.getRegionContaining(startSavedNode.getLocation());
            HPARegion endRegion = graph.getRegionContaining(endSavedNode.getLocation());

            HPANode startNode = startRegion.getOrCreateNode(startSavedNode.getLocation(), startSavedNode.getType());
            HPANode endNode = endRegion.getOrCreateNode(endSavedNode.getLocation(), endSavedNode.getType());

            startNode.addHpaEdge(endNode, savedEdge.getType(), savedEdge.getCost()).setPathKey(savedEdge.getPathKey());
            edgeCount++;
        }
        log.info("Loaded {} SavedEdge(s).", edgeCount);

        log.info("Finished loading HPA graph version {} into {}.", version, graph);

        return graph;
    }

    public void save(HPAGraph graph, int version) {
        log.info("Starting save of {} as version {}.", graph, version);

        AtomicInteger keyIndex = new AtomicInteger(0);
        Supplier<String> keySupplier = () -> version + "_" + keyIndex.getAndIncrement();

        Collection<SavedRegion> savedRegions = new HashSet<>();
        Collection<SavedEdge> savedEdges = new HashSet<>();
        Collection<SavedPath> savedPaths = new HashSet<>();
        Map<HPANode, SavedNode> nodeMap = new HashMap<>();

        for (HPARegion hpaRegion : graph.getRegions().values()) {
            SavedRegion savedRegion = createSavedRegion(keySupplier.get(), hpaRegion);
            savedRegion.setWebVersion(version);
            savedRegions.add(savedRegion);

            for (HPANode hpaNode : hpaRegion.getNodes().values()) {
                if (hpaNode.getType() == EdgeType.CUSTOM) continue;

                SavedNode savedNode = createSavedNode(keySupplier.get(), savedRegion, hpaNode);
                savedNode.setWebVersion(version);
                nodeMap.put(hpaNode, savedNode);

                savedRegion.getNodes().put(savedNode.getLocation().toString(), savedNode.getKey());
            }
        }

        for (Map.Entry<HPANode, SavedNode> entry : nodeMap.entrySet()) {
            for (Edge edge : entry.getKey().getHpaEdges()) {
                HPAEdge hpaEdge = (HPAEdge) edge;

                SavedNode startNode = nodeMap.get(hpaEdge.getStart());
                SavedNode endNode = nodeMap.get(hpaEdge.getEnd());

                if (startNode == null || endNode == null) continue;

                SavedEdge savedEdge = createSavedEdge(keySupplier.get(), hpaEdge, startNode, endNode);
                savedEdge.setWebVersion(version);

                List<Location> edgePath = RsEnvironment.getRsMap().getPath(hpaEdge);
                if (edgePath != null){
                    SavedPath savedPath = createSavedPath(keySupplier.get(), savedEdge, edgePath);
                    savedPath.setWebVersion(version);
                    savedEdge.setPathKey(savedPath.getKey());
                    savedPaths.add(savedPath);
                }

                savedEdges.add(savedEdge);
            }
        }

        log.info("Finished processing {}, starting write to db.", graph);

        int chunkSize = 300;

        long saveStartTime = System.currentTimeMillis();

        log.info("Saving {} regions.", savedRegions.size());
        ArangoUtils.saveAll(regionRepository, chunkSize, savedRegions);

        log.info("Saving {} nodes.", nodeMap.values().size());
        ArangoUtils.saveAll(nodeRepository, chunkSize, nodeMap.values());

        log.info("Saving {} edges.", savedEdges.size());
        ArangoUtils.saveAll(edgeRepository, chunkSize, savedEdges);

        log.info("Saving {} paths.", savedPaths.size());
        ArangoUtils.saveAll(pathRepository, chunkSize, savedPaths);

        log.info("Finished saving of {} as version {} with {} regions, {} edges, {} paths, and {} nodes in {} seconds.", graph, version, savedRegions.size(), savedEdges.size(), savedPaths.size(), nodeMap.size(), (System.currentTimeMillis() - saveStartTime) / 1000);
    }

    private SavedPath createSavedPath(String key, SavedEdge edge, List<Location> path){
        SavedPath savedPath = new SavedPath();
        savedPath.setKey(key);
        savedPath.setPath(path);
        savedPath.setEdgeKey(edge.getKey());
        return savedPath;

    }

    private SavedEdge createSavedEdge(String key, HPAEdge hpaEdge, SavedNode startNode, SavedNode endNode) {
        SavedEdge savedEdge = new SavedEdge();
        savedEdge.setKey(key);
        savedEdge.setStartKey(startNode.getKey());
        savedEdge.setEndKey(endNode.getKey());
        savedEdge.setCost(hpaEdge.getCost());
        savedEdge.setType(hpaEdge.getType());
        return savedEdge;
    }

    private SavedNode createSavedNode(String key, SavedRegion savedRegion, HPANode hpaNode) {
        SavedNode savedNode = new SavedNode();
        savedNode.setKey(key);
        savedNode.setLocation(hpaNode.getLocation());
        savedNode.setType(hpaNode.getType());
        savedNode.setRegionKey(savedRegion.getKey());
        return savedNode;
    }

    private SavedRegion createSavedRegion(String key, HPARegion hpaRegion) {
        SavedRegion savedRegion = new SavedRegion();
        savedRegion.setKey(key);
        savedRegion.setRoot(hpaRegion.getRoot());
        savedRegion.setWidth(hpaRegion.getWidth());
        savedRegion.setHeight(hpaRegion.getHeight());
        return savedRegion;
    }
}
