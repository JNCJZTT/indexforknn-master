package Baseline.SGrid.domain;

import Baseline.base.domain.Node;
import Baseline.base.domain.api.Vertex;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SGridVertex
 * 2022/4/27 zhoutao
 */
@Getter
@Setter
public class SGridVertex extends Vertex {
    private Set<Node> virtualLink;

    private List<String> clusterNames;

    private boolean border;

    public SGridVertex() {
        this.virtualLink = new HashSet<>();
    }

    public String getClusterName() {
        return clusterNames.get(0);
    }

    public void addVirtualLink(int neighbor, int dis) {
        virtualLink.add(new Node(neighbor, dis));
    }

}
