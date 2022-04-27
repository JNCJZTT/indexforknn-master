package buildGraph;


import common.Constants;
import common.Utils;
import graph.Car;
import graph.Cluster;
import graph.Vertex;
import graph.Vnode;

import java.io.*;

import static common.GlobalVariable.*;
import static common.Utils.resetValue;


/**
 * @author: zhoutao
 * @since: 2021/11/11 10:05 下午
 * @description: TODO
 */
public class BuildIndex {
    public BuildIndex() throws IOException {
        readVertex2ClusterFile();
        readEdgeFile();

        System.gc();
        long BuildStart = System.currentTimeMillis();
        long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        computeCluster();
        new Utils().initDistribution();
        buildCar();
        resetValue();

        buildTime = Double.parseDouble(String.format("%.2f", (float) (System.currentTimeMillis() - BuildStart) / 1000));
        long memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        buildMemory = Double.parseDouble(String.format("%.2f", ((float) (memoryAfter - memoryBefore) / (Math.pow(1024, 2)))));


        System.out.println("构建时间为：" + buildTime + "秒");
        System.out.println("构建内存为：" + buildMemory + "MB");
    }


    /*
     * 读取每个节点属于簇的文件
     */
    private void readVertex2ClusterFile() throws IOException {
        InputStreamReader read = new InputStreamReader(new FileInputStream(MAP_URL));
        BufferedReader bufferedReader = new BufferedReader(read);

        for (int i = 0; i < VERTEX_NUM; i++) {
            int clusterName = Integer.parseInt(bufferedReader.readLine());

            allVertices.add(new Vertex(clusterName));
            allClusters.get(clusterName).addVertex(i);
        }

        read.close();
    }

    /*
     * 读取边文件
     */
    private void readEdgeFile() throws IOException {
        InputStreamReader read = new InputStreamReader(new FileInputStream(EDGE_URL));
        BufferedReader bufferedReader = new BufferedReader(read);

        for (int i = 0; i < VERTEX_NUM; i++) {
            String[] s = bufferedReader.readLine().split("\\s+");

            Vertex vi = allVertices.get(i);

            for (int k = 0; k < s.length; k += 2) {
                int j = Integer.parseInt(s[k]), dis = Integer.parseInt(s[k + 1]);
                if (j < i) continue;        //剪枝

                Vertex vj = allVertices.get(j);

                vi.addOriginalEdges(j, dis);
                vj.addOriginalEdges(i, dis);

                //判断两个节点是否在同一个网格内
                if (vi.clusterName.get(0).equals(vj.clusterName.get(0))) {
                    allClusters.get(vi.clusterName.get(0)).addClusterEdge(i, j, dis);
                    vi.Link.add(new Vnode(j, dis));
                    vj.Link.add(new Vnode(i, dis));
                } else {
                    generateBorder(vi.clusterName.get(0), vj.clusterName.get(0), i, j, dis);
                }
            }
        }
        read.close();
    }

    // 生成一个存在两个簇中的边节点
    private void generateBorder(Integer clusterName1, Integer clusterName2, int vi, int vj, int dis) {


        int dis1 = dis / 2;

        Vertex v = new Vertex(clusterName1, clusterName2);
        v.Link.add(new Vnode(vi, dis1));
        v.Link.add(new Vnode(vj, dis - dis1));
        allVertices.add(v);

        allClusters.get(clusterName1).addBorder(VERTEX_NUM + BORDER_NUM, vi, dis1);
        allClusters.get(clusterName2).addBorder(VERTEX_NUM + BORDER_NUM, vj, dis - dis1);


        BORDER_NUM++;
    }

    /*
     * 对每个子图进行FLoyd计算
     * */
    private void computeCluster() {
        allClusters.stream().forEach(Cluster::computeCluster);
    }

    /*
     * 构建移动对象
     * */
    private void buildCar() throws IOException {
        new Utils().buildCar();

        for (Car c : allCars) {
            int activeName = c.activeName;
            Vertex activeVertex = allVertices.get(activeName);

            activeVertex.addCar(c);
        }
    }

    public void updateCar(int updateSize) {
        updateTime = System.nanoTime();
        for (int i = 0; i < updateSize; i++) {
            Car c = allCars.get(Constants.RANDOM.nextInt(CAR_NUM));
            int originalActiveName = c.updateLocation();
            if (originalActiveName == -1) {
                continue;
            }
            int newActiveName = c.activeName;
            allVertices.get(newActiveName).addCar(c);
            allVertices.get(originalActiveName).removeCar(c);
        }
        updateTime = (float) (System.nanoTime() - updateTime) / 1000;
    }
}
