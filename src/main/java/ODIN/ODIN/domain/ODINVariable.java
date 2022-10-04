package ODIN.ODIN.domain;

import ODIN.ODIN.common.ODINConstants;
import ODIN.base.domain.api.Variable;
import ODIN.base.service.dto.IndexDTO;
import lombok.Getter;

/**
 * ODINVariable
 * 2022/4/24 zhoutao
 */
@Getter
public class ODINVariable extends Variable<ODINVertex, ODINCluster> {
    public static final ODINVariable INSTANCE = new ODINVariable();

    private int leastActiveNum;

    private int mostActiveNum;

    private ODINVariable() {
    }

    /**
     * initVariable
     *
     * @param ahgIndex ahgIndex
     */
    public void initVariables(IndexDTO ahgIndex) {
        leastActiveNum = ahgIndex.getLeastActiveNum();
        mostActiveNum = ODINConstants.MULTIPLE_LEAST_ACTIVE_NUM * leastActiveNum;
        super.initVariables();
    }

}
