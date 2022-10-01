package com.index.indexforknn.data.process;

import com.index.indexforknn.base.common.BaseException;
import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.base.domain.enumeration.MapInfo;
import com.index.indexforknn.data.common.Constants;

import java.io.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * transform original Edge file to readable file for metis
 * 2022/7/17 zhoutao
 */
public class dataProcess {
    private static final int MAX_BUFFERED = 10000;
    private MapInfo map;
    private String edgeUrl = Constants.rootUrl + "{0}/USA-road-d.{0}.gr";
    private String metisUrl = Constants.rootUrl + "{0}/{0}_Edge.txt";
    private List<Node>[] nodes;

    /**
     * init
     */
    public dataProcess(MapInfo map) {
        this.map = map;
        this.edgeUrl = MessageFormat.format(edgeUrl, map.name());
        this.metisUrl = MessageFormat.format(metisUrl, map.name());
        nodes = new List[map.getSize()];
        for (int i = 0; i < map.getSize(); i++) {
            nodes[i] = new ArrayList<>();
        }
    }

    public void transform() throws BaseException, IOException {
        readEdgeFile();
        writeFile();
    }

    private void readEdgeFile() throws IOException, BaseException {
        InputStreamReader read = new InputStreamReader(new FileInputStream(edgeUrl));
        BufferedReader bufferedReader = new BufferedReader(read);
        String line;
        while (true) {
            line = bufferedReader.readLine();
            if (line == null) {
                break;
            }
            String[] s = line.split("\\s+");
            if (!Objects.equals(s[0], "a")) {
                continue;
            }
            int v1 = Integer.parseInt(s[1]) - 1;
            int v2 = Integer.parseInt(s[2]) - 1;
            int dis = Integer.parseInt(s[3]);
            nodes[v1].add(new Node(v2, dis));

        }
        read.close();
    }

    private void writeFile() throws IOException {

        File file = new File(this.metisUrl);
        if (!file.exists()) {
            FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
            BufferedWriter writer = new BufferedWriter(fileWriter);
            int bufferNum = 1;
            for (int i = 0; i < map.getSize(); i++) {
                List<Node> neighbors = nodes[i];
                StringBuilder sb = new StringBuilder();
                // Add the first node to control whitespace
                sb.append(neighbors.get(0).getName()).append(" ").append(neighbors.get(0).getDis());
                for (int j = 1; j < neighbors.size(); j++) {
                    Node node = neighbors.get(j);
                    sb.append(" ").append(node.getName()).append(" ").append(node.getDis());
                }
                writer.write(sb.toString());
                writer.write("\r\n");

                if (i > bufferNum * MAX_BUFFERED) {
                    writer.flush();
                    bufferNum++;
                }
            }
            writer.flush();
            writer.close();
        }
    }

    public static void main(String[] args) throws BaseException, IOException {
        new dataProcess(MapInfo.COL).transform();
    }

}
