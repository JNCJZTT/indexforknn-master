package com.index.indexforknn.ahg.domain;


import com.index.indexforknn.ahg.service.utils.AhgUtil;
import com.index.indexforknn.base.domain.Car;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * TODO
 * 2022/3/12 zhoutao
 */
public class AhgVariable {

    public static AhgVariable INSTANCE = new AhgVariable();

    public static int LEAST_ACTIVE_NUM;

    public static int MOST_ACTIVE_NUM;

    public static List<AhgVertex> vertices;

    public static Map<String, AhgCluster> clusters;



    private AhgVariable() {
    }

    /**
     * 创建簇
     */
    public void addCluster(String name, AhgCluster cluster) {
        AhgVariable.clusters.put(name, cluster);
    }

    /**
     * 获得结点
     */
    public AhgVertex getVertex(int index) {
        return AhgVariable.vertices.get(index);
    }

    /**
     * 获得簇
     */
    public AhgCluster getCluster(String name) {
        return AhgVariable.clusters.get(name);
    }

    /**
     * cluster是否在全量树中
     */
    public boolean containsClusterKey(String name) {
        return AhgVariable.clusters.containsKey(name);
    }

    /**
     * 是否存在cluster
     */
    public boolean containsClusterValue(String name) {
        return containsClusterKey(name) && AhgVariable.clusters.get(name) != null;
    }

    /**
     * getClusterKeySet
     */
    public Set<String> getClusterKeySet() {
        return AhgVariable.clusters.keySet();
    }

    /**
     * 构建全量树key
     */
    public void buildFullTreeKey() {
        Queue<String> queue = new LinkedList<>(clusters.keySet());
        while (!queue.isEmpty()) {
            String clusterName = queue.poll();
            String parentName;
            // 当parentName存在，且clusters不存在parentName时，向上递归
            while (StringUtils.hasLength(parentName = AhgUtil.getParentClusterName(clusterName))
                    && !AhgVariable.clusters.containsKey(parentName)) {
                AhgVariable.clusters.put(parentName, null);
                clusterName = parentName;
            }
        }
    }


}
