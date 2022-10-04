package Baseline.VTree.service;

import Baseline.VTree.domain.VtreeVariable;
import Baseline.VTree.service.build.VTreeClusterBuilder;
import Baseline.VTree.service.dto.VTreeUpdateProcessDTO;
import Baseline.VTree.service.graph.VTreeActiveService;
import Baseline.VTree.service.graph.VTreeCarService;
import Baseline.VTree.service.graph.VTreeClusterService;
import Baseline.VTree.service.graph.VTreeVertexService;
import Baseline.base.domain.enumeration.IndexType;
import Baseline.base.service.api.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * TODO
 * 2022/9/14 zhoutao
 */
@Service
public class VTreeIndexService extends IndexService {

    @Autowired
    private VTreeVertexService vertexService;

    @Autowired
    private VTreeClusterService clusterService;

    @Autowired
    private VTreeActiveService activeService;

    public VTreeIndexService() {
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
        VtreeVariable.INSTANCE.addCluster("0", VTreeClusterBuilder.build("0", false));
    }

    @Override
    protected void update() {
        VTreeUpdateProcessDTO processDTO = ((VTreeCarService) carService).getUpdateProcessDTO();
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
        return IndexType.VTree;
    }
}
