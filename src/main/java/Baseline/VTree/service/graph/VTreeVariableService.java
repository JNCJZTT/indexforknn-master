package Baseline.VTree.service.graph;

import Baseline.VTree.domain.VTreeCluster;
import Baseline.VTree.domain.VtreeVariable;
import Baseline.VTree.domain.VTreeVertex;
import Baseline.VTree.service.build.VTreeClusterBuilder;
import Baseline.base.domain.Node;
import Baseline.base.domain.enumeration.IndexType;
import Baseline.base.service.api.IVariableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * TODO
 * 2022/9/14 zhoutao
 */
@Slf4j
@Service
public class VTreeVariableService implements IVariableService {

    public VTreeVariableService() {
        register();
    }

    @Override
    public void buildVertex(int vertexName, String clusterName) {
        if (!VtreeVariable.INSTANCE.containsClusterKey(clusterName)) {
            VtreeVariable.INSTANCE.addCluster(clusterName, VTreeClusterBuilder.build(clusterName, true));
        }

        VTreeVertex vertex = new VTreeVertex();
        vertex.setName(vertexName);
        vertex.setClusterName(clusterName);
        VtreeVariable.INSTANCE.addVertex(vertex);
        VtreeVariable.INSTANCE.getCluster(clusterName).addVertex(vertexName);
    }

    @Override
    public void buildEdge(int vertexName, String[] edgeInfo) {
        VTreeVertex vertex = VtreeVariable.INSTANCE.getVertex(vertexName);
        String clusterName = vertex.getClusterName();
        VTreeCluster cluster = VtreeVariable.INSTANCE.getCluster(clusterName);

        for (int j = 0; j < edgeInfo.length; j += 2) {
            int neighbor = Integer.parseInt(edgeInfo[j]), dis = Integer.parseInt(edgeInfo[j + 1]);

            if (neighbor < vertexName) continue;

            VTreeVertex neighborVertex = VtreeVariable.INSTANCE.getVertex(neighbor);

            // add Origion Edge
            vertex.addOrigionEdge(new Node(neighbor, dis));
            neighborVertex.addOrigionEdge(new Node(vertexName, dis));

            if (clusterName.equals(neighborVertex.getClusterName())) {
                cluster.addClusterLink(vertexName, neighbor, dis);
            } else {
                cluster.addBorderLink(vertexName, neighbor, dis);
                VtreeVariable.INSTANCE.getCluster(neighborVertex.getClusterName())
                        .addBorderLink(neighbor, vertexName, dis);
            }

        }
    }

    @Override
    public IndexType supportType() {
        return IndexType.VTree;
    }
}
