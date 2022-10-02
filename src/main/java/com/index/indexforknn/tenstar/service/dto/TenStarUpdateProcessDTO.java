package com.index.indexforknn.tenstar.service.dto;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * TODO
 * 2022/10/2 zhoutao
 */
@Getter
public class TenStarUpdateProcessDTO {
    private Set<Integer> change2InActiveSet;

    public TenStarUpdateProcessDTO() {
        change2InActiveSet = new HashSet<>();
    }

    public void addToChange2InActiveSet(Integer name) {
        change2InActiveSet.add(name);
    }


}
