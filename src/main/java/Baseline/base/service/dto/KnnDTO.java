package Baseline.base.service.dto;


import lombok.Data;

/**
 * KnnDTO
 * 2022/4/17 zhoutao
 */
@Data
public class KnnDTO {
    private int k;

    private int queryName;

    private boolean printQueryName;

    private boolean printKnn;

    private boolean dijkstra;

    private int querySize;
}
