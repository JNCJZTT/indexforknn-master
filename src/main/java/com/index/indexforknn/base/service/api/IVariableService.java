package com.index.indexforknn.base.service.api;

import com.index.indexforknn.base.domain.api.factory.VariableFactory;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.dto.IndexDTO;
import com.index.indexforknn.base.service.factory.ServiceFactory;
import org.springframework.stereotype.Service;

/**
 * IVariableService
 * 2022/3/12 zhoutao
 */
@Service
public interface IVariableService extends IBaseService {

    /**
     * init variables
     */
    default void initVariable(IndexDTO index){
        VariableFactory.getVariable().initVariables(index);
    }

    /**
     * build vertex
     */
    void buildVertex(int vertexName, String clusterName);

    /**
     * build edge
     */
    void buildEdge(int vertexName, String edgeInfo[]);

    IndexType supportType();

    default void register() {
        ServiceFactory.register(supportType(), this);
    }

}
