package com.index.indexforknn.ahg.service.graph;

import com.index.indexforknn.ahg.domain.AhgActive;
import com.index.indexforknn.ahg.domain.AhgCluster;
import com.index.indexforknn.ahg.domain.AhgVariable;
import com.index.indexforknn.ahg.domain.AhgVertex;
import com.index.indexforknn.base.domain.Car;
import com.index.indexforknn.base.domain.GlobalVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * TODO
 * 2022/3/26 zhoutao
 */
@Service
public class AhgActiveService {

    @Autowired
    AhgClusterService clusterService;

    /**
     * 构造兴趣点
     */
    public void buildActive() {
        for (Car car : GlobalVariable.CARS) {
            int active = car.getActive();
            AhgVertex activeVertex = AhgVariable.INSTANCE.getVertex(active);

            if (!activeVertex.isActive()) {
                // 兴趣点未激活，创建ActiveInfo
                AhgActive activeInfo = new AhgActive(activeVertex);
                String LeafClusterName = activeVertex.getClusterName();
                AhgCluster leafCluster = AhgVariable.INSTANCE.getCluster(LeafClusterName);

                clusterService.updateHighestBorderInfo(activeInfo, leafCluster);
                leafCluster.addActive(active);

                activeVertex.setActiveInfo(activeInfo);
                activeVertex.setActive(true);
            }
            activeVertex.addCar(car);
        }
    }
}
