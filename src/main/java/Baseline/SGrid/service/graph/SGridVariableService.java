package Baseline.SGrid.service.graph;

import Baseline.SGrid.domain.SGridCluster;
import Baseline.SGrid.domain.SGridVariable;
import Baseline.SGrid.domain.SGridVertex;
import Baseline.base.domain.Node;
import Baseline.base.domain.enumeration.IndexType;
import Baseline.base.service.api.IVariableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * SGridVariableService
 * 2022/4/27 zhoutao
 */
@Service
public class SGridVariableService implements IVariableService {
    @Autowired
    SGridVertexService vertexService;

    public SGridVariableService() {
        register();
    }

//    @Override
//    public void initVariable(IndexDTO index) {
//        SGridVariable.INSTANCE.initVariables(index);
//    }

    @Override
    public void buildVertex(int vertexName, String clusterName) {
        if (!SGridVariable.INSTANCE.containsClusterKey(clusterName)) {
            SGridVariable.INSTANCE.addCluster(clusterName, new SGridCluster(clusterName));
        }
        SGridVertex vertex = new SGridVertex();
        vertex.setName(vertexName);
        vertex.setClusterNames(Collections.singletonList(clusterName));

        SGridVariable.INSTANCE.addVertex(vertex);
        SGridVariable.INSTANCE.getCluster(clusterName).addVertex(vertexName);
    }

    @Override
    public void buildEdge(int vertexName, String[] edgeInfo) {
        SGridVertex vertex = SGridVariable.INSTANCE.getVertex(vertexName);
        String clusterName = vertex.getClusterName();
        SGridCluster cluster = SGridVariable.INSTANCE.getCluster(clusterName);

        for (int i = 0; i < edgeInfo.length; i += 2) {
            int neighbor = Integer.parseInt(edgeInfo[i]), dis = Integer.parseInt(edgeInfo[i + 1]);
            if (neighbor < vertexName) continue;

            SGridVertex neighborVertex = SGridVariable.INSTANCE.getVertex(neighbor);

            // add Origion Edge
            vertex.addOrigionEdge(new Node(neighbor, dis));
            neighborVertex.addOrigionEdge(new Node(vertexName, dis));

            if (clusterName.equals(neighborVertex.getClusterName())) {
                cluster.addClusterLink(vertexName, neighbor, dis);
                vertex.addVirtualLink(neighbor, dis);
                neighborVertex.addVirtualLink(vertexName, dis);
            } else {
                List<String> clusterNames = Arrays.asList(clusterName, neighborVertex.getClusterName());
                SGridVertex border = vertexService.generateBorderVertex(clusterNames);

                int dis1 = dis / 2, dis2 = dis - dis1;
                border.addVirtualLink(vertexName, dis1);
                border.addVirtualLink(neighbor, dis2);

                // add borders
                clusterNames.forEach(borderCluster ->
                        SGridVariable.INSTANCE.getCluster(borderCluster).addVertex(border.getName()));

                SGridVariable.INSTANCE.getCluster(clusterName).addClusterLink(border.getName(), vertexName, dis1);
                SGridVariable.INSTANCE.getCluster(neighborVertex.getClusterName())
                        .addClusterLink(border.getName(), neighbor, dis2);
            }

        }
    }


    @Override
    public IndexType supportType() {
        return IndexType.SGrid;
    }
}
