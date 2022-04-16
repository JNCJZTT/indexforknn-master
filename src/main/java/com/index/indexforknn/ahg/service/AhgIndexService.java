package com.index.indexforknn.ahg.service;

import com.index.indexforknn.ahg.common.AhgConstants;
import com.index.indexforknn.ahg.domain.AhgCluster;
import com.index.indexforknn.ahg.domain.AhgVariable;
import com.index.indexforknn.ahg.service.graph.AhgActiveService;
import com.index.indexforknn.ahg.service.graph.AhgClusterService;
import com.index.indexforknn.ahg.service.graph.AhgVariableService;
import com.index.indexforknn.ahg.service.graph.AhgVertexService;
import com.index.indexforknn.ahg.service.utils.Trie;
import com.index.indexforknn.base.common.BaseException;
import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.domain.Car;
import com.index.indexforknn.base.domain.annotation.CostTime;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.domain.enumeration.TimeType;
import com.index.indexforknn.base.service.IndexService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;


/**
 * TODO
 * 2022/2/10 zhoutao
 */
@Slf4j
@Service
public class AhgIndexService extends IndexService {

    @Autowired
    private AhgVertexService vertexService;

    @Autowired
    private AhgClusterService clusterService;

    @Autowired
    private AhgActiveService activeService;

    public AhgIndexService() {
        register();
        variableService = new AhgVariableService();
    }

    /**
     * 构建索引
     */
    @CostTime(msg = "构建时间", timeType = TimeType.Second)
    @Override
    public void build() {
        // 构建边界点
        vertexService.buildBorders();
        // 计算子图
        clusterService.computeClusters();
        // 构建兴趣点
        activeService.buildActive();
        // 构建虚拟路网
        buildVirtualMap();
        log.info("Build Done!");
    }

    @Override
    protected IndexType supportType() {
        return IndexType.AHG;
    }

    /**
     * 更新索引
     */
    private void updateIndex() {

    }

    /**
     * 删除索引
     */
    private void clearIndex() {

    }

    /**
     * 构建虚拟路网
     */
    @CostTime(msg = "构建虚拟路网", timeType = TimeType.MilliSecond)
    private void buildVirtualMap() {

        int sum = 0;
        for (AhgCluster cluster : AhgVariable.clusters.values()) {
            if (cluster.isLeaf()) {
                sum += cluster.getActiveNames().size();
            }
        }
        log.info("all active Num in Leaf={}", sum);

        Queue<String> queue = new LinkedList<>(AhgVariable.INSTANCE.getClusterKeySet());
        // 构建全量树的key
        AhgVariable.INSTANCE.buildFullTreeKey();

        Trie trie = new Trie();
        while (!queue.isEmpty()) {
            String clusterName = queue.poll();
            if (trie.isProcessed(clusterName)) {
                continue;
            }

            AhgCluster cluster = AhgVariable.INSTANCE.getCluster(clusterName);

            while (cluster.isSuitableCluster() == AhgConstants.NEED_TO_MERGE) {
                clusterName = cluster.getParentName();
                // 如果parentCluster不存在，创建parentCluster
                if (!AhgVariable.INSTANCE.containsClusterValue(clusterName)) {
                    AhgVariable.INSTANCE.addCluster(clusterName, new AhgCluster(clusterName, false));
                }
                cluster = AhgVariable.INSTANCE.getCluster(clusterName);
            }
            trie.insert(clusterName);
            cluster.buildVirtualMap();
        }
    }


}
