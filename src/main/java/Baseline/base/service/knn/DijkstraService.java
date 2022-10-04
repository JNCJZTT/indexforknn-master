package Baseline.base.service.knn;

import Baseline.base.domain.Car;
import Baseline.base.domain.GlobalVariable;
import Baseline.base.domain.Node;
import Baseline.base.domain.api.Vertex;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * base Dijkstra algorithm
 * 2022/4/21 zhoutao
 */
@Service
public class DijkstraService {
    public PriorityQueue<Car> kCars;

    public int[] queryDis;

    public int[] path;

    private int queryName;

    public DijkstraService() {
    }

    public void Dijkstra(List vertices, int queryName) {
        this.queryName = queryName;
        kCars = new PriorityQueue<>(new Car.QueryDisComprator());
        int num = vertices.size(), nearestName = queryName;
        queryDis = new int[num];
        path = new int[num];
        Arrays.fill(path, -1);
        Arrays.fill(queryDis, -1);
        queryDis[queryName] = 0;

        PriorityQueue<Integer> currentNames = new PriorityQueue<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return Integer.compare(queryDis[o1], queryDis[o2]);
            }
        });

        while (true) {
            Vertex vertex = (Vertex) vertices.get(nearestName);
            if (vertex.isActive()) {
                int dis = queryDis[nearestName];
                for (Car car : vertex.getCars()) {
                    car.setQueryDis(dis);
                    if (kCars.size() == GlobalVariable.K) {
                        if (kCars.peek().getQueryDis() > car.getQueryDis()) {
                            kCars.poll();
                            kCars.add(car);
                        } else {
                            break;
                        }
                    } else {
                        kCars.add(car);
                    }
                }
            }


            //遍历邻节点
            for (Node vn : ((Vertex) vertices.get(nearestName)).getOrigionEdges()) {
                int name = vn.getName(), dis = vn.getDis() + queryDis[nearestName];

                if (queryDis[name] == -1 || queryDis[name] > dis) {
                    if (queryDis[name] > dis) {
                        currentNames.remove(name);
                    }
                    path[name] = nearestName;
                    queryDis[name] = dis;
                    currentNames.add(name);
                }
            }
            if (kCars.size() >= GlobalVariable.K && kCars.peek().getQueryDis() <= queryDis[nearestName]) {
                break;
            }
            nearestName = currentNames.poll();
        }
    }

    public Stack<Integer> printPath(int name) {
        Stack<Integer> stack = new Stack<>();
        while (name != -1 && name != queryName) {
            stack.push(name);
            name = path[name];
        }
        return stack;
    }
}
