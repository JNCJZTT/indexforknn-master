package com.index.indexforknn.er.graph;

import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.api.IVariableService;
import com.index.indexforknn.er.domain.ErVariable;
import com.index.indexforknn.er.domain.ErVertex;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * SimVariableService
 * 2022/4/27 zhoutao
 */

@Slf4j
@Service
public class ErVariableService implements IVariableService {




    @Override
    public void buildVertex(int vertexName,String line){
        String[] s=line.split(" ");
        double x = Integer.parseInt(s[1]);
        double y = Integer.parseInt(s[2]);

        if (ErVariable.INSTANCE.getXMax()<x){
            ErVariable.INSTANCE.setXMax(x);
        }
        if (ErVariable.INSTANCE.getXMin()>x){
            ErVariable.INSTANCE.setXMin(x);
        }
        if (ErVariable.INSTANCE.getYMax()<y){
            ErVariable.INSTANCE.setYMax(y);
        }
        if (ErVariable.INSTANCE.getYMin()>y){
            ErVariable.INSTANCE.setYMin(y);
        }
        ErVertex vertex = new ErVertex();
        vertex.setName(vertexName);
        vertex.setX(x);
        vertex.setY(y);
        ErVariable.INSTANCE.addVertex(vertex);

    }
    @Override
    public void buildEdge(int vertexName,String[] edgeInfo){
        // edgeInfo[0] edgeInfo[1]  edgeInfo[2]

        ErVertex vertex = ErVariable.INSTANCE.getVertex(vertexName);
//        String clusterName = vertex.getClusterName();
//        ErCluster cluster = ErVariable.INSTANCE.getCluster(clusterName);

        for (int j = 0; j < edgeInfo.length; j += 2) {
            int neighbor = Integer.parseInt(edgeInfo[j]), dis = Integer.parseInt(edgeInfo[j + 1]);

            vertex.addOrigionEdge(new Node(neighbor, dis));
            vertex.addVirtualLink(neighbor,dis);

        }




    }
    @Override
    public IndexType supportType() {
        return IndexType.ER;
    }

    public ErVariableService() {
        register();
    }


}
