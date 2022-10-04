package Baseline.ERkNN;

import Baseline.ERkNN.domain.ERkNN;
import Baseline.base.domain.GlobalVariable;
import Baseline.base.domain.enumeration.IndexType;
import Baseline.base.service.api.IKnnService;
import Baseline.base.service.dto.KnnDTO;
import Baseline.base.service.dto.result.KnnResultDTO;
import Baseline.base.service.utils.DistributionUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ERkNNService implements IKnnService {
    ERkNN erKnn;

    public ERkNNService(){
        register();
    }

    @Override
    public void initKnn(int queryName) {
        erKnn = new ERkNN(queryName);
    }

    @Override
    public void knnSearch(int queryName) {

        if(queryName==-1){
            ArrayList<Integer> list1 = new ArrayList<>(DistributionUtil.getRandomVertexList());
            long startKnn = System.nanoTime();
//            list.parallelStream().forEach(integer -> knnService.knnSearch(integer));
            long endKnn=0;
            for (int i:list1){
                initKnn(i);
                long start = System.nanoTime();
                erKnn.knn();
                long end = (long) ((System.nanoTime() - start) / 1000.0 / GlobalVariable.COMPUTE_NUM);
                endKnn += end;
//                System.out.println(end);
            }
            long endKnnt= (long) ((System.nanoTime() - startKnn) / 1000.0);

            erKnn.setQueryTime(endKnn);
//            System.out.println("querytime1"+endKnn+"us");
//            System.out.println("querytime2"+endKnnt+"us");
        }else {
            initKnn(queryName);
            erKnn.knn();
        }


    }




    public KnnResultDTO buildResult(KnnDTO knnDTO) {
        return buildResult(erKnn, knnDTO);
    }

    @Override
    public IndexType supportType() {
        return IndexType.ERkNN;
    }
}
