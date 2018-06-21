package com.acuitybotting.path_finding.web_processing;

import com.acuitybotting.db.arango.path_finding.domain.hpa.SavedEdge;
import com.acuitybotting.db.arango.path_finding.domain.hpa.SavedNode;
import com.acuitybotting.db.arango.path_finding.domain.hpa.SavedRegion;
import com.acuitybotting.db.arango.path_finding.repositories.hpa.EdgeRepository;
import com.acuitybotting.db.arango.path_finding.repositories.hpa.NodeRepository;
import com.acuitybotting.db.arango.path_finding.repositories.hpa.RegionRepository;
import com.acuitybotting.path_finding.algorithms.graph.Edge;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.HPAGraph;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPAEdge;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPANode;
import com.acuitybotting.path_finding.algorithms.hpa.implementation.graph.HPARegion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Zachary Herridge on 6/21/2018.
 */
@Slf4j
@Service
public class HpaWebService {

    private final RegionRepository regionRepository;
    private final NodeRepository nodeRepository;
    private final EdgeRepository edgeRepository;

    @Autowired
    public HpaWebService(RegionRepository regionRepository, NodeRepository nodeRepository, EdgeRepository edgeRepository) {
        this.regionRepository = regionRepository;
        this.nodeRepository = nodeRepository;
        this.edgeRepository = edgeRepository;
    }

    public void clearRepos(){
        regionRepository.deleteAll();
        nodeRepository.deleteAll();
        edgeRepository.deleteAll();
    }

    public void save(HPAGraph graph, int version){
        log.info("Starting save of {} as version {}.", graph, version);

        Collection<SavedRegion> savedRegions = new HashSet<>();
        Collection<SavedEdge> savedEdges = new HashSet<>();
        Map<HPANode, SavedNode> nodeMap = new HashMap<>();

        for (HPARegion hpaRegion : graph.getRegions().values()) {
            SavedRegion savedRegion = createSavedRegion(hpaRegion);
            savedRegion.setWebVersion(version);
            savedRegions.add(savedRegion);

            for (HPANode hpaNode : hpaRegion.getNodes().values()) {
                if (hpaNode.getType() == HPANode.CUSTOM) continue;
                SavedNode savedNode = createSavedNode(savedRegion, hpaNode);
                savedNode.setWebVersion(version);
                nodeMap.put(hpaNode, savedNode);

                savedRegion.getNodes().put(savedNode.getLocation(), String.valueOf(savedNode.hashCode()));
            }
        }

        for (Map.Entry<HPANode, SavedNode> entry : nodeMap.entrySet()) {
            for (Edge edge : entry.getKey().getEdges()) {
                HPAEdge hpaEdge = (HPAEdge) edge;

                SavedNode startNode = nodeMap.get(hpaEdge.getStart());
                SavedNode endNode = nodeMap.get(hpaEdge.getEnd());

                if (startNode == null || endNode == null) continue;

                SavedEdge savedEdge = createSavedEdge(hpaEdge, startNode, endNode);
                savedEdge.setWebVersion(version);
                savedEdges.add(savedEdge);
            }
        }

        log.info("Finished processing {}, starting write to db.", graph);
        for (SavedRegion savedRegion : savedRegions) {
            regionRepository.save(savedRegion);
        }
        log.info("Finished saving regions.", graph);

        for (SavedNode savedNode : nodeMap.values()) {
            nodeRepository.save(savedNode);
        }
        log.info("Finished saving nodes.", graph);

        for (SavedEdge savedEdge : savedEdges) {
            edgeRepository.save(savedEdge);
        }
        log.info("Finished saving of {} as version {} with {} regions, {} edges, and {} nodes.", graph, version, savedRegions.size(), savedEdges.size(), nodeMap.size());
    }

    private SavedEdge createSavedEdge(HPAEdge hpaEdge, SavedNode startNode, SavedNode endNode){
        SavedEdge savedEdge = new SavedEdge();
        savedEdge.setStartKey(String.valueOf(startNode.hashCode()));
        savedEdge.setEndKey(String.valueOf(endNode.hashCode()));
        savedEdge.setCost(hpaEdge.getCost());
        savedEdge.setPath(hpaEdge.getPath());
        return savedEdge;
    }

    private SavedNode createSavedNode(SavedRegion savedRegion, HPANode hpaNode){
        SavedNode savedNode = new SavedNode();
        savedNode.setLocation(hpaNode.getLocation());
        savedNode.setType(hpaNode.getType());
        savedNode.setRegionKey(String.valueOf(savedRegion.hashCode()));
        return savedNode;
    }

    private SavedRegion createSavedRegion(HPARegion hpaRegion){
        SavedRegion savedRegion = new SavedRegion();
        savedRegion.setRoot(hpaRegion.getRoot());
        savedRegion.setWidth(hpaRegion.getWidth());
        savedRegion.setHeight(hpaRegion.getHeight());
        return savedRegion;
    }
}
