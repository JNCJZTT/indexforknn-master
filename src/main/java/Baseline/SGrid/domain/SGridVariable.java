package Baseline.SGrid.domain;


import Baseline.base.domain.api.Variable;
import Baseline.base.service.dto.IndexDTO;
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
