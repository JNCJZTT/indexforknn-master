package Baseline.SIMkNN.domain;

import Baseline.base.domain.Node;
import Baseline.base.domain.api.ClusterLink;

import java.util.HashSet;
import java.util.Set;

public class SIMkNNClusterLink extends ClusterLink {

    private Set<Node> borderLink;
    public SIMkNNClusterLink() {
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
