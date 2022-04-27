package graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author: zhoutao
 * @since: 2021/11/13 8:20 下午
 * @description: TODO
 */
public class ClusterLink {

    private Map<Integer, int[]> clusterDisMap;         //邻接表形式的ClusterDis
    public int index;


    public ClusterLink(int index) {
        this.index = index;
        clusterDisMap = new HashMap<>();
    }


    public void setClusterDis(int to, int dis) {
        if (clusterDisMap.containsKey(to)) {
            clusterDisMap.get(to)[0] = dis;
        } else {
            clusterDisMap.put(to, new int[]{dis});
        }

    }

    public int getClusterDis(int to) {
        if (!clusterDisMap.containsKey(to)) return -1;
        return clusterDisMap.get(to)[0];
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Map<Integer, int[]> getClusterDisMap() {
        return clusterDisMap;
    }

}
