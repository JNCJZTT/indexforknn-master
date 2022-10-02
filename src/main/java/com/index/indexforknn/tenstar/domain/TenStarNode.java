package com.index.indexforknn.tenstar.domain;

import com.index.indexforknn.base.domain.Node;
import lombok.Getter;
import lombok.Setter;

/**
 * TODO
 * 2022/10/1 zhoutao
 */
@Getter
@Setter
public class TenStarNode extends Node {
    private int index;

    public TenStarNode(Node node) {
        this.setNode(node);
    }

    public TenStarNode(int name) {
        this.setName(name);
        this.setDis(0);
    }
}
