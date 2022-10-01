package com.index.indexforknn.ahg.service.dto;

import com.index.indexforknn.base.service.dto.IndexDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * AhgIndexDTO
 * 2022/3/12 zhoutao
 */
@Data
public class AhgIndexDTO extends IndexDTO{
    private int leastActiveNum;

}
