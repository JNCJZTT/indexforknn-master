package com.index.indexforknn.ahg.service;

import com.index.indexforknn.ahg.service.dto.AhgUpdateProcessDTO;
import com.index.indexforknn.ahg.service.graph.*;
import com.index.indexforknn.ahg.domain.AhgVariable;
import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.api.IndexService;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.util.RamUsageEstimator;
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

    public AhgIndexService() {
        register();
    }

    /**
     * build index
     */
    @Override
    public void build() {
        // build borders
        vertexService.buildBorders();
        // computer clusters
        clusterService.computeClusters();
        // build Actives
        activeService.buildActive();
        // build Vritual Map
        buildVirtualMap();

    }

    @Override
    protected void update() {
        AhgUpdateProcessDTO processDTO = ((AhgCarService) carService).getUpdateProcessDTO();
        Set<Integer> changedActiveSet = processDTO.mergeChangedActive();
        activeService.updateActive(changedActiveSet);
        Set<String> changedActiveClusterSet = changedActiveSet.stream()
                .map(active -> AhgVariable.INSTANCE.getVertex(active).getActiveClusterName())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        clusterService.updateActiveClusters(changedActiveClusterSet, false);
    }


    /**
     * build virtual map
     */
    private void buildVirtualMap() {
//        long buildStartTime = System.nanoTime();
        Set<String> leafClusterNames = new HashSet<>(AhgVariable.INSTANCE.getClusterKeySet());
        // build full tree key
        ((AhgVariableService) variableService).buildFullTreeKey();
//        long buildFullTreeTime = System.nanoTime();
        clusterService.updateActiveClusters(leafClusterNames, true);

//        long buildVirtualMapTime = System.nanoTime() - buildFullTreeTime;
//        buildFullTreeTime -= buildStartTime;
//        System.out.println("the time of building full tree : " + (float) (buildFullTreeTime) / 1000_000 + "ms");
//        System.out.println("the time of building virtual map : " + (float) (buildVirtualMapTime) / 1000_000 + "ms");
    }

    @Override
    public IndexType supportType() {
        return IndexType.AHG;
    }

}
