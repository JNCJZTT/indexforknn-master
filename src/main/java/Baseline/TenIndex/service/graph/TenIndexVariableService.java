package Baseline.TenIndex.service.graph;

import Baseline.TenIndex.domain.TenIndexVariable;
import Baseline.TenIndex.domain.TenIndexVertex;
import Baseline.base.domain.Node;
import Baseline.base.domain.enumeration.IndexType;
import Baseline.base.service.api.IVariableService;
import org.springframework.stereotype.Service;

/**
 * TODO
 * 2022/9/30 zhoutao
 */
@Service
public class TenIndexVariableService implements IVariableService {

    public TenIndexVariableService() {
        register();
    }

    @Override
    public void buildVertex(int vertexName, String clusterName) {
        TenIndexVertex vertex = new TenIndexVertex();
        vertex.setName(vertexName);
        TenIndexVariable.INSTANCE.addVertex(vertex);
    }

    @Override
    public void buildEdge(int vertexName, String[] edgeInfo) {
        TenIndexVertex vertex = TenIndexVariable.INSTANCE.getVertex(vertexName);
        for (int i = 0; i < edgeInfo.length; i += 2) {
            int neighbor = Integer.parseInt(edgeInfo[i]), dis = Integer.parseInt(edgeInfo[i + 1]);
            if (neighbor < vertexName) continue;

            TenIndexVertex neighborVertex = TenIndexVariable.INSTANCE.getVertex(neighbor);
            // add Origion Edge
            vertex.addOrigionEdge(new Node(neighbor, dis));
            neighborVertex.addOrigionEdge(new Node(vertexName, dis));
        }
    }

    @Override
    public IndexType supportType() {
        return IndexType.TenIndex;
    }
}
