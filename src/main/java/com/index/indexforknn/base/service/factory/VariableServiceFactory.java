package com.index.indexforknn.base.service.factory;

import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.api.IVariableService;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 * 2022/3/12 zhoutao
 */
public class VariableServiceFactory {
    private static Map<IndexType, IVariableService> variableServiceFactory = new HashMap<>();

//    static {
//        variableServiceFactory.put(IndexType.AHG, new AhgVariableService());
//    }

    public static void register(IndexType indexType, IVariableService variableService) {
        if (indexType != null && !variableServiceFactory.containsKey(indexType)) {
            variableServiceFactory.put(indexType, variableService);
        }
    }

    public static <T> T getVariableService() {
        return (T) variableServiceFactory.get(GlobalVariable.INDEX_TYPE);
    }
}
