package common;

import graph.Car;
import graph.Cluster;
import graph.Vertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: zhoutao
 * @since: 2021/11/11 10:05 下午
 * @description: TODO
 */
public class GlobalVariable {
    public static String MAP;                 //地图

    public static String MAP_URL;             //文件读取url

    public static String EDGE_URL;            //边文件URL

    public static int SUB_GRAPH_NUM;          //子图的数量

    public static int BRANCH_NUM = 4;         //分支数

    public static int VERTEX_NUM;             //节点数量

    public static int BORDER_NUM = 0;         //边界点点数量

    public static List<Vertex> allVertices;   //所有的节点数量

    public static List<Cluster> allClusters;  //所有的簇

    public static String CAR_URL;              //移动对象保存路径

    public static int CAR_NUM;                 //汽车数量

    public static List<Car> allCars;           //所有的汽车

    public static int CLUSTER_LEVEL;           //图的最高层

    /*
     * 构建移动对象的分布
     * */
    public static String MAP_DISTRIBUTE;       //分布方式

    public static HashMap<Integer, ArrayList<Integer>> DEGREE_LIST;

    public static Double[] RANK;

    public static List<Integer> DEGREE;

    public static int START_INDEX;

    public static List<Integer> activeClusters;//所有激活的簇

    public static int K;

    public static int[] Value;

    public static double queryTime;

    public static double updateTime;

    public static double buildTime;

    public static double buildMemory;


}
