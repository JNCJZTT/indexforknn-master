package com.index.indexforknn.er.graph;


import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.base.domain.api.Vertex;
import com.index.indexforknn.base.service.utils.DisComputerUtil;
import com.index.indexforknn.er.domain.ErCluster;
import com.index.indexforknn.er.domain.ErVariable;
import com.index.indexforknn.er.domain.ErVertex;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class ErClusterService {

    ArrayList<Integer> list = new ArrayList<>();
    ArrayList<Node> clusterNode=new ArrayList<Node>();

    public void computeClusters(){
        createGrid();
//        ErVariable.INSTANCE.getClusters().parallelStream().forEach(this::saveBorder);
        for (ErCluster cluster:ErVariable.INSTANCE.getClusters()){
            DisComputerUtil.floyd(cluster);
            saveBorder(cluster);
        }
//        for (ErCluster cluster: ErVariable.INSTANCE.getClusters()){
//            saveBorder(cluster);
//        }
        dj();
        list.clear();
        clusterNode.clear();

    }

    /**
     * save border info
     *
     * @param cluster cluster
     */
    public void saveBorder(ErCluster cluster){
        List<Integer> clusterVertex = cluster.getVertex();

        for (int i:clusterVertex){
            Set<Node> aList = ErVariable.INSTANCE.getVertex(i).getVirtualLink();
            for (Node a:aList){
                if (!clusterVertex.contains(a.getName())){
                    ErVariable.INSTANCE.getVertex(i).setBorder(true);
                    if (!cluster.borderLink.contains(i)){cluster.addBorderLink(i);}
                    int gName= Integer.parseInt(ErVariable.INSTANCE.getVertex(a.getName()).getClusterName());
                    ErVariable.INSTANCE.getVertex(i).addBorderGNames(gName);
//                    if (!collect.isEmpty()){
//                        ErVariable.INSTANCE.getVertex(vertex).setBorder(true);
//                        for (Node i : collect){
//                            String clusterName = ErVariable.INSTANCE.getVertex(i.getName()).getClusterName();
//                            ErVariable.INSTANCE.getVertex(vertex).addBorderGNames(Integer.parseInt(clusterName));
//                         }
//                    }
//                    break;
                }
            }
            if (ErVariable.INSTANCE.getVertex(i).isActive()){
                cluster.addActiveNode(i);
                cluster.addCar(ErVariable.INSTANCE.getVertex(i).getCars().size());
            }

        }
//        for (Integer vertex:clusterVertex){
//            List<Node> collect = ErVariable.INSTANCE.getVertex(vertex).getVirtualLink().stream().filter(name -> !clusterVertex.contains(name))
//                    .collect(Collectors.toList());
//            if (!collect.isEmpty()){
//                ErVariable.INSTANCE.getVertex(vertex).setBorder(true);
////                for (Node i : collect){
////                    String clusterName = ErVariable.INSTANCE.getVertex(i.getName()).getClusterName();
////                    ErVariable.INSTANCE.getVertex(vertex).addBorderGNames(Integer.parseInt(clusterName));
////                }
//            }
//            if (ErVariable.INSTANCE.getVertex(vertex).isActive()){
//                cluster.addActiveNode(vertex);
//                cluster.addCar(ErVariable.INSTANCE.getVertex(vertex).getCars().size());
//            }
//        }

    }
    private void createGrid(){
        //设置网格大小和行数列数
        double maxX = ErVariable.INSTANCE.getXMax(), minX = ErVariable.INSTANCE.getXMin(),
                maxY = ErVariable.INSTANCE.getYMax(), minY=ErVariable.INSTANCE.getYMin();
        maxY -=minY;
        maxX -=minX;

        double GridWidth =  (maxX / 100) + 1;
        double GridLength =  (maxY / 100) + 1;

        int n=0;
        while (n<10000){
            ErCluster cluster = new ErCluster(n);
            cluster.setName(String.valueOf(n));
            ErVariable.INSTANCE.addCluster(String.valueOf(n),cluster);
            ++n;
        }
        for (int i = 0; i< GlobalVariable.VERTEX_NUM; i++){
            ErVertex vertex=ErVariable.INSTANCE.getVertex(i);
            double x=vertex.x, y=vertex.y;

            y -= minY;
            x -= minX;
            vertex.setX(x);
            vertex.setY(y);
            int row= (int) (y/GridLength);
            int col= (int) (x/GridWidth);

            String gridName = String.valueOf(row*100+col);
            vertex.setClusterName(gridName);
            ErVariable.INSTANCE.getCluster(gridName).addVertex(i);
        }
        log.info("cluster Num={}", GlobalVariable.variable.getClusterSize());
    }

    public void dj(){
        Dijkstra(ErVariable.INSTANCE.getVertices(),1);
        for (int i:list){
            computePath(ErVariable.INSTANCE.getVertex(i));
        }
    }


    public void Dijkstra(List vertices, int query) {
        int[] queryDis;
        int queryName;
        queryName = query;
//        kCars = new PriorityQueue<>(new Car.QueryDisComprator());
        int num = vertices.size(), nearestName = query;
        queryDis = new int[num];
//        path = new int[num];
//        Arrays.fill(path, -1);
        Arrays.fill(queryDis, -1);
        queryDis[queryName] = 0;

        PriorityQueue<Integer> currentNames = new PriorityQueue<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return Integer.compare(queryDis[o1], queryDis[o2]);
            }
        });

        while (true) {


            //遍历邻节点
            for (Node vn : ((Vertex) vertices.get(nearestName)).getOrigionEdges()) {
                int name = vn.getName(), dis = vn.getDis() + queryDis[nearestName];

                if (queryDis[name] == -1 || queryDis[name] > dis) {
                    if (queryDis[name] > dis) {
                        currentNames.remove(name);
//                        ErVariable.INSTANCE.getVertex(name).changePath(ErVariable.INSTANCE.getVertex(nearestName).path);
//                        String clusterName = ErVariable.INSTANCE.getVertex(name).getClusterName();
//                        ErVariable.INSTANCE.getVertex(name).path[Integer.parseInt(clusterName)] = 1;
                    }
                    ErVariable.INSTANCE.getVertex(name).setPreNode(nearestName);
//                    path[name] = nearestName;
                    queryDis[name] = dis;
                    currentNames.add(name);
                }
            }
//            if (kCars.size() >= GlobalVariable.K && kCars.peek().getQueryDis() <= queryDis[nearestName]) {
//                break;
//            }
            addList(nearestName);
            if (currentNames.isEmpty()){break;}
            nearestName = currentNames.poll();
        }
    }

    public void addList(int name){
        list.add(0,name);
    }

//    public void computePath(ErVertex vertex){
//        if (vertex.getPreNode()!=-1){
//            ErVertex preVertex = ErVariable.INSTANCE.getVertex(vertex.getPreNode());
//            preVertex.changePath(vertex.getPath());
//            preVertex.path[Integer.parseInt(vertex.getClusterName())]=1;
//        }
//    }
    public void computePath(ErVertex vertex){
        if (vertex.getPreNode()!=-1){
            ErVertex preVertex = ErVariable.INSTANCE.getVertex(vertex.getPreNode());
            preVertex.changePath(vertex.getBorderGNames());
            preVertex.getBorderGNames().add(Integer.parseInt(vertex.getClusterName()));
        }
    }



}
