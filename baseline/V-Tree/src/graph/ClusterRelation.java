package graph;

import java.util.*;

/**
 * @author: zhoutao
 * @since: 2021/11/18 9:55 下午
 * @description: TODO
 */
public class ClusterRelation {
    private int parent = -1;

    private List<Integer> sons;

    private Map<Integer, Integer> neighborClusterMap;

    public int level;

    public ClusterRelation(int level) {
        this.level = level;
        sons = new ArrayList<>();
        neighborClusterMap = new HashMap<>();
    }

    public List<Integer> getSortedNeighbor() {
        List<Integer> neighborClusterList = new LinkedList<>(neighborClusterMap.keySet());
        Collections.sort(neighborClusterList, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return Integer.compare(neighborClusterMap.get(o2), neighborClusterMap.get(o1));
            }
        });
        neighborClusterMap.clear();
        return neighborClusterList;
    }

    public List<Integer> getSons() {
        return sons;
    }

    public void addNeighborCluster(Integer name) {
        neighborClusterMap.put(name, neighborClusterMap.getOrDefault(name, 0) + 1);
    }

    public void addSonCluster(Integer sonName) {
        sons.add(sonName);
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public int getParent() {
        return parent;
    }


}
