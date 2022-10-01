package com.index.indexforknn.ahg.domain;

import com.index.indexforknn.ahg.common.AhgConstants;
import com.index.indexforknn.base.domain.api.Variable;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.dto.IndexDTO;
import lombok.Getter;

/**
 * AhgVariable
 * 2022/4/24 zhoutao
 */
@Getter
public class AhgVariable extends Variable<AhgVertex, AhgCluster> {
    public static final AhgVariable INSTANCE = new AhgVariable();

    private int leastActiveNum;

    private int mostActiveNum;

    private AhgVariable() {
    }

    /**
     * initVariable
     *
     * @param ahgIndex ahgIndex
     */
    public void initVariables(IndexDTO ahgIndex) {
        leastActiveNum = ahgIndex.getLeastActiveNum();
        mostActiveNum = AhgConstants.MULTIPLE_LEAST_ACTIVE_NUM * leastActiveNum;
        super.initVariables();
    }

}
