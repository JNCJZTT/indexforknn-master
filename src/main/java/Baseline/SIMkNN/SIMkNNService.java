package Baseline.SIMkNN;

import Baseline.SIMkNN.domain.SIMkNN;
import Baseline.base.domain.enumeration.IndexType;
import Baseline.base.service.api.IKnnService;
import Baseline.base.service.dto.KnnDTO;
import Baseline.base.service.dto.result.KnnResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SIMkNNService implements IKnnService {
    SIMkNN simKnn;

    public SIMkNNService(){
        register();
    }

    @Override
    public void initKnn(int queryName) {
        simKnn = new SIMkNN(queryName);
    }

    @Override
    public void knnSearch(int queryName) {
        initKnn(queryName);
        simKnn.knn();
    }



    public KnnResultDTO buildResult(KnnDTO knnDTO) {
        return buildResult(simKnn, knnDTO);
    }

    @Override
    public IndexType supportType() {
        return IndexType.SIMkNN;
    }
}
