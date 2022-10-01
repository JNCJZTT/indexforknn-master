package com.index.indexforknn.amt.service.dto;

import com.index.indexforknn.base.service.dto.IndexDTO;
import lombok.Data;

/**
 * AhgIndexDTO
 * 2022/3/12 zhoutao
 */
@Data
public class AmtIndexDTO extends IndexDTO{
    private int leastActiveNum;

}
