package com.index.indexforknn.er.domain;

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
    public class ErVariable extends Variable<ErVertex, ErCluster> {
        public static final ErVariable INSTANCE = new ErVariable();

        private int borderSize;

        private double xMin,yMin,xMax,yMax;

        private ErVariable() {
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

