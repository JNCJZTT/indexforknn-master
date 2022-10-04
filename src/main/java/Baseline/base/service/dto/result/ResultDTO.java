package Baseline.base.service.dto.result;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ResultDTO
 * 2022/4/17 zhoutao
 */
@Data
public abstract class ResultDTO {
    protected Map<String, Object> result;

    public ResultDTO() {
        result = new LinkedHashMap<>();
    }
}
