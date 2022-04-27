package com.index.indexforknn.base.service.dto;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ResultDTO
 * 2022/4/17 zhoutao
 */
@Data
public class ResultDTO {
    protected Map<String, Object> result;

    public ResultDTO() {
        result = new LinkedHashMap<>();
    }

    /**
     * build Result
     */
    public void buildResult() {
    }
}
