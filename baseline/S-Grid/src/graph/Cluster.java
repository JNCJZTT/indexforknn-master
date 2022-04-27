package graph;


import javax.print.attribute.standard.JobName;
import java.util.*;
import java.util.stream.Collectors;

import static common.GlobalVariable.allVertices;


/**
 * @author: zhoutao
 * @since: 2021/11/11 10:11 下午
 * @description: TODO
 */
public class Cluster {
    private Map<Integer, Integer> Vertex2IndexMap;           //顶点->Index

    private Set<Edge> clusterEdge;

    private int clusterSize = 0;                                      //簇中节点数量


    public Cluster() {
        Vertex2IndexMap = new HashMap<>();
        clusterEdge = new HashSet<>();
    }

    /*
     * 往簇中添加节点
     */
    public void addVertex(Integer vertexName) {
        Vertex2IndexMap.put(vertexName, clusterSize++);
    }

    public void addBorder(Integer vertexName, Integer to, int dis) {
        addVertex(vertexName);
        addClusterEdge(vertexName, to, dis);
    }

    /*
     * 连接clusterDis
     * */
    public void addClusterEdge(int from, int to, int dis) {
        clusterEdge.add(new Edge(from, to, dis));
    }


    public void computeCluster() {
        floyd();
    }

    /*
     * 运行Floyd算法，计算俩俩之间的距离
     * */
    private void floyd() {
        int[][] Dis = new int[clusterSize][clusterSize];
        int[] Index2VertexArray = new int[clusterSize];
        Vertex2IndexMap.forEach((key, value) -> Index2VertexArray[value] = key);
        for (int[] dis : Dis) {
            Arrays.fill(dis, -1);
        }
        for (Edge edge : clusterEdge) {
            int fromIndex = Vertex2IndexMap.get(edge.from),
                    toIndex = Vertex2IndexMap.get(edge.to);
            Dis[fromIndex][toIndex] = edge.dis;
            Dis[toIndex][fromIndex] = edge.dis;
        }

        for (int k = 0; k < clusterSize; k++) {
            Dis[k][k] = 0;
            for (int i = 0; i < clusterSize; i++) {
                if (k == i || Dis[i][k] == -1) continue;
                for (int j = i + 1; j < clusterSize; j++) {
                    if (Dis[k][j] == -1) continue;
                    if (Dis[i][j] == -1 || Dis[i][j] > Dis[i][k] + Dis[k][j]) {
                        Dis[i][j] = Dis[i][k] + Dis[k][j];
                    }
                }
            }
        }

        // 存储子图中所有结点到边界点之间对距离
        for (int j : Vertex2IndexMap.entrySet().stream().
                filter(entry -> allVertices.get(entry.getKey()).isBorder).
                map(Map.Entry::getValue).collect(Collectors.toList())) {
            Integer borderName = Index2VertexArray[j];
            for (int i = 0; i < clusterSize; i++) {
                if (Dis[i][j] != -1 && i != j) {
                    allVertices.get(Index2VertexArray[i]).Link.add(new Vnode(borderName, Dis[i][j]));
                }
            }
        }
    }



}
