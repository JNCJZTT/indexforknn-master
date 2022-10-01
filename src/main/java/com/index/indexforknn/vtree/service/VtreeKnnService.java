package com.index.indexforknn.vtree.service;

import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.api.IKnnService;
import com.index.indexforknn.base.service.dto.KnnDTO;
import com.index.indexforknn.base.service.dto.result.KnnResultDTO;
import com.index.indexforknn.vtree.domain.VtreeKnn;
import org.springframework.stereotype.Service;

/**
 * TODO
 * 2022/9/18 zhoutao
 */
@Service
public class VtreeKnnService implements IKnnService {
    VtreeKnn knn;

    public VtreeKnnService() {
        register();
    }

    @Override
    public void initKnn(int queryName) {
        knn = new VtreeKnn(queryName);
    }

    @Override
    public void knnSearch(int queryName) {
        initKnn(queryName);
        knn.knn();
    }

    @Override
    public KnnResultDTO buildResult(KnnDTO knnDTO) {
        return buildResult(knn, knnDTO);
    }

    @Override
    public IndexType supportType() {
        return IndexType.VTREE;
    }
}
