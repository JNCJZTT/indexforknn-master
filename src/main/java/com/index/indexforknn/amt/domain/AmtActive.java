package com.index.indexforknn.amt.domain;

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
public class AmtActive {


    private String currentClusterName = null;

    private Map<String, List<Node>> highestBorderInfo;

    public AmtActive(AmtVertex activeVertex) {
        highestBorderInfo = new HashMap<>();

    }


}
