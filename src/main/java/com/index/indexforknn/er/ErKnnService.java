package com.index.indexforknn.er;

import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.api.IKnnService;
import com.index.indexforknn.base.service.dto.KnnDTO;
import com.index.indexforknn.base.service.dto.result.KnnResultDTO;
import com.index.indexforknn.base.service.utils.DistributionUtil;
import com.index.indexforknn.er.domain.ErKnn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Slf4j
public class ErKnnService implements IKnnService {
    ErKnn erKnn;

    public ErKnnService(){
        register();
    }

    @Override
    public void initKnn(int queryName) {
        erKnn = new ErKnn(queryName);
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
        return IndexType.ER;
    }
}
