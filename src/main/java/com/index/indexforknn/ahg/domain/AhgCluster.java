package com.index.indexforknn.ahg.domain;

import com.index.indexforknn.ahg.common.AhgConstants;
import com.index.indexforknn.ahg.service.utils.AhgUtil;
import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.domain.Node;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TODO
 * 2022/2/12 zhoutao
 */
@Getter
@Setter
@Accessors(chain = true)
@Slf4j
public class AhgCluster {

    private String name;

    private boolean leaf;

    private int level;

    // 激活状态
    private int status;

    private String parentName;

    private List<String> sonNames;

    private Map<Integer, AhgClusterLink> clusterLinkMap;

    private List<Integer> borderNames;

    private Set<Integer> activeNames;

    public AhgCluster(String clusterName, boolean leaf) {
        name = clusterName;
        clusterLinkMap = new HashMap<>();
        level = AhgUtil.getClusterLevel(name);
        parentName = AhgUtil.getParentClusterName(name);

        activeNames = new HashSet<>();
        this.leaf = leaf;
        if (!leaf) {
            status = AhgConstants.SON_ACTIVE;
            buildCluster();
        } else {
            // 如果是叶子簇，初始化自身为激活子图
            status = AhgConstants.CLUSTER_ACTIVE;
        }
    }

    /**
     * 非叶子簇，构建簇
     */
    private void buildCluster() {
        // 初始化孩子簇
        sonNames = new ArrayList<>();
        for (int i = 0; i < GlobalVariable.BRANCH; i++) {
            String sonName = name + AhgConstants.CLUSTER_NAME_SUFFIX + i;
            // 孩子簇存在于全量树中
            if (!AhgVariable.INSTANCE.containsClusterKey(sonName)) {
                continue;
            }
            AhgCluster sonCluster = AhgVariable.INSTANCE.getCluster(sonName);

            if (sonCluster == null) {
                sonCluster = new AhgCluster(sonName, false);
                AhgVariable.INSTANCE.addCluster(sonName, sonCluster);
            }
            sonNames.add(sonName);
            // 添加孩子簇的边界点和兴趣点
            sonCluster.borderNames.forEach(this::addVertex);
            this.activeNames.addAll(sonCluster.getActiveNames());
        }
        // 添加边
        sonNames.forEach(sonName ->
                AhgVariable.INSTANCE.getCluster(sonName).addClusterLinks2Parent(this));
        // 计算簇
        computeCluster();
    }

    public void addVertex(int name) {
        clusterLinkMap.put(name, new AhgClusterLink());
    }

    /**
     * 添加子图内部边
     * 剪枝:无向图，只存储一条边即可
     */
    public void addClusterLink(Integer from, Integer to, Integer dis) {
        if (from.equals(to)) {
            return;
        }
        if (from < to) {
            clusterLinkMap.get(from).addClusterLink(to, dis);
        } else {
            clusterLinkMap.get(to).addClusterLink(from, dis);
        }
    }

    /**
     * 添加边到父亲簇中去
     */
    private void addClusterLinks2Parent(AhgCluster parentCluster) {
        for (Integer borderName : borderNames) {
            for (Node node : clusterLinkMap.get(borderName).getBorderLink()) {
                int name = node.getName(), dis = node.getDis();
                if (parentCluster.clusterLinkMap.containsKey(name)) {
                    parentCluster.addClusterLink(borderName, name, dis);
                } else {
                    parentCluster.addBorderLink(borderName, name, dis);
                }
            }
        }
    }

    /**
     * 获得内部边距离
     */
    public int getClusterDis(Integer from, Integer to) {
        if (from.equals(to)) {
            return 0;
        }
        if (from > to) {
            Integer temp = from;
            from = to;
            to = temp;
        }
        return clusterLinkMap.get(from).getClusterDis(to);
    }

    /**
     * 添加子图中外部边
     */
    public void addBorderLink(int from, int to, int dis) {
        clusterLinkMap.get(from).addBorderLink(new Node(to, dis));
    }

    public void computeCluster() {
        floyd();
        saveBorderNames();
    }

    /**
     * Floyd 算法
     */
    private void floyd() {
        Set<Integer> clusterVertices = clusterLinkMap.keySet();

        for (Integer middle : clusterVertices) {
            for (Integer from : clusterVertices) {

                if (from.equals(middle)) continue;
                int disFromMiddle = getClusterDis(middle, from);
                if (disFromMiddle == -1) continue;

                for (Integer to : clusterVertices) {
                    if (to <= from) continue;

                    int disMiddleTo = getClusterDis(middle, to);
                    if (disMiddleTo == -1) {
                        continue;
                    }

                    int dis = getClusterDis(from, to);
                    if (dis == -1 || dis > (disFromMiddle + disMiddleTo)) {
                        addClusterLink(from, to, disFromMiddle + disMiddleTo);
                    }
                }
            }
        }
    }

    /**
     * 保存边界点信息
     */
    private void saveBorderNames() {
        // parallelStream不保证顺序，streamp保证顺序
        borderNames = clusterLinkMap.keySet().stream()
                .filter(vertexName -> AhgVariable.INSTANCE.getVertex(vertexName).isBorder(level))
                .collect(Collectors.toList());

        for (int i = 0; i < borderNames.size(); i++) {
            int borderName1 = borderNames.get(i);
            for (int j = i + 1; j < borderNames.size(); j++) {
                int borderName2 = borderNames.get(j);

                int dis = getClusterDis(borderName1, borderName2);
                if (dis != -1) {
                    clusterLinkMap.get(borderName1).addBorderLink(new Node(borderName2, dis));
                    clusterLinkMap.get(borderName2).addBorderLink(new Node(borderName1, dis));
                }
            }
        }
    }

    /**
     * 添加兴趣点
     */
    public void addActive(Integer activeName) {
        activeNames.add(activeName);

        // 如果存在父亲簇，继续向上迭代
        if (AhgVariable.INSTANCE.containsClusterValue(parentName)) {
            AhgVariable.INSTANCE.getCluster(parentName).addActive(activeName);
        }
    }

    /**
     * 更新兴趣点到边界点之间的边
     */
    public void updateHighestBorderInfo(AhgActive activeInfo) {
        Map<String, List<Node>> highestBorderInfo = activeInfo.getHighestBorderInfo();

        // 如果不存在，则要更新
        if (!highestBorderInfo.containsKey(name)) {
            Integer activeName = activeInfo.getName();
            AhgVertex activeVertex = AhgVariable.INSTANCE.getVertex(activeName);

            if (activeVertex.isBorder(level)) {
                highestBorderInfo.put(name, Collections.emptyList());
            } else if (leaf || activeVertex.isBorder(level - 1)) {
                // 如果兴趣点属于本层簇中结点
                List<Node> thisLayerBorders = new ArrayList<>();
                for (Integer border : this.borderNames) {
                    int dis = 0;
                    try {
                        dis = getClusterDis(activeName, border);
                    } catch (Exception e) {
                        System.out.println("ssd");
                    }
                    if (dis != -1) {
                        thisLayerBorders.add(new Node(border, dis));
                    }
                }
                highestBorderInfo.put(name, thisLayerBorders);
            } else {
                // 兴趣点不存在该簇中
                String lastLayerName = name + AhgConstants.CLUSTER_NAME_SUFFIX +
                        activeVertex.getLayerClusterName(level + 1);
                List<Node> lastLayerBorders = highestBorderInfo.get(lastLayerName);
                if (lastLayerBorders == null) {
                    AhgVariable.INSTANCE.getCluster(lastLayerName).updateHighestBorderInfo(activeInfo);
                } else {
                    List<Node> thisLayerBorders = new ArrayList<>();

                    for (Integer borderName : borderNames) {
                        int dis = Integer.MAX_VALUE;
                        for (Node lastNode : lastLayerBorders) {
                            int lastBorderName = lastNode.getName();
                            if (borderName.equals(lastBorderName)) {
                                dis = lastNode.getDis();
                                break;
                            }
                            int borderDis = getClusterDis(borderName, lastBorderName);
                            if (borderDis == -1) {
                                continue;
                            }
                            dis = Math.min(dis, borderDis + lastNode.getDis());
                        }
                        thisLayerBorders.add(new Node(borderName, dis));
                    }
                    highestBorderInfo.put(name, thisLayerBorders);
                }

            }
        }
        if (status == AhgConstants.PARENT_ACTIVE) {
            AhgVariable.INSTANCE.getCluster(parentName).updateHighestBorderInfo(activeInfo);
        }
    }

    /**
     * 判断是否是合适的簇
     *
     * @return 1:兴趣点数量过少 需要往上合并；  0：合适   -1:兴趣点数量过多 需要向下分裂
     */
    public int isSuitableCluster() {
        int activeSize = activeNames.size();

        // 簇中兴趣点小于最少值，应向上合并
        if (activeSize < AhgVariable.LEAST_ACTIVE_NUM && parentName != null) {
            return AhgConstants.NEED_TO_MERGE;
        }
        // 簇中兴趣点大于最大值，应向下分裂
        if (activeSize > AhgVariable.MOST_ACTIVE_NUM && !leaf) {
            return AhgConstants.NEED_TO_SPLIT;
        }
        return AhgConstants.SUITABLE;
    }

    /**
     * 更新上下层兴趣点状态
     */
    private void updateActiveState(int updateState) {
        // 如果原先激活子图在上层

        if (updateState == AhgConstants.PARENT_ACTIVE && AhgVariable.INSTANCE.containsClusterValue(parentName)) {
            AhgCluster cluster = AhgVariable.INSTANCE.getCluster(parentName);
            if (cluster.status != AhgConstants.SON_ACTIVE) {
                cluster.setStatus(AhgConstants.SON_ACTIVE);
                cluster.updateActiveState(updateState);
            }
        } else if (updateState == AhgConstants.SON_ACTIVE) {
            for (String sonName : sonNames) {
                AhgCluster cluster = AhgVariable.INSTANCE.getCluster(sonName);
                if (cluster.status != AhgConstants.PARENT_ACTIVE) {
                    cluster.setStatus(AhgConstants.PARENT_ACTIVE);
                    if (!cluster.isLeaf()) {
                        cluster.updateActiveState(updateState);
                    }
                }
            }
        }
    }

    /**
     * 构建虚拟路网
     */
    public void buildVirtualMap() {
        updateActiveState(status);
        this.status = AhgConstants.SUITABLE;

        for (int activeName : activeNames) {
            AhgVertex active = AhgVariable.INSTANCE.getVertex(activeName);
            AhgActive activeInfo = active.getActiveInfo();
            updateHighestBorderInfo(activeInfo);

            String currentName = activeInfo.getCurrentClusterName();

            if (currentName != null) {
                if (currentName.equals(name)) {
                    // 如果维护点就是本层簇，则直接跳过
                    continue;
                }
                AhgVariable.INSTANCE.getCluster(currentName).removeActive(activeName);
            }

            activeInfo.getHighestBorderInfo().get(name).
                    forEach(node -> clusterLinkMap.get(node.getName()).
                            addBorderLink(new Node(activeName, node.getDis())));
        }

        for (int borderName : borderNames) {
            AhgVariable.INSTANCE.getVertex(borderName).
                    buildVirtualMap(clusterLinkMap.get(borderName).getBorderLink());
        }

    }


    /**
     * 从边界点中点虚拟路网中删除兴趣点
     */
    private void removeActive(int activeName) {
        for (Integer borderName : borderNames) {
            clusterLinkMap.get(borderName).removeBorderLink(activeName);
        }
    }


}
