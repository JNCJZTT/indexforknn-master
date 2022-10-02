package com.index.indexforknn.tenstar.service.graph;

import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.tenstar.common.TenStarConstants;
import com.index.indexforknn.tenstar.domain.TenStarNode;
import com.index.indexforknn.tenstar.domain.TenStarVariable;
import com.index.indexforknn.tenstar.domain.TenStarVertex;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * TODO
 * 2022/10/1 zhoutao
 */
@Service
public class TenStarVertexService {
    public void buildClique(TenStarVertex vertex) {
        Map<Integer, TenStarNode> treeNodes = vertex.getTreeNodes();
        int size = treeNodes.size();                           //邻居节点的数量
        int vertexName = vertex.getName();

        //初始化名字索引和距离数组
        int[] names = new int[size];
        int[][] minDis = new int[size][size];                       //Minidis记录 邻节点和邻节点之间的最短距离（局部最短）
        int[] disArray = new int[size];

        int i = 0;
        for (TenStarNode node : treeNodes.values()) {
            disArray[i] = node.getDis();
            names[i] = node.getName();
            node.setIndex(i++);
        }


        for (i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                //初始化Minidis
                minDis[i][j] = -1;
                minDis[j][i] = -1;
            }

            TenStarVertex neighborVertex = TenStarVariable.INSTANCE.getVertex(names[i]);                    //邻居节点（此时还不包括自身）
            neighborVertex.removeTreeNode(vertexName);                          //从每个节点中删除目标节点

            //遍历邻居节点的邻居节点，如果邻居节点的邻居节点也是我的邻居节点
            for (Integer neighborName : neighborVertex.getTreeNodes().keySet()) {
                if (treeNodes.containsKey(neighborName)) {
                    int k = treeNodes.get(neighborName).getIndex();
                    int dis = neighborVertex.getTreeNodeDis(neighborName);                //如果v中存在目标节点的邻居节点，则返回距离
                    minDis[i][k] = dis;
                    minDis[k][i] = dis;
                }
            }

            for (int j = i + 1; j < size; j++) {
                int dis = disArray[i] + disArray[j];

                if (minDis[i][j] == -1) {
                    //如果-1，说明两个节点不连通
                    TenStarVariable.INSTANCE.getVertex(names[i]).addTreeNode(new Node(names[j], dis));
                    TenStarVariable.INSTANCE.getVertex(names[j]).addTreeNode(new Node(names[i], dis));
                } else if (minDis[i][j] > dis) {
                    //更新邻节点之间的距离
                    TenStarVariable.INSTANCE.getVertex(names[i]).updateTreeNode(names[j], dis);
                    TenStarVariable.INSTANCE.getVertex(names[j]).updateTreeNode(names[i], dis);
                } else if (disArray[i] > disArray[j] + minDis[i][j]) {
                    //更新目标节点到邻居节点之间的距离
                    vertex.updateTreeNode(names[i], disArray[j] + minDis[i][j]);
                } else if (disArray[j] > disArray[i] + minDis[i][j]) {
                    vertex.updateTreeNode(names[j], disArray[i] + minDis[i][j]);
                }

            }
        }
    }

    public void buildAncestor(TenStarVertex vertex) {
        if (vertex.isBuiltAncestor()) {
            return;
        }

        vertex.setBuiltAncestor(true);
        initAncestor(vertex);

        //待处理的祖先节点
        HashSet<Integer> updateParents = new HashSet<>(vertex.getTreeNodes().keySet());
        updateParents.remove(vertex.getName());
        List<Node> ancestorInfo = vertex.getAncestorInfo();

        while (!updateParents.isEmpty()) {
            int minDis = Integer.MAX_VALUE;
            Node nearestNode = null;
            int index = -1;
            for (Integer Name : updateParents) {
                //遍历待处理的祖先节点，找到距离目标节点最小的
                if (ancestorInfo.get(TenStarVariable.INSTANCE.getVertex(Name).getIndex()).getDis() < minDis) {
                    index = TenStarVariable.INSTANCE.getVertex(Name).getIndex();
                    minDis = ancestorInfo.get(index).getDis();                                //更新MinDis
                }
            }

            nearestNode = ancestorInfo.get(index);
            updateAncestor(TenStarVariable.INSTANCE.getVertex(nearestNode.getName()), new Node(vertex.getName(), nearestNode.getDis()), ancestorInfo, updateParents);
            updateParents.remove(nearestNode.getName());
        }
    }

    private void updateAncestor(TenStarVertex vertex, Node node, List<Node> Temp, HashSet<Integer> updateParents) {
        int index = vertex.getIndex();           //自己在祖先中到序号
        int vertexName = vertex.getName();

        //先遍历子孙节点
        for (int i = 0; i < index; i++) {
            Node ancestor = Temp.get(i);
            TenStarVertex childVertex = TenStarVariable.INSTANCE.getVertex(ancestor.getName());

            //如果 目标节点到子孙节点到距离
            if ((ancestor.getDis() == -1 || ancestor.getDis() > node.getDis()) && childVertex.getTreeNodes().containsKey(vertexName)) {
                //如果到该祖先节点到距离未赋值 或者
                int dis = node.getDis() + childVertex.getTreeNodes().get(vertexName).getDis();               //目标节点经过自身再到祖先节点的距离

                if ((dis < TenStarConstants.MAX_DIS && ancestor.getDis() == -1) || ancestor.getDis() > dis) {
                    ancestor.setDis(dis);                                                       //更新距离
                    updateParents.add(ancestor.getName());                                       //添加待处理的祖先节点
                }
            }
        }

        //遍历自己以上到节点
        for (TenStarNode ln : vertex.getTreeNodes().values()) {
            int pindex = TenStarVariable.INSTANCE.getVertex(ln.getName()).getIndex();
            Node Ancestor = Temp.get(pindex);
            int dis = ln.getDis() + node.getDis();
            if ((dis < TenStarConstants.MAX_DIS && Ancestor.getDis() == -1) || Ancestor.getDis() > dis) {
                Ancestor.setDis(dis);
                updateParents.add(Ancestor.getName());
            }
        }
    }


    //初始化到所有祖先节点的距离
    private void initAncestor(TenStarVertex vertex) {
        int vertexName = vertex.getName();
        List<Node> ancestorInfo = vertex.getAncestorInfo();
        ancestorInfo.add(new Node(vertexName, 0));

        int index = 0;
        vertex.setIndex(index++);


        while (vertex.getParent() != -1) {
            vertex = TenStarVariable.INSTANCE.getVertex(vertex.getParent());
            vertex.setIndex(index++);
            ancestorInfo.add(new Node(vertex.getName(), -1));
        }
        for (TenStarNode treeNode : vertex.getTreeNodes().values()) {
            ancestorInfo.get(TenStarVariable.INSTANCE.getVertex(vertex.getName()).getIndex()).setDis(treeNode.getDis());
        }
    }

}
