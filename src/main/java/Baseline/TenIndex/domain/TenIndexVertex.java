package Baseline.TenIndex.domain;

import Baseline.base.domain.GlobalVariable;
import Baseline.base.domain.Node;
import Baseline.base.domain.api.Vertex;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * TODO
 * 2022/9/30 zhoutao
 */
@Getter
@Setter
public class TenIndexVertex extends Vertex {
    private int degree;

    private int treeLevel;

    private Map<Integer, TenIndexNode> treeNodes;

    private boolean leaf;

    private boolean isTreeNode;

    private int parent;

    private List<Integer> sons;

    private boolean builtAncestor;

    private List<Node> ancestorInfo;

    private int index;

    private List<Node> ktnn;

    private Set<Integer> ktnnName;

    public TenIndexVertex() {
        this.degree = 0;
        this.treeLevel = -1;
        this.parent = -1;
        this.treeNodes = new HashMap<>();
        this.isTreeNode = false;
        this.sons = new ArrayList<>();
        this.leaf = true;
        builtAncestor = false;
        ancestorInfo = new ArrayList<>();
        index = -1;
        ktnn = new ArrayList<>();
        ktnnName = new HashSet<>();
    }

    @Override
    public void addOrigionEdge(Node node) {
        super.addOrigionEdge(node);
        addTreeNode(node);
    }

    public void addTreeNode(Node node) {
        this.degree++;
        this.treeNodes.put(node.getName(), new TenIndexNode(node));
    }

    public void removeTreeNode(Integer name) {
        this.treeNodes.remove(name);
        this.degree--;
    }

    public int getTreeNodeDis(Integer name) {
        return this.treeNodes.get(name).getDis();
    }

    public void updateTreeNode(Integer name, int dis) {
        this.treeNodes.get(name).setDis(dis);
    }

    public void setRoot() {
        leaf = false;
        treeLevel = 0;
        treeNodes.put(this.getName(), new TenIndexNode(this.getName()));
        this.isTreeNode = true;
    }


    private boolean isParent(Set<Integer> bag) {
        return this.isTreeNode && this.treeNodes.keySet().containsAll(bag);
    }

    public void setParent() {
        TenIndexVertex vertexParent = null;

        for (Integer neighbor : this.treeNodes.keySet()) {

            if ((vertexParent == null || vertexParent.degree > TenIndexVariable.INSTANCE.getVertex(neighbor).degree)
                    && TenIndexVariable.INSTANCE.getVertex(neighbor).isParent(this.treeNodes.keySet())) {
                vertexParent = TenIndexVariable.INSTANCE.getVertex(neighbor);
            }
        }

        this.isTreeNode = true;
        this.treeNodes.put(this.getName(), new TenIndexNode((this.getName())));
        if (vertexParent != null) {
            this.parent = vertexParent.getName();
            this.treeLevel = vertexParent.treeLevel + 1;
            vertexParent.setSon(this.getName());
        }
    }


    public void setSon(Integer Son) {
        if (leaf) {
            leaf = false;
        }
        this.sons.add(Son);
    }


    public void buildCKNN() {
        if (this.isActive()) {

            UpdateKTNN(this.getName(), 0);
        }
        for (Integer i : this.treeNodes.keySet()) {
            if (i != this.getName()) {

                TenIndexVertex v = TenIndexVariable.INSTANCE.getVertex(i);
                for (int j = 0; j < this.ktnn.size(); j++) {


                    int Name = this.ktnn.get(j).getName();
                    int Dis = TenIndexVariable.INSTANCE.getVertex(Name).getAncestorInfo()
                            .get(TenIndexVariable.INSTANCE.getVertex(Name).getTreeLevel() - v.getTreeLevel()).getDis();       //v是树包节点，Name是活跃点
                    v.UpdateKTNN(Name, Dis);
                }
            }
        }
    }


    private void UpdateKTNN(Integer Name, int dis) {
        if (this.ktnnName.contains(Name)) {
            return;
        }
        if (this.ktnn.size() < GlobalVariable.K) {

            this.ktnn.add(new Node(Name, dis));
            this.ktnnName.add(Name);
            if (this.ktnn.size() == GlobalVariable.K) {

                this.ktnn.sort(Comparator.comparingInt(Node::getDis));
            }
        } else if (this.ktnn.get(GlobalVariable.K - 1).getDis() > dis) {

            this.ktnn = this.ktnn.subList(0, GlobalVariable.K - 1);
            this.ktnn.add(new Node(Name, dis));
            this.ktnn.sort(Comparator.comparingInt(Node::getDis));

            this.ktnnName.clear();
            for (int i = 0; i < GlobalVariable.K; i++) {
                this.ktnnName.add(this.ktnn.get(i).getName());
            }
        }
    }
}
