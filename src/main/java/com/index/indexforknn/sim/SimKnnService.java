package com.index.indexforknn.sim;

import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.api.IKnnService;
import com.index.indexforknn.base.service.dto.KnnDTO;
import com.index.indexforknn.base.service.dto.result.KnnResultDTO;
import com.index.indexforknn.sim.domain.SimKnn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SimKnnService implements IKnnService {
    SimKnn simKnn;

    public SimKnnService(){
        register();
    }

    @Override
    public void initKnn(int queryName) {
        simKnn = new SimKnn(queryName);
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
        return IndexType.SIM;
    }
}
