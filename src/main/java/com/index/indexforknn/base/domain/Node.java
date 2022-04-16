package com.index.indexforknn.base.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * TODO
 * 2022/2/12 zhoutao
 */
@Getter
@Setter
@AllArgsConstructor
public class Node {
    private int name;

    private int dis;

    public Node(int name) {
        this.name = name;
    }

    /**
     * 重写hashCode和equals函数，Node之间只要name相同则视为同一个Node
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Node)) {
            return false;
        }
        Node node = (Node) obj;
        return name == node.name;
    }
}
