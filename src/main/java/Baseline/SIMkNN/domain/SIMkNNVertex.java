package Baseline.SIMkNN.domain;

import Baseline.base.domain.Node;
import Baseline.base.domain.api.Vertex;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
public class SIMkNNVertex extends Vertex {
    private Set<Node> virtualLink;

    private List<String> clusterNames;

    public double x;
    public double y;

    private boolean border;
    public SIMkNNVertex() {
        this.virtualLink = new HashSet<>();
    }

    public SIMkNNVertex(int name, int neighbor, int dis){
        setName(name);
        addVirtualLink(neighbor,dis);
    }

    public String getClusterName() {
        return clusterNames.get(0);
    }

    public Set<Node> getVirtualLink(){return virtualLink;}


    public void setClusterNames(List<String> clusterName){clusterNames.addAll(clusterName);}


    public void addVirtualLink(int neighbor, int dis) {
        virtualLink.add(new Node(neighbor, dis));
    }

    public void setBorder(boolean b) {
        this.border = b;
    }
    public boolean getBorder(){return border;}
}
