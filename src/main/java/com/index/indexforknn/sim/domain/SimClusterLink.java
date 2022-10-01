package com.index.indexforknn.sim.domain;

import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.base.domain.api.ClusterLink;

import java.util.HashSet;
import java.util.Set;

public class SimClusterLink extends ClusterLink {

    private Set<Node> borderLink;
    public SimClusterLink() {
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
