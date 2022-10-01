package com.index.indexforknn.amt.domain;

import com.index.indexforknn.amt.common.AmtConstants;
import com.index.indexforknn.base.domain.api.Variable;
import com.index.indexforknn.base.service.dto.IndexDTO;
import lombok.Getter;

/**
 * AhgVariable
 * 2022/4/24 zhoutao
 */
@Getter
public class AmtVariable extends Variable<AmtVertex, AmtCluster> {
    public static final AmtVariable INSTANCE = new AmtVariable();

    private int leastActiveNum;

    private int mostActiveNum;

    private AmtVariable() {
    }

    /**
     * initVariable
     *
     * @param ahgIndex ahgIndex
     */
    public void initVariables(IndexDTO ahgIndex) {
        leastActiveNum = ahgIndex.getLeastActiveNum();
        mostActiveNum = AmtConstants.MULTIPLE_LEAST_ACTIVE_NUM * leastActiveNum;
        super.initVariables();
    }

}
