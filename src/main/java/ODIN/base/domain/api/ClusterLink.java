package ODIN.base.domain.api;

import java.util.HashMap;
import java.util.Map;

/**
 * ClusterLink
 * 2022/5/12 zhoutao
 */
public abstract class ClusterLink {
    protected Map<Integer, Integer> clusterLinkMap;

    public ClusterLink() {
        clusterLinkMap = new HashMap<>();
    }

    /**
     * addClusterLink
     *
     * @param to  to
     * @param dis dis
     */
    public void addClusterLink(Integer to, Integer dis) {
        clusterLinkMap.put(to, dis);
    }

    /**
     * get dis between this node to "to"
     *
     * @param to to
     * @return dis
     */
    public int getClusterDis(Integer to) {
        return clusterLinkMap.getOrDefault(to, -1);
//        if (!clusterLinkMap.containsKey(to)) {
//            return -1;
//        }
//        return clusterLinkMap.get(to);
    }
}
