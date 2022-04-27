package com.index.indexforknn.ahg.domain;

import com.index.indexforknn.base.domain.Node;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * AhgActive
 * 2022/2/17 zhoutao
 */
@Getter
@Setter
public class AhgActive {
    private int name;

    private String currentClusterName = null;

    private Map<String, List<Node>> highestBorderInfo;

    public AhgActive(AhgVertex activeVertex) {
        highestBorderInfo = new HashMap<>();
        name = activeVertex.getName();
    }


}
