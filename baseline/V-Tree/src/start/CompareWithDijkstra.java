package start;

import buildGraph.BuildGraph;
import buildGraph.BuildIndex;
import common.GlobalVariable;
import graph.Car;
import search.Dijkstra;
import search.VTreeSearch;

import java.io.IOException;
import java.util.Scanner;

import static common.Constants.*;
import static common.GlobalVariable.*;
import static common.Utils.getVertexName;
import static common.Utils.initGlobalValue;

public class CompareWithDijkstra {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);


        int branchNum = 4;
        int subGraphSize = 50;
        int carNum = 600_000;
        int k = 10;

//        System.out.println("请输入branchNum和subGraphSize");

//        branchNum=sc.nextInt();
//        subGraphSize=sc.nextInt();
//        System.out.println("输入完毕");

        String map = MAP_NY;
        initGlobalValue(map, branchNum, subGraphSize, carNum, RANDOM_DISTRIBUTE, k);
        BuildIndex b = new BuildIndex();

        while (true) {
            int queryName=getVertexName();

            System.out.println("queryName=" + queryName);
            VTreeSearch vTreeSearch = new VTreeSearch(queryName);
            System.out.println("----------DIJkstra------------");
            Dijkstra dijkstra = new Dijkstra(queryName);

            for (int i = 0; i < K; i++) {
                Car c2 = dijkstra.kCars.poll();
                System.out.println("CarName= " + c2.carName + "  dis=" + c2.queryDis);
            }

            int x = sc.nextInt();
            b.updateCar(x);
            System.out.println("更新时间为："+ updateTime+"微秒");
        }
    }
}
