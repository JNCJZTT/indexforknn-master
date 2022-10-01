package com.index.indexforknn.base.service.api;

import com.index.indexforknn.ahg.domain.AhgVariable;
import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.domain.api.factory.VariableFactory;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.dto.IndexDTO;
import com.index.indexforknn.base.service.factory.ServiceFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.Queue;

/**
 * IVariableService
 * 2022/3/12 zhoutao
 */
@Service
public interface IVariableService extends IBaseService {

    /**
     * init variables
     */
    default void initVariable(IndexDTO index) {
        VariableFactory.getVariable().initVariables(index);
    }

    /**
     * build vertex
     */
    void buildVertex(int vertexName, String clusterName);

    /**
     * build edge
     */
    void buildEdge(int vertexName, String[] edgeInfo);

    IndexType supportType();

    default void register() {
        ServiceFactory.register(supportType(), this);
    }

    default void buildFullTreeKey() {
        Queue<String> queue = new LinkedList<>(GlobalVariable.variable.getClusterKeySet());
        while (!queue.isEmpty()) {
            String clusterName = queue.poll();
            String parentName;
            while (StringUtils.hasLength(parentName = GlobalVariable.variable.getParentClusterName(clusterName))
                    && !GlobalVariable.variable.containsClusterKey(parentName)) {
                GlobalVariable.variable.addCluster(parentName, null);
                clusterName = parentName;
            }
        }
    }
}
