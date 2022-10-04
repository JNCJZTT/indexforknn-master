package ODIN.ODIN.domain;

import ODIN.base.domain.api.ClusterLink;
import ODIN.base.domain.Node;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * ODINClusterLink
 * 2022/2/12 zhoutao
 */
@Slf4j
@Getter
public class ODINClusterLink extends ClusterLink {
//    private Map<Integer, Integer> clusterLinkMap;

    private final Set<Node> borderLink;

    public ODINClusterLink() {
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
