package Baseline.SIMkNN.graph;

import Baseline.SIMkNN.domain.SIMkNNVariable;
import Baseline.SIMkNN.domain.SIMkNNVertex;
import Baseline.base.domain.Node;
import Baseline.base.domain.enumeration.IndexType;
import Baseline.base.service.api.IVariableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * SimVariableService
 * 2022/4/27 zhoutao
 */

@Slf4j
@Service
public class SIMkNNVariableService implements IVariableService {

    public SIMkNNVariableService() {
        register();
    }
    @Override
    public IndexType supportType() {
        return IndexType.SIMkNN;
    }

    @Override
    public void buildVertex(int vertexName,String line){
        String[] s=line.split(" ");
        double x = Integer.parseInt(s[1]);
        double y = Integer.parseInt(s[2]);
//        int i = 0;
//        String[] s=line.split(" ");
//        if (s[0]=="v"){i=2;}
//        double x = Integer.parseInt(s[i]);
//        double y = Integer.parseInt(s[i+1]);

        if (SIMkNNVariable.INSTANCE.getXMax()<x){
            SIMkNNVariable.INSTANCE.setXMax(x);
        }
        if (SIMkNNVariable.INSTANCE.getXMin()>x){
            SIMkNNVariable.INSTANCE.setXMin(x);
        }
        if (SIMkNNVariable.INSTANCE.getYMax()<y){
            SIMkNNVariable.INSTANCE.setYMax(y);
        }
        if (SIMkNNVariable.INSTANCE.getYMin()>y){
            SIMkNNVariable.INSTANCE.setYMin(y);
        }
        SIMkNNVertex vertex = new SIMkNNVertex();
        vertex.setName(vertexName);
        vertex.setX(x);
        vertex.setY(y);
        SIMkNNVariable.INSTANCE.addVertex(vertex);

    }
    @Override
    public void buildEdge(int vertexName,String[] edgeInfo){
        // edgeInfo[0] edgeInfo[1]  edgeInfo[2]

        SIMkNNVertex vertex = SIMkNNVariable.INSTANCE.getVertex(vertexName);
//        String clusterName = vertex.getClusterName();
//        ERkNNCluster cluster = ERkNNVariable.INSTANCE.getCluster(clusterName);

        for (int j = 0; j < edgeInfo.length; j += 2) {
            int neighbor = Integer.parseInt(edgeInfo[j]), dis = Integer.parseInt(edgeInfo[j + 1]);
            // Only save one - way edge.
            // Need to be modified if it is a directed graph
//            if (neighbor < vertexName) continue;

//            ERkNNVertex neighborVertex = ERkNNVariable.INSTANCE.getVertex(neighbor);

            // add Origion Edge
            vertex.addOrigionEdge(new Node(neighbor, dis));
            vertex.addVirtualLink(neighbor,dis);
//            neighborVertex.addOrigionEdge(new Node(vertexName, dis));

//            // connect neighbor node
//            if (clusterName.equals(neighborVertex.getClusterName())) {
//                cluster.addClusterLink(vertexName, neighbor, dis);
//            } else {
//                cluster.addBorderLink(vertexName, neighbor, dis);
//                ERkNNVariable.INSTANCE.getCluster(neighborVertex.getClusterName())
//                        .addBorderLink(neighbor, vertexName, dis);
//            }
        }




    }



}
