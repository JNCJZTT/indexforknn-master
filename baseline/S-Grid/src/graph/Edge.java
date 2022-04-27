package graph;

/**
 * @author: zhoutao
 * @since: 2021/11/13 10:42 下午
 * @description: TODO
 */
public class Edge {
    public int from;
    public int to;
    public int dis;

    public Edge(int from, int to, int dis) {
        this.from = from;
        this.to = to;
        this.dis = dis;
    }
}
