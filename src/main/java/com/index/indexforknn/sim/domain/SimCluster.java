package com.index.indexforknn.sim.domain;

import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.base.domain.api.Cluster;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class SimCluster extends Cluster<SimClusterLink> {

//    public int name;

    public ArrayList<Integer> vertex = new ArrayList<>();

    private ArrayList<Integer> borderLink = new ArrayList<>();


    public SimCluster(int name) {
        setName(String.valueOf(name));
        clusterLinkMap = new HashMap<>();
    }

    public void addBorderLink(int vertexName, int neighbor,int dis){
        clusterLinkMap.get(vertexName).addBorderLink(new Node(neighbor, dis));
    }


    public void addVertex(int vertexName){
        vertex.add(vertexName);
    }

    public List<Integer> getVertex() {
        return vertex;
    }
}
