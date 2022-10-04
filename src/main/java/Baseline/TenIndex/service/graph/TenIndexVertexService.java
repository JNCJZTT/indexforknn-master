package Baseline.TenIndex.service.graph;

import Baseline.TenIndex.common.TenIndexConstants;
import Baseline.TenIndex.domain.TenIndexNode;
import Baseline.TenIndex.domain.TenIndexVariable;
import Baseline.TenIndex.domain.TenIndexVertex;
import Baseline.base.domain.Node;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * TODO
 * 2022/10/1 zhoutao
 */
@Service
public class TenIndexVertexService {
    public void buildClique(TenIndexVertex vertex) {
        Map<Integer, TenIndexNode> treeNodes = vertex.getTreeNodes();
        int size = treeNodes.size();
        int vertexName = vertex.getName();


        int[] names = new int[size];
        int[][] minDis = new int[size][size];
        int[] disArray = new int[size];

        int i = 0;
        for (TenIndexNode node : treeNodes.values()) {
            disArray[i] = node.getDis();
            names[i] = node.getName();
            node.setIndex(i++);
        }


        for (i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {

                minDis[i][j] = -1;
                minDis[j][i] = -1;
            }

            TenIndexVertex neighborVertex = TenIndexVariable.INSTANCE.getVertex(names[i]);
            neighborVertex.removeTreeNode(vertexName);


            for (Integer neighborName : neighborVertex.getTreeNodes().keySet()) {
                if (treeNodes.containsKey(neighborName)) {
                    int k = treeNodes.get(neighborName).getIndex();
                    int dis = neighborVertex.getTreeNodeDis(neighborName);
                    minDis[i][k] = dis;
                    minDis[k][i] = dis;
                }
            }

            for (int j = i + 1; j < size; j++) {
                int dis = disArray[i] + disArray[j];

                if (minDis[i][j] == -1) {

                    TenIndexVariable.INSTANCE.getVertex(names[i]).addTreeNode(new Node(names[j], dis));
                    TenIndexVariable.INSTANCE.getVertex(names[j]).addTreeNode(new Node(names[i], dis));
                } else if (minDis[i][j] > dis) {

                    TenIndexVariable.INSTANCE.getVertex(names[i]).updateTreeNode(names[j], dis);
                    TenIndexVariable.INSTANCE.getVertex(names[j]).updateTreeNode(names[i], dis);
                } else if (disArray[i] > disArray[j] + minDis[i][j]) {

                    vertex.updateTreeNode(names[i], disArray[j] + minDis[i][j]);
                } else if (disArray[j] > disArray[i] + minDis[i][j]) {
                    vertex.updateTreeNode(names[j], disArray[i] + minDis[i][j]);
                }

            }
        }
    }

    public void buildAncestor(TenIndexVertex vertex) {
        if (vertex.isBuiltAncestor()) {
            return;
        }

        vertex.setBuiltAncestor(true);
        initAncestor(vertex);


        HashSet<Integer> updateParents = new HashSet<>(vertex.getTreeNodes().keySet());
        updateParents.remove(vertex.getName());
        List<Node> ancestorInfo = vertex.getAncestorInfo();

        while (!updateParents.isEmpty()) {
            int minDis = Integer.MAX_VALUE;
            Node nearestNode = null;
            int index = -1;
            for (Integer Name : updateParents) {

                if (ancestorInfo.get(TenIndexVariable.INSTANCE.getVertex(Name).getIndex()).getDis() < minDis) {
                    index = TenIndexVariable.INSTANCE.getVertex(Name).getIndex();
                    minDis = ancestorInfo.get(index).getDis();
                }
            }

            nearestNode = ancestorInfo.get(index);
            updateAncestor(TenIndexVariable.INSTANCE.getVertex(nearestNode.getName()), new Node(vertex.getName(), nearestNode.getDis()), ancestorInfo, updateParents);
            updateParents.remove(nearestNode.getName());
        }
    }

    private void updateAncestor(TenIndexVertex vertex, Node node, List<Node> Temp, HashSet<Integer> updateParents) {
        int index = vertex.getIndex();
        int vertexName = vertex.getName();


        for (int i = 0; i < index; i++) {
            Node ancestor = Temp.get(i);
            TenIndexVertex childVertex = TenIndexVariable.INSTANCE.getVertex(ancestor.getName());


            if ((ancestor.getDis() == -1 || ancestor.getDis() > node.getDis()) && childVertex.getTreeNodes().containsKey(vertexName)) {

                int dis = node.getDis() + childVertex.getTreeNodes().get(vertexName).getDis();

                if ((dis < TenIndexConstants.MAX_DIS && ancestor.getDis() == -1) || ancestor.getDis() > dis) {
                    ancestor.setDis(dis);
                    updateParents.add(ancestor.getName());
                }
            }
        }


        for (TenIndexNode ln : vertex.getTreeNodes().values()) {
            int pindex = TenIndexVariable.INSTANCE.getVertex(ln.getName()).getIndex();
            Node Ancestor = Temp.get(pindex);
            int dis = ln.getDis() + node.getDis();
            if ((dis < TenIndexConstants.MAX_DIS && Ancestor.getDis() == -1) || Ancestor.getDis() > dis) {
                Ancestor.setDis(dis);
                updateParents.add(Ancestor.getName());
            }
        }
    }



    private void initAncestor(TenIndexVertex vertex) {
        int vertexName = vertex.getName();
        List<Node> ancestorInfo = vertex.getAncestorInfo();
        ancestorInfo.add(new Node(vertexName, 0));

        int index = 0;
        vertex.setIndex(index++);


        while (vertex.getParent() != -1) {
            vertex = TenIndexVariable.INSTANCE.getVertex(vertex.getParent());
            vertex.setIndex(index++);
            ancestorInfo.add(new Node(vertex.getName(), -1));
        }
        for (TenIndexNode treeNode : vertex.getTreeNodes().values()) {
            ancestorInfo.get(TenIndexVariable.INSTANCE.getVertex(vertex.getName()).getIndex()).setDis(treeNode.getDis());
        }
    }

}
