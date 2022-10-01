package com.index.indexforknn.vtree.domain;

import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.base.domain.api.Cluster;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TODO
 * 2022/9/14 zhoutao
 */
@Getter
@Setter
@SuperBuilder
@Slf4j

public class VtreeCluster extends Cluster<VtreeClusterLink> {
    private int layer;

    private boolean leaf;

    private List<Integer> borderNames;

    private List<String> children;

    private String parentName;

    private Set<Integer> activeNames;


    /**
     * add vertex into cluster
     *
     * @param name name
     */
    public void addVertex(int name) {
        clusterLinkMap.put(name, new VtreeClusterLink());
    }

    public void addVertices(List<Integer> names) {
        names.forEach(this::addVertex);
    }

    public void addBorderLink(int from, int to, int dis) {
        clusterLinkMap.get(from).addBorderLink(new Node(to, dis));
    }

    public void addActive(int activeName) {
        VtreeClusterLink activeLink = clusterLinkMap.get(activeName);
        activeLink.setLNAV(activeName, 0);
        activeNames.add(activeName);
        // 遍历所有节点
        for (Integer link : clusterLinkMap.keySet()) {
            int dis = getClusterDis(activeName, link);
            if (dis == -1) continue;
            clusterLinkMap.get(link).updateLNAV(activeName, dis);
        }
        addActiveIterationUp(activeName);
    }

    private void addActiveIterationUp(int activeName) {
        if (layer == 0) {
            return;
        }
        boolean propagation = false;
        VtreeCluster parentCluster = VtreeVariable.INSTANCE.getCluster(parentName);

        // 更新交集
        for (int borderName : borderNames) {
            Node lnav = clusterLinkMap.get(borderName).getLNAV();
            if (lnav.getName() != activeName) continue;
            if (parentCluster.clusterLinkMap.get(borderName).updateLNAV(activeName, lnav.getDis())) {
                parentCluster.activeNames.add(activeName);
                propagation = true;
            }
        }
        // 更新集合
        for (int innerBorder : borderNames) {
            if (!clusterLinkMap.get(innerBorder).equalLNAV(activeName) ||
                    !parentCluster.clusterLinkMap.get(innerBorder).equalLNAV(activeName)) continue;
            Node lnav = clusterLinkMap.get(innerBorder).getLNAV();
            for (int outBorder : parentCluster.borderNames) {
                int dis = parentCluster.getClusterDis(innerBorder, outBorder);
                if (dis == -1) continue;

                parentCluster.clusterLinkMap.get(outBorder).updateLNAV(activeName, lnav.getDis() + dis);
            }
        }
        if (propagation) {
            parentCluster.addActiveIterationUp(activeName);
        }
    }

    public Node getGNAV(int queryName) {
        Node gnav = new Node(-1, -1);
        gnav.setNode(clusterLinkMap.get(queryName).getLNAV());
        if (gnav.getDis() == 0) {
            return gnav;
        }

        for (Map.Entry<Integer, VtreeClusterLink> entry : clusterLinkMap.entrySet()) {
            int name = entry.getKey();
            int dis = getClusterDis(name, queryName);
            if (dis == -1) continue;
            gnav.updateNode(entry.getValue().getLNAV(), dis);
        }
        int gridDis = -1;

        int borderSize = borderNames.size();
        int[] interDisArray = new int[borderSize];
        for (int i = 0; i < borderSize; i++) {
            interDisArray[i] = getClusterDis(queryName, borderNames.get(i));
            if (interDisArray[i] != -1 && (gridDis == -1 || interDisArray[i] < gridDis)) {
                gridDis = interDisArray[i];
            }
        }
        return getGNAVIterationUp(gnav, gridDis, interDisArray);
    }

    private Node getGNAVIterationUp(Node gnav, int gridDis, int[] interDis) {
        int limit = gnav.getDis() == -1 ? Integer.MAX_VALUE : gnav.getDis();

        if (layer == 0 || limit < gridDis) {
            return gnav;
        }
        gridDis = -1;

        VtreeCluster parentCluster = VtreeVariable.INSTANCE.getCluster(parentName);
        int[] outerDis = new int[parentCluster.getClusterLinkMap().size()];

        int j = 0;
        int[] borderDis = new int[parentCluster.borderNames.size()];
        int borderIndex = 0;
        for (Map.Entry<Integer, VtreeClusterLink> entry : parentCluster.getClusterLinkMap().entrySet()) {
            int outerName = entry.getKey();
            outerDis[j] = -1;
            for (int i = 0; i < borderNames.size(); i++) {
                if (interDis[i] == -1) continue;
                int interName = borderNames.get(i);
                int dis = parentCluster.getClusterDis(outerName, interName);
                if (dis == -1) continue;
                dis += interDis[i];
                if (outerDis[j] == -1 || outerDis[j] > dis) {
                    outerDis[j] = dis;
                }
            }
            if (VtreeVariable.INSTANCE.getVertex(outerName).isBorder(parentCluster.layer)) {
                borderDis[borderIndex] = outerDis[j];
                if (outerDis[j] != -1 && (gridDis == -1 || gridDis > outerDis[j])) {
                    gridDis = outerDis[j];
                }
                borderIndex++;
            }
            if (outerDis[j] != -1) {
                gnav.updateNode(entry.getValue().getLNAV(), outerDis[j]);
            }
            j++;
        }
        return VtreeVariable.INSTANCE.getCluster(parentName).getGNAVIterationUp(gnav, gridDis, borderDis);
    }

    public void deleteActive(Integer activeName) {
        this.activeNames.remove(activeName);
        // 重置
        clusterLinkMap.entrySet().stream()
                .filter(entry -> entry.getValue().equalLNAV(activeName))
                .forEach(this::resetActive);
        deleteActiveIterationUp(activeName);

    }


    private void resetActive(Map.Entry<Integer, VtreeClusterLink> link) {
        link.getValue().setLNAV(-1, -1);
        int name = link.getKey();
        for (int active : activeNames) {
            int dis = getClusterDis(name, active);
            if (dis != -1) {
                link.getValue().updateLNAV(active, dis);
            }
        }
    }

    private void deleteActiveIterationUp(int activeName) {
        if (layer == 0) return;

        VtreeCluster parentCluster = VtreeVariable.INSTANCE.getCluster(parentName);
        boolean propagation = false;

        parentCluster.activeNames.remove(activeName);

        //遍历所有节点
        for (Integer name : parentCluster.getClusterLinkMap().keySet()) {
            if (parentCluster.getClusterLinkMap().get(name).equalLNAV(activeName)) {
                propagation = true;
                VtreeVertex vertex = VtreeVariable.INSTANCE.getVertex(name);
                String leafClusterName = vertex.getClusterName();
                Node newLNAV = VtreeVariable.INSTANCE.getCluster(VtreeVariable.INSTANCE.getLayerClusterName(leafClusterName, layer)).clusterLinkMap.get(name).getLNAV();
                parentCluster.getClusterLinkMap().get(name).setLNAV(
                        newLNAV
                );

                for (Integer link : parentCluster.getClusterLinkMap().keySet()) {
                    int dis = parentCluster.getClusterDis(name, link);
                    Node lnav = parentCluster.getClusterLinkMap().get(link).getLNAV();
                    if (dis == -1 || lnav.getName() == activeName || lnav.getName() == -1) continue;
                    parentCluster.getClusterLinkMap().get(name).updateLNAV(lnav.getName(), dis + lnav.getDis());
                }
            }

        }
        if (propagation) {
            parentCluster.deleteActiveIterationUp(activeName);
        }

    }

}
