package Baseline.base.service.api;

import Baseline.base.common.BaseException;
import Baseline.base.domain.GlobalVariable;
import Baseline.base.domain.enumeration.IndexType;
import Baseline.base.service.dto.IndexDTO;
import Baseline.base.service.dto.UpdateDTO;
import Baseline.base.service.dto.result.IndexResultDTO;
import Baseline.base.service.dto.result.ResultDTO;
import Baseline.base.service.factory.ServiceFactory;
import Baseline.base.service.factory.SpringBeanFactory;
import Baseline.base.service.graph.CarService;
import Baseline.base.service.utils.FileUtil;
import Baseline.base.service.utils.MemoryUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.util.RamUsageEstimator;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * IndexService
 * 2022/3/20 zhoutao
 */
@Slf4j
@Service
public abstract class IndexService implements IBaseService {

    protected IVariableService variableService;

    protected CarService carService;

    protected double buildTime;

    protected double memoryConsumed;

    protected double updateTime;

    /**
     * build index
     *
     * @param indexDTO indexDTO
     */
    public void buildIndex(IndexDTO indexDTO) {
        initVariables(indexDTO);

        // record the memory and time
        long memoryBefore = MemoryUtil.getUsedMemory();
        long startTime = System.nanoTime();

        build();

        buildTime = (System.nanoTime() - startTime);
//        memoryConsumed = (MemoryUtil.getUsedMemory() - memoryBefore);
        memoryConsumed = RamUsageEstimator.sizeOf(GlobalVariable.variable.getVertices()) + RamUsageEstimator.sizeOf(GlobalVariable.variable.getClusters());
//        log.info("the cost memory of vertices:" + RamUsageEstimator.humanSizeOf(GlobalVariable.variable.getVertices()));
//        log.info("the cost memory of clusters:" + RamUsageEstimator.humanSizeOf(GlobalVariable.variable.getClusters()));
        log.info("Build Done!");
    }

    /**
     * init Variable and read files
     *
     * @param index indexDTO
     */
    private void initVariables(IndexDTO index) {
        variableService = ServiceFactory.getVariableService();
        variableService.initVariable(index);
        carService = ServiceFactory.getCarService();
        try {
            SpringBeanFactory.getBean(FileUtil.class).readFiles();
        } catch (IOException | BaseException ioException) {
            log.error("Read File Error！ " + ioException);
        } finally {
            log.info("vertex Num={}", GlobalVariable.variable.getVerticeSize());
            log.info("cluster Num={}", GlobalVariable.variable.getClusterSize());
        }
    }

    /**
     * build index in details
     */
    protected abstract void build();

    /**
     * update car location
     *
     * @param updateDTO updateDTO
     */
    public void updateCar(UpdateDTO updateDTO) {
        carService.batchUpdateRandomCarLocation(updateDTO);
        updateTime = System.nanoTime();

        update();

        updateTime = System.nanoTime() - updateTime;
        log.info("Update Done!");
    }

    /**
     * updateIndex
     */
    protected abstract void update();

    /**
     * build Result
     *
     * @param indexDTO indexDTO
     */
    public ResultDTO buildResult(IndexDTO indexDTO) {
        IndexResultDTO resultDTO = new IndexResultDTO();
        resultDTO.buildResult(buildTime, memoryConsumed, indexDTO);
        return resultDTO;
    }

    public abstract IndexType supportType();

    public void register() {
        ServiceFactory.register(supportType(), this);
    }

    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        ;
        System.out.println(RamUsageEstimator.sizeOf(map));
    }

}
