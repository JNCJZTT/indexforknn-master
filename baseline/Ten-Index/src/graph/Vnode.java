package graph;

import java.util.Objects;

public class Vnode {

    //邻结点

    public Integer name;
    public int dis=0;

    public Vnode(int name) {
        this.name = name;
    }

    public Vnode(int name, int dis) {
        this.name = name;
        this.dis = dis;
    }

    public void SetVnode(int name,int dis){
        this.name=name;
        this.dis=dis;
    }

    public boolean equals(Object temp) {
        if (temp == this) {
            return true;
        }
        if (!(temp instanceof Vnode)) {
            return false;
        }
        Vnode c = (Vnode) temp;
        return name.equals(c.name) ;
    }

    public int hashCode() {
        return Objects.hash(name);
    }
}
