package graph;

public class LinkNode {
    private Vnode vn;
    private int index;

    //构造邻居节点
    public LinkNode(Vnode vn) {
        this.vn = vn;
    }

    //构造自身 （ 所以dis=0)
    public LinkNode(Integer Name){
        this.vn=new Vnode(Name,0);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Integer getName(){
        return this.vn.name;
    }

    public int getDis(){
        return this.vn.dis;
    }

    public void setDis(int dis){
        this.vn.dis=dis;
    }

    public Vnode getVnode(){
        return vn;
    }
}
