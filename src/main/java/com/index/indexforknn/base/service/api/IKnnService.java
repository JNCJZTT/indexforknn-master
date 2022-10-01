package com.index.indexforknn.base.service.api;

import com.index.indexforknn.base.domain.api.knn.Knn;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.dto.KnnDTO;
import com.index.indexforknn.base.service.dto.result.KnnResultDTO;
import com.index.indexforknn.base.service.factory.ServiceFactory;
import org.springframework.stereotype.Service;

/**
 * IKnnService
 * 2022/5/14 zhoutao
 */
@Service
public interface IKnnService extends IBaseService {
    void initKnn(int queryName);

    void knnSearch(int queryName);

    KnnResultDTO buildResult(KnnDTO knnDTO);

    default KnnResultDTO buildResult(Knn knn, KnnDTO knnDTO) {
        KnnResultDTO resultDTO = new KnnResultDTO();
        resultDTO.buildResult(knn, knnDTO);
        return resultDTO;
    }

    IndexType supportType();

    default void register() {
        ServiceFactory.register(supportType(), this);
    }
}
