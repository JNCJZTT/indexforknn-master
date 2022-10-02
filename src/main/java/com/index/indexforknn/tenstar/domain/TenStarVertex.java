package com.index.indexforknn.tenstar.domain;

import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.base.domain.api.Vertex;
import lombok.Getter;
import lombok.Setter;
import org.dom4j.util.NodeComparator;

import java.util.*;

/**
 * TODO
 * 2022/9/30 zhoutao
 */
@Getter
@Setter
public class TenStarVertex extends Vertex {
    private int degree;

    private int treeLevel;

    private Map<Integer, TenStarNode> treeNodes;

    private boolean leaf;

    private boolean isTreeNode;

    private int parent;

    private List<Integer> sons;                    //孩子节点

    private boolean builtAncestor;

    private List<Node> ancestorInfo;

    private int index;                                           //暂时的下标 （ 仅为计算 KTNN）

    private List<Node> ktnn;

    private Set<Integer> ktnnName;

    public TenStarVertex() {
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
        this.treeNodes.put(node.getName(), new TenStarNode(node));
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
        treeNodes.put(this.getName(), new TenStarNode(this.getName()));         //新建自身（因为root没有setparent)
        this.isTreeNode = true;
    }

    //如果一个节点包含自己所有的邻节点，就是自己的父亲节点 (首先自己已经出栈）
    private boolean isParent(Set<Integer> bag) {
        return this.isTreeNode && this.treeNodes.keySet().containsAll(bag);
    }

    public void setParent() {
        TenStarVertex vertexParent = null;

        for (Integer neighbor : this.treeNodes.keySet()) {
            //遍历邻节点，从邻节点中寻找自己的父亲节点 (存在自己的所有keyset）  已出栈&&度数最小
            if ((vertexParent == null || vertexParent.degree > TenStarVariable.INSTANCE.getVertex(neighbor).degree)
                    && TenStarVariable.INSTANCE.getVertex(neighbor).isParent(this.treeNodes.keySet())) {
                vertexParent = TenStarVariable.INSTANCE.getVertex(neighbor);
            }
        }

        this.isTreeNode = true;
        this.treeNodes.put(this.getName(), new TenStarNode((this.getName())));   //新建自身
        if (vertexParent != null) {
            this.parent = vertexParent.getName();                                      //设置父亲节点
            this.treeLevel = vertexParent.treeLevel + 1;                                //设置层数 为父亲层+1
            vertexParent.setSon(this.getName());                                       //父亲节点修改为非叶子节点 且添加孩子节点
        }
    }

    //添加孩子
    public void setSon(Integer Son) {
        if (leaf) {
            leaf = false;
        }
        this.sons.add(Son);
    }


    public void buildCKNN() {
        if (this.isActive()) {
            //如果自身是活跃的，添加自身
            UpdateKTNN(this.getName(), 0);
        }
        for (Integer i : this.treeNodes.keySet()) {
            if (i != this.getName()) {
                //更新不是自己的邻居节点
                TenStarVertex v = TenStarVariable.INSTANCE.getVertex(i);
                for (int j = 0; j < this.ktnn.size(); j++) {
                    //更新KTNN

                    int Name = this.ktnn.get(j).getName();
                    int Dis = TenStarVariable.INSTANCE.getVertex(Name).getAncestorInfo()
                            .get(TenStarVariable.INSTANCE.getVertex(Name).getTreeLevel() - v.getTreeLevel()).getDis();       //v是树包节点，Name是活跃点
                    v.UpdateKTNN(Name, Dis);
                }
            }
        }
    }

    //更新KTNN
    private void UpdateKTNN(Integer Name, int dis) {
        if (this.ktnnName.contains(Name)) {
            //如果已经存在了，直接return
            return;
        }
        if (this.ktnn.size() < GlobalVariable.K) {
            //如果未满，则直接添加
            this.ktnn.add(new Node(Name, dis));
            this.ktnnName.add(Name);
            if (this.ktnn.size() == GlobalVariable.K) {
                //如果算上这个为K的话，则排序
                this.ktnn.sort(Comparator.comparingInt(Node::getDis));
            }
        } else if (this.ktnn.get(GlobalVariable.K - 1).getDis() > dis) {
            //如果满K个了，则如果最后一个的距离大于它，则删除最后一个
            this.ktnn = this.ktnn.subList(0, GlobalVariable.K - 1);
            this.ktnn.add(new Node(Name, dis));
            this.ktnn.sort(Comparator.comparingInt(Node::getDis));
            //重置索引匹配
            this.ktnnName.clear();
            for (int i = 0; i < GlobalVariable.K; i++) {
                this.ktnnName.add(this.ktnn.get(i).getName());
            }
        }
    }
}
