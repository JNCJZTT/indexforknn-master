package com.index.indexforknn.base.service.factory;

import com.index.indexforknn.ahg.service.AhgIndexService;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.IndexService;
import com.index.indexforknn.base.service.api.IVariableService;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 * 2022/3/20 zhoutao
 */
public class IndexServiceFactory {
    private static Map<IndexType, IndexService> indexServiceFactory = new HashMap<>();

    public static void register(IndexType indexType, IndexService indexService) {
        if (indexType != null && !indexServiceFactory.containsKey(indexType)) {
            indexServiceFactory.put(indexType, indexService);
        }
    }

    public static <T> T getIndexService(IndexType indexType) {
        return (T) indexServiceFactory.get(indexType);
    }
}
