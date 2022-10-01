package com.index.indexforknn.vtree.service;

import com.index.indexforknn.ahg.service.dto.AhgUpdateProcessDTO;
import com.index.indexforknn.ahg.service.graph.AhgActiveService;
import com.index.indexforknn.ahg.service.graph.AhgCarService;
import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.api.IndexService;
import com.index.indexforknn.vtree.domain.VtreeVariable;
import com.index.indexforknn.vtree.service.build.VtreeClusterBuilder;
import com.index.indexforknn.vtree.service.dto.VtreeUpdateProcessDTO;
import com.index.indexforknn.vtree.service.graph.VtreeActiveService;
import com.index.indexforknn.vtree.service.graph.VtreeCarService;
import com.index.indexforknn.vtree.service.graph.VtreeClusterService;
import com.index.indexforknn.vtree.service.graph.VtreeVertexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * TODO
 * 2022/9/14 zhoutao
 */
@Service
public class VtreeIndexService extends IndexService {

    @Autowired
    private VtreeVertexService vertexService;

    @Autowired
    private VtreeClusterService clusterService;

    @Autowired
    private VtreeActiveService activeService;

    public VtreeIndexService() {
        register();
    }

    @Override
    protected void build() {
        vertexService.buildBorders();
        clusterService.computeClusters();
        variableService.buildFullTreeKey();
        buildTree();
        activeService.buildActive();
    }

    public void buildTree() {
        variableService.buildFullTreeKey();
        VtreeVariable.INSTANCE.addCluster("0", VtreeClusterBuilder.build("0", false));
    }

    @Override
    protected void update() {
        VtreeUpdateProcessDTO processDTO = ((VtreeCarService) carService).getUpdateProcessDTO();
        Set<Integer> changedActiveSet = processDTO.mergeChangedActive();
        for (Integer name : changedActiveSet) {
            if (VtreeVariable.INSTANCE.getVertex(name).isActive()) {
                VtreeVariable.INSTANCE.getCluster(VtreeVariable.INSTANCE.getVertex(name).getClusterName()).addActive(name);
            } else {
                VtreeVariable.INSTANCE.getCluster(VtreeVariable.INSTANCE.getVertex(name).getClusterName()).deleteActive(name);
            }
        }
    }

    @Override
    public IndexType supportType() {
        return IndexType.VTREE;
    }
}
