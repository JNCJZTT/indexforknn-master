package ODIN.base.service.factory;

import ODIN.ODIN.service.ODINService;
import ODIN.base.domain.GlobalVariable;
import ODIN.base.domain.enumeration.IndexType;
import ODIN.base.service.api.IndexService;
import ODIN.base.service.api.IKnnService;
import ODIN.base.service.api.IVariableService;
import ODIN.base.service.graph.CarService;

import java.util.HashMap;
import java.util.Map;

/**
 * ServiceFactory
 * 2022/4/23 zhoutao
 */
public class ServiceFactory {
    private static final Map<IndexType, CarService> carServiceFactory = new HashMap<>();

    private static final Map<IndexType, IndexService> indexServiceFactory = new HashMap<>();

    private static final Map<IndexType, IVariableService> variableServiceFactory = new HashMap<>();

    private static final Map<IndexType, IKnnService> knnServiceFactory = new HashMap<>();


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

    public static ODINService getAhgKnnService() {
        return (ODINService) knnServiceFactory.get(IndexType.ODIN);
    }
}
