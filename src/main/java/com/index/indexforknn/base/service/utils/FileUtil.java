package com.index.indexforknn.base.service.utils;

import com.index.indexforknn.base.common.BaseException;
import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.domain.Car;
import com.index.indexforknn.base.domain.Vertex;
import com.index.indexforknn.base.service.api.IVariableService;
import com.index.indexforknn.base.service.factory.SpringBeanFactory;
import com.index.indexforknn.base.service.factory.VariableServiceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件工具类
 * 2022/2/12 zhoutao
 */
@Slf4j
@Component
public class FileUtil {
    // 最大缓冲区容量
    private final static int MAX_BUFFERED = 10000;

    private IVariableService variableService;

    public void readFile() throws BaseException, IOException {
        variableService = VariableServiceFactory.getVariableService();

        log.info("开始读取节点文件");
        readVertexFile();
        log.info("开始读取边文件");
        readEdgeFile();

        log.info("开始初始化{}分布", GlobalVariable.DISTRIBUTE.name());
        List vertices = variableService.getVertices();

        SpringBeanFactory.getBean(DistributionUtil.class).initDistribute(vertices);
        log.info("开始读取移动对象文件");
        readCarFile(vertices);
    }

    /**
     * 读取结点文件
     */
    public void readVertexFile() throws IOException, BaseException {
        InputStreamReader read = new InputStreamReader(new FileInputStream(GlobalVariable.vertexUrl));
        BufferedReader bufferedReader = new BufferedReader(read);

        for (int i = 0; i < GlobalVariable.VERTEX_NUM; i++) {
            String clusterName = bufferedReader.readLine();
            if (!StringUtils.hasLength(clusterName)) {
                log.error("Read Vertex File Error! Line {} is Empty", i);
                throw new BaseException("read File Exception");
            }
            // 创建结点和簇
            variableService.buildVertex(i, clusterName);
        }
        read.close();
    }

    /**
     * 读取边文件
     */
    public void readEdgeFile() throws IOException, BaseException {
        InputStreamReader read = new InputStreamReader(new FileInputStream(GlobalVariable.edgeUrl));
        BufferedReader bufferedReader = new BufferedReader(read);

        for (int i = 0; i < GlobalVariable.VERTEX_NUM; i++) {
            String line = bufferedReader.readLine();
            if (!StringUtils.hasLength(line)) {
                log.error("Read Edge File Error! Line {} is Empty", i);
                throw new BaseException("read File Exception");
            }

            // 构建边
            String[] s = line.split("\\s+");
            variableService.buildEdge(i, s);
        }
        read.close();
    }

    /**
     * 读取移动对象文件
     */
    public void readCarFile(List vertices) throws IOException {
        GlobalVariable.CARS = new ArrayList<>();

        File file = new File(GlobalVariable.carUrl);
        if (!file.exists()) {
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
            BufferedWriter writer = new BufferedWriter(fileWriter);
            int bufferNum = 1;    //防止缓冲区满
            for (int i = 0; i < GlobalVariable.CAR_NUM; i++) {
                int activeName = DistributionUtil.getVertexName(),
                        activeDis = ((Vertex) vertices.get(activeName)).getRandomEdge().getDis();

                GlobalVariable.CARS.add(
                        new Car().setName(i)
                                .setActive(activeName)
                                .setActiveDis(activeDis)
                );
                // optim: name可以直接按行
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
