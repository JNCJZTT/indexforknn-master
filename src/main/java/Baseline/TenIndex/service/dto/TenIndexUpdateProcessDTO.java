package Baseline.TenIndex.service.dto;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * TODO
 * 2022/10/2 zhoutao
 */
@Getter
public class TenIndexUpdateProcessDTO {
    private Set<Integer> change2InActiveSet;

    public TenIndexUpdateProcessDTO() {
        change2InActiveSet = new HashSet<>();
    }

    public void addToChange2InActiveSet(Integer name) {
        change2InActiveSet.add(name);
    }


}
