package Baseline.base.service.api;

import Baseline.base.domain.GlobalVariable;
import Baseline.base.domain.api.factory.VariableFactory;
import Baseline.base.domain.enumeration.IndexType;
import Baseline.base.service.dto.IndexDTO;
import Baseline.base.service.factory.ServiceFactory;
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
