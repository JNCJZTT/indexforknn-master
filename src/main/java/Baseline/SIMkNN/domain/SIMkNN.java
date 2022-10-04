package Baseline.SIMkNN.domain;

import Baseline.base.domain.GlobalVariable;
import Baseline.base.domain.Node;
import Baseline.base.domain.api.knn.DijkstraKnn;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Getter
@Setter
@Slf4j
public class SIMkNN extends DijkstraKnn<SIMkNNVertex> {


    public SIMkNN(int queryName) {
        super(queryName, GlobalVariable.VERTEX_NUM);

        nearestVertex = SIMkNNVariable.INSTANCE.getVertex(queryName);
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
                if (SIMkNNVariable.INSTANCE.getVertex(node.getName()).getBorder()){findMinBorder=true;}
                setNode(node);
            }
            updateNearestName();
        }
        queryTime = (System.nanoTime() - startKnn) / 1000.0;
    }

    @Override
    public void updateNearestName(){
        nearestName = current.poll();
        nearestVertex = SIMkNNVariable.INSTANCE.getVertex(nearestName);
    }


    public boolean terminateEarly(boolean findMinBorder){
        return super.terminateEarly()&&findMinBorder;

    }



}
