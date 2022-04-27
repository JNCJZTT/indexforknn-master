package com.index.indexforknn.base.service.utils;

import com.index.indexforknn.base.common.BaseException;
import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.domain.Car;
import com.index.indexforknn.base.domain.Vertex;
import com.index.indexforknn.base.service.api.IVariableService;
import com.index.indexforknn.base.service.factory.ServiceFactory;
import com.index.indexforknn.base.service.factory.SpringBeanFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * File Util
 * 2022/2/12 zhoutao
 */
@Slf4j
@Component
public class FileUtil {
    private final static int MAX_BUFFERED = 10000;

    private IVariableService variableService;

    /**
     * read Files
     */
    public void readFiles() throws BaseException, IOException {
        variableService = ServiceFactory.getVariableService();

        readVertexFile();
        readEdgeFile();
        List vertices = variableService.getVertices();

        SpringBeanFactory.getBean(DistributionUtil.class).initDistribute(vertices);
        readCarFile(vertices);
    }

    private void readVertexFile() throws IOException, BaseException {
        InputStreamReader read = new InputStreamReader(new FileInputStream(GlobalVariable.vertexUrl));
        BufferedReader bufferedReader = new BufferedReader(read);

        for (int i = 0; i < GlobalVariable.VERTEX_NUM; i++) {
            String clusterName = bufferedReader.readLine();
            if (!StringUtils.hasLength(clusterName)) {
                log.error("Read Vertex File Error! Line {} is Empty", i);
                throw new BaseException("read File Exception");
            }
            variableService.buildVertex(i, clusterName);
        }
        read.close();
    }

    private void readEdgeFile() throws IOException, BaseException {
        InputStreamReader read = new InputStreamReader(new FileInputStream(GlobalVariable.edgeUrl));
        BufferedReader bufferedReader = new BufferedReader(read);

        for (int i = 0; i < GlobalVariable.VERTEX_NUM; i++) {
            String line = bufferedReader.readLine();
            if (!StringUtils.hasLength(line)) {
                log.error("Read Edge File Error! Line {} is Empty", i);
                throw new BaseException("read File Exception");
            }
            String[] s = line.split("\\s+");
            variableService.buildEdge(i, s);
        }
        read.close();
    }

    private void readCarFile(List vertices) throws IOException {
        GlobalVariable.CARS = new ArrayList<>();

        File file = new File(GlobalVariable.carUrl);
        if (!file.exists()) {
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
            BufferedWriter writer = new BufferedWriter(fileWriter);
            int bufferNum = 1;
            for (int i = 0; i < GlobalVariable.CAR_NUM; i++) {
                int activeName = DistributionUtil.getVertexName(),
                        activeDis = ((Vertex) vertices.get(activeName)).getRandomEdge().getDis();

                GlobalVariable.CARS.add(
                        new Car().setName(i)
                                .setActive(activeName)
                                .setActiveDis(activeDis)
                );
                writer.write(i + " " + activeName + " " + activeDis);
                writer.write("\r\n");

                if (i > bufferNum * MAX_BUFFERED) {
                    writer.flush();
                    bufferNum++;
                }
            }
            writer.flush();
            writer.close();
        } else {
            InputStreamReader read = new InputStreamReader(new FileInputStream(GlobalVariable.carUrl));
            BufferedReader bufferedReader = new BufferedReader(read);
            for (int i = 0; i < GlobalVariable.CAR_NUM; i++) {
                String[] Line = bufferedReader.readLine().split("\\s+");

                GlobalVariable.CARS.add(new Car().setName(Integer.parseInt(Line[0]))
                        .setActive(Integer.parseInt(Line[1]))
                        .setActiveDis(Integer.parseInt(Line[2])));
            }
            read.close();
        }
    }


}
