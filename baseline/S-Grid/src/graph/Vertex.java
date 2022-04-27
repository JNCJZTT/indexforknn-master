package graph;

import javax.swing.border.Border;
import java.util.*;

import static common.Constants.RANDOM;

/**
 * @author: zhoutao
 * @since: 2021/11/11 10:11 下午
 * @description: TODO
 */
public class Vertex {
    public List<Integer> clusterName;

    public List<Vnode> originalEdges;

    public boolean isBorder = false;

    public Set<Vnode> Link;

    public List<Car> vertexCars;

    public boolean isActive = false;

    public Vertex(Integer clusterName) {
        this.clusterName = Collections.singletonList(clusterName);
        initLink();
    }

    public Vertex(Integer clusterName1, Integer clusterName2) {
        this.clusterName = Arrays.asList(clusterName1, clusterName2);
        isBorder = true;
        initLink();
    }

    private void initLink() {
        originalEdges = new ArrayList<>();
        Link = new HashSet<>();
    }


    public void addOriginalEdges(int name, int dis) {
        originalEdges.add(new Vnode(name, dis));
    }

    public Vnode getRandomEdge() {
        return originalEdges.get(RANDOM.nextInt(originalEdges.size()));
    }

    /*
     * 返回是否更新状态
     * */
    public void addCar(Car c) {
        if (!isActive) {
            isActive=true;
            vertexCars = new ArrayList<>();
        }
        vertexCars.add(c);
    }

    /*
     * 返回是否更新状态
     * */
    public void removeCar(Car c) {
        vertexCars.remove(c);
        if (vertexCars.isEmpty()) {
            isActive = false;
        }
    }
}
