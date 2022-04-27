package graph;

import java.io.*;
import java.util.*;

import static buildindex.BuildIndex.EdgeFile;

public class Graph {
    public Node[] Vertex;
    public boolean[] EstimatedSpace;
    public ArrayList<Node> Nodes = new ArrayList<Node>();
    public double MapX1, MapX2, MapY1, MapY2;
    public Map<Integer, Integer> VertexMap = new HashMap<Integer, Integer>();//key nodename obj index

    public Graph(ArrayList<Node> allnodes, double x1, double x2, double y1, double y2) throws FileNotFoundException {
        MapX1 = x1;
        MapY1 = y1;
        MapX2 = x2;
        MapY2 = y2;
        EstimatedSpace = new boolean[allnodes.size() + 1];
        Arrays.fill(EstimatedSpace, false);
        int j = 0;
        for (Node n : allnodes) {
            if (BelongTo(n, MapX1, MapX2, MapY1, MapY2)) {
                Nodes.add(n);
                VertexMap.put(n.NodeName, j);
                j++;
                EstimatedSpace[n.NodeName] = true;
            }
        }
        Vertex = new Node[Nodes.size()];
        for (int i = 0; i < Nodes.size(); i++) {
            Vertex[i] = new Node(Nodes.get(i).NodeName, Nodes.get(i).X, Nodes.get(i).Y);
        }
        CreatRoadNetwork();
    }

    public void connect(int x, int y, int linkvalue) {
        Node node = new Node(y, Vertex[y].X, Vertex[y].Y);
        node.SetLinkvalue(linkvalue);
        node.next = Vertex[x].next;
        Vertex[x].next = node;
    }

    public boolean BelongTo(Node o, double X1, double X2, double Y1, double Y2) {
        return o.X >= X1 && o.X <= X2 && o.Y >= Y1 && o.Y <= Y2;
    }

    public void CreatRoadNetwork() throws FileNotFoundException {
        int x1, x2, x3;
        try {
            String encoding = "GBK";
           // File file = new File("graph\\USA-road-d.NW.gr");

            //  File file = new File("F:\\课题\\San Francisco Bay Area\\1.txt");
            if (EdgeFile.isFile() && EdgeFile.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(EdgeFile), encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    if (lineTxt.charAt(0) == 'a') {
                        String[] s = lineTxt.split("\\s+");
                        x1 = Integer.parseInt(s[1]);
                        x2 = Integer.parseInt(s[2]);
                        x3 = Integer.parseInt(s[3]);
                        if (EstimatedSpace[x1] && EstimatedSpace[x2]) {
                            connect(VertexMap.get(x1), VertexMap.get(x2), x3);
                        }
                    }
                }
                read.close();
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("读取文件内容出错");
        }

    }

    public void getAllNeighbours(int index, LinkedList<Integer> neighbor) {
        Node temp = null;
        boolean stop = true;
        temp = Vertex[index];
        while (stop) {
            temp = temp.next;
            if (temp != null) {
                neighbor.add(temp.NodeName);
                stop = true;
            } else {
                stop = false;
            }

        }
    }

    public double getEdgeDistance(int from, int to) {
        double value = Double.MAX_VALUE;
        Node temp = null;
        temp = Vertex[from].next;
        while (temp != null) {
            if (temp.NodeName == to) {
                value = temp.linkvalue;
                break;
            }
            temp = temp.next;
        }
        return value;
    }
}
