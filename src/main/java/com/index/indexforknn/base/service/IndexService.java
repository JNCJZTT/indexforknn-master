package com.index.indexforknn.base.service;

import com.index.indexforknn.base.common.BaseException;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.api.IBaseService;
import com.index.indexforknn.base.service.api.IVariableService;
import com.index.indexforknn.base.service.dto.IndexDTO;
import com.index.indexforknn.base.service.dto.ResultDTO;
import com.index.indexforknn.base.service.dto.UpdateDTO;
import com.index.indexforknn.base.service.factory.ServiceFactory;
import com.index.indexforknn.base.service.factory.SpringBeanFactory;
import com.index.indexforknn.base.service.graph.CarService;
import com.index.indexforknn.base.service.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * IndexService
 * 2022/3/20 zhoutao
 */
@Slf4j
@Service
public abstract class IndexService implements IBaseService {

    protected IVariableService variableService;

    protected CarService carService;

    /**
     * build index
     *
     * @param indexDTO indexDTO
     */
    public void buildIndex(IndexDTO indexDTO) {
        initVariables(indexDTO);
        build();
    }

    /**
     * init Variable and read files
     *
     * @param index indexDTO
     */
    private void initVariables(IndexDTO index) {
        variableService.initVariable(index);
        carService = ServiceFactory.getCarService();
        try {
            SpringBeanFactory.getBean(FileUtil.class).readFiles();
        } catch (IOException | BaseException ioException) {
            log.error("Read File Error！ " + ioException);
        } finally {
            log.info("vertex Num={}", variableService.getVertexSize());
            log.info("cluster Num={}", variableService.getClusterSize());
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
        update();
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
    public abstract ResultDTO buildResult(IndexDTO indexDTO);

    public abstract IndexType supportType();

    public void register() {
        ServiceFactory.register(supportType(), this);
    }

}
