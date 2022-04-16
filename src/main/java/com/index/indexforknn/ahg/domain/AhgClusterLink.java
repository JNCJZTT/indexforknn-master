package com.index.indexforknn.ahg.domain;

import com.index.indexforknn.base.domain.Node;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * TODO
 * 2022/2/12 zhoutao
 */
@Slf4j
@Getter
public class AhgClusterLink {
    private Map<Integer, List<Integer>> clusterLinkMap;

    private Set<Node> borderLink;

    public AhgClusterLink() {
        clusterLinkMap = new HashMap<>();
        borderLink = new HashSet<>();
    }

    /**
     * 添加子图内部边
     *
     * @param to  邻居节点
     * @param dis 距离
     */
    public void addClusterLink(Integer to, Integer dis) {
        if (clusterLinkMap.containsKey(to)) {
            clusterLinkMap.get(to).set(0, dis);
        } else {
            clusterLinkMap.put(to, Arrays.asList(dis));
        }
    }

    public void addBorderLink(Node node) {
        borderLink.add(node);
    }

    public void removeBorderLink(int name) {
        borderLink.remove(new Node(name));
    }

    public int getClusterDis(Integer to) {
        if (!clusterLinkMap.containsKey(to)) {
            return -1;
        }
        return clusterLinkMap.get(to).get(0);
    }


}
