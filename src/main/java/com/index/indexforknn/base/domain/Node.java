package com.index.indexforknn.base.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * Node
 * 2022/2/12 zhoutao
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Node {
    private int name;

    private int dis;

    public Node(int name) {
        this.name = name;
    }

    /**
     * Rewrite the hashCode and equals functions,
     * as long as the names between Nodes are the same, they are regarded as the same Node
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
