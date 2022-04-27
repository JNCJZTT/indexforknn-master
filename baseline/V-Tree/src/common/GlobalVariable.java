package common;

import graph.Car;
import graph.Cluster;
import graph.Vertex;

import java.util.*;

public class GlobalVariable {
    public static String MAP;                 //地图

    public static String MAP_URL;             //文件读取url

    public static String EDGE_URL;            //边文件URL

    public static int SUB_GRAPH_SIZE;         //子图的大小

    public static List<Integer> SUB_GRAPH_NUM;//子图的数量

    public static int BRANCH_NUM = 4;         //分支数

    public static int VERTEX_NUM;             //节点数量


    public static Vertex[] allVertices;       //所有的节点数量

    public static HashMap<Integer, Cluster> allClusters;  //所有的簇

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

    public static int K;

    public static Integer ROOT;

    public static int[] Value;

    public static double queryTime;

    public static double updateTime;

    public static double buildTime;

    public static double buildMemory;

}
