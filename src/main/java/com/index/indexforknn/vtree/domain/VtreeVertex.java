package com.index.indexforknn.vtree.domain;

import com.index.indexforknn.base.common.constants.Constants;
import com.index.indexforknn.base.domain.api.Vertex;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * TODO
 * 2022/9/14 zhoutao
 */
@Getter
@Setter
@Slf4j
public class VtreeVertex extends Vertex {
    private boolean[] border;

    private String[] clusterNames;

    public void setClusterName(String clusterName) {
        super.setClusterName(clusterName);
        clusterNames = clusterName.split(Constants.CLUSTER_NAME_SUFFIX);
        border = new boolean[clusterNames.length];
        Arrays.fill(border, false);
    }

    public boolean isBorder(int layer) {
        return border[layer];
    }

}
