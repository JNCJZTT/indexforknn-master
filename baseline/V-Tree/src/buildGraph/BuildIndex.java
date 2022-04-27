package buildGraph;

import common.Utils;
import graph.*;

import java.io.*;
import java.util.*;

import static common.Constants.RANDOM;
import static common.GlobalVariable.*;
import static common.Utils.*;

public class BuildIndex {

    public BuildIndex() throws IOException {
        readVertex2ClusterFile();
        readEdgeFile();


        long BuildStart = System.currentTimeMillis();
        System.gc();

        Utils utils = new Utils();
        utils.buildClusterRelation();
        utils.initDistribution();
        buildCar();

        long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

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

            Vertex vi = allVertices[i];

            for (int k = 0; k < s.length; k += 2) {
                int j = Integer.parseInt(s[k]), dis = Integer.parseInt(s[k + 1]);
                if (j < i) continue;        //剪枝

                Vertex vj = allVertices[j];

                vi.addOriginalEdges(j, dis);
                vj.addOriginalEdges(i, dis);

                //判断两个节点是否在同一个网格内
                if (vi.getClusterName(0).equals(vj.getClusterName(0))) {
                    allClusters.get(vi.getClusterName(0)).setClusterDis(i, j, dis);
                }
            }
        }
        read.close();
    }

    /*
     * 构建移动对象
     * */
    private void buildCar() throws IOException {
        new Utils().buildCar();

        for (Car c : allCars) {
            int activeName = c.activeName;
            Vertex activeVertex = allVertices[activeName];
            if (activeVertex.addCar(c)) {
                allClusters.get(activeVertex.getClusterName(0)).addActive(activeName);
            }
        }
    }

    /*
     * 更新移动对象
     * */
    public void updateCar(int updateNum) {
        long updateStart = System.nanoTime();

        for (int i = 0; i < updateNum; i++) {
            Car c = allCars.get(RANDOM.nextInt(CAR_NUM));
            int originalActiveName = c.updateLocation();
            if (originalActiveName == -1) {
                continue;
            }
            int newActiveName = c.activeName;
            Vertex originalActiveVertex = allVertices[originalActiveName];
            if (originalActiveVertex.removeCar(c)) {
                allClusters.get(originalActiveVertex.getClusterName(0)).deleteActive(originalActiveName);
            }
            Vertex newActiveVertex = allVertices[newActiveName];
            if (newActiveVertex.addCar(c)) {
                allClusters.get(newActiveVertex.getClusterName(0)).addVertex(newActiveName);
            }
        }
        updateTime = (float) (System.nanoTime() - updateStart) / 1000;

    }


}
