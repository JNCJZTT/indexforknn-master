package graph;

public class Edge {
    public int from;
    public int to;
    public int distence;

    public Edge(int from, int to, int distence) {
        this.from = from;
        this.to = to;
        this.distence = distence;
    }

    public Edge(int from, Vnode vn) {
        this.from = from;
        this.to = vn.name;
        this.distence = vn.dis;
    }
}
