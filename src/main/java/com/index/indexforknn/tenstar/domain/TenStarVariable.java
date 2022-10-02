package com.index.indexforknn.tenstar.domain;

import com.index.indexforknn.base.domain.api.Cluster;
import com.index.indexforknn.base.domain.api.Variable;
import com.index.indexforknn.base.service.dto.IndexDTO;

/**
 * TODO
 * 2022/9/30 zhoutao
 */
public class TenStarVariable extends Variable<TenStarVertex, Cluster> {
    public static final TenStarVariable INSTANCE = new TenStarVariable();


    @Override
    public void initVariables(IndexDTO indexDTO) {
        super.initVariables();
    }
}
