package graph;


import java.util.*;

import static common.GlobalVariable.*;

public class Cluster {

    private HashMap<Integer, ClusterLink> vertex2IndexTable;          //顶点->Index

    private int clusterName;

    private int[] borderInfo;                                         //边界点名字

    private int borderSize = 0;                                       //簇中边界点数量

    private ClusterRelation clusterRelation;                          //簇中的关系

    public List<Edge> sonEdges;

    private Set<Integer> activeSet;


    public Cluster(int level, int clusterName) {
        vertex2IndexTable = new HashMap<>(SUB_GRAPH_SIZE + 1);
        clusterRelation = new ClusterRelation(level);
        this.clusterName = clusterName;
        sonEdges = new ArrayList<>();
        activeSet = new HashSet<>();
    }

    /*
     * 往簇中添加节点
     */
    public void addVertex(int vertexName) {
        vertex2IndexTable.put(vertexName, new ClusterLink(vertexName));
        allVertices[vertexName].setClusterName(clusterRelation.level, clusterName);
    }

    /*
     * 往簇中添加子簇
     */
    public void addSonCluster(Integer sonName) {
        clusterRelation.addSonCluster(sonName);
    }


    /*
     * 连接clusterDis,因为是无向图，所以只有名字更小的节点存放边即可
     * */
    public void setClusterDis(Integer from, Integer to, int dis) {
        if (from <= to) {
            vertex2IndexTable.get(from).setClusterDis(to, dis);
        } else {
            vertex2IndexTable.get(to).setClusterDis(from, dis);
        }
    }

    /*
     * 构造邻接网格的权重，连接距离
     * */
    public void computeCluster() {
        // 保存边节点信息
        if (clusterName == ROOT) {
            borderInfo = new int[0];
            return;
        }
        borderInfo = new int[vertex2IndexTable.size()];

        for (Integer vertexName : vertex2IndexTable.keySet()) {
            Vertex vertex = allVertices[vertexName];
            for (Vnode vn : vertex.originalEdges) {
                // 同一个子图中的节点，跳过
                if (isBorder(vertexName, vn)) {
                    continue;
                }
                vertex.isBorder[clusterRelation.level] = true;
                allVertices[vn.name].isBorder[clusterRelation.level] = true;
                clusterRelation.addNeighborCluster(getVertexClusterName(vn.name));
            }
            if (vertex.isBorder[clusterRelation.level]) {
                borderInfo[borderSize++] = vertexName;
            }
        }
        borderInfo = Arrays.copyOf(borderInfo, borderSize);

        connectEdges();
        floyd();
        addEdges();


    }

    private void connectEdges() {
        if (sonEdges.isEmpty()) {
            return;
        }
        for (Edge e : sonEdges) {
            setClusterDis(e.from, e.to, e.dis);
        }
    }

    private Integer getVertexClusterName(int vertexName) {
        return allVertices[vertexName].getClusterName(clusterRelation.level);
    }

    /*
     * 是否是边界点
     * */
    private boolean isBorder(Integer from, Vnode vn) {
        if (vertex2IndexTable.containsKey(vn.name)) {
            setClusterDis(from, vn.name, vn.dis);
            return true;
        }
        if (clusterRelation.level != 0 && !allVertices[vn.name].isBorder[clusterRelation.level - 1]) {
            return true;
        }
        return false;

    }


    /*
     * 返回有序邻接子图
     * */
    public List<Integer> getSortedNeighbor() {
        return clusterRelation.getSortedNeighbor();
    }

    public void addEdges() {
        List<Edge> edges = new ArrayList<>();
        for (int borderName : borderInfo) {
            vertex2IndexTable.get(borderName).getClusterDisMap().entrySet().stream()
                    .filter(entry -> allVertices[entry.getKey()].isBorder[clusterRelation.level])
                    .forEach(entry -> edges.add(new Edge(borderName, entry.getKey(), entry.getValue()[0])));
        }
    }


    /*
     * 运行Floyd算法，计算俩俩之间的距离
     * */
    private void floyd() {
        for (Integer middle : vertex2IndexTable.keySet()) {
            for (Integer from : vertex2IndexTable.keySet()) {

                if (from.equals(middle)) continue;
                int disFromMiddle = getClusterDis(middle, from);
                if (disFromMiddle == -1) continue;

                for (Integer to : vertex2IndexTable.keySet()) {
                    if (to <= from) continue;

                    int disMiddleTo = getClusterDis(middle, to),
                            dis = getClusterDis(from, to);
                    if (disMiddleTo != -1 && (dis == -1 || dis > (disFromMiddle + disMiddleTo))) {
                        setClusterDis(from, to, disFromMiddle + disMiddleTo);
                    }
                }
            }
        }
    }

    //获得簇中的距离
    private int getClusterDis(int from, int to) {
        if (from == to) {
            return 0;
        }
        if (from > to) {
            return vertex2IndexTable.get(to).getClusterDis(from);
        }
        return vertex2IndexTable.get(from).getClusterDis(to);
    }

    // 设置父亲簇
    public void setParent(Integer parent) {
        clusterRelation.setParent(parent);


        // 向父亲簇添加节点
        for (int border : borderInfo) {
//            System.out.println(parent);
            allClusters.get(parent).addVertex(border);
        }
    }

    public int getBorderSize() {
        return borderSize;
    }

    public int getClusterSize() {
        return vertex2IndexTable.size();
    }

    public void addActive(int activeName) {
        ClusterLink activeLink = vertex2IndexTable.get(activeName);
        activeLink.setSelf();
        activeSet.add(activeName);
        // 遍历所有节点
        for (Integer link : activeLink.getClusterDisMap().keySet()) {
            int dis = getClusterDis(activeName, link);
            if (dis == -1) continue;
            vertex2IndexTable.get(link).updateLNAV(activeName, dis);
        }
        addActiveIterationUp(activeName);
    }

    private void addActiveIterationUp(int activeName) {
        if (clusterRelation.getParent() == -1) {
            return;
        }
        boolean propagation = false;
        Cluster parentCluster = allClusters.get(clusterRelation.getParent());

        // 更新交集
        for (int borderName : borderInfo) {
            Vnode lnav = vertex2IndexTable.get(borderName).getLNAV();
            if (lnav.name != activeName) continue;
            if (parentCluster.vertex2IndexTable.get(borderName).updateLNAV(activeName, lnav.dis)) {
                parentCluster.activeSet.add(activeName);
                propagation = true;
            }
        }
        // 更新集合
        for (int innerBorder : borderInfo) {
            if (!vertex2IndexTable.get(innerBorder).equalLNAV(activeName) ||
                    !parentCluster.vertex2IndexTable.get(innerBorder).equalLNAV(activeName)) continue;
            Vnode lnav = vertex2IndexTable.get(innerBorder).getLNAV();
            for (int outBorder : parentCluster.borderInfo) {
                int dis = parentCluster.getClusterDis(innerBorder, outBorder);
                if (dis == -1) continue;

                parentCluster.vertex2IndexTable.get(outBorder).updateLNAV(activeName, lnav.dis + dis);
            }
        }
        if (propagation) {
            parentCluster.addActiveIterationUp(activeName);
        }
    }

    public void deleteActive(Integer activeName) {
        this.activeSet.remove(activeName);
        // 重置
        vertex2IndexTable.values().stream()
                .filter(clusterLink -> clusterLink.equalLNAV(activeName))
                .forEach(this::resetActive);
        deleteActiveIterationUp(activeName);

    }

    private void resetActive(ClusterLink link) {
        link.resetLNAV();
        int name = link.vertexName;
        for (int active : activeSet) {
            int dis = getClusterDis(name, active);
            if (dis != -1) {
                link.updateLNAV(active, dis);
            }
        }
    }

    private void deleteActiveIterationUp(int activeName) {
        if (clusterRelation.getParent() == -1) return;

        Cluster parentCluster = allClusters.get(clusterRelation.getParent());
        boolean propagation = false;

        parentCluster.activeSet.remove(activeName);

        //遍历所有节点

        for (Integer name : parentCluster.vertex2IndexTable.keySet()) {
            if (parentCluster.vertex2IndexTable.get(name).equalLNAV(activeName)) {
                propagation = true;
                parentCluster.vertex2IndexTable.get(name).setLNAV(
                        allClusters.get(allVertices[name].getClusterName(clusterRelation.level))
                                .vertex2IndexTable.get(name).getLNAV());

                for (Integer link : parentCluster.vertex2IndexTable.keySet()) {
                    int dis = parentCluster.getClusterDis(name, link);
                    Vnode lnav = parentCluster.vertex2IndexTable.get(link).getLNAV();
                    if (dis == -1 || lnav.name == activeName || lnav.name == -1) continue;
                    parentCluster.vertex2IndexTable.get(name).updateLNAV(lnav.name, dis + lnav.dis);
                }
            }

        }
        if (propagation) {
            parentCluster.deleteActiveIterationUp(activeName);
        }

    }


    public Vnode getGNAV(int queryName) {
        Vnode gnav = new Vnode(-1, -1);
        gnav.setVnode(vertex2IndexTable.get(queryName).getLNAV());
        if (gnav.dis == 0) {
            return gnav;
        }

        for (Map.Entry<Integer, ClusterLink> entry : vertex2IndexTable.entrySet()) {
            int name = entry.getKey();
            int dis = getClusterDis(name, queryName);
            if (dis == -1) continue;
            gnav.updateVnode(entry.getValue().getLNAV(), dis);
        }
        int gridDis = -1;

        int interDisArray[] = new int[borderSize];
        for (int i = 0; i < borderSize; i++) {
            interDisArray[i] = getClusterDis(queryName, borderInfo[i]);
            if (interDisArray[i] != -1 && (gridDis == -1 || interDisArray[i] < gridDis)) {
                gridDis = interDisArray[i];
            }
        }
        return getGNAVIterationUp(gnav, gridDis, interDisArray);
    }

    private Vnode getGNAVIterationUp(Vnode gnav, int gridDis, int[] interDis) {
        int limit = gnav.dis == -1 ? Integer.MAX_VALUE : gnav.dis;

        if (clusterRelation.getParent() == -1 || limit < gridDis) {
            return gnav;
        }
        gridDis = -1;

        Cluster parentCluster = allClusters.get(clusterRelation.getParent());
        int outerDis[] = new int[parentCluster.vertex2IndexTable.size()];

        int j = 0;
        int borderDis[] = new int[parentCluster.borderSize];
        int borderIndex = 0;
        for (Map.Entry<Integer, ClusterLink> entry : parentCluster.vertex2IndexTable.entrySet()) {
            int outerName = entry.getKey();
            outerDis[j] = -1;
            for (int i = 0; i < borderSize; i++) {
                if (interDis[i] == -1) continue;
                int interName = borderInfo[i];
                int dis = parentCluster.getClusterDis(outerName, interName);
                if (dis == -1) continue;
                dis += interDis[i];
                if (outerDis[j] == -1 || outerDis[j] > dis) {
                    outerDis[j] = dis;
                }
            }
            if (allVertices[outerName].isBorder[parentCluster.clusterRelation.level]) {
                borderDis[borderIndex] = outerDis[j];
                if (outerDis[j] != -1 && (gridDis == -1 || gridDis > outerDis[j])) {
                    gridDis = outerDis[j];
                }
                borderIndex++;
            }
            if (outerDis[j] != -1) {
                gnav.updateVnode(entry.getValue().getLNAV(), outerDis[j]);
            }
            j++;
        }
        return allClusters.get(clusterRelation.getParent()).getGNAVIterationUp(gnav, gridDis, borderDis);
    }


}
