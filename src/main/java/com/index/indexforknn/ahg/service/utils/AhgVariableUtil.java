package com.index.indexforknn.ahg.service.utils;

import com.index.indexforknn.ahg.common.AhgConstants;
import com.index.indexforknn.ahg.domain.AhgCluster;
import com.index.indexforknn.ahg.domain.AhgVariable;
import com.index.indexforknn.ahg.domain.AhgVertex;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * AhgVariableUtil
 * 2022/4/24 zhoutao
 */
public class AhgVariableUtil {

    /**
     * create cluster
     *
     * @param name    name
     * @param cluster cluster
     */
    public static void addCluster(String name, AhgCluster cluster) {
        AhgVariable.clusters.put(name, cluster);
    }

    /**
     * create vertex
     *
     * @param vertex vertex
     */
    public static void addVertex(AhgVertex vertex) {
        AhgVariable.vertices.add(vertex);
    }

    /**
     * get vertex
     *
     * @param index index
     * @return vertex
     */
    public static AhgVertex getVertex(int index) {
        return AhgVariable.vertices.get(index);
    }

    /**
     * get vertices
     *
     * @return vertices
     */
    public static List<AhgVertex> getVertices() {
        return AhgVariable.vertices;
    }

    /**
     * get cluster
     *
     * @param name cluster name
     * @return cluster
     */
    public static AhgCluster getCluster(String name) {
        return AhgVariable.clusters.get(name);
    }

    /**
     * get leaf cluster
     *
     * @param vertex vertex
     * @return leaf cluster
     */
    public static AhgCluster getLeafCluster(AhgVertex vertex) {
        return AhgVariable.clusters.get(vertex.getClusterName());
    }

    /**
     * get layer cluster name
     *
     * @param clusterName clusterName
     * @param layer layer
     */
    public static String getLayerClusterName(String clusterName, int layer) {
        return clusterName.substring(0, AhgVariable.DIGIT + layer * (AhgVariable.DIGIT + 1));
    }

    /**
     * getParentClusterName
     *
     * @param name name
     * @return parent cluster name
     */
    public static String getParentClusterName(String name) {
        int lastIndex = name.lastIndexOf(AhgConstants.CLUSTER_NAME_SUFFIX);
        if (lastIndex == -1) {
            return null;
        }
        return name.substring(0, lastIndex);
    }


    /**
     * get clusters
     *
     * @return clusters
     */
    public static Collection<AhgCluster> getClusters() {
        return AhgVariable.clusters.values();
    }

    /**
     * Does it exist in the full tree
     *
     * @return return
     */
    public static boolean containsClusterKey(String name) {
        return AhgVariable.clusters.containsKey(name);
    }

    /**
     * is already in the current tree
     *
     * @return return
     */
    public static boolean containsClusterValue(String name) {
        return containsClusterKey(name) && AhgVariable.clusters.get(name) != null;
    }

    /**
     * getClusterKeySet
     *
     * @return clusterKeySet
     */
    public static Set<String> getClusterKeySet() {
        return AhgVariable.clusters.keySet();
    }

    /**
     * get the layer of cluster
     *
     * @param clusterName clusterName
     */
    public static int getClusterLayer(String clusterName) {
        return (clusterName.length() + 1) / (AhgVariable.DIGIT + 1) - 1;
    }

}
