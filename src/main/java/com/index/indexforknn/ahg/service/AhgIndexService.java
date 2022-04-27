package com.index.indexforknn.ahg.service;

import com.index.indexforknn.ahg.common.status.AhgClusterUpdateStatus;
import com.index.indexforknn.ahg.domain.AhgCluster;
import com.index.indexforknn.ahg.domain.AhgVariable;
import com.index.indexforknn.ahg.service.build.AhgClusterBuilder;
import com.index.indexforknn.ahg.service.dto.AhgUpdateProcessDTO;
import com.index.indexforknn.ahg.service.dto.result.AhgIndexResultDTO;
import com.index.indexforknn.ahg.service.graph.*;
import com.index.indexforknn.ahg.service.utils.AhgVariableUtil;
import com.index.indexforknn.ahg.service.utils.Trie;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.IndexService;
import com.index.indexforknn.base.service.dto.IndexDTO;
import com.index.indexforknn.base.service.dto.ResultDTO;
import com.index.indexforknn.base.service.utils.MemoryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Index Service
 * 2022/2/10 zhoutao
 */
@Slf4j
@Service
public class AhgIndexService extends IndexService {

    @Autowired
    private AhgVertexService vertexService;

    @Autowired
    private AhgClusterService clusterService;

    @Autowired
    private AhgActiveService activeService;

    private double buildTime;

    private double memoryConsumed;

    public AhgIndexService() {
        register();
        variableService = new AhgVariableService();
    }

    /**
     * build index
     */
    @Override
    public void build() {

        // record the memory and time
        long memoryBefore = MemoryUtil.getUsedMemory();
        long startTime = System.nanoTime();

        // build borders
        vertexService.buildBorders();
        // computer clusters
        clusterService.computeClusters();
        // build Actives
        activeService.buildActive();
        // build Vritual Map
        buildVirtualMap();

        buildTime = (System.nanoTime() - startTime);
        memoryConsumed = (MemoryUtil.getUsedMemory() - memoryBefore);

        log.info("Build Done!");
    }

    @Override
    protected void update() {
        AhgUpdateProcessDTO processDTO = ((AhgCarService) carService).getUpdateProcessDTO();
        Set<Integer> changedActiveSet = processDTO.mergeChangedActive();
        activeService.updateActive(changedActiveSet);
        Set<String> changedActiveClusterSet = changedActiveSet.stream()
                .map(active -> AhgVariable.vertices.get(active).getActiveClusterName())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        clusterService.updateActiveClusters(changedActiveClusterSet);
        log.info("Update Done!");
    }

    @Override
    public ResultDTO buildResult(IndexDTO indexDTO) {
        AhgIndexResultDTO resultDTO = new AhgIndexResultDTO();
        resultDTO.buildResult(buildTime, memoryConsumed, indexDTO);
        return resultDTO;
    }

    /**
     * 删除索引
     */
    private void clearIndex() {

    }

    /**
     * build virtual map
     */
    private void buildVirtualMap() {
        Set<String> leafClusterNames = new HashSet<>(AhgVariableUtil.getClusterKeySet());
//        Queue<String> queue = new LinkedList<>(AhgVariableUtil.getClusterKeySet());
        // build full tree key
        ((AhgVariableService) variableService).buildFullTreeKey();

        clusterService.updateActiveClusters(leafClusterNames);

//        Trie trie = new Trie();
//        while (!queue.isEmpty()) {
//            String clusterName = queue.poll();
//            if (trie.isProcessed(clusterName)) {
//                continue;
//            }
//
//            AhgCluster cluster = AhgVariableUtil.getCluster(clusterName);
//
//            while (cluster.isSuitableCluster() == AhgClusterUpdateStatus.NEED_TO_MERGE) {
//                clusterName = cluster.getParentName();
//                // If parentCluster does not exist, create parentCluster
//                if (!AhgVariableUtil.containsClusterValue(clusterName)) {
////                    AhgVariableUtil.addCluster(clusterName, new AhgCluster(clusterName, false));
//                    AhgVariableUtil.addCluster(clusterName, AhgClusterBuilder.build(clusterName, false));
//                }
//                cluster = AhgVariableUtil.getCluster(clusterName);
//            }
//            trie.insert(clusterName);
//            cluster.buildVirtualMap();
//        }
    }

    @Override
    public IndexType supportType() {
        return IndexType.AHG;
    }


    public static void main(String[] args) {
        int a = 500;
        double c = a / 15.0;
        System.out.println(c);
    }
}
