package Baseline.SIMkNN.graph;

import Baseline.SIMkNN.domain.SIMkNNVariable;
import Baseline.SIMkNN.domain.SIMkNNVertex;
import Baseline.base.domain.GlobalVariable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SIMkNNVertexService {

    public SIMkNNVertex generateBorderVertex(List<String> clusterNames) {
        int borderName = GlobalVariable.VERTEX_NUM + SIMkNNVariable.INSTANCE.getBorderSize();

        SIMkNNVertex vertex = new SIMkNNVertex();
        vertex.setName(borderName);
        vertex.setClusterNames(clusterNames);
        vertex.setBorder(true);

        SIMkNNVariable.INSTANCE.autoIncrementBorderSize();
        SIMkNNVariable.INSTANCE.addVertex(vertex);

        return vertex;
    }
}
