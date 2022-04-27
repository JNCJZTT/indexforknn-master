package search;

import graph.Car;
import graph.Vertex;
import graph.Vnode;

import java.util.*;

import static buildindex.BuildIndex.*;

public class Search {

    private Vertex QueryVertex;
    public List<Vnode> KNN=new ArrayList<Vnode>();
    public List<Car> KCars=new ArrayList<>();
    private ArrayList<Vnode> Ancestors;
    private HashSet<Integer> Names=new HashSet<>();
    private int FarthestCarDis;                                                                         //最远的车的距离
    private long time=0;


    public Search(int QueryName,String se){
        QueryVertex=AllVertices.get(QueryName);
        if(!QueryVertex.isBuildAncestor()){
            QueryVertex.BuildAncestor();
        }

        Ancestors=QueryVertex.getAncestors();
        long StartSearchTime,EndSeachTime;

        if(se=="t"){
            StartSearchTime=System.nanoTime();
            TenQuery();
            EndSeachTime=System.nanoTime();
        }else{
            StartSearchTime=System.nanoTime();
            TenQueryIP();
            EndSeachTime=System.nanoTime();
        }
        time=+(EndSeachTime-StartSearchTime)/1000;



    }

    public long getTime() {
        return time;
    }

    private void TenQuery(){

        for(Vnode vn:Ancestors){
            Vertex Parent = AllVertices.get(vn.name);

            for(Vnode Active:Parent.getKTNN()){
                int dis=vn.dis+Active.dis;
                if(vn.dis!=-1&&Active.dis!=-1&&(Value[Active.name]==-1||Value[Active.name]>dis)){
                    Value[Active.name]=dis;
                    Names.add(Active.name);
                }
            }
        }

        //System.out.println("添加KNN-------------------");
        for(Integer Name:Names){
            Vertex v=AllVertices.get(Name);
            v.SetCarQueryDis(Value[Name]);
            KCars.addAll(v.getVertexCars());
            //KNN.add(new Vnode(Name,Value[Name]));
        }
        Collections.sort(KCars,new QueryDisComprator());
        KCars=KCars.subList(0,K);
    }

    private void TenQueryIP(){
        HashSet<Integer> Output=new HashSet<>();
        ArrayList<Integer> R=new ArrayList<>();
        ArrayList<Integer> step=new ArrayList<>();

        //初始化
        for(int i=0;i<Ancestors.size();i++){
            step.add(0);
            R.add(-1);
        }

        for(int i=0;i<Ancestors.size();i++){
            Vnode vn=Ancestors.get(i);
            Vertex parent=AllVertices.get(vn.name);
            if(vn.dis!=-1&&!parent.getKTNN().isEmpty()){
                //如果非空

                Vnode active=parent.getKTNN().get(0);
                if(active.dis!=-1&&(Value[active.name]==-1||Value[active.name]>vn.dis+active.dis)){
                    Value[active.name]=vn.dis+active.dis;
                }
                if(Value[active.name]!=-1){
                    //只要是连通的，就先添加
                    R.set(i,active.name);
                }
            }
        }


//        for(int i=0;i<Ancestors.size();i++){
//            System.out.println("r="+R.get(i));
//        }


        while(true){
            int Mindis=Integer.MAX_VALUE;
            Integer MinName=-1;
            int index=-1;
          //  System.out.println("第"+i+"轮");
            for(int j=0;j<R.size();j++){
                Integer r=R.get(j);
                if(r!=-1&&Value[r]!=-1&&Value[r]<Mindis){
                    Mindis=Value[r];             //找到最小值
                    MinName=r;
                    index = j;
                }

      //          System.out.println("MinName="+MinName+"  index="+ index+"  dis="+Mindis+"  r="+r);
            }

            if(MinName==-1||index==-1){
                System.out.println("-1-1 出错！");
            }
            //从R中，找到一个距离最小的节点

            if(!Output.contains(MinName)) {
                //如果output中，不存在该节点
                Vertex v=AllVertices.get(MinName);
                v.SetCarQueryDis(Value[MinName]);
                if(IsBreak(v)){
                    System.out.println("算法结束");
                    break;
                }
                Output.add(MinName);
            }
            Vnode vn=Ancestors.get(index);
            Vertex parent = AllVertices.get(vn.name);        //找到所在节点

            while(true) {
                int s = step.get(index) + 1;
                step.set(index, s);                         //更新KTNN的位置


                if (parent.getKTNN().size() > s) {
                    Vnode active = parent.getKTNN().get(s);
                    if (active.dis != -1 && (Value[active.name] == -1 || Value[active.name] > vn.dis + active.dis)) {
                        Value[active.name] = vn.dis + active.dis;
                        R.set(index, active.name);
                    }
                    if(!Output.contains(R.get(index))){
                        break;
                    }
                } else {
                    R.set(index, -1);
                    break;
                }
            }
        }



    }


    //处理每个顶点；返回true时，直接结束算法
    private boolean IsBreak(Vertex v) {
        if (KCars.size() < K) {                                                                 //还未满K辆车
            KCars.addAll(v.getVertexCars());                                                    //设置移动对象的QueryDis,并添加到KCars中去
            return false;
        } else {                                                                                //   >=k辆车
            Collections.sort(KCars,new QueryDisComprator());                                    //对车进行排序，剪枝为正好k辆汽车，并设置FarthestCarDis
            KCars=KCars.subList(0,K);
            FarthestCarDis=KCars.get(K-1).QueryDis;

           // displayKNN();
            if (Value[v.VertexName] >= FarthestCarDis) {
                return true;
            } else{
                KCars.addAll(v.getVertexCars());                                        //设置移动对象的QueryDis,并添加到KCars中去
            }
            return false;
        }
    }

    private void displayKNN(){
        for(int i=0;i<K;i++){
            System.out.println("Name="+KCars.get(i).CarName+" dis="+KCars.get(i).QueryDis);
        }
    }



    //移动对象的Comprator方法
    static class QueryDisComprator implements Comparator {

        public int compare(Object object1, Object object2) {
            Car p1 = (Car) object1;
            Car p2 = (Car) object2;
            return Integer.compare(p1.QueryDis, p2.QueryDis);
        }
    }
}
