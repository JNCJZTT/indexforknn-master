package com.index.indexforknn.base.service;

import com.index.indexforknn.base.common.BaseException;
import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.api.IVariableService;
import com.index.indexforknn.base.service.dto.IndexDTO;
import com.index.indexforknn.base.service.factory.IndexServiceFactory;
import com.index.indexforknn.base.service.factory.SpringBeanFactory;
import com.index.indexforknn.base.service.factory.VariableServiceFactory;
import com.index.indexforknn.base.service.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * TODO
 * 2022/3/20 zhoutao
 */
@Slf4j
@Service
public abstract class IndexService {

    protected IVariableService variableService;

    public void buildIndex(IndexDTO indexDTO) {
        initVariables(indexDTO);
        build();
    }

    /**
     * 初始化变量,以及读取文件
     */
    private void initVariables(IndexDTO index) {
        variableService.initVariable(index);
        try {
            SpringBeanFactory.getBean(FileUtil.class).readFile();
        } catch (IOException | BaseException ioException) {
            log.error("读取文件有误！ " + ioException);
        } finally {
            log.info("vertex Num={}", variableService.getVertexSize());
            log.info("cluster Num={}", variableService.getClusterSize());
        }
    }

    protected abstract void build();

    protected abstract IndexType supportType();

    protected void register() {
        IndexServiceFactory.register(supportType(), this);
    }

}
