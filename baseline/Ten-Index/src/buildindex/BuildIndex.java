package buildindex;

import graph.Car;

import graph.Edge;
import graph.Vertex;
import graph.Vnode;

import java.io.*;
import java.util.*;

public class BuildIndex {
    //打开文件--------------------------------------------------------------------------------------------------------------

    private String VertexUrl = "/file/map/", EdgeUrl = "/file/map/";

    public static HashMap<Integer, Vertex> AllVertices = new HashMap<Integer, Vertex>();      //节点
    public static HashSet<Integer> AllNames = new HashSet<>();
    public Stack<Integer> TreeStack;                                            //树栈
    public static HashMap<HashSet<Integer>, Integer> TreeNode = new HashMap<>();
    public static Vertex Root;                                                  //根节点
    public static Integer K = 10;

    private int VertexSize = 0;
    public static Random random = new Random();
    public static int[] Value;                                                  //查询点到每个点的值

    private int CarSize = 20000;
    private List<Car> Cars = new ArrayList<Car>(CarSize);                       //移动对象数量
    long UpdateTime = 0;


    public BuildIndex(int CarSize, String map) {
        this.CarSize = CarSize;
        VertexUrl += map;
        EdgeUrl += map;

        VertexUrl += ("/USA-road-d." + map + ".co");
        EdgeUrl += ("/USA-road-d." + map + ".gr");

    }


    public long Run() throws IOException {
        long BuildStart = System.currentTimeMillis();                           //开始构造 （毫秒）

        BuildVertex();
        BuildEdge();
        BuildStack();
        BuildTree();

        BuildCar();

        // TestBuildCar();

        InitValue();
        long BuildEnd = System.currentTimeMillis();        //结束构造
        return (BuildEnd - BuildStart) / 1000 ;
    }

    //构造索引-------------------------------------------------------------------------------
    //读取文件，构造节点
    private final void BuildVertex() throws IOException {

        //打开顶点文件
        InputStreamReader read = new InputStreamReader(this.getClass().getResourceAsStream(VertexUrl));
        BufferedReader bufferedReader = new BufferedReader(read);
        String lineText = null;                                             //按行读
        while ((lineText = bufferedReader.readLine()) != null) {
            if (lineText.charAt(0) == 'v') {
                String[] s = lineText.split("\\s+");
                int name = Integer.parseInt(s[1]);                          //结点姓名和横纵坐标
//                    int x = Integer.parseInt(s[2]);
//                    int y = Integer.parseInt(s[3]);
                AllVertices.put(name, new Vertex(name));                     //创建结点
            }
        }
        read.close();

        AllNames.addAll(AllVertices.keySet());
        VertexSize = AllVertices.size();
    }

    //读取边文件
    //读取文件，构造关系,判断是否是边界点
    private final void BuildEdge() throws FileNotFoundException, IOException {


        //判断文件是否存在
        InputStreamReader read = new InputStreamReader(this.getClass().getResourceAsStream(EdgeUrl));
        BufferedReader bufferedReader = new BufferedReader(read);
        String lineText = null;
        boolean times = true;
        while ((lineText = bufferedReader.readLine()) != null) {
            if (times) {
                times = false;
                if (lineText.charAt(0) == 'a') {
                    String[] s = lineText.split("\\s+");                 //按空格划分
                    int from = Integer.parseInt(s[1]);                          //name1
                    int to = Integer.parseInt(s[2]);                            //name2
                    int dis = Integer.parseInt(s[3]);                           //name1到name2的距离

                    Vertex v1 = AllVertices.get(from), v2 = AllVertices.get(to);

                    v1.AddNodes(new Vnode(to, dis));
                    v2.AddNodes(new Vnode(from, dis));
                }
            } else {
                //由于上下两条边是相同边仅仅不同方向，所以可以只计算一次。
                times = true;
            }
        }
        read.close();

    }

    //构造索引
    private final void BuildStack() {
        TreeStack = new Stack<>();
        int MinDegree = 1;


        while (!AllNames.isEmpty()) {
            Iterator<Integer> AllNamesIter = AllNames.iterator();

            while (AllNamesIter.hasNext()) {
                Integer Name = AllNamesIter.next();
                Vertex v = AllVertices.get(Name);
                if (v.Degree <= MinDegree) {
                    v.BuildClique();                    //构建团

                    TreeStack.add(Name);                //进栈
                    AllNamesIter.remove();
                }
            }
            MinDegree++;
        }
        //       System.out.println("结束BuildStack");
    }

    //根据Stack构建树
    private final void BuildTree() {
        //栈顶作为根节点
        Root = AllVertices.get(TreeStack.pop());
        Root.SetRoot();
        //     System.out.println("Root=" + Root.VertexName);

        //构建树
        while (!TreeStack.empty()) {
            //如果栈不为空
            Integer Name = TreeStack.pop();                       //出栈
            Vertex v = AllVertices.get(Name);
            v.SetParent();                                        //寻找父亲
        }

        //       System.out.println("结束BuildTree");
    }

    //构建汽车 活跃点对象
    private final void BuildCar() {

        //     long CarBegin = System.currentTimeMillis();
        for (int i = 0; i < CarSize; i++) {

            Edge e = GetRandomEdge();
            Car c = new Car(i, e);
            Cars.add(c);
            Vertex v = AllVertices.get(e.to);
            if (!v.isActive()) {
//                v.setActive();
//                v.BuildAncestor();
                v.BuildKTNN();
            }
            v.AddCar(c);                                    //添加移动对象
        }
        //       long CarEnd = System.currentTimeMillis();
//        System.out.println("构建汽车时间：" + (CarEnd - CarBegin) + "毫秒");
    }

    public void UpdateCar(int Size) {
        long CarBegin = System.currentTimeMillis();
        for (int i = 0; i < Size; i++) {
            Car c = Cars.get(random.nextInt(Cars.size()));                //随机获得一辆Car
            if (c.isUpdate()) {
                Vertex v = c.GetToVertex();
                v.RemoveCar(c);
                v = c.UpdateLocation();
                if (!v.isActive()) {
                    v.BuildKTNN();
                }
                v.AddCar(c);
            }
        }
        long CarEnd = System.currentTimeMillis();
        UpdateTime = CarEnd - CarBegin;
    }


    //测试是否所有的Link都是自己的父亲节点
    private final void TestParent() {
        for (int i = 1; i <= VertexSize; i++) {
            Vertex v = AllVertices.get(i);
//            for(Vnode vn:v.GetLink()){
//                if(!vn.name.equals(v.VertexName)){
//                    if(!v.IsAncestor(vn.name)){
//                        System.out.println("vn.name="+vn.name+"v.name="+v.VertexName);
//                    }
//                }
//            }
        }
        System.out.println("测试结束");
    }

    //返回一个随机的查询点名字---------------------------------------------------------
    public int GetRandomQueryName() {
        return random.nextInt(VertexSize) + 1;
    }

    //返回一个随机的节点
    public Vertex GetRandomVertex() {
        return AllVertices.get(GetRandomQueryName());
    }

    //返回一条随机的边
    private Edge GetRandomEdge() {
        Vertex v = GetRandomVertex();         //随机一个节点
        return v.GetRandomEdge();           //返回一条v做起点的随机的边
    }

    //初始化Value数组
    private final void InitValue() {
        Value = new int[VertexSize + 1];
        Arrays.fill(Value, -1);
    }

    public int getVertexSize() {
        return VertexSize;
    }

    public void ResetValue() {
        Arrays.fill(Value, -1);
    }


    public long GetUpdateTime() {
        return this.UpdateTime;
    }


}
