package common;

import graph.*;

import java.io.*;
import java.util.*;

import static common.Constants.*;
import static common.GlobalVariable.*;

public class Utils {


    /*
     * 初始化全局变量
     * */
    public static void initGlobalValue(String map, int branch_Num, int sub_GRAPH_SIZE, int car_Num, String map_Distribute, int k) {
        MAP = map;
        BRANCH_NUM = branch_Num;
        MAP_DISTRIBUTE = map_Distribute;
        SUB_GRAPH_SIZE = sub_GRAPH_SIZE;
        VERTEX_NUM = getVertexNum(MAP);

        SUB_GRAPH_NUM = new ArrayList<>();
        SUB_GRAPH_NUM.add((int) Math.ceil((double) VERTEX_NUM / SUB_GRAPH_SIZE));


        K = k;
        buildSubGraphNum();
        MAP_URL = BASE_URL + MAP + "/" + MAP + "_" + SUB_GRAPH_NUM.get(0) + "_" + SUB_GRAPH_SIZE + ".txt";
        EDGE_URL = BASE_URL + MAP + "/" + MAP + "_Edge.txt";


        System.out.println("Map Url=" + MAP_URL);
        System.out.println("Edge Url=" + EDGE_URL);

        //初始化节点
        allVertices = new Vertex[VERTEX_NUM];
        for (int i = 0; i < VERTEX_NUM; i++) {
            allVertices[i] = new Vertex(i);
        }


        //初始化对象
        allClusters = new HashMap<>((int) (SUB_GRAPH_NUM.stream().mapToInt(num -> num).sum() * 1.25));

        int num = 0;
        for (int level = 0; level < CLUSTER_LEVEL; level++) {
            for (int i = 0; i < SUB_GRAPH_NUM.get(level); i++) {
                allClusters.put(num + i, new Cluster(level, num + i));
            }
            num += SUB_GRAPH_NUM.get(level);
        }
        ROOT = allClusters.size() - 1;


        //初始化移动对象
        CAR_NUM = car_Num;
    }

    /*
     * 获得所有层子图的数量
     * */
    private static void buildSubGraphNum() {
        CLUSTER_LEVEL = 1;
        do {
            SUB_GRAPH_NUM.add(Math.max(1,
                    (int) Math.floor((double) SUB_GRAPH_NUM.get(CLUSTER_LEVEL - 1) / BRANCH_NUM)));
        } while (SUB_GRAPH_NUM.get(CLUSTER_LEVEL++) != 1);
        for (int i = 0; i < CLUSTER_LEVEL; i++) {
            System.out.println("When Level=" + i + " , sub graph num =" + SUB_GRAPH_NUM.get(i));
        }
        System.out.println("all graph num=" + SUB_GRAPH_NUM.stream().mapToInt(num -> num).sum());
    }

    /*
     * 初始化移动对象分布模式
     * */
    public void initDistribution() {
        if (MAP_DISTRIBUTE.equals(RANDOM_DISTRIBUTE)) {
            return;
        }

        DEGREE = new ArrayList<>();
        DEGREE_LIST = new HashMap<>();
        for (int i = 0; i < VERTEX_NUM; i++) {
            int d = allVertices[i].originalEdges.size();
            if (!DEGREE_LIST.containsKey(d)) {
                DEGREE_LIST.put(d, new ArrayList());
            }
            DEGREE_LIST.get(d).add(i);
        }
        DEGREE.addAll(DEGREE_LIST.keySet());
        Collections.sort(DEGREE);

        if (MAP_DISTRIBUTE.equals(NORMAL_DISTRIBUTE)) {
            double count = 0;
            for (int i = 0; i < DEGREE.size(); i++) {
                int d = DEGREE.get(i);
                count += DEGREE_LIST.get(d).size();
                if (count / VERTEX_NUM > NORMAL_PER) {
                    START_INDEX = i + 1;
                    if (START_INDEX == DEGREE.size()) {
                        START_INDEX = i;
                    }
                    System.out.println("Start Index=" + START_INDEX);
                    System.out.println("count/Vertex_NUm=" + (count / VERTEX_NUM));
                    break;
                }
            }
        } else if (MAP_DISTRIBUTE.equals(ZIPF_DISTRIBUTE)) {
            RANK = new Double[DEGREE.size()];
            double sum = 0;
            int Means = VERTEX_NUM / DEGREE.size();         //添加一个参数Means，来平衡数量极少的情况下
            for (int i = 0; i < DEGREE.size(); i++) {
                int d = DEGREE.get(i);
                double count = DEGREE_LIST.get(d).size() + Means;
                sum += (VERTEX_NUM / count);
                RANK[i] = (VERTEX_NUM / count);
            }

            for (int i = 0; i < RANK.length; i++) {
                RANK[i] = RANK[i] / sum;
                System.out.print(String.format("%.2f", RANK[i]) + "--" + DEGREE_LIST.get(DEGREE.get(i)).size() + " /");
            }
            System.out.println();

        } else {
            System.out.println("distribute not fount");
        }
    }

    /*
     * 获得符合分布的特殊节点
     * */
    public static int getVertexName() {
        if (MAP_DISTRIBUTE.equals(RANDOM_DISTRIBUTE)) {
            return RANDOM.nextInt(VERTEX_NUM);
        } else if (MAP_DISTRIBUTE.equals(NORMAL_DISTRIBUTE)) {
            int i = -1;
            if (RANDOM.nextDouble() > NORMAL_PER) {
                i = RANDOM.nextInt(START_INDEX);
                i = DEGREE.get(i);
            } else {
                i = RANDOM.nextInt(DEGREE.size() - START_INDEX);
                i = DEGREE.get(START_INDEX + i);
            }
            return DEGREE_LIST.get(i).get(RANDOM.nextInt((DEGREE_LIST.get(i).size())));

        } else if (MAP_DISTRIBUTE.equals(ZIPF_DISTRIBUTE)) {
            double x = RANDOM.nextDouble();
            for (int i = 0; i < RANK.length; i++) {
                if (x < RANK[i]) {
                    return DEGREE_LIST.get(DEGREE.get(i)).get(RANDOM.nextInt((DEGREE_LIST.get(DEGREE.get(i)).size())));
                }
                x -= RANK[i];
            }
        }
        System.out.println("distribute not fount");
        return -1;
    }


    /*
     * 构建所有簇的层次关系
     * */
    public void buildClusterRelation() {
        Integer sonName = 0, parentName = 0;
        for (int level = 0; level < CLUSTER_LEVEL - 1; level++) {
            parentName += SUB_GRAPH_NUM.get(level);
            int avgBorderSize = 0;
            for (int i = 0; i < SUB_GRAPH_NUM.get(level); i++) {
                allClusters.get(i + sonName).computeCluster();
                avgBorderSize += allClusters.get(i + sonName).getBorderSize();
            }
            System.out.println("when level=" + level + " , avgBorderSize=" + avgBorderSize / SUB_GRAPH_NUM.get(level));

            BFS(level, sonName, parentName);
            sonName = parentName;
        }
        allClusters.get(ROOT).computeCluster();
        System.out.println("when level=" + (CLUSTER_LEVEL - 1) + " , avgBorderSize=" + allClusters.get(ROOT).getClusterSize());

    }

    /*
     * 通过BFS来构建一层的簇关系
     * */
    private void BFS(int level, Integer start, Integer parent) {
        boolean[] isVisit = new boolean[SUB_GRAPH_NUM.get(level)];
        Queue<Integer> queue = new LinkedList();
        queue.add(start);
        int branch = 0;
        int parentLimit = parent + SUB_GRAPH_NUM.get(level + 1);
        while (!queue.isEmpty()) {
            Integer clusterName = queue.poll();
            int index = clusterName - start;
            try {
                if (isVisit[index]) continue;
            } catch (Exception e) {
                System.out.println(456);
            }
            isVisit[index] = true;

            allClusters.get(parent).addSonCluster(clusterName);
            allClusters.get(clusterName).setParent(parent);


            branch++;
            if (branch == BRANCH_NUM) {
                branch = 0;
                if (parent < parentLimit - 1) {
                    parent++;
                }
            }
            queue.addAll(allClusters.get(clusterName).getSortedNeighbor());
        }
    }

    /*
     * 宽度优先遍历，设置父亲节点
     * */
//    private void BFS(int clusterName, int parentClusterName, int start, int end, boolean[] isVisit) {
//        allClusters.put(CLUSTER_NAME++, new Cluster(CLUSTER_LEVEL));
//
//        int branchNum = BRANCH_NUM;
//        if (parentClusterName == (end + SUB_GRAPH_NUM.get(CLUSTER_LEVEL) - 1)) {
//            branchNum = BRANCH_NUM * 2;
//        }
//
//        for (int i = clusterName; i < end; i++) {
//
//            if (isVisit[i - start]) continue;
//
//            allClusters.get(i).parent = parentClusterName;
//            allClusters.get(parentClusterName).sons.add(i);
//            isVisit[i - start] = true;
//            if (allClusters.get(parentClusterName).sons.size() >= branchNum) {
//                return;
//            }
//
//            for (int neighbor : allClusters.get(i).neighbores) {
//                if (isVisit[neighbor - start]) continue;
//
//                allClusters.get(neighbor).parent = parentClusterName;
//                allClusters.get(parentClusterName).sons.add(neighbor);
//                isVisit[neighbor - start] = true;
//                if (allClusters.get(parentClusterName).sons.size() >= branchNum) {
//                    return;
//                }
//            }
//        }
//    }

    /*
     * 为一个高层的簇，找到与它邻接的簇
     * */
//    public static void buildClusterRelationNeighbors(int start, int end) {
//        for (int i = start; i < end; i++) {
//            List<Integer> clusterSons = allClusters.get(i).sons;
//            for (int son : clusterSons) {
//                if (allClusters.get(son).parent != i) {
//                    allClusters.get(i).neighbores.add(allClusters.get(son).parent);
//                }
//            }
//        }
//    }

    /*
     * 两个子图找到最近的相同祖先
     * */
//    public static Integer getSameClusterParent(Integer clusterName1, Integer clusterName2) {
//        while (true) {
//            if (clusterName1.equals(clusterName2)) {
//                return clusterName1;
//            }
//            clusterName1 = allClusters.get(clusterName1).parent;
//            clusterName2 = allClusters.get(clusterName2).parent;
//        }
//    }

    public static void resetValue() {
        if (Value == null) {
            Value = new int[VERTEX_NUM];
        }
        Arrays.fill(Value, -1);
    }

    public static int getVertexNum(String map) {
        if (!MAP2VERTEXNUM.containsKey(map)) {
            System.out.println("Worng Map!");
        }
        return MAP2VERTEXNUM.get(map);
    }

    /*
     * 构建移动对象列表
     * */
    public void buildCar() throws IOException {
        allCars = new ArrayList<>(CAR_NUM);
        CAR_URL = BASE_URL + MAP + "_Car_" + ((double) CAR_NUM) / 10000 + "w_" + MAP_DISTRIBUTE;

        if (this.getClass().getResourceAsStream(CAR_URL) != null) {
            InputStreamReader read = new InputStreamReader(new FileInputStream(new File(CAR_URL)));
            BufferedReader bufferedReader = new BufferedReader(read);

            for (int i = 0; i < CAR_NUM; i++) {
                String[] s = bufferedReader.readLine().split("\\s+");

                int carName = Integer.parseInt(s[0]);
                int activeName = Integer.parseInt(s[1]);
                int edgeDis = Integer.parseInt(s[2]);

                Car car = new Car(carName, activeName, edgeDis);
                allCars.add(car);
            }
            read.close();
        } else {
            String carUrl = BASE_URL + MAP + "/" + MAP + "_Car_" + ((double) CAR_NUM) / 10000 + "w_" + MAP_DISTRIBUTE;

            File file = new File(carUrl);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
            BufferedWriter writer = new BufferedWriter(fileWriter);

            int x = 0;    //防止缓冲区满
            for (int i = 0; i < CAR_NUM; i++) {
                int activeName = getVertexName(),
                        edgeDis = allVertices[activeName].getRandomEdge().dis;

                Car c = new Car(i, activeName, edgeDis);
                allCars.add(c);

                writer.write(i + " " + " " + activeName + " " + edgeDis);
                writer.write("\r\n");
                if (i > x * 10000) {
                    writer.flush();
                    x++;
                }
            }
            writer.close();
        }
    }


}
