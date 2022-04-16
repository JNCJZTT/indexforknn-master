package com.index.indexforknn.ahg.service;

import com.index.indexforknn.ahg.domain.AhgCluster;
import com.index.indexforknn.ahg.domain.AhgKnn;
import com.index.indexforknn.ahg.domain.AhgVariable;
import com.index.indexforknn.ahg.service.graph.AhgClusterService;
import com.index.indexforknn.base.domain.Car;
import com.index.indexforknn.base.domain.GlobalVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * TODO
 * 2022/4/15 zhoutao
 */
@Service
public class AhgKnnService {
    AhgKnn ahgKnn;

    @Autowired
    AhgClusterService clusterService;

    /**
     * 初始化AhgKNN
     *
     * @param queryName:查询点
     */
    private void initAhgKnn(int queryName) {
        ahgKnn = AhgKnn.builder()
                .kCars(new PriorityQueue<>(new Car.QueryDisComprator()))
                .current(new PriorityQueue<>(new AhgKnn.QueryArrayDisComprator()))
                .nearestName(queryName)
                .searchLimit(Integer.MAX_VALUE)
                .build();

        AhgKnn.queryArray = new int[GlobalVariable.VERTEX_NUM];
        AhgKnn.queryArray[queryName] = 0;

        // 将查询点添加到虚拟路网中
        String leafClusterName = AhgVariable.vertices.get(queryName).getClusterName();


    }

    public void knnSearch(int queryName) {
        initAhgKnn(queryName);
        //fas as

    }
}
