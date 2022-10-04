package Baseline.TenIndex.domain;

import Baseline.base.domain.api.Cluster;
import Baseline.base.domain.api.Variable;
import Baseline.base.service.dto.IndexDTO;

/**
 * TODO
 * 2022/9/30 zhoutao
 */
public class TenIndexVariable extends Variable<TenIndexVertex, Cluster> {
    public static final TenIndexVariable INSTANCE = new TenIndexVariable();


    @Override
    public void initVariables(IndexDTO indexDTO) {
        super.initVariables();
    }
}
