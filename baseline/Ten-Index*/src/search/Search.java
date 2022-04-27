package search;

import graph.Car;
import graph.LinkNode;
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
    private  long time=0;
    public Search(int QueryName){
        QueryVertex=AllVertices.get(QueryName);
        if(!QueryVertex.isBuildAncestor()){
            QueryVertex.BuildAncestor();
        }

        Ancestors=QueryVertex.getAncestors();

        long StartSearchTime=System.nanoTime();
        TenQuery();
        long EndSeachTime=System.nanoTime();
        //System.out.println("查询时间为："+(EndSeachTime-StartSearchTime)/1000+"微秒");
        time=(EndSeachTime-StartSearchTime)/1000;
    }

    public long getTime(){
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
//        System.out.println("KCars.size="+KCars.size());
        KCars=KCars.subList(0,K);
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
