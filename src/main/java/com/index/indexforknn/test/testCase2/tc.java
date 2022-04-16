package com.index.indexforknn.test.testCase2;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * TODO
 * 2022/4/1 zhoutao
 */
public class tc {
    private static String testPath = "/Users/zhoutao/Documents/kNNIndexData/NY/USA-road-d.NY.branch-4.avg-50.txt";

    private Map<String, List<Integer>> map = new HashMap<>();

    public void testData() throws IOException {
        InputStreamReader read = new InputStreamReader(new FileInputStream(testPath));
        BufferedReader bufferedReader = new BufferedReader(read);
        String line;
        int index = 0;
        while (true) {
            line = bufferedReader.readLine();
            if (line == null) {
                break;
            }
            if (!map.containsKey(line)) {
                map.put(line, new ArrayList<>());
            }
            map.get(line).add(index);
            index++;
        }
        read.close();

        for (String s : map.keySet()) {
            if (s == null) {
                continue;
            }
            String parent = s.substring(0, s.lastIndexOf(","));
            if (map.containsKey(parent)) {
                System.out.println(s);
                System.out.println(parent);
                System.out.println(map.get(parent));
                System.out.println("error!");
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new tc().testData();
    }
}
