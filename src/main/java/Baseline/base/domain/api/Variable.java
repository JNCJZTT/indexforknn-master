package Baseline.base.domain.api;

import Baseline.base.common.constants.Constants;
import Baseline.base.domain.GlobalVariable;
import Baseline.base.service.dto.IndexDTO;

import java.util.*;

/**
 * VariableUtil
 * 2022/4/27 zhoutao
 */
public abstract class Variable<T extends Vertex, E extends Cluster> {
    protected List<T> vertices;

    protected Map<String, E> clusters;

    public void initVariables() {
        vertices = new ArrayList<>(GlobalVariable.MAP_INFO.getSize());
        clusters = new HashMap<>(GlobalVariable.MAP_INFO.getSize() / GlobalVariable.SUB_GRAPH_SIZE);
    }

    /**
     * initVariable
     *
     * @param indexDTO indexDTO
     */
    public abstract void initVariables(IndexDTO indexDTO);

    /**
     * create vertex
     *
     * @param vertex vertex
     */
    public void addVertex(T vertex) {
        vertices.add(vertex);
    }

    /**
     * create cluster
     *
     * @param name    name
     * @param cluster cluster
     */
    public void addCluster(String name, E cluster) {
        clusters.put(name, cluster);
    }


    /**
     * get vertex
     *
     * @param index index
     * @return vertex
     */
    public T getVertex(int index) {
        return vertices.get(index);
    }

    /**
     * get vertices
     *
     * @return vertices
     */
    public List<T> getVertices() {
        return vertices;
    }

    /**
     * get cluster
     *
     * @param name cluster name
     * @return cluster
     */
    public E getCluster(String name) {
        return clusters.get(name);
    }


    /**
     * get layer cluster name
     *
     * @param clusterName clusterName
     * @param layer       layer
     */
    public String getLayerClusterName(String clusterName, int layer) {
        return clusterName.substring(0, GlobalVariable.DIGIT + layer * (GlobalVariable.DIGIT + 1));
    }

    /**
     * getParentClusterName
     *
     * @param name name
     * @return parent cluster name
     */
    public String getParentClusterName(String name) {
        int lastIndex = name.lastIndexOf(Constants.CLUSTER_NAME_SUFFIX);
        if (lastIndex == -1) {
            return null;
        }
        return name.substring(0, lastIndex);
    }

    /**
     * getLeafCluster
     */
    public E getLeafCluster(T vertex) {
        return clusters.get(vertex.getClusterName());
    }


    /**
     * get clusters
     *
     * @return clusters
     */
    public Collection<E> getClusters() {
        return clusters.values();
    }

    /**
     * get clusterKeySet
     *
     * @return keySet
     */
    public Set<String> getClusterKeySet() {
        return clusters.keySet();
    }

    /**
     * Does it exist in the full tree
     *
     * @return return
     */
    public boolean containsClusterKey(String name) {
        return clusters.containsKey(name);
    }

    /**
     * is already in the current tree
     *
     * @return return
     */
    public boolean containsClusterValue(String name) {
        return containsClusterKey(name) && clusters.get(name) != null;
    }

    /**
     * get the layer of cluster
     *
     * @param clusterName clusterName
     */
    public static int getClusterLayer(String clusterName) {
        return (clusterName.length() + 1) / (GlobalVariable.DIGIT + 1) - 1;
    }

    /**
     * getVerticesSize
     *
     * @return vertices size
     */
    public int getVerticeSize() {
        return vertices.size();

    }

    /**
     * getClusterSize
     *
     * @return cluster size
     */
    public int getClusterSize() {
        return clusters.size();
    }


}
