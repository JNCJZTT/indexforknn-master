package Baseline.SIMkNN.graph;


import Baseline.SIMkNN.domain.SIMkNNCluster;
import Baseline.SIMkNN.domain.SIMkNNVariable;
import Baseline.SIMkNN.domain.SIMkNNVertex;
import Baseline.base.domain.GlobalVariable;
import Baseline.base.domain.Node;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SIMkNNClusterService {

    public void computeClusters(){
        createGrid();
        for (SIMkNNCluster cluster: SIMkNNVariable.INSTANCE.getClusters()){
            saveBorder(cluster);
        }
    }

    /**
     * save border info
     *
     * @param cluster cluster
     */
    public void saveBorder(SIMkNNCluster cluster){
        List<Integer> clusterVertex = cluster.getVertex();
        for (Integer vertex:clusterVertex){
            List<Node> collect = SIMkNNVariable.INSTANCE.getVertex(vertex).getVirtualLink().stream().filter(name -> !clusterVertex.contains(name))
                    .collect(Collectors.toList());
            if (!collect.isEmpty()){
                SIMkNNVariable.INSTANCE.getVertex(vertex).setBorder(true);
            }
        }

    }
    private void createGrid(){
        //设置网格大小和行数列数
        double maxX = SIMkNNVariable.INSTANCE.getXMax(), minX = SIMkNNVariable.INSTANCE.getXMin(),
                maxY = SIMkNNVariable.INSTANCE.getYMax(), minY= SIMkNNVariable.INSTANCE.getYMin();
        maxY -=minY;
        maxX -=minX;

        double GridWidth =  (maxX / 100) + 1;
        double GridLength =  (maxY / 100) + 1;

        int n=0;
        while (n<10000){
            SIMkNNCluster cluster = new SIMkNNCluster(n);
            cluster.setName(String.valueOf(n));
            SIMkNNVariable.INSTANCE.addCluster(String.valueOf(n),cluster);
            ++n;
        }
        for (int i = 0; i< GlobalVariable.VERTEX_NUM; i++){
            SIMkNNVertex vertex= SIMkNNVariable.INSTANCE.getVertex(i);
            double x=vertex.x, y=vertex.y;

            y -= minY;
            x -= minX;
            vertex.setX(x);
            vertex.setY(y);
            int row= (int) (y/GridLength);
            int col= (int) (x/GridWidth);

            String gridName = String.valueOf(row*100+col);
            vertex.setClusterName(gridName);
            SIMkNNVariable.INSTANCE.getCluster(gridName).addVertex(i);
        }
        log.info("cluster Num={}", GlobalVariable.variable.getClusterSize());
    }




}
