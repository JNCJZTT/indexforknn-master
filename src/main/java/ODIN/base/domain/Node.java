package ODIN.base.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public void setNode(Node node) {
        this.name = node.name;
        this.dis = node.dis;
    }

    public void updateNode(Node vn, int cur) {
        if (vn.name == -1 || vn.dis == -1 || cur == -1) return;
        if (this.name == -1 || this.dis > (vn.dis + cur)) {
            this.name = vn.name;
            this.dis = vn.dis + cur;
        }

    }


    /**
     * Rewrite the hashCode and equals functions,
     * as long as the names between Nodes are the same, they are regarded as the same Node
     */

}
