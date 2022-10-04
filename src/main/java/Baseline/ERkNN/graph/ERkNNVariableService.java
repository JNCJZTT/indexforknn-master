package Baseline.ERkNN.graph;

import Baseline.ERkNN.domain.ERkNNVariable;
import Baseline.ERkNN.domain.ERkNNVertex;
import Baseline.base.domain.Node;
import Baseline.base.domain.enumeration.IndexType;
import Baseline.base.service.api.IVariableService;
import org.springframework.stereotype.Service;


/**
 * SimVariableService
 * 2022/4/27 zhoutao
 */


@Service
public class ERkNNVariableService implements IVariableService {




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

        if (ERkNNVariable.INSTANCE.getXMax()<x){
            ERkNNVariable.INSTANCE.setXMax(x);
        }
        if (ERkNNVariable.INSTANCE.getXMin()>x){
            ERkNNVariable.INSTANCE.setXMin(x);
        }
        if (ERkNNVariable.INSTANCE.getYMax()<y){
            ERkNNVariable.INSTANCE.setYMax(y);
        }
        if (ERkNNVariable.INSTANCE.getYMin()>y){
            ERkNNVariable.INSTANCE.setYMin(y);
        }
        ERkNNVertex vertex = new ERkNNVertex();
        vertex.setName(vertexName);
        vertex.setX(x);
        vertex.setY(y);
        ERkNNVariable.INSTANCE.addVertex(vertex);

    }
    @Override
    public void buildEdge(int vertexName,String[] edgeInfo){
        // edgeInfo[0] edgeInfo[1]  edgeInfo[2]

        ERkNNVertex vertex = ERkNNVariable.INSTANCE.getVertex(vertexName);
//        String clusterName = vertex.getClusterName();
//        ERkNNCluster cluster = ERkNNVariable.INSTANCE.getCluster(clusterName);

        for (int j = 0; j < edgeInfo.length; j += 2) {
            int neighbor = Integer.parseInt(edgeInfo[j]), dis = Integer.parseInt(edgeInfo[j + 1]);

            vertex.addOrigionEdge(new Node(neighbor, dis));
            vertex.addVirtualLink(neighbor,dis);

        }




    }
    @Override
    public IndexType supportType() {
        return IndexType.ERkNN;
    }

    public ERkNNVariableService() {
        register();
    }


}
