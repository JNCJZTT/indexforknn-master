package com.index.indexforknn.amt.domain;

import com.index.indexforknn.base.domain.api.ClusterLink;
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
public class AmtClusterLink extends ClusterLink {
//    private Map<Integer, Integer> clusterLinkMap;

    private Set<Node> borderLink;

    public AmtClusterLink() {
        super();
        borderLink = new HashSet<>();
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




}
