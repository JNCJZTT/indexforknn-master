package Baseline.base.domain.api;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Cluster
 * 2022/4/27 zhoutao
 */
@Getter
@Setter
@Slf4j
@SuperBuilder
public abstract class Cluster<T extends ClusterLink> {
    private String name;

    protected Map<Integer, T> clusterLinkMap;

    public Cluster() {
    }

    /**
     * add cluster edge
     * Only saved one-way edges
     * Need to be modifed if the graph is directed
     *
     * @param from from
     * @param to   to
     * @param dis  dis
     */
    public void addClusterLink(Integer from, Integer to, Integer dis) {
        if (from.equals(to)) {
            return;
        }
        if (from < to) {
            clusterLinkMap.get(from).addClusterLink(to, dis);
        } else {
            clusterLinkMap.get(to).addClusterLink(from, dis);
        }
    }

    /**
     * get cluster dis
     *
     * @param from from
     * @param to   to
     * @return the dis between from and to
     */
    public int getClusterDis(Integer from, Integer to) {
        if (from.equals(to)) {
            return 0;
        }
        if (from > to) {
            Integer temp = from;
            from = to;
            to = temp;
        }
        return clusterLinkMap.get(from).getClusterDis(to);
    }

}
