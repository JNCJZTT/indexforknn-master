package buildindex;

import graph.Car;
import graph.Edge;
import graph.Vertex;
import graph.Vnode;

import java.io.*;
import java.util.*;

public class BuildIndex {
    //打开文件--------------------------------------------------------------------------------------------------------------
    File VertexFile,EdgeFile;
    //打开TenIndex测试集
//    File VertexFile = new File("/Users/zhoutao/Nutstore Files/我的坚果云/论文/地图/TenIndex/TenIndex_vertex.txt");
//    File EdgeFile   = new File("/Users/zhoutao/Nutstore Files/我的坚果云/论文/地图/TenIndex/TenIndex_edge.txt");
    //打开USA_纽约文件
//    File VertexFile = new File("/Users/zhoutao/Nutstore Files/我的坚果云/论文/地图/NY/USA-road-d.NY.co/USA-road-d.NY.txt");
//    File EdgeFile = new File("/Users/zhoutao/Nutstore Files/我的坚果云/论文/地图/NY/USA-road-d.NY.gr/USA-road-d.NY.txt");
    //打开USA_kele文件
//    File VertexFile = new File("/Users/zhoutao/Nutstore Files/我的坚果云/论文/地图/COL/USA-road-d.COL.co");
//    File EdgeFile = new File("/Users/zhoutao/Nutstore Files/我的坚果云/论文/地图/COL/USA-road-d.COL.gr");
    //打开NW文件
    //    File VertexFile = new File("/Users/zhoutao/Nutstore Files/我的坚果云/论文/地图/NW/USA-road-d.NW.co");
//    File EdgeFile = new File("/Users/zhoutao/Nutstore Files/我的坚果云/论文/地图/NW/USA-road-d.NW.gr");

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


    public static HashMap<Integer, Vertex> AllVertices = new HashMap<Integer, Vertex>();      //节点
    public static HashSet<Integer> AllNames = new HashSet<>();
    public Stack<Integer> TreeStack;                                            //树栈
    public static HashMap<HashSet<Integer>, Integer> TreeNode = new HashMap<>();
    public static Vertex Root;                                                  //根节点
    public static Integer K=10;

    private int VertexSize=0;
    public static Random random = new Random();
    public static int[] Value;                                                  //查询点到每个点的值

    private int CarSize=20000;
    private List<Car> Cars = new ArrayList<Car>(CarSize);                       //移动对象数量
    long UpdateTime;

    public BuildIndex(int CarSize, String map) {
        this.CarSize = CarSize;
        if (map == "NY") {
            //打开USA_纽约文件
            VertexFile = new File("/Users/zhoutao/Nutstore Files/我的坚果云/论文/地图/NY/USA-road-d.NY.co/USA-road-d.NY.txt");
            EdgeFile = new File("/Users/zhoutao/Nutstore Files/我的坚果云/论文/地图/NY/USA-road-d.NY.gr/USA-road-d.NY.txt");
        } else if (map == "COL") {
            //打开USA_kele文件
            VertexFile = new File("/Users/zhoutao/Nutstore Files/我的坚果云/论文/地图/COL/USA-road-d.COL.co");
            EdgeFile = new File("/Users/zhoutao/Nutstore Files/我的坚果云/论文/地图/COL/USA-road-d.COL.gr");
        } else if (map == "NW") {
            //打开NW文件
            VertexFile = new File("/Users/zhoutao/Nutstore Files/我的坚果云/论文/地图/NW/USA-road-d.NW.co");
            EdgeFile = new File("/Users/zhoutao/Nutstore Files/我的坚果云/论文/地图/NW/USA-road-d.NW.gr");
        }
    }

    public void Run() throws IOException {
        long BuildStart = System.currentTimeMillis();                           //开始构造 （毫秒）

        BuildVertex();
        BuildEdge();
        BuildStack();
        BuildTree();

        //TestBuildCar();
        BuildCar();
        BuildCKNN();


        InitValue();
        long BuildEnd = System.currentTimeMillis();        //结束构造
        System.out.println("构造时间=" + (BuildEnd - BuildStart) / 1000 + "秒 ");
    }



    //构造索引-------------------------------------------------------------------------------
    //读取文件，构造节点
    private final void BuildVertex() throws IOException {
        if (VertexFile.isFile() && VertexFile.exists()) {
            //打开顶点文件
            InputStreamReader read = new InputStreamReader(new FileInputStream(VertexFile));
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
        }
        AllNames.addAll(AllVertices.keySet());
        VertexSize=AllVertices.size();
    }

    //读取边文件
    //读取文件，构造关系,判断是否是边界点
    private final void BuildEdge() throws FileNotFoundException, IOException {

        if (EdgeFile.isFile() && EdgeFile.exists()) {
            //判断文件是否存在
            InputStreamReader read = new InputStreamReader(new FileInputStream(EdgeFile));
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
                if (v.Degree<=MinDegree) {
                    v.BuildClique();                    //构建团

                    TreeStack.add(Name);                //进栈
                    AllNamesIter.remove();
                }
            }
            MinDegree++;
        }
      //  System.out.println("结束BuildStack");
    }

    //根据Stack构建树
    private final void BuildTree() {
        //栈顶作为根节点
        Root = AllVertices.get(TreeStack.pop());
        Root.SetRoot();
    //    System.out.println("Root=" + Root.VertexName);

        //构建树
        while (!TreeStack.empty()) {
            //如果栈不为空
            Integer Name = TreeStack.pop();                       //出栈
            Vertex v = AllVertices.get(Name);
            v.SetParent();                                        //寻找父亲
        }

   //     System.out.println("结束BuildTree");
    }

    //构建汽车 活跃点对象
    private final void BuildCar(){
        long CarBegin = System.currentTimeMillis();
        for (int i = 0; i < CarSize; i++) {

            Edge e = GetRandomEdge();
            Car c = new Car(i, e);
            Cars.add(c);
            Vertex v = AllVertices.get(e.to);
            if(!v.isActive()){
                v.setActive();
                v.BuildAncestor();                          //只是计算到祖先的距离
            }
            v.AddCar(c);                                    //添加移动对象
        }
        long CarEnd = System.currentTimeMillis();
    //    System.out.println("构建汽车时间：" + (CarEnd - CarBegin) + "毫秒");
    }

    public void UpdateCar(int Size){
        long CarBegin = System.currentTimeMillis();
        for (int i = 0; i < Size; i++) {
            Car c = Cars.get(random.nextInt(Cars.size()));                //随机获得一辆Car
            if(c.isUpdate()){
                Vertex v = c.GetToVertex();
                v.RemoveCar(c);
                v = c.UpdateLocation();
                if (!v.isActive()) {
                    v.BUildCKNN();
                }
                v.AddCar(c);
            }
        }
        long CarEnd = System.currentTimeMillis();
        UpdateTime=CarEnd - CarBegin;
    }

    private final void TestBuildCar(){
//        for(int i=1;i<=VertexSize;i++){
//            Car c = new Car(i);
//            Cars.add(c);
//            Vertex v = AllVertices.get(i);
//            if(!v.isActive()){
//                v.BuildAncestor();                          //只是计算到祖先的距离
//            }
//            v.AddCar(c);                                    //添加移动对象
//        }
        AllVertices.get(5).setActive();
        AllVertices.get(5).AddCar(new Car(0));
        AllVertices.get(5).BuildAncestor();
        AllVertices.get(7).setActive();
        AllVertices.get(7).BuildAncestor();
        AllVertices.get(7).AddCar(new Car(1));
        AllVertices.get(9).setActive();
        AllVertices.get(9).BuildAncestor();
        AllVertices.get(9).AddCar(new Car(2));
        AllVertices.get(19).setActive();
        AllVertices.get(19).BuildAncestor();
        AllVertices.get(19).AddCar(new Car(3));
    }

    private void BuildCKNN(){

        AllNames.addAll(AllVertices.keySet());

        int MaxTreeLevel=0;
        //找到最大的层高
        for(int i=1;i<=VertexSize;i++){
            if(AllVertices.get(i).getTreeLevel()>MaxTreeLevel) {
                MaxTreeLevel = AllVertices.get(i).getTreeLevel();
            }
        }
        System.out.println("MaxTreeLevel="+MaxTreeLevel);


        while(!AllNames.isEmpty()){
            Iterator<Integer> AllNamesIter = AllNames.iterator();

            while (AllNamesIter.hasNext()) {
                Integer Name = AllNamesIter.next();
                Vertex v = AllVertices.get(Name);
                //当层高等于最大层高时
                if (v.getTreeLevel()==MaxTreeLevel) {

                    v.BUildCKNN();
                    AllNamesIter.remove();
                }
            }
            MaxTreeLevel--;
        }








    }



    //返回一个随机的查询点名字---------------------------------------------------------
    public int GetRandomQueryName() {
        return random.nextInt(VertexSize) + 1;
    }

    //返回一个随机的节点
    public Vertex GetRandomVertex(){
        return AllVertices.get(GetRandomQueryName());
    }

    //返回一条随机的边
    private Edge GetRandomEdge(){
        Vertex v=GetRandomVertex();         //随机一个节点
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

    public long GetUpdateTime(){
        return this.UpdateTime;
    }


}
