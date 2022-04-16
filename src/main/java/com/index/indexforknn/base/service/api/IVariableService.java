package com.index.indexforknn.base.service.api;

import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.dto.IndexDTO;
import com.index.indexforknn.base.service.factory.VariableServiceFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * TODO
 * 2022/3/12 zhoutao
 */
@Service
public interface IVariableService {

    /**
     * 初始化变量
     */
    void initVariable(IndexDTO index);

    /**
     * 构建结点
     */
    void buildVertex(int vertexName, String clusterName);

    /**
     * 构建边
     */
    void buildEdge(int vertexName, String edgeInfo[]);

    /**
     * 获得结点的数量
     */
    int getVertexSize();

    /**
     * 获得子图的数量
     */
    int getClusterSize();

    List getVertices();

    IndexType supportType();

    default void register() {
        VariableServiceFactory.register(supportType(), this);
    }

}
