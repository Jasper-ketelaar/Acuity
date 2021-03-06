package com.acuitybotting.path_finding.web_processing;

import com.acuitybotting.path_finding.domain.hpa.SavedEdge;
import com.acuitybotting.path_finding.domain.hpa.SavedNode;
import com.acuitybotting.path_finding.domain.hpa.SavedPath;
import com.acuitybotting.path_finding.domain.hpa.SavedRegion;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.HPAGraph;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPAEdge;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPANode;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPARegion;
import com.acuitybotting.path_finding.enviroment.PathingEnviroment;
import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.rs.utils.EdgeType;
import com.acuitybotting.path_finding.rs.utils.RsEnvironment;
import lombok.extern.slf4j.Slf4j;
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

    public void deleteVersion(int version) {
        PathingEnviroment.deleteAll(PathingEnviroment.EDGES);
        PathingEnviroment.deleteAll(PathingEnviroment.NODES);
        PathingEnviroment.deleteAll(PathingEnviroment.REGIONS);
        PathingEnviroment.deleteAll(PathingEnviroment.PATHS);
    }


    public HPAGraph loadInto(HPAGraph graph, int version, boolean loadPaths) {
        log.info("Starting load of HPA graph version {} into {}.", version, graph);


        for (SavedRegion savedRegion : PathingEnviroment.loadFrom(PathingEnviroment.REGIONS, "regions_" + version, SavedRegion[].class).orElse(null)) {
            HPARegion region = new HPARegion(graph, savedRegion.getRoot(), savedRegion.getWidth(), savedRegion.getHeight());
            graph.getRegions().put(region.getKey(), region);
        }

        log.info("Loaded {} SavedRegion(s).", graph.getRegions().size());

        Map<String, SavedNode> nodeMap = new HashMap<>();
        for (SavedNode savedNode : PathingEnviroment.loadFrom(PathingEnviroment.NODES, "nodes_" + version, SavedNode[].class).orElse(null)) {
            nodeMap.put(savedNode.getKey(), savedNode);
        }

        log.info("Loaded {} SavedNode(s).", nodeMap.size());

        if (loadPaths) {
            for (SavedPath savedPath : PathingEnviroment.loadFrom(PathingEnviroment.PATHS, "paths_" + version, SavedPath[].class).orElse(null)) {
                RsEnvironment.getRsMap().getPathMap().put(savedPath.getKey(), savedPath.getPath());
            }
            log.info("Loaded {} SavedPath(s).", RsEnvironment.getRsMap().getPathMap().size());
        }

        int edgeCount = 0;
        for (SavedEdge savedEdge : PathingEnviroment.loadFrom(PathingEnviroment.EDGES, "edges_" + version, SavedEdge[].class).orElse(null)) {
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
                if (edgePath != null) {
                    SavedPath savedPath = createSavedPath(keySupplier.get(), savedEdge, edgePath);
                    savedPath.setWebVersion(version);
                    savedEdge.setPathKey(savedPath.getKey());
                    savedPaths.add(savedPath);
                }

                savedEdges.add(savedEdge);
            }
        }

        log.info("Finished processing {}, starting write to db.", graph);

        long saveStartTime = System.currentTimeMillis();
        int poolSize = 100;

        log.info("Saving {} regions.", savedRegions.size());
        PathingEnviroment.save(PathingEnviroment.REGIONS, "regions_" + version, savedRegions);

        log.info("Saving {} nodes.", nodeMap.values().size());
        PathingEnviroment.save(PathingEnviroment.NODES, "nodes_" + version, nodeMap.values());

        log.info("Saving {} edges.", savedEdges.size());
        PathingEnviroment.save(PathingEnviroment.EDGES, "edges_" + version, savedEdges);

        log.info("Saving {} paths.", savedPaths.size());
        PathingEnviroment.save(PathingEnviroment.PATHS, "paths_" + version, savedPaths);

        log.info("Finished saving of {} as version {} with {} regions, {} edges, {} paths, and {} nodes in {} seconds.", graph, version, savedRegions.size(), savedEdges.size(), savedPaths.size(), nodeMap.size(), (System.currentTimeMillis() - saveStartTime) / 1000);
    }

    private SavedPath createSavedPath(String key, SavedEdge edge, List<Location> path) {
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
