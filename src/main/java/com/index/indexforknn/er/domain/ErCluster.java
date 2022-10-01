package com.index.indexforknn.er.domain;

import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.base.domain.api.Cluster;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class ErCluster extends Cluster<ErClusterLink> {

//    public int name;

    public ArrayList<Integer> vertex = new ArrayList<>();

    public ArrayList<Integer> borderLink = new ArrayList<>();

    private ArrayList<Integer> activeNode = new ArrayList<>();

    private int carNum=0;

    public boolean visited = false;


    public ErCluster(int name) {
        setName(String.valueOf(name));
        clusterLinkMap = new HashMap<>();
    }
    public void addCar(int a){
        carNum+=a;
    }

    public void addBorderLink(int vertexName, int neighbor,int dis){
        clusterLinkMap.get(vertexName).addBorderLink(new Node(neighbor, dis));
    }

    public void addActiveNode(int vertexName){
        activeNode.add(vertexName);
    }
    public void addBorderLink(int vertexName){
        borderLink.add(vertexName);
    }


    public void addVertex(int vertexName){
        vertex.add(vertexName);
    }

    public List<Integer> getVertex() {
        return vertex;
    }
}
