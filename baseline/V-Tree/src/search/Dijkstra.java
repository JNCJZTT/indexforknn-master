package search;

import common.Comprators;
import graph.Car;
import graph.Vertex;
import graph.Vnode;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

import static common.GlobalVariable.VERTEX_NUM;
import static common.GlobalVariable.allVertices;
import static common.GlobalVariable.K;


public class Dijkstra {
    private int[] queryDis;

    private int nearestName;
    private int nearestDis = 0;                                             //最近的距离
    public PriorityQueue<Car> kCars = new PriorityQueue<>(new Comprators.QueryDisComprator());
    ;
    private PriorityQueue<Integer> currentNames = new PriorityQueue<>(new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
            return Integer.compare(queryDis[o1], queryDis[o2]);
        }
    });

    public Dijkstra(int queryName) {

        queryDis = new int[VERTEX_NUM];
        Arrays.fill(queryDis, -1);


        nearestName = queryName;
        queryDis[nearestName] = 0;

        dijkstra();


    }


    private void dijkstra() {
        while (true) {

            //添加移动对象
            Vertex v = allVertices[nearestName];
            if (v.isActive) {
                int dis = queryDis[nearestName];
                for (Car c : v.vertexCars) {
                    c.queryDis = c.curDis + dis;
                    if (kCars.size() == K) {
                        if (kCars.peek().queryDis > c.queryDis) {
                            kCars.poll();
                            kCars.add(c);
                        } else {
                            break;
                        }
                    } else {
                        kCars.add(c);
                    }
                }

            }

            //遍历邻节点
            for (Vnode vn : allVertices[nearestName].originalEdges) {
                int name = vn.name, dis = vn.dis + queryDis[nearestName];

                if (queryDis[name] == -1 || queryDis[name] > dis) {

                    if (queryDis[name] > dis) {
                        currentNames.remove((Integer) name);
                    }
                    queryDis[name] = dis;
                    currentNames.add(name);
                }
            }

            //判断是否跳出
            if (kCars.size() >= K && kCars.peek().queryDis <= queryDis[nearestName]) {
                break;
            }
            nearestName = currentNames.poll();

        }
    }
}
