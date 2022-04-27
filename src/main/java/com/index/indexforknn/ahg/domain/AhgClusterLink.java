package com.index.indexforknn.ahg.domain;

import com.index.indexforknn.base.domain.Node;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * AhgClusterLink
 * 2022/2/12 zhoutao
 */
@Slf4j
@Getter
public class AhgClusterLink {
    private Map<Integer, Integer> clusterLinkMap;

    private Set<Node> borderLink;

    public AhgClusterLink() {
        clusterLinkMap = new HashMap<>();
        borderLink = new HashSet<>();
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

    public void addBorderLink(Node node) {
        borderLink.add(node);
    }

    public void addActiveLink(Node node) {
        borderLink.add(node);
    }

    public void removeBorderLink(int name) {
        borderLink.remove(new Node(name));
    }

    public int getClusterDis(Integer to) {
        if (!clusterLinkMap.containsKey(to)) {
            return -1;
        }
        return clusterLinkMap.get(to);
    }


}
