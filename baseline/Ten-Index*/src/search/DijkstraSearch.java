package search;

import graph.Car;
import graph.Vertex;
import graph.Vnode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static buildindex.BuildIndex.AllVertices;

public class DijkstraSearch {
    public int[] Dis;
    int QueryName;
    int NearestName;
    private int NearestDis = 0;                                         //最近的距离
    int FarthestCarDis = -1;                                            //最远的车的距离
    public ArrayList<Vnode> KNN=new ArrayList<>();
    public List<Car> KCars = new ArrayList<Car>(26000);    //查询到的k辆车
    List<Integer> CurrentNames = new ArrayList<Integer>();

    public DijkstraSearch(int Size, int QueryName) {

        Dis = new int[Size + 1];
        for (int i = 0; i < (Size + 1); i++) {
            Dis[i] = -1;
        }
        NearestName = QueryName;
        this.QueryName = QueryName;
        Dis[NearestName] = 0;
        CurrentNames.add(NearestName);
        Dijkstra();
        Collections.sort(KCars, new Search.QueryDisComprator());
    }

    private void Display(){
        for(Vnode vn:KNN){
            System.out.println("name="+vn.name+"  dis="+vn.dis);
        }
    }

    private void Dijkstra() {
        while (true) {
            Vertex v = AllVertices.get(NearestName);
            IsBreak(v);
            CurrentNames.remove((Integer) (NearestName));

            for (Vnode vn : v.Nodes) {
                Relax(NearestName, vn.name, vn.dis);
            }

            if (CurrentNames.isEmpty()) {
                break;
            }
            NearestName = -1;
            NearestDis = Integer.MAX_VALUE;
            for (Integer i : CurrentNames) {
                if (Dis[i] < NearestDis) {
                    NearestName = i;
                    NearestDis = Dis[i];
                }
            }

        }

    }

    private void Relax(int u, int v, int w) {

        if (Dis[v] == -1 || Dis[v] > Dis[u] + w) {
            if (Dis[v] == -1) {
                CurrentNames.add(v);
            }
            Dis[v] = Dis[u] + w;
        }

    }

    private void IsBreak(Vertex v) {

        if (v.isActive()) {
            v.SetCarQueryDis(NearestDis);
            KCars.addAll(v.getVertexCars());
        }

    }

    public void GetDis(int x) {
        System.out.println("Dis[" + x + "]=" + Dis[x]);
    }


}
