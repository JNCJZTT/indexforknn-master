package com.index.indexforknn.ahg.service.dto;

import com.index.indexforknn.base.service.dto.KnnDTO;
import lombok.Data;


/**
 * TODO
 * 2022/9/11 zhoutao
 */
@Data
public class AhgKnnDTO extends KnnDTO {
    /**
     * 1 : node Query
     * 2 : Edge Query
     */
    private int queryType;

    private int querySize;

    private boolean reset;
}
