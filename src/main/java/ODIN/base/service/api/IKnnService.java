package ODIN.base.service.api;

import ODIN.base.service.dto.KnnDTO;
import ODIN.base.service.dto.result.KnnResultDTO;
import ODIN.base.service.factory.ServiceFactory;
import ODIN.base.domain.api.knn.Knn;
import ODIN.base.domain.enumeration.IndexType;
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
