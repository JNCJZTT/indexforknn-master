package graph;

import java.util.Objects;

public class Vnode {
    public int name;
    public int dis;

    public Vnode(int name) {
        this.name = name;
    }

    public Vnode(int name, int dis) {
        this.name = name;
        this.dis = dis;
    }

    /*
     * 只要Vnode的name一样，即视为同一个邻节点
     */
    public int hashCode() {
        return Objects.hash(name);
    }

    public boolean equals(Object temp) {
        if (temp == this) {
            return true;
        }
        if (!(temp instanceof Vnode)) {
            return false;
        }
        Vnode c = (Vnode) temp;
        return name == c.name;
    }


}
