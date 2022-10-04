package Baseline.ERkNN.domain;

import Baseline.base.domain.GlobalVariable;
import Baseline.base.domain.Node;
import Baseline.base.domain.api.knn.DijkstraKnn;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;


@Getter
@Setter
@Slf4j
public class ERkNN extends DijkstraKnn<ERkNNVertex> {





    public HashSet<Integer> activeNodes=new HashSet<>();
    public ArrayList<Integer>list;



    public ERkNN(int queryName) {
        super(queryName, GlobalVariable.VERTEX_NUM);
        activeNodes.clear();
        nearestVertex = ERkNNVariable.INSTANCE.getVertex(queryName);
        findGird();
    }


    @Override
    public void knn(){

        long startKnn = System.nanoTime();
        list =new ArrayList<>(activeNodes);
        while (!list.isEmpty()){
            int i = list.get(0);
//            if (kCars.size()==GlobalVariable.K){
//                ERkNNVariable.INSTANCE.getVertex(i).getCars();
//            }
            A(i);
        }
        queryTime = (System.nanoTime() - startKnn) / 1000.0;
//        int num=kCars.size()-GlobalVariable.K;
//        while (num>0){
//            kCars.poll();
//            num--;
//        }
    }

    @Override
    public void updateNearestName(){

    }

    public void findGird(){
        String clusterName = ERkNNVariable.INSTANCE.getVertex(queryName).getClusterName();
        ERkNNCluster cluster = ERkNNVariable.INSTANCE.getCluster(clusterName);
        ArrayList<Integer> borderList = new ArrayList<>(cluster.getBorderLink());
        HashSet<Integer> collect=new HashSet<>();
        collect.clear();
        activeNodes.clear();
        activeNodes.addAll(ERkNNVariable.INSTANCE.getCluster(clusterName).getActiveNode());
        cluster.visited = true;
        int n = 0;
        while (activeNodes.size()<GlobalVariable.K){

//            n++;
//            int addCluName;//
//            int p=n*2+1;
//            Integer.parseInt(cluster.getName())

//            int addCluName;//
//            n++;
//
//            if (Integer.parseInt(cluster.getName())%100+n<=100&&Integer.parseInt(cluster.getName())/100+n<=100){
//                addCluName= Integer.parseInt(cluster.getName()) + n + 100 * n;
//                activeNodes.addAll(ERkNNVariable.INSTANCE.getCluster(String.valueOf(addCluName)).getActiveNode());
//            }
//            if (Integer.parseInt(cluster.getName())%100-n>=0&&Integer.parseInt(cluster.getName())/100+n<=100){
//                addCluName = Integer.parseInt(cluster.getName()) - n + 100 * n;
//                activeNodes.addAll(ERkNNVariable.INSTANCE.getCluster(String.valueOf(addCluName)).getActiveNode());
//            }
//            if (Integer.parseInt(cluster.getName())%100+n<=100&&Integer.parseInt(cluster.getName())/100-n>=0){
//                addCluName = Integer.parseInt(cluster.getName()) + n - 100 * n;
//                activeNodes.addAll(ERkNNVariable.INSTANCE.getCluster(String.valueOf(addCluName)).getActiveNode());
//            }
//            if (Integer.parseInt(cluster.getName())%100-n>=0&&Integer.parseInt(cluster.getName())/100-n>=0){
//                addCluName = Integer.parseInt(cluster.getName()) - n - 100 * n;
//                activeNodes.addAll(ERkNNVariable.INSTANCE.getCluster(String.valueOf(addCluName)).getActiveNode());
//            }

            while (!borderList.isEmpty()&&activeNodes.size()<GlobalVariable.K){
                int i = borderList.get(0);
                for (int j: ERkNNVariable.INSTANCE.getVertex(i).getBorderGNames()){
                    if (ERkNNVariable.INSTANCE.getCluster(String.valueOf(j)).visited){
                        continue;
                    }
                    ERkNNVariable.INSTANCE.getCluster(String.valueOf(j)).visited=true;
                    activeNodes.addAll(ERkNNVariable.INSTANCE.getCluster(String.valueOf(j)).getActiveNode());

                    collect.addAll(ERkNNVariable.INSTANCE.getCluster(String.valueOf(j)).getBorderLink());
                }
                borderList.remove(0);
            }
            if (activeNodes.size()<GlobalVariable.K){borderList.addAll(collect);collect.clear();}
        }

//        while (carNumber<GlobalVariable.K){
//            while (!borderList.isEmpty()){
//                int i = borderList.get(0);
//                for (int j:ERkNNVariable.INSTANCE.getVertex(i).getBorderGNames()){
//                    if (ERkNNVariable.INSTANCE.getCluster(String.valueOf(j)).visited){
//                        continue;
//                    }
//                    ERkNNVariable.INSTANCE.getCluster(String.valueOf(j)).visited=true;
//                    activeNodes.addAll(ERkNNVariable.INSTANCE.getCluster(String.valueOf(j)).getActiveNode());
//                    collect.addAll(ERkNNVariable.INSTANCE.getCluster(String.valueOf(j)).getBorderLink());
//                    carNumber+=ERkNNVariable.INSTANCE.getCluster(String.valueOf(j)).getCarNum();
//                }
//                borderList.remove(0);
//            }
//            if (carNumber<GlobalVariable.K){borderList.addAll(collect);collect.clear();}
//        }

    }



    private int eDis(int queryName, int iop){
        double qx= ERkNNVariable.INSTANCE.getVertex(queryName).getX(),
                qy= ERkNNVariable.INSTANCE.getVertex(queryName).getY(),
                iopx= ERkNNVariable.INSTANCE.getVertex(iop).getX(),
                iopy= ERkNNVariable.INSTANCE.getVertex(iop).getY();
        return (int) Math.sqrt(Math.pow(qx-iopx,2)+Math.pow(qy-iopy,2));
    }


    public boolean terminateEarly(boolean findMinBorder){
        return super.terminateEarly()&&findMinBorder;

    }
    public void initA(){
        this.nearestName = queryName;
        this.queryArray = new int[GlobalVariable.VERTEX_NUM];
        PriorityQueue<Integer> current = new PriorityQueue<>(Comparator.comparingInt(o -> queryArray[o]));
        Arrays.fill(queryArray, -1);
        queryArray[queryName] = 0;
    }

    public void A(int iop){
//        int nearestName = queryName;
//        int[] queryArray = new int[GlobalVariable.VERTEX_NUM];
//        PriorityQueue<Integer> current = new PriorityQueue<>(Comparator.comparingInt(o -> queryArray[o]));
//        Arrays.fill(queryArray, -1);
//        queryArray[queryName] = 0;
//        nearestVertex = ERkNNVariable.INSTANCE.getVertex(nearestName);
        while (true){
            if (terminateEarly()) {
                list.remove((Integer) iop);
                break;
            }
            if (list.contains(nearestName)){list.remove((Integer) nearestName);}
            if (nearestName==iop){break;}
//            if (nearestVertex.isActive()){
//                int dis = queryArray[nearestName];
//                for (Car car : nearestVertex.getCars()) {
//                    car.setQueryDis(dis);
//                    kCars.add(car);
//                }
//
//            }

            for (Node node : nearestVertex.getVirtualLink()) {
                int name = node.getName(), dis = node.getDis() + queryArray[nearestName];
                if (queryArray[name] == -1) {
                    queryArray[name] = dis;
                    current.add(name);
                } else if (queryArray[name] > dis) {
                    current.remove(name);
                    queryArray[name] = dis;
                    current.add(name);
                }
            }
            double max = Double.MAX_VALUE;
            for (int n:current){
                int value = queryArray[n]+eDis(n,iop);
                if (max > value){
                    max = value;
                    nearestName = n;
                }
                if (n==iop){nearestName=n;break;}
            }
            current.remove(nearestName);
            nearestVertex = ERkNNVariable.INSTANCE.getVertex(nearestName);

        }
        }
    @Override
    public void setNode(Node node) {

    }




}
