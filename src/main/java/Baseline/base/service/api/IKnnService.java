package Baseline.base.service.api;

import Baseline.base.domain.api.knn.Knn;
import Baseline.base.domain.enumeration.IndexType;
import Baseline.base.service.dto.KnnDTO;
import Baseline.base.service.dto.result.KnnResultDTO;
import Baseline.base.service.factory.ServiceFactory;
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
