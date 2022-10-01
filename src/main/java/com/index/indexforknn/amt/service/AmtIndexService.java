package com.index.indexforknn.amt.service;

import com.index.indexforknn.amt.service.dto.AmtUpdateProcessDTO;
import com.index.indexforknn.amt.service.graph.*;
import com.index.indexforknn.amt.domain.AmtVariable;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.api.IndexService;
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
public class AmtIndexService extends IndexService {

    @Autowired
    private AmtVertexService vertexService;

    @Autowired
    private AmtClusterService clusterService;

    @Autowired
    private AmtActiveService activeService;

    public AmtIndexService() {
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
        AmtUpdateProcessDTO processDTO = ((AmtCarService) carService).getUpdateProcessDTO();
        Set<Integer> changedActiveSet = processDTO.mergeChangedActive();
        activeService.updateActive(changedActiveSet);
        Set<String> changedActiveClusterSet = changedActiveSet.stream()
                .map(active -> AmtVariable.INSTANCE.getVertex(active).getActiveClusterName())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        clusterService.updateActiveClusters(changedActiveClusterSet, false);
    }


    /**
     * build virtual map
     */
    private void buildVirtualMap() {
        Set<String> leafClusterNames = new HashSet<>(AmtVariable.INSTANCE.getClusterKeySet());
        // build full tree key
        ((AmtVariableService) variableService).buildFullTreeKey();

        clusterService.updateActiveClusters(leafClusterNames, true);
    }

    @Override
    public IndexType supportType() {
        return IndexType.AHG;
    }

}
