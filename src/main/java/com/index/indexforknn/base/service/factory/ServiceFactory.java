package com.index.indexforknn.base.service.factory;

import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.api.IndexService;
import com.index.indexforknn.base.service.api.IKnnService;
import com.index.indexforknn.base.service.api.IVariableService;
import com.index.indexforknn.base.service.graph.CarService;

import java.util.HashMap;
import java.util.Map;

/**
 * ServiceFactory
 * 2022/4/23 zhoutao
 */
public class ServiceFactory {
    private static Map<IndexType, CarService> carServiceFactory = new HashMap<>();

    private static Map<IndexType, IndexService> indexServiceFactory = new HashMap<>();

    private static Map<IndexType, IVariableService> variableServiceFactory = new HashMap<>();

    private static Map<IndexType, IKnnService> knnServiceFactory = new HashMap<>();


    public static void register(IndexType indexType, CarService carService) {
        if (indexType != null && !carServiceFactory.containsKey(indexType)) {
            carServiceFactory.put(indexType, carService);
        }
    }

    public static void register(IndexType indexType, IndexService indexService) {
        if (indexType != null && !indexServiceFactory.containsKey(indexType)) {
            indexServiceFactory.put(indexType, indexService);
        }
    }

    public static void register(IndexType indexType, IVariableService variableService) {
        if (indexType != null && !variableServiceFactory.containsKey(indexType)) {
            variableServiceFactory.put(indexType, variableService);
        }
    }

    public static void register(IndexType indexType, IKnnService knnService) {
        if (indexType != null && !knnServiceFactory.containsKey(indexType)) {
            knnServiceFactory.put(indexType, knnService);
        }
    }

    public static <T> T getVariableService() {
        return (T) variableServiceFactory.get(GlobalVariable.INDEX_TYPE);
    }

    public static <T> T getIndexService() {
        return (T) indexServiceFactory.get(GlobalVariable.INDEX_TYPE);
    }

    public static <T> T getCarService() {
        return (T) carServiceFactory.get(GlobalVariable.INDEX_TYPE);
    }

    public static <T> T getKnnService() {
        return (T) knnServiceFactory.get(GlobalVariable.INDEX_TYPE);
    }
}
