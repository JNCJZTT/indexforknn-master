package graph;

public class Objects {
    public String ObjectName;
    public int ObjectNo;
    public double X;
    public double Y;
    public Grid grid;
    public int NodeName;

    public Objects(int objno, double x, double y,int nodename) {
        ObjectNo = objno;
        X = x;
        Y = y;
        NodeName=nodename;
    }
}
