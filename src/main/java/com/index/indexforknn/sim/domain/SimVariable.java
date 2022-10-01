package com.index.indexforknn.sim.domain;

import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.domain.api.Variable;
import com.index.indexforknn.base.service.dto.IndexDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

//    @Service
    @Getter
    @Setter
    public class SimVariable extends Variable<SimVertex, SimCluster> {
        public static final SimVariable INSTANCE = new SimVariable();

        private int borderSize;

        private double xMin,yMin,xMax,yMax;

        private SimVariable() {
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

