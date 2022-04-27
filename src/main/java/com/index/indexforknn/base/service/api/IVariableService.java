package com.index.indexforknn.base.service.api;

import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.base.domain.Vertex;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.dto.IndexDTO;
import com.index.indexforknn.base.service.factory.ServiceFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * IVariableService
 * 2022/3/12 zhoutao
 */
@Service
public interface IVariableService extends IBaseService {

    /**
     * init variables
     */
    void initVariable(IndexDTO index);

    /**
     * build vertex
     */
    void buildVertex(int vertexName, String clusterName);

    /**
     * build edge
     */
    void buildEdge(int vertexName, String edgeInfo[]);

    /**
     * get Vertex Size
     */
    int getVertexSize();

    /**
     * get cluster size
     */
    int getClusterSize();

    /**
     * get vertices
     */
    List getVertices();

    IndexType supportType();

    default void register() {
        ServiceFactory.register(supportType(), this);
    }

}
