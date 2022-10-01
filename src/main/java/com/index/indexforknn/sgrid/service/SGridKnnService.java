package com.index.indexforknn.sgrid.service;

import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.api.IKnnService;
import com.index.indexforknn.base.service.dto.KnnDTO;
import com.index.indexforknn.base.service.dto.result.KnnResultDTO;
import com.index.indexforknn.sgrid.domain.SGridKnn;
import org.springframework.stereotype.Service;

/**
 * SGridKnnService
 * 2022/5/14 zhoutao
 */
@Service
public class SGridKnnService implements IKnnService {

    SGridKnn sgridKnn;

    public SGridKnnService(){
        register();
    }

    @Override
    public void initKnn(int queryName) {
        sgridKnn = new SGridKnn(queryName);
    }

    @Override
    public void knnSearch(int queryName) {
        initKnn(queryName);
        sgridKnn.knn();
    }


    public KnnResultDTO buildResult(KnnDTO knnDTO) {
        return buildResult(sgridKnn, knnDTO);
    }

    @Override
    public IndexType supportType() {
        return IndexType.SGRID;
    }
}
