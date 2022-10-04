package Baseline.SIMkNN.domain;

import Baseline.base.domain.GlobalVariable;
import Baseline.base.domain.api.Variable;
import Baseline.base.service.dto.IndexDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

//    @Service
    @Getter
    @Setter
    public class SIMkNNVariable extends Variable<SIMkNNVertex, SIMkNNCluster> {
        public static final SIMkNNVariable INSTANCE = new SIMkNNVariable();

        private int borderSize;

        private double xMin,yMin,xMax,yMax;

        private SIMkNNVariable() {
            borderSize = 0;
        }

        public void autoIncrementBorderSize() {
            borderSize++;
        }

        @Override
        public void initVariables(IndexDTO indexDTO) {
//          super.initVariables();
            vertices = new ArrayList<>(GlobalVariable.MAP_INFO.getSize());
            clusters = new HashMap<>(GlobalVariable.MAP_INFO.getSize());
            xMin=Double.MAX_VALUE;
            yMin=Double.MAX_VALUE;
            xMax=-xMin;
            yMax=-yMin;


        }


    }

