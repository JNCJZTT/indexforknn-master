package Baseline.VTree.domain;

import Baseline.base.domain.Node;
import Baseline.base.domain.api.ClusterLink;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

/**
 * TODO
 * 2022/9/14 zhoutao
 */
@Getter
@Setter
@Slf4j
public class VTreeClusterLink extends ClusterLink {
    private final Set<Node> borderLink;

    private Node LNAV;

    public VTreeClusterLink() {
        super();
        borderLink = new HashSet<>();
        LNAV = new Node(-1, -1);
    }

    public void setLNAV(int name, int dis) {
        this.LNAV = new Node(name, dis);
    }

    public boolean updateLNAV(int activeName, int dis) {
        if (dis == -1) System.out.println("error!");
        if (LNAV.getName() != -1 && LNAV.getDis() <= dis) return false;
        this.setLNAV(activeName, dis);
        return true;
    }

    public boolean equalLNAV(int activeName) {
        return LNAV.getName() == activeName;
    }

    public void addBorderLink(Node node) {
        borderLink.add(node);
    }
}
