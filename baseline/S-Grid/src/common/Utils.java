package common;

import graph.Car;
import graph.Cluster;
import graph.Vertex;
import graph.Vnode;

import java.io.*;
import java.util.*;

import static common.Constants.*;
import static common.Constants.RANDOM;
import static common.GlobalVariable.*;
import static common.GlobalVariable.MAP;

/**
 * @author: zhoutao
 * @since: 2021/11/11 9:46 下午
 * @description: TODO
 */
public class Utils {


    /*
     * 初始化全局变量
     * */
    public static void initGlobalValue(String map, int sub_GRAPH_SIZE, int car_Num, String map_Distribute, int k) {
        MAP_DISTRIBUTE = map_Distribute;
        VERTEX_NUM = getVertexNum(map);
        MAP = map;

        SUB_GRAPH_NUM = (int) Math.ceil((double) VERTEX_NUM / sub_GRAPH_SIZE);
        K = k;

        MAP_URL = BASE_URL + map + "/" + map + "_" + SUB_GRAPH_NUM + "_" + sub_GRAPH_SIZE + ".txt";
        EDGE_URL = BASE_URL + map + "/" + map + "_Edge.txt";

        System.out.println("Graph Num=" + SUB_GRAPH_NUM);
        System.out.println("Map Url=" + MAP_URL);
        System.out.println("Edge Url=" + EDGE_URL);

        //初始化节点
        allVertices = new ArrayList<>(VERTEX_NUM);

        //初始化对象
        allClusters = new ArrayList<>((int) (SUB_GRAPH_NUM * 1.25));
        for (int i = 0; i < SUB_GRAPH_NUM; i++) {
            allClusters.add(new Cluster());
        }

        //初始化移动对象
        CAR_NUM = car_Num;
    }


    public static int getVertexNum(String map) {
        if (!MAP2VERTEXNUM.containsKey(map)) {
            System.out.println("Worng Map!");
        }
        return MAP2VERTEXNUM.get(map);
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
            int d = allVertices.get(i).originalEdges.size();
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
                        edgeDis = allVertices.get(activeName).getRandomEdge().dis;

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

    public static void resetValue() {
        if (Value == null) {
            Value = new int[VERTEX_NUM + BORDER_NUM];
        }
        Arrays.fill(Value, -1);
    }


}
