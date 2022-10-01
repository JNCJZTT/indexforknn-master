package com.index.indexforknn.sim.domain;

import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.base.domain.api.knn.DijkstraKnn;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Getter
@Setter
@Slf4j
public class SimKnn extends DijkstraKnn<SimVertex> {


    public SimKnn(int queryName) {
        super(queryName,GlobalVariable.VERTEX_NUM);

        nearestVertex = SimVariable.INSTANCE.getVertex(queryName);
    }


    @Override
    public void knn(){
        boolean findMinBorder =false;
        long startKnn = System.nanoTime();
        while (true) {

            if (terminateEarly(findMinBorder)) {
                break;
            }
            for (Node node : nearestVertex.getVirtualLink()) {
                if (SimVariable.INSTANCE.getVertex(node.getName()).getBorder()){findMinBorder=true;}
                setNode(node);
            }
            updateNearestName();
        }
        queryTime = (System.nanoTime() - startKnn) / 1000.0;
    }

    @Override
    public void updateNearestName(){
        nearestName = current.poll();
        nearestVertex = SimVariable.INSTANCE.getVertex(nearestName);
    }


    public boolean terminateEarly(boolean findMinBorder){
        return super.terminateEarly()&&findMinBorder;

    }



}
