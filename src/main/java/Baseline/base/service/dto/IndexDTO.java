package Baseline.base.service.dto;

import lombok.Data;


/**
 * IndexDTO
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

    private String timeType;

    private boolean memory;

    private String memoryType;



    private int K;

}
