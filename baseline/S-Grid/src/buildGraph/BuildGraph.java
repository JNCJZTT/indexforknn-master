package buildGraph;

import graph.Vnode;

import java.io.*;
import java.util.*;

import static common.Constants.*;
import static common.Utils.getVertexNum;

/**
 * @author: zhoutao
 * @since: 2021/10/31 3:04 下午
 * @description: 生成edgeFile和saveClusterFile
 */
public class BuildGraph {
    public BuildGraph(String map, int clusterSize) throws IOException {

        String edgeUrl = BASE_URL + map + ".txt";               //读取地图边文件
        String saveFileFolder = BASE_URL + map;              //地图保存文件

        // 保存边文件
        System.out.println("保存文件夹路径--》" + saveFileFolder);
        File saveEdgefile = new File(saveFileFolder);
        if (!saveEdgefile.exists() && !saveEdgefile.isDirectory()) {
            //文件夹不存在，创建文件夹
            saveEdgefile.mkdirs();

        }
        saveEdgefile = new File(saveFileFolder + "/" + map + "_Edge.txt");

        if (saveEdgefile.exists()) {
            // 如果EdgeFile已存在，说明已经创建过，直接返回
            System.out.println("文件已存在！");
            return;
        }

        // 保存节点-簇映射文件
        int vertexNum = getVertexNum(map);
        int clusterNum = (int) Math.ceil((double) vertexNum / clusterSize);

        File saveClusterFile = new File(saveFileFolder + "/" + map + "_" + clusterNum + "_" + clusterSize + ".txt");


        List<Vnode>[] arrayList = new ArrayList[vertexNum];
        for (int i = 0; i < vertexNum; i++) {
            arrayList[i] = new ArrayList<>();
        }

        readFile(edgeUrl, arrayList);
        saveFile(saveEdgefile, vertexNum, arrayList);
        buildVertex2Cluster(saveClusterFile, clusterNum, clusterSize, vertexNum, arrayList);
    }


    //读取文件
    private void readFile(String edgeUrl, List<Vnode>[] arrayList) throws IOException {
        System.out.println("读取地图路径文件--》" + edgeUrl);
        InputStreamReader read = new InputStreamReader(new FileInputStream(edgeUrl));
        BufferedReader bufferedReader = new BufferedReader(read);
        String lineText = null;                                             //按行读
        while ((lineText = bufferedReader.readLine()) != null) {
            if (lineText.charAt(0) == 'a') {
                String[] s = lineText.split("\\s+");
                int from = Integer.parseInt(s[1]) - 1,
                        to = Integer.parseInt(s[2]) - 1,
                        dis = Integer.parseInt(s[3]);
                arrayList[from].add(new Vnode(to, dis));
            }
        }
        read.close();
    }

    private void saveFile(File file, int vertexNum, List<Vnode>[] arrayList) throws IOException {

        file.createNewFile();

        FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
        BufferedWriter writer = new BufferedWriter(fileWriter);

        int x = 0;

        for (int i = 0; i < vertexNum; i++) {
            Collections.sort(arrayList[i], new Comparator<Vnode>() {
                @Override
                public int compare(Vnode o1, Vnode o2) {
                    return Integer.compare(o1.dis, o2.dis);
                }
            });
            StringBuilder sb = new StringBuilder();

            for (Vnode vn : arrayList[i]) {
                sb.append(vn.name + " " + vn.dis + " ");
            }
            writer.write(sb.toString());
            writer.write("\r\n");
            if (i > x * 10000) {
                writer.flush();
                x++;
            }
        }
        writer.close();
    }

    /*
     * 给每个节点构建簇
     * */
    private void buildVertex2Cluster(File file, int clusterNumPreCal, int clusterSize, int vertexNum, List<Vnode>[] arrayList) throws IOException {
        int[] Cluster = new int[vertexNum];
        Arrays.fill(Cluster, -1);

        int clusterNum = BFS(Cluster, clusterSize, arrayList);
        if(clusterNumPreCal!=clusterNum){
            System.out.println("error!子图数量计算错误！");
        }
        System.out.println("共有" + clusterNum + "个子图！");


        if (file.exists()) {
            System.out.println("文件已存在！");
            return;
        }
        file.createNewFile();

        FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
        BufferedWriter writer = new BufferedWriter(fileWriter);

        int x = 0;

        for (int i = 0; i < vertexNum; i++) {
            writer.write(Integer.toString(Cluster[i]));
            writer.write("\r\n");
            if (i > x * 10000) {
                writer.flush();
                x++;
            }
        }
        writer.close();
    }

    /*
     * 宽度优先遍历，确立每个节点所在的子图名
     * */
    private int BFS(int[] Cluster, int clusterSize, List<Vnode>[] arrayList) {
        Queue<Integer> queue = new LinkedList();
        int vertex = 0, clusterName = 0, size = 1;

        Cluster[vertex] = clusterName;
        queue.add(vertex);
        while (!queue.isEmpty()) {
            for (Vnode vn : arrayList[queue.poll()]) {
                if (Cluster[vn.name] == -1) {
                    Cluster[vn.name] = clusterName;
                    size++;
                    if (size == clusterSize) {
                        size = 0;
                        clusterName++;
                    }
                    queue.add(vn.name);
                }
            }
        }
        return (clusterName + 1);
    }

    public static void main(String[] args) throws IOException {
        new BuildGraph(MAP_NY, 50);
    }

}
