package graph;

public class Node {
    public int NodeName;
    public double X;
    public double Y;
    public Node next;
    public int linkvalue;

    public Node(int nodename, double x, double y) {
        NodeName = nodename;
        X = x;
        Y = y;
        next = null;
        linkvalue = -1;
    }

    public void SetLinkvalue(int v) {
        linkvalue = v;
    }
}
