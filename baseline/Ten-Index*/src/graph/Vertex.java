package graph;

import java.util.*;

import static buildindex.BuildIndex.random;
import static buildindex.BuildIndex.AllVertices;
import static buildindex.BuildIndex.K;


public class Vertex {
    public Integer VertexName;                                              //节点的名字
    public int Degree = 0;                                                  //度数
    private int TreeLevel = -1;                                             //层数
    public List<Vnode> Nodes = new ArrayList<>();                         //真实路网节点

    private boolean IsRoot = false;                                         //是否是根节点
    private boolean IsLeaf = true;                                          //是否是叶子节点

    private boolean IsTreeNode = false;                                     //是否出栈

    public Integer Parent = -1;                                              //父亲节点
    private ArrayList<Integer> Sons = new ArrayList<Integer>();                     //孩子节点


    //团
    private HashMap<Integer, LinkNode> LinkNodes = new HashMap<>();         //树的实际邻节点

    private boolean IsActive = false;
    private List<Car> VertexCars=new ArrayList<>();                         //索引的汽车

    private ArrayList<Vnode> Ancestors = new ArrayList<>();                 //到祖先节点到距离
    private boolean IsBuildAncestor=false;                                  //是否构造到祖先
    private boolean IsComputeLinkNode =false;                                  //是否计算到邻居节点到最短距离
    private int AncestorIndex=-1;                                           //暂时的下标 （ 仅为计算 KTNN）
    private int MaxDis =200000;                                              //KTNN的阈值 距离超过MaxDis,则认为不连通
    private List<Vnode> KTNN=new ArrayList<>(K);
    private HashSet<Integer> KTNNName=new HashSet<>();


    public Vertex(Integer vertexName) {
        this.VertexName = vertexName;
    }

    //添加路网节点
    public void AddNodes(Vnode vn) {
        this.Nodes.add(vn);
        this.LinkNodes.put(vn.name, new LinkNode(vn));
        this.Degree++;
    }

    //添加邻居节点
    private void AddLink(Vnode vn) {
        this.LinkNodes.put(vn.name, new LinkNode(vn));
        this.Degree++;
    }

    //删除邻居节点
    private void RemoveLink(Integer Name) {
        this.LinkNodes.remove(Name);
        this.Degree--;
    }

    //更新邻居节点的距离
    private void UpdateLinkDis(Integer Name, int dis) {
        this.LinkNodes.get(Name).setDis(dis);
    }

    //返回邻居节点的距离
    private int GetLinkDis(Integer Name) {
        return this.LinkNodes.get(Name).getDis();
    }

    //将邻居节点构造程一个团
    public void BuildClique() {
        int size = this.LinkNodes.size();                           //邻居节点的数量

        //初始化名字索引和距离数组
        int[] Name = new int[size];
        int[][] MinDis = new int[size][size];                       //Minidis记录 邻节点和邻节点之间的最短距离（局部最短）
        int Dis[] = new int[size];

        int i = 0;
        for (LinkNode ln : this.LinkNodes.values()) {
            Dis[i] = ln.getDis();
            Name[i] = ln.getName();
            ln.setIndex(i++);
        }



        for (i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                //初始化Minidis
                MinDis[i][j] = -1;
                MinDis[j][i] = -1;
            }

            Vertex v = AllVertices.get(Name[i]);                    //邻居节点（此时还不包括自身）
            v.RemoveLink(this.VertexName);                          //从每个节点中删除目标节点

            //遍历邻居节点的邻居节点，如果邻居节点的邻居节点也是我的邻居节点
            for (Integer Neighbor : v.LinkNodes.keySet()) {
                if (this.LinkNodes.keySet().contains(Neighbor)) {
                    int k = this.LinkNodes.get(Neighbor).getIndex();
                    int dis = v.GetLinkDis(Neighbor);                //如果v中存在目标节点的邻居节点，则返回距离
                    MinDis[i][k] = dis;
                    MinDis[k][i] = dis;
                }
            }

            for (int j = i + 1; j < size; j++) {
                int dis = Dis[i] + Dis[j];

                if (MinDis[i][j] == -1) {
                    //如果-1，说明两个节点不连通
                    AllVertices.get(Name[i]).AddLink(new Vnode(Name[j], dis));
                    AllVertices.get(Name[j]).AddLink(new Vnode(Name[i], dis));
                } else if (MinDis[i][j] > dis) {
                    //更新邻节点之间的距离
                    AllVertices.get(Name[i]).UpdateLinkDis(Name[j], dis);
                    AllVertices.get(Name[j]).UpdateLinkDis(Name[i], dis);
                } else if (Dis[i] > Dis[j] + MinDis[i][j]) {
                    //更新目标节点到邻居节点之间的距离
                    this.UpdateLinkDis(Name[i], Dis[j] + MinDis[i][j]);
                } else if (Dis[j] > Dis[i] + MinDis[i][j]) {
                    this.UpdateLinkDis(Name[j], Dis[i] + MinDis[i][j]);
                }

            }
        }
        //新建自身
        //LinkNodes.put(this.VertexName, new LinkNode(this.VertexName));
    }

    //设置为根节点
    public void SetRoot() {
        IsRoot = true;
        IsLeaf=false;
        TreeLevel = 0;
        LinkNodes.put(this.VertexName,new LinkNode((this.VertexName)));         //新建自身（因为root没有setparent)
        this.IsTreeNode=true;
    }

    //如果一个节点包含自己所有的邻节点，就是自己的父亲节点 (首先自己已经出栈）
    public boolean IsParent(Set<Integer> bag) {
        return this.IsTreeNode&&this.LinkNodes.keySet().containsAll(bag);
    }

    //设置本节点的父亲节点
    public void SetParent() {
//        HashSet<Integer> bag = new HashSet<>(this.LinkNodes.keySet());  //新建包
//        bag.remove(this.VertexName);                                    //去掉关键字
        Vertex vp = null;

        for (Integer Neighbor : this.LinkNodes.keySet()) {
            //遍历邻节点，从邻节点中寻找自己的父亲节点 (存在自己的所有keyset）  已出栈&&度数最小
            if ((vp == null || vp.Degree > AllVertices.get(Neighbor).Degree) && AllVertices.get(Neighbor).IsParent(this.LinkNodes.keySet())) {
                vp = AllVertices.get(Neighbor);
            }
        }
        this.Parent = vp.VertexName;                                      //设置父亲节点
        this.TreeLevel = vp.TreeLevel + 1;                                //设置层数 为父亲层+1
        this.IsTreeNode=true;
        this.LinkNodes.put(this.VertexName,new LinkNode((this.VertexName)));   //新建自身
        vp.SetSon(this.VertexName);                                       //父亲节点修改为非叶子节点 且添加孩子节点
    }

    //添加孩子
    public void SetSon(Integer Son) {
        if (IsLeaf) {
            IsLeaf = false;
        }
        this.Sons.add(Son);
    }

    //判断是否活跃
    public boolean isActive() {
        return IsActive;
    }

    public void setActive() {
        IsActive = true;
    }

    private void SetInActive(){
        if(IsActive){
            IsActive=false;
        }
    }

    private Vertex GetParent() {
        return AllVertices.get(this.Parent);
    }

    //初始化到所有祖先节点的距离
    private void InitAncestor() {
        Vertex v = AllVertices.get(this.VertexName);
        Ancestors.add(new Vnode(this.VertexName, 0));

        int index=0;
        v.AncestorIndex=index++;

        while (v.Parent != -1) {
            v = v.GetParent();
            v.AncestorIndex=index++;
            Ancestors.add(new Vnode(v.VertexName, -1));
        }
        for(LinkNode ln:this.LinkNodes.values()){
            Ancestors.get(AllVertices.get(ln.getName()).AncestorIndex).dis=ln.getDis();
        }
    }

    //计算到 团的 最短距离
    public void ComputeLinkNode(){
        if(IsBuildAncestor||IsComputeLinkNode){
            //如果已经是true了
            return ;
        }
        IsComputeLinkNode =true;
        //初始化祖先节点
        InitAncestor();

        //待处理的祖先节点
        HashSet<Integer> UpdateParents=new HashSet<>(Ancestors.size());
        UpdateParents.addAll(LinkNodes.keySet());
        UpdateParents.remove(this.VertexName);

        HashSet<Integer> EndLink=new HashSet<>(LinkNodes.size());
        EndLink.addAll(LinkNodes.keySet());
        EndLink.remove(this.VertexName);

        while(!UpdateParents.isEmpty()||!EndLink.isEmpty()){
            Integer MinDis=Integer.MAX_VALUE;
            Vnode NearestVnode=null;
            int index=-1;
            for(Integer Name:UpdateParents){
                //遍历待处理的祖先节点，找到距离目标节点最小的
                if(Ancestors.get(AllVertices.get(Name).AncestorIndex).dis<MinDis){
                    index = AllVertices.get(Name).AncestorIndex;
                    MinDis=Ancestors.get(index).dis;                                //更新MinDis
                }
            }

            NearestVnode=Ancestors.get(index);
            AllVertices.get(NearestVnode.name).UpdateAncestor(new Vnode(this.VertexName,NearestVnode.dis),Ancestors,UpdateParents);
            UpdateParents.remove(NearestVnode.name);
            if(LinkNodes.keySet().contains(this.VertexName)){
                LinkNodes.get(this.VertexName).setDis(MinDis);
                EndLink.remove(this.VertexName);
            }
        }
    }

    //计算到祖先节点的最短距离
    public void BuildAncestor() {
        if(IsBuildAncestor){
            //如果已经是true了
            return ;
        }
        IsBuildAncestor=true;
        //初始化祖先节点
        InitAncestor();

        //待处理的祖先节点
        HashSet<Integer> UpdateParents=new HashSet<>(Ancestors.size());
        UpdateParents.addAll(LinkNodes.keySet());
        UpdateParents.remove(this.VertexName);

        while(!UpdateParents.isEmpty()){
            Integer MinDis=Integer.MAX_VALUE;
            Vnode NearestVnode=null;
            int index=-1;
            for(Integer Name:UpdateParents){
                //遍历待处理的祖先节点，找到距离目标节点最小的
                if(Ancestors.get(AllVertices.get(Name).AncestorIndex).dis<MinDis){
                    index = AllVertices.get(Name).AncestorIndex;
                    MinDis=Ancestors.get(index).dis;                                //更新MinDis
                }
            }

            NearestVnode=Ancestors.get(index);
            AllVertices.get(NearestVnode.name).UpdateAncestor(new Vnode(this.VertexName,NearestVnode.dis),Ancestors,UpdateParents);
            UpdateParents.remove(NearestVnode.name);
        }
    }

    //vn.name 是活跃节点   vn.dis = this.vertexName 到 vn.name 的距离
    private void UpdateAncestor(Vnode vn, ArrayList<Vnode> Temp, HashSet<Integer> UpdateParents) {
        int index = this.AncestorIndex;           //自己在祖先中到序号

        //先遍历子孙节点
        for (int i = 0; i < index; i++) {
            Vnode Ancestor = Temp.get(i);
            Vertex v = AllVertices.get(Ancestor.name);

            //如果 目标节点到子孙节点到距离
            if ((Ancestor.dis==-1||Ancestor.dis > vn.dis) && v.LinkNodes.containsKey(this.VertexName)) {
                //如果到该祖先节点到距离未赋值 或者
                int dis = vn.dis + v.LinkNodes.get(this.VertexName).getDis();               //目标节点经过自身再到祖先节点的距离

                if((dis<MaxDis&&Ancestor.dis==-1)||Ancestor.dis>dis){
                    Ancestor.dis=dis;                                                       //更新距离
                    UpdateParents.add(Ancestor.name);                                       //添加待处理的祖先节点
                }
            }
        }

        //遍历自己以上到节点
        for(LinkNode ln:this.LinkNodes.values()){
            int pindex =AllVertices.get(ln.getName()).AncestorIndex;
            Vnode Ancestor=Temp.get(pindex);
            int dis=ln.getDis()+vn.dis;
            if((dis<MaxDis&&Ancestor.dis==-1)||Ancestor.dis>dis){
                Ancestor.dis=dis;
                UpdateParents.add(Ancestor.name);
            }
        }
    }

    //BuildCkNN
    public void BUildCKNN(){
        if(this.IsActive){
            //如果自身是活跃的，添加自身
            UpdateKTNN(this.VertexName,0);
        }
        for(Integer i:this.LinkNodes.keySet()){
            if(i!=this.VertexName){
                //更新不是自己的邻居节点
                Vertex v=AllVertices.get(i);
                for(int j=0;j<this.KTNN.size();j++){
                    //更新KTNN

                    int Name=this.KTNN.get(j).name;
                    int Dis = AllVertices.get(Name).Ancestors.get(AllVertices.get(Name).TreeLevel - v.TreeLevel).dis;       //v是树包节点，Name是活跃点
                    v.UpdateKTNN(Name,Dis);
                }
            }
        }
    }

    //更新KTNN
    private void UpdateKTNN(Integer Name, int dis){

        if(this.KTNNName.contains(Name)){
            //如果已经存在了，直接return
            return;
        }
        if(this.KTNN.size()<K){
            //如果未满，则直接添加
            this.KTNN.add(new Vnode(Name,dis));
            this.KTNNName.add(Name);
            if(this.KTNN.size()==K){
                //如果算上这个为K的话，则排序
                Collections.sort(this.KTNN, new VnodeComprator());
            }
        }else if(this.KTNN.get(K-1).dis>dis) {
            //如果满K个了，则如果最后一个的距离大于它，则删除最后一个
            this.KTNN=this.KTNN.subList(0,K-1);
            this.KTNN.add(new Vnode(Name, dis));
            Collections.sort(this.KTNN, new VnodeComprator());       //排序

            //重置索引匹配
            this.KTNNName.clear();
            for(int i=0;i<K;i++){
                this.KTNNName.add(this.KTNN.get(i).name);
            }

        }

    }

    public void AddCar(Car c){
        VertexCars.add(c);
    }

    public void RemoveCar(Car c){
        VertexCars.remove(c);
        if(VertexCars.isEmpty()){
            //如果为空,则设置为不活跃节点
            this.SetInActive();             //不活跃节点
            for(Vnode Ancestor:Ancestors){
                if(Ancestor.dis!=-1){
                    AllVertices.get(Ancestor.name).RemoveKTNN(this.VertexName,Ancestor.dis);
                }
            }

        }
    }

    private void RemoveKTNN(Integer Name,int dis){
        if(this.KTNN.size()<K){
            //如果KTNN小于K  说明已经没有其它的活跃点了，直接删除即可
            this.KTNN.remove(new Vnode(Name));
        }
        else if(this.KTNN.get(K-1).dis>dis) {
            this.KTNN.remove(new Vnode(Name));                  //删除该节点
            Collections.sort(this.KTNN,new VnodeComprator());   //重新排序

            Queue<Integer> SonsQueue = new LinkedList<>();
            SonsQueue.add(this.VertexName);
            //遍历所有子孙节点
            Vnode vn = new Vnode(-1,MaxDis);

            int MinDis=this.KTNN.get(K-2).dis;                  //此时KTNN的大小为K-1
            while(!SonsQueue.isEmpty()){
                //队头出队
                Vertex v=AllVertices.get(SonsQueue.poll());

                if(v.isActive()){
                    Vnode Son=v.Ancestors.get(v.TreeLevel-this.TreeLevel);
                    //如果v是活跃点 且此时不在KTNN中 (Son.dis>MinDis已经表示Son.dis!=-1了）
                    if(Son.dis>MinDis&&Son.dis<vn.dis){
                        //找到除前k-1个node中，最小的一个node
                        vn.SetVnode(v.VertexName,Son.dis);
                    }
                }
                SonsQueue.addAll(v.Sons);
            }
            if(vn.name!=-1) {
                this.KTNN.add(vn);
            }
        }
    }

    public void SetCarQueryDis(int ToDis){
        for(Car c:this.VertexCars){
            c.SetQueryDis(ToDis);
        }
    }

    public boolean isBuildAncestor() {
        return IsBuildAncestor;
    }

    public ArrayList<Vnode> getAncestors() {
        return Ancestors;
    }

    public List<Vnode> getKTNN() {
        return KTNN;
    }

    //返回一个随机的路网邻节点
    public Vnode GetRandomNode(){
        return Nodes.get(random.nextInt(Nodes.size()));
    }

    //返回一条随机的道路
    public Edge GetRandomEdge(){
        Vnode vn=GetRandomNode();
        return new Edge(this.VertexName,vn.name,vn.dis);
    }

    public Collection<LinkNode> GetLinkNodes() {
        return LinkNodes.values();
    }

    //返回兴趣点内的移动对象
    public List<Car> getVertexCars() {
        return VertexCars;
    }

    //返回
    public boolean GetIsLeaf(){
        return IsLeaf;
    }

    public int getTreeLevel(){
        return this.TreeLevel;
    }

    public int GetLinkNodeSize(){
        return this.LinkNodes.size();
    }

    public void displayKTNN(){
        for(int i=0;i<this.KTNN.size();i++){
            System.out.print(this.KTNN.get(i).name);
        }
        System.out.println();
    }

    //移动对象的Comprator方法
    public static class VnodeComprator implements Comparator {

        public int compare(Object object1, Object object2) {
            Vnode p1 = (Vnode) object1;
            Vnode p2 = (Vnode) object2;
            return Integer.compare(p1.dis, p2.dis);
        }
    }


}
