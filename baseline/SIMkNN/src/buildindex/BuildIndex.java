package buildindex;

import graph.Graph;
import graph.Grid;
import graph.Node;
import graph.Objects;

import java.io.*;
import java.util.*;

public class BuildIndex {
    //打开文件--------------------------------------------------------------------------------------------------------------
    //打开TenIndex测试集
//    File VertexFile = new File("/Users/zhoutao/Nutstore Files/我的坚果云/论文/地图/TenIndex/TenIndex_vertex.txt");
//    File EdgeFile   = new File("/Users/zhoutao/Nutstore Files/我的坚果云/论文/地图/TenIndex/TenIndex_edge.txt");
    //打开USA_纽约文件
//    File VertexFile = new File("/Users/zhoutao/Nutstore Files/我的坚果云/论文/地图/NY/USA-road-d.NY.co/USA-road-d.NY.txt");
//    public static File EdgeFile = new File("/Users/zhoutao/Nutstore Files/我的坚果云/论文/地图/NY/USA-road-d.NY.gr/USA-road-d.NY.txt");
    //打开USA_kele文件
    File VertexFile = new File("/Users/zhoutao/Nutstore Files/我的坚果云/论文/地图/COL/USA-road-d.COL.co");
    public static File EdgeFile = new File("/Users/zhoutao/Nutstore Files/我的坚果云/论文/地图/COL/USA-road-d.COL.gr");
    //打开NW文件
//    File VertexFile = new File("/Users/zhoutao/Nutstore Files/我的坚果云/论文/地图/NW/USA-road-d.NW.co");
//    public static File EdgeFile = new File("/Users/zhoutao/Nutstore Files/我的坚果云/论文/地图/NW/USA-road-d.NW.gr");

    //Windows--------------------------------------------------------------------------------------------------------------
//    File VertexFile = new File("C:\\Users\\GangNam277\\Documents\\我的坚果云\\论文\\地图\\TenIndex\\TenIndex_vertex.txt");
//    File EdgeFile   = new File("C:\\Users\\GangNam277\\Documents\\我的坚果云\\论文\\地图\\TenIndex\\TenIndex_edge.txt");
    //打开USA_纽约文件
//    File VertexFile = new File("C:\\Users\\GangNam277\\Documents\\我的坚果云\\论文\\地图\\NY\\USA-road-d.NY.co\\USA-road-d.NY.txt");
//    File EdgeFile = new File("C:\\Users\\GangNam277\\Documents\\我的坚果云\\论文\\地图\\NY\\USA-road-d.NY.gr\\USA-road-d.NY.txt");
    //打开USA_kele文件
//    File VertexFile = new File("D:\\本科生导师制\\出租车论文\\地图\\COL\\USA-road-d.COL.co");
//    File EdgeFile = new File("D:\\本科生导师制\\出租车论文\\地图\\COL\\USA-road-d.COL.gr");
    //打开NW文件
//    File VertexFile = new File("/Users/zhoutao/Nutstore Files/我的坚果云/论文/地图/NW/USA-road-d.NW.co");
//    File EdgeFile = new File("/Users/zhoutao/Nutstore Files/我的坚果云/论文/地图/NW/USA-road-d.NW.gr");

    public double X1, X2, Y1, Y2;
    public ArrayList<Node> AllNode = new ArrayList<Node>();
    public Graph graph;
    public Grid grid;//init grid
    public int NodeNum;
    public int Nc;
    public int m;
    public int K;

    public int ObjectNum;//set objects number
    public ArrayList<Grid> AllGrids = new ArrayList<Grid>();
    public int GridsNum;
    public ArrayList<Objects> AllObjects = new ArrayList<Objects>();
    public ArrayList<Objects> EstimatedObjects = new ArrayList<Objects>();
    public double MapX1, MapX2, MapY1, MapY2;

    public Map<Integer, Objects> ObjectsMap = new HashMap<Integer, Objects>();
    public int ChangeTimes;
    public int CurrentNode;//查询点
    public Objects current;//查询点

    public float CreatGridTime, CreatRoadnetworkTime, kNNTime, ObjectUpdateTime;

    public BuildIndex(int nc, int mm, int k, int objectnum) {
        Nc = nc;
        m = mm;
        K = k;
        ObjectNum = objectnum;
        ChangeTimes = 1;

    }

    public void SIMkNNPrcocess() throws IOException {
        //step1 init   初始化分成m^2个cell，初始化目标放入相应cell，判断进行分裂操作
        long startTime = System.currentTimeMillis();//记录开始时间
        Init();
        long endTime = System.currentTimeMillis();//记录开始时间
        float excTime = (float) (endTime - startTime);
        CreatGridTime = excTime;
        // System.out.println("创建格子时间" + excTime + "ms");
        //step2 init
        int Times = 0;
        Random rand = new Random();
        int index1 = rand.nextInt(AllNode.size()) ;
        CurrentNode=AllNode.get(index1).NodeName;
        current = new Objects(-1, AllNode.get(index1).X, AllNode.get(index1).Y, CurrentNode);
        while (Times < ChangeTimes) {
            //create roadnetwork
            startTime = System.currentTimeMillis();//记录结束时间
            BuildEstimatedResultSpace(current);
            graph = new Graph(AllNode, MapX1, MapX2, MapY1, MapY2);
            endTime = System.currentTimeMillis();//记录结束时间
            excTime = (float) (endTime - startTime);
            CreatRoadnetworkTime = excTime;
            //        System.out.println("创建路网时间：" + excTime + "ms");
            //KNN
            startTime = System.currentTimeMillis();//记录结束时间
            knn();
            endTime = System.currentTimeMillis();//记录结束时间
            excTime = (float) (endTime - startTime);
            kNNTime = excTime;
//            System.out.println("knn时间：" + excTime + "ms");

            //step3 change object position
            Times++;
            startTime = System.currentTimeMillis();//记录结束时间
            ChangeObjectPostion();
            endTime = System.currentTimeMillis();//记录结束时间
            excTime = (float) (endTime - startTime);
            ObjectUpdateTime = excTime ;
//            System.out.println("更新移动对象时间：" + excTime * 1000 + "us");

            //Adjustment
        }

        //endwhile
    }

    public void Init() throws IOException {
        BuildNode();
        NodeNum = AllNode.size();
        boolean l = true;

        grid = new Grid(Nc, m, X1, Y1, X2, Y2, l);
        grid.Split(AllGrids);
        GridsNum = AllGrids.size();

        Random rand = new Random();
        int objectindex;
        for (int i = 0; i < ObjectNum; i++) {
            objectindex = rand.nextInt(NodeNum);
            Objects o = new Objects(i, AllNode.get(objectindex).X, AllNode.get(objectindex).Y, objectindex);
            ObjectsMap.put(o.NodeName, o);
            AllObjects.add(o);
            for (int j = 0; j < AllGrids.size(); j++) {
                if (BelongTo(o, AllGrids.get(j))) {
                    AllGrids.get(j).ObjectList.add(o);
                    AllGrids.get(j).CountObject();
                    o.grid = AllGrids.get(j);
                }
            }
        }
        boolean update = true;
        while (update) {
            update = false;
            for (int i = 0; i < AllGrids.size(); i++) {
                if (AllGrids.get(i).JudgeSplit()) {
                    AllGrids.get(i).Split(AllGrids);
                    update = true;
                }
            }

        }

    }

    public void ChangeObjectPostion() throws IOException {
        int changenum = 100;
        Random rand = new Random();
        int objectindex, nodeindex;
        for (int i = 0; i < changenum; i++) {
            objectindex = rand.nextInt(AllObjects.size());
            nodeindex = rand.nextInt(NodeNum);
            AllObjects.get(objectindex).grid.Remove(AllObjects.get(objectindex));
            AllObjects.get(objectindex).X = AllNode.get(nodeindex).X;
            AllObjects.get(objectindex).Y = AllNode.get(nodeindex).Y;
            GridsNum = AllGrids.size();
            for (int j = 0; j < GridsNum; j++) {
                if (AllGrids.get(j).Leaf && BelongTo(AllObjects.get(objectindex), AllGrids.get(j))) {
                    AllGrids.get(j).ObjectList.add(AllObjects.get(objectindex));
                    AllGrids.get(j).CountObject();
                    AllObjects.get(objectindex).grid = AllGrids.get(j);
                }
            }
        }
        Split();
        Merge();
    }

    public boolean BelongTo(Objects o, Grid g) {
        return o.X >= g.X1 && o.X <= g.X2 && o.Y >= g.Y1 && o.Y <= g.Y2;
    }

    public void BuildNode() throws FileNotFoundException, IOException {
        int name, x, y;             //结点名字，横纵坐标
        double X_MIN = Double.MAX_VALUE;        //X轴最大数值
        double X_MAX = -X_MIN;
        double Y_MIN = Double.MAX_VALUE;
        double Y_MAX = -Y_MIN;
//        File file = new File("graph\\USA-road-d.NW.co");//打开NW点坐标文件
        if (VertexFile.isFile() && VertexFile.exists()) {
            InputStreamReader read = new InputStreamReader(new FileInputStream(VertexFile));
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineText = null;//按行读
            while ((lineText = bufferedReader.readLine()) != null) {
                if (lineText.charAt(0) == 'v') {
                    String[] s = lineText.split("\\s+");
                    name = Integer.parseInt(s[1]);//结点姓名和横纵坐标
                    x = Integer.parseInt(s[2]);
                    y = Integer.parseInt(s[3]);
                    if (x < X_MIN) {
                        X_MIN = x;
                    } else if (x > X_MAX) {
                        X_MAX = x;
                    }

                    if (y < Y_MIN) {
                        Y_MIN = y;
                    } else if (y > Y_MAX) {
                        Y_MAX = y;
                    }
                    Node newnode = new Node(name, x, y);
                    AllNode.add(newnode);
                }
            }
            read.close();
        }
        X1 = X_MIN;
        X2 = X_MAX;
        Y1 = Y_MIN;
        Y2 = Y_MAX;
    }

    public void MergeSideProcess1(ArrayList<Grid> side, Grid par) {
        for (int i = 0; i < side.size(); i++) {
            if (side.get(i).parent == par) {
                side.set(i, par);
            }
        }
    }

    public void MergeSideProcess2(Grid par) {
        for (int i = 0; i < m; i++) {
            //leftside
            for (int j = 0; j < par.splitarray[i][0].left.size(); j++) {
                if (!par.left.contains(par.splitarray[i][0].left.get(j))) {
                    par.left.add(par.splitarray[i][0].left.get(j));
                }
            }
            //rightside
            for (int j = 0; j < par.splitarray[i][m - 1].right.size(); j++) {
                if (!par.right.contains(par.splitarray[i][m - 1].right.get(j))) {
                    par.right.add(par.splitarray[i][m - 1].right.get(j));
                }
            }
            //upside
            for (int j = 0; j < par.splitarray[m - 1][i].up.size(); j++) {
                if (!par.up.contains(par.splitarray[m - 1][i].up.get(j))) {
                    par.up.add(par.splitarray[m - 1][i].up.get(j));
                }
            }
            //downside
            for (int j = 0; j < par.splitarray[0][i].down.size(); j++) {
                if (!par.down.contains(par.splitarray[0][i].down.get(j))) {
                    par.down.add(par.splitarray[0][i].down.get(j));
                }
            }

        }
    }

    public void MergeAngleProcess(Grid par) {
        if (par.splitarray[0][0].bottom_left != null) {
            par.splitarray[0][0].bottom_left.top_right = par;
        }
        if (par.splitarray[m - 1][0].top_left != null) {
            par.splitarray[m - 1][0].top_left.bottom_right = par;
        }
        if (par.splitarray[m - 1][m - 1].top_right != null) {
            par.splitarray[m - 1][m - 1].top_right.bottom_left = par;
        }
        if (par.splitarray[0][m - 1].bottom_right != null) {
            par.splitarray[0][m - 1].bottom_right.top_left = par;
        }

        par.top_left = par.splitarray[m - 1][0].top_left;
        par.top_right = par.splitarray[m - 1][m - 1].top_right;
        par.bottom_left = par.splitarray[0][0].bottom_left;
        par.bottom_right = par.splitarray[0][m - 1].bottom_right;
    }

    public void SetSubLeafFalse(Grid par) {
        for (int i = 0; i < AllGrids.size(); i++) {
            if (AllGrids.get(i).parent == par) {
                AllGrids.get(i).valid = false;
                AllGrids.get(i).Leaf = false;
            }
        }
    }

    public void Split() throws IOException {
        boolean update = true;
        int iii = 0;
        while (update) {
            update = false;
            for (int i = 0; i < AllGrids.size(); i++) {
                if (AllGrids.get(i).valid && AllGrids.get(i).Leaf && AllGrids.get(i).JudgeSplit()) {
//                    iii++;
//                    System.out.println(iii + "数量a" + AllGrids.get(i).ObjectNum);
                    AllGrids.get(i).Split(AllGrids);
                    update = true;
                }
            }
        }
    }

    public void Merge() {
        boolean judge = true;
        while (judge) {
            judge = false;
            for (int i = 0; i < AllGrids.size(); i++) {
                if (AllGrids.get(i).valid && AllGrids.get(i).Leaf && Nc > AllGrids.get(i).CountParentObject()) {//触发Merge
                    judge = true;
                    Grid p = AllGrids.get(i);
                    Grid par = p.parent;
                    MergeSideProcess1(p.up, par);
                    MergeSideProcess1(p.down, par);
                    MergeSideProcess1(p.left, par);
                    MergeSideProcess1(p.right, par);
                    MergeSideProcess2(par);
                    MergeAngleProcess(par);
                    SetSubLeafFalse(par);
                    p.Leaf = true;
                    p.valid = false;
                    par.valid = true;
                    par.Leaf = true;
                }
            }
        }

    }

    public int CurrentObjectSum(double x1, double x2, double y1, double y2) {
        for (Objects c : AllObjects) {
            if (c.X >= x1 && c.X <= x2 && c.Y >= y1 && c.Y <= y2) {
                if (!EstimatedObjects.contains(c)) {
                    EstimatedObjects.add(c);
                }
            }
        }
        return EstimatedObjects.size();
    }

    public int CountObjectsNum(ArrayList<Grid> inside, double x1, double x2, double y1, double y2) {
        int sum = 0;
        //   System.out.println("len"+inside.size());
        for (Grid e : inside) {
            if (!e.Leaf) {
                System.out.println("ai");
            }
            sum += e.CountObject();
        }
        System.out.println(sum);
        return sum;
    }

    public void BuildEstimatedResultSpace(Objects CurrentPosition) {
        ArrayList<Grid> CS_inside = new ArrayList<Grid>();
        ArrayList<Grid> CS_covered = new ArrayList<Grid>();
        ArrayList<Grid> CS_overlap = new ArrayList<Grid>();
        ArrayList<Grid> CS_neighbor = new ArrayList<Grid>();
        ArrayList<Grid> searchGrid = new ArrayList<Grid>();
        ArrayList<Grid> temp = new ArrayList<Grid>();
        ArrayList<Grid> NS = new ArrayList<Grid>();
        Grid center = null;
        EstimatedObjects.clear();
        for (int i = 0; i < AllGrids.size(); i++) {
            if (AllGrids.get(i).valid && AllGrids.get(i).Leaf && BelongTo(CurrentPosition, AllGrids.get(i))) {
                center = AllGrids.get(i);
                break;
            }
        }
        MapX1 = center.X1;
        MapY1 = center.Y1;
        MapX2 = center.X2;
        MapY2 = center.Y2;
        searchGrid.add(center);
        CS_covered.add(center);
        while (CurrentObjectSum(MapX1, MapX2, MapY1, MapY2) < K) {
            CS_inside.clear();
            if (CS_neighbor.size() == 0) {
                Neighbor(CS_neighbor, center);
            } else {
                for (Grid c : CS_neighbor) {
                    if (c.X1 >= MapX1 && c.X2 <= MapX2 && c.Y1 >= MapY1 && c.Y2 <= MapY2) {
                        if (!CS_inside.contains(c)) {
                            CS_inside.add(c);
                        }
                    }
                }
            }
            //line 12
            for (Grid e : CS_neighbor) {
                if (!CS_inside.contains(e)) {
                    CS_overlap.add(e);
                }
            }//line13

            CS_overlap.clear();
            for (Grid e : CS_neighbor) {
                if (!CS_inside.contains(e)) {
                    CS_overlap.add(e);
                }
            }
            for (Grid e : CS_inside) {
                if (!CS_covered.contains(e)) {
                    CS_covered.add(e);//line14
                }
            }
            temp.clear();
            NS.clear();
            for (Grid e : CS_inside) {
                temp.clear();
                Neighbor(temp, e);
                for (Grid ee : temp) {
                    if (!NS.contains(ee)) {
                        NS.add(ee);
                    }
                }
            }
            temp.clear();
            CS_neighbor.clear();
            for (Grid e : NS) {
                if (!CS_covered.contains(e)) {
                    temp.add(e);
                }
            }
            for (Grid e : CS_overlap) {
                if (!temp.contains(e)) {
                    temp.add(e);
                }
            }
            CS_neighbor.clear();
            for (Grid e : temp) {
                CS_neighbor.add(e);
            }
            temp.clear();//line 16

            double delt = Double.MAX_VALUE;
            Grid cc = null;
            for (Grid c : CS_neighbor) {
                //                   System.out.println("minus" + (c.X2 - c.X1));
                if (delt > (c.X2 - c.X1)) {
                    delt = c.X2 - c.X1;
                    cc = c;
                }
            }
            if (cc != null) {
                MapX1 -= delt;
                MapX2 += delt;
                MapY1 -= (cc.Y2 - cc.Y1);
                MapY2 += (cc.Y2 - cc.Y1);
            }

            //2020/1/17 接下来需要随机一个起点并设计cell扩展的过程。
        }
//        System.out.println("x1" + MapX1 + " x2" + MapX2);
//        System.out.println("y1" + MapY1 + " y2" + MapY2);

    }

    public void Neighbor(ArrayList<Grid> CS, Grid cell) {
        CS.clear();
        ArrayList<Grid> e = new ArrayList<Grid>();
        e = cell.up;
        if (e.size() > 0) {
            for (int i = 0; i < e.size(); i++) {
                if (!CS.contains(e.get(i)) && e.get(i).Leaf) {
                    CS.add(e.get(i));
                }
            }
        }
        e.clear();
        e = cell.down;
        if (e.size() > 0) {
            for (int i = 0; i < e.size(); i++) {
                if (!CS.contains(e.get(i)) && e.get(i).Leaf) {
                    CS.add(e.get(i));
                }
            }
        }
        e.clear();
        e = cell.left;
        if (e.size() > 0) {
            for (int i = 0; i < e.size(); i++) {
                if (!CS.contains(e.get(i)) && e.get(i).Leaf) {
                    CS.add(e.get(i));
                }
            }
        }
        e.clear();
        e = cell.right;
        if (e.size() > 0) {
            for (int i = 0; i < e.size(); i++) {
                if (!CS.contains(e.get(i)) && e.get(i).Leaf) {
                    CS.add(e.get(i));
                }
            }
        }

        Grid f;
        f = cell.top_left;
        if (f != null && !CS.contains(f)) {
            CS.add(f);
        }
        f = cell.top_right;
        if (f != null && !CS.contains(f)) {
            CS.add(f);
        }
        f = cell.bottom_left;
        if (f != null && !CS.contains(f)) {
            CS.add(f);
        }
        f = cell.bottom_right;
        if (f != null && !CS.contains(f)) {
            CS.add(f);
        }
    }

    public void knn() {
        int size = graph.Nodes.size();
        double[] value = new double[size];
        Arrays.fill(value, Double.MAX_VALUE);
        ArrayList<Integer> S = new ArrayList<Integer>();
        ArrayList<Integer> Q = new ArrayList<Integer>();
        LinkedList<Objects> ObjectCandidate = new LinkedList<Objects>();
        Map<Integer, Integer> map = graph.VertexMap;
        value[map.get(CurrentNode)] = 0;
        //   S.add(graph.Map[CurrentNode]);
        for (int i = 0; i < size; i++) {
            Q.add(i);
        }
        Q.remove(map.get(CurrentNode));
        LinkedList<Integer> neighbor = new LinkedList<Integer>();
        graph.getAllNeighbours(map.get(CurrentNode), neighbor);
        for (Integer i : neighbor) {
            value[i] = graph.getEdgeDistance(map.get(CurrentNode), i);
        }
        int s = 0;
        int index = 0;
        boolean ContinueProcess = true;
        while (ContinueProcess) {
            double d = Double.MAX_VALUE;
            boolean flag=false;
            for (int i = 0; i < Q.size(); i++) {
                if (value[Q.get(i)] < d) {
                    d = value[Q.get(i)];
                    index = i;
                    s = Q.get(i);
                    flag=true;
                }
            }
            // System.out.println("min"+d);
           try{
               Q.remove(index);
           }catch (Exception e){
               System.out.println("index="+index);
               System.out.println("Size="+Q.size());
               System.out.println("flag="+flag);
               System.out.println("value="+value[Q.get(0)]);
           }

            for (Integer i : Q) {
                if (value[i] > value[s] + graph.getEdgeDistance(s, i)) {
                    value[i] = value[s] + graph.getEdgeDistance(s, i);
                    Objects ob = ObjectsMap.get(graph.Vertex[i].NodeName);
                    if (ob != null) {
                        ObjectCandidate.add(ob);
                    }
                    if (ObjectCandidate.size() >= K) {
                        ContinueProcess = false;
                    }
                }
            }

        }
//        System.out.println("结果如下");
//        ShowResult(value, ObjectCandidate);
    }

    public void ShowResult(double[] value, LinkedList<Objects> ObjectCandidate) {
        int i = 0;
        for (Objects o : ObjectCandidate) {
            System.out.println((++i) + " object name " + o.ObjectNo + " Node name" + o.NodeName + " Distance" + value[o.NodeName]);
        }
    }

    //*************************************************
    public void ceshi() {
        LinkedList<Integer> neighbor = new LinkedList<Integer>();
        System.out.println("node num=" + graph.Nodes.size());
        int n = graph.Nodes.get(2).NodeName;
        graph.getAllNeighbours(n, neighbor);
        System.out.println("nei len" + neighbor.size());
        System.out.println("序号" + n + " node名字 " + graph.Vertex[n].NodeName);
        for (Integer i : neighbor) {
            System.out.println(i);
        }
        System.out.println(graph.Vertex[n].NodeName + "-" + graph.Vertex[neighbor.get(0)].NodeName + "距离为" + graph.getEdgeDistance(n, neighbor.get(0)));
    }

    public void showxy(Grid g) {
        System.out.println(g.X1 + " " + g.X2 + " " + g.Y1 + " " + g.Y2);
    }

    public void showsize(ArrayList a) {
        System.out.println("len " + a.size());
    }
    //*************************************************
}
