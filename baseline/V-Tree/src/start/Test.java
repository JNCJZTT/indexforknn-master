package start;

import common.Comprators;
import graph.Car;
import graph.ClusterRelation;

import java.io.IOException;
import java.util.*;

import static common.GlobalVariable.K;


public class Test {


    public static void main(String[] args) {
        ClusterRelation clusterRelation=new ClusterRelation(0);
        clusterRelation.addNeighborCluster(1);
        clusterRelation.addNeighborCluster(1);
        clusterRelation.addNeighborCluster(2);
        clusterRelation.addNeighborCluster(2);
        clusterRelation.addNeighborCluster(2);

        System.out.println(clusterRelation.getSortedNeighbor());
    }
}
