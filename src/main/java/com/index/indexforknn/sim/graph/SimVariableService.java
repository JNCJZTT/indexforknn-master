package com.index.indexforknn.sim.graph;

import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.api.IVariableService;
import com.index.indexforknn.sim.domain.SimVariable;
import com.index.indexforknn.sim.domain.SimVertex;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * SimVariableService
 * 2022/4/27 zhoutao
 */

@Slf4j
@Service
public class SimVariableService implements IVariableService {

    public SimVariableService() {
        register();
    }
    @Override
    public IndexType supportType() {
        return IndexType.SIM;
    }

    @Override
    public void buildVertex(int vertexName,String line){
        String[] s=line.split(" ");
        double x = Integer.parseInt(s[1]);
        double y = Integer.parseInt(s[2]);

        if (SimVariable.INSTANCE.getXMax()<x){
            SimVariable.INSTANCE.setXMax(x);
        }
        if (SimVariable.INSTANCE.getXMin()>x){
            SimVariable.INSTANCE.setXMin(x);
        }
        if (SimVariable.INSTANCE.getYMax()<y){
            SimVariable.INSTANCE.setYMax(y);
        }
        if (SimVariable.INSTANCE.getYMin()>y){
            SimVariable.INSTANCE.setYMin(y);
        }
        SimVertex vertex = new SimVertex();
        vertex.setName(vertexName);
        vertex.setX(x);
        vertex.setY(y);
        SimVariable.INSTANCE.addVertex(vertex);

    }
    @Override
    public void buildEdge(int vertexName,String[] edgeInfo){
        // edgeInfo[0] edgeInfo[1]  edgeInfo[2]

        SimVertex vertex = SimVariable.INSTANCE.getVertex(vertexName);
//        String clusterName = vertex.getClusterName();
//        ErCluster cluster = ErVariable.INSTANCE.getCluster(clusterName);

        for (int j = 0; j < edgeInfo.length; j += 2) {
            int neighbor = Integer.parseInt(edgeInfo[j]), dis = Integer.parseInt(edgeInfo[j + 1]);
            // Only save one - way edge.
            // Need to be modified if it is a directed graph
//            if (neighbor < vertexName) continue;

//            ErVertex neighborVertex = ErVariable.INSTANCE.getVertex(neighbor);

            // add Origion Edge
            vertex.addOrigionEdge(new Node(neighbor, dis));
            vertex.addVirtualLink(neighbor,dis);
//            neighborVertex.addOrigionEdge(new Node(vertexName, dis));

//            // connect neighbor node
//            if (clusterName.equals(neighborVertex.getClusterName())) {
//                cluster.addClusterLink(vertexName, neighbor, dis);
//            } else {
//                cluster.addBorderLink(vertexName, neighbor, dis);
//                ErVariable.INSTANCE.getCluster(neighborVertex.getClusterName())
//                        .addBorderLink(neighbor, vertexName, dis);
//            }
        }




    }



}
