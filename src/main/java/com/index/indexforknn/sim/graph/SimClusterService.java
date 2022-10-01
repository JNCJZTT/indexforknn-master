package com.index.indexforknn.sim.graph;


import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.sim.domain.SimCluster;
import com.index.indexforknn.sim.domain.SimVariable;
import com.index.indexforknn.sim.domain.SimVertex;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SimClusterService {

    public void computeClusters(){
        createGrid();
        for (SimCluster cluster:SimVariable.INSTANCE.getClusters()){
            saveBorder(cluster);
        }
    }

    /**
     * save border info
     *
     * @param cluster cluster
     */
    public void saveBorder(SimCluster cluster){
        List<Integer> clusterVertex = cluster.getVertex();
        for (Integer vertex:clusterVertex){
            List<Node> collect = SimVariable.INSTANCE.getVertex(vertex).getVirtualLink().stream().filter(name -> !clusterVertex.contains(name))
                    .collect(Collectors.toList());
            if (!collect.isEmpty()){
                SimVariable.INSTANCE.getVertex(vertex).setBorder(true);
            }
        }

    }
    private void createGrid(){
        //设置网格大小和行数列数
        double maxX = SimVariable.INSTANCE.getXMax(), minX = SimVariable.INSTANCE.getXMin(),
                maxY = SimVariable.INSTANCE.getYMax(), minY=SimVariable.INSTANCE.getYMin();
        maxY -=minY;
        maxX -=minX;

        double GridWidth =  (maxX / 100) + 1;
        double GridLength =  (maxY / 100) + 1;

        int n=0;
        while (n<10000){
            SimCluster cluster = new SimCluster(n);
            cluster.setName(String.valueOf(n));
            SimVariable.INSTANCE.addCluster(String.valueOf(n),cluster);
            ++n;
        }
        for (int i = 0; i< GlobalVariable.VERTEX_NUM; i++){
            SimVertex vertex=SimVariable.INSTANCE.getVertex(i);
            double x=vertex.x, y=vertex.y;

            y -= minY;
            x -= minX;
            vertex.setX(x);
            vertex.setY(y);
            int row= (int) (y/GridLength);
            int col= (int) (x/GridWidth);

            String gridName = String.valueOf(row*100+col);
            vertex.setClusterName(gridName);
            SimVariable.INSTANCE.getCluster(gridName).addVertex(i);
        }
        log.info("cluster Num={}", GlobalVariable.variable.getClusterSize());
    }




}
