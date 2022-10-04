package Baseline.ERkNN.graph;

import Baseline.ERkNN.domain.ERkNNVariable;
import Baseline.ERkNN.domain.ERkNNVertex;
import Baseline.base.domain.GlobalVariable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ERkNNVertexService {

    public ERkNNVertex generateBorderVertex(List<String> clusterNames) {
        int borderName = GlobalVariable.VERTEX_NUM + ERkNNVariable.INSTANCE.getBorderSize();

        ERkNNVertex vertex = new ERkNNVertex();
        vertex.setName(borderName);
        vertex.setClusterNames(clusterNames);
        vertex.setBorder(true);

        ERkNNVariable.INSTANCE.autoIncrementBorderSize();
        ERkNNVariable.INSTANCE.addVertex(vertex);

        return vertex;
    }
}
