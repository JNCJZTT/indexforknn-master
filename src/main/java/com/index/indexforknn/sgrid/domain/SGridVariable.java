package com.index.indexforknn.sgrid.domain;


import com.index.indexforknn.base.domain.api.Variable;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.dto.IndexDTO;
import lombok.Getter;

/**
 * SgridVariable
 * 2022/4/27 zhoutao
 */
@Getter
public class SGridVariable extends Variable<SGridVertex, SGridCluster> {
    public static final SGridVariable INSTANCE = new SGridVariable();

    private int borderSize;

    private SGridVariable() {
        borderSize = 0;
    }

    public void autoIncrementBorderSize() {
        borderSize++;
    }

    @Override
    public void initVariables(IndexDTO indexDTO) {
        super.initVariables();
    }


}
