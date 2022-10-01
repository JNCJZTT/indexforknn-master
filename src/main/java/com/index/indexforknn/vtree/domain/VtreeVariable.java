package com.index.indexforknn.vtree.domain;

import com.index.indexforknn.base.domain.api.Variable;
import com.index.indexforknn.base.service.dto.IndexDTO;
import lombok.Getter;

/**
 * TODO
 * 2022/9/14 zhoutao
 */
@Getter
public class VtreeVariable extends Variable<VtreeVertex, VtreeCluster> {
    public static final VtreeVariable INSTANCE = new VtreeVariable();

    private VtreeVariable() {
    }

    @Override
    public void initVariables(IndexDTO indexDTO) {
        super.initVariables();
    }
}
