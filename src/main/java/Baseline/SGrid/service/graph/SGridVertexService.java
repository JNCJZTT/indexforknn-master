package Baseline.SGrid.service.graph;

import Baseline.SGrid.domain.SGridVariable;
import Baseline.SGrid.domain.SGridVertex;
import Baseline.base.domain.GlobalVariable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * SGridVertexService
 * 2022/4/27 zhoutao
 */
@Service
public class SGridVertexService {
    /**
     * generate a border vertex
     *
     * @param clusterNames clusterNames
     * @return border
     */
    public SGridVertex generateBorderVertex(List<String> clusterNames) {
        int borderName = GlobalVariable.VERTEX_NUM + SGridVariable.INSTANCE.getBorderSize();

        SGridVertex vertex = new SGridVertex();
        vertex.setName(borderName);
        vertex.setClusterNames(clusterNames);
        vertex.setBorder(true);

        SGridVariable.INSTANCE.autoIncrementBorderSize();
        SGridVariable.INSTANCE.addVertex(vertex);

        return vertex;
    }
}
