package Baseline.ERkNN.domain;

import Baseline.base.domain.Node;
import Baseline.base.domain.api.Vertex;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
public class ERkNNVertex extends Vertex {
    private Set<Node> virtualLink;

    private List<String> clusterNames;

//    public int[] path = new int[10000];


    public double x;
    public double y;
    private int preNode;

    private boolean border;
    private HashSet<Integer> borderGNames=new HashSet<>();

    public ERkNNVertex() {
        this.virtualLink = new HashSet<>();
//        this.borderGNames = new HashSet<>();
//        Arrays.fill(path,0);
    }

    public ERkNNVertex(int name, int neighbor, int dis){
        setName(name);
        addVirtualLink(neighbor,dis);
    }
//    public void changePath(int[] path){
//        for(int i=0; i<path.length;i++){
//            if (path[i]==1){
//                this.path[i]=1;
//            }
//        }
//    }
    public void changePath(HashSet<Integer> borderGName){
        this.borderGNames.addAll(borderGName);
    }



    public void addBorderGNames(int cName){
        borderGNames.add(cName);
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
