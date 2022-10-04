package Baseline.base.service.utils;

import Baseline.base.domain.Edge;
import Baseline.base.domain.GlobalVariable;
import Baseline.base.domain.Node;
import Baseline.base.domain.api.Vertex;
import Baseline.base.domain.enumeration.Distribution;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * DistributionUtil
 * 2022/2/15 zhoutao
 */
@Slf4j
@Component
public class DistributionUtil {
    /**
     * The proportion of the center of the normal distribution
     */
    private static final double NORMAL_PER = 0.65;

    /**
     * the least types of vertices
     */
    private static final double MIN_VERTICES = 100;
    /**
     * The built distribution
     * (when the internationalization variable is reset, it needs to be reset synchronously)
     */
    private Set<Distribution> builtDistributions = new HashSet<>();

    private static int startIndex;

    public static Double[] rank;

    private static List<ArrayList<Integer>> degreeList;

    private DistributionUtil() {
        builtDistributions.add(Distribution.RANDOM);
    }

    /**
     * Initialize distribution (random, normal, Zip)
     */
    public void initDistribute(List vertices) {
        if (builtDistributions.contains(GlobalVariable.DISTRIBUTE)) {
            log.info("Distribution has built");
            return;
        }

        if (degreeList == null) {
            degreeList = new ArrayList<>();
            for (int i = 0; i < GlobalVariable.VERTEX_NUM; i++) {
                Vertex vertex = (Vertex) vertices.get(i);
                int d = Integer.max(vertex.getOrigionEdges().size() - 1, 0);
                while (degreeList.size() <= d) {
                    degreeList.add(new ArrayList<>());
                }
                degreeList.get(d).add(i);
            }

            // 去除数量小于最小限度的度数集合
            for (int i = 0; i < degreeList.size(); i++) {
                if (degreeList.get(i).size() < MIN_VERTICES && i != 0) {
                    degreeList.get(i - 1).addAll(degreeList.get(i));
                    degreeList.remove(i--);
                }
            }

            for (int i = 0; i < degreeList.size(); i++) {
                log.info("degree={},size={}", i, degreeList.get(i).size());
            }
        }

        int maxDegree = degreeList.size();

        if (GlobalVariable.DISTRIBUTE.equals(Distribution.NORMAL)) {
            double count = 0;
            for (int i = 0; i < maxDegree; i++) {

                count += degreeList.get(i).size();
                if (count / GlobalVariable.VERTEX_NUM > NORMAL_PER) {
                    startIndex = i;
                    if (i == maxDegree - 1) {
                        startIndex = i - 1;
                    }
                    log.info("Start Index=" + startIndex);
                    log.info("count/Vertex_NUm=" + (count / GlobalVariable.VERTEX_NUM));
                    break;
                }
            }
        } else if (GlobalVariable.DISTRIBUTE.equals(Distribution.ZIPF)) {
            rank = new Double[maxDegree];
            double sum = 0;
            int Means = GlobalVariable.VERTEX_NUM / maxDegree;         //添加一个参数Means，来平衡数量极少的情况下
            for (int i = 0; i < maxDegree; i++) {
                double count = degreeList.get(i).size() + Means;
                sum += (GlobalVariable.VERTEX_NUM / count);
                rank[i] = (GlobalVariable.VERTEX_NUM / count);
            }

            for (int i = 0; i < rank.length; i++) {
                rank[i] = rank[i] / sum;
                log.info(String.format("%.2f", rank[i]) + "--" + degreeList.get(i).size());
            }
        } else {
            log.error("distribute not fount");
            return;
        }

        builtDistributions.add(GlobalVariable.DISTRIBUTE);
    }

    /**
     * get Vertex name
     *
     * @return vertex name based on distribution
     */
    public static int getVertexName() {
        if (GlobalVariable.DISTRIBUTE.equals(Distribution.RANDOM)) {
            return GlobalVariable.RANDOM.nextInt(GlobalVariable.VERTEX_NUM);
        } else if (GlobalVariable.DISTRIBUTE.equals(Distribution.NORMAL)) {
            int i;
            if (GlobalVariable.RANDOM.nextDouble() > NORMAL_PER) {
                i = GlobalVariable.RANDOM.nextInt(startIndex);
            } else {
                i = startIndex + GlobalVariable.RANDOM.nextInt(degreeList.size() - startIndex);
            }
            int size = degreeList.get(i).size();
            return degreeList.get(i).get(GlobalVariable.RANDOM.nextInt(size));

        } else if (GlobalVariable.DISTRIBUTE.equals(Distribution.ZIPF)) {
            double x = GlobalVariable.RANDOM.nextDouble();
            for (int i = 0; i < rank.length; i++) {
                if (x < rank[i]) {
                    return degreeList.get(i).get(GlobalVariable.RANDOM.nextInt((degreeList.get(i).size())));
                }
                x -= rank[i];
            }
        }
        log.error("distribute not fount");
        return -1;
    }

    /**
     * get Edge
     *
     * @return Edge based on distribution
     */
    public static Edge getEdge() {
        int vertexName = getVertexName();
        Node node = GlobalVariable.variable.getVertex(vertexName).getRandomEdge();
        return new Edge(vertexName, node.getName(), node.getDis());
    }
    public static List<Integer> getRandomVertexList(){
        ArrayList<Integer> list = new ArrayList<Integer>() ;
        while (list.size()!= GlobalVariable.COMPUTE_NUM){
            list.add(GlobalVariable.RANDOM.nextInt(GlobalVariable.VERTEX_NUM));
        }
        return list;
    }
}
