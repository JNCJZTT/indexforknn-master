package start;


import buildGraph.BuildIndex;
import common.GlobalVariable;
import graph.Car;
import search.Dijkstra;
import search.SGridSearch;

import java.io.IOException;
import java.util.Scanner;

import static common.Constants.MAP_NY;
import static common.Constants.RANDOM_DISTRIBUTE;
import static common.GlobalVariable.K;
import static common.GlobalVariable.updateTime;
import static common.Utils.getVertexName;
import static common.Utils.initGlobalValue;

public class StartSGrid {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        int subGraphSize = 50;
        int carNum = 600_000;
        int k = 10;


        String map = MAP_NY;
        initGlobalValue(map, subGraphSize, carNum, RANDOM_DISTRIBUTE, k);
        BuildIndex b = new BuildIndex();

        while (true) {
            int queryName = getVertexName();
            System.out.println("queryName=" + queryName);
            SGridSearch ahgSearch = new SGridSearch(queryName);
            System.out.println("----------DIJkstra------------");
            Dijkstra dijkstra = new Dijkstra(queryName);

            for (int i = 0; i < K; i++) {
                Car c1 = ahgSearch.kCars.poll(), c2 = dijkstra.kCars.poll();
                System.out.println("CarName= " + c1.carName + " / " + c2.carName + "  dis=" + c1.queryDis + " /" + c2.queryDis);
            }
            System.out.println("查询时间为：" + GlobalVariable.queryTime + "微秒");

            int x = sc.nextInt();
            b.updateCar(x);
            System.out.println("更新时间为："+ updateTime+"微秒");
        }
    }

}
