package com.index.indexforknn.base.service.dto;

import com.index.indexforknn.base.domain.enumeration.Distribution;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.domain.enumeration.MapInfo;
import lombok.Data;

import java.io.Serializable;


/**
 * 基本IndexDTO
 * 2022/3/12 zhoutao
 */
@Data
public class IndexDTO {

    private int branch;

    private int subGraphSize;

    private String indexType;

    private String mapInfo;

    private String distribution;

    private int carNum;

    private int leastActiveNum;
}
