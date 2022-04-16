package com.index.indexforknn.base.domain;

import com.index.indexforknn.base.common.constants.Constants;
import com.index.indexforknn.base.domain.enumeration.Distribution;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.domain.enumeration.MapInfo;
import com.index.indexforknn.base.service.dto.IndexDTO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

/**
 * 全局变量
 * 2022/2/11 zhoutao
 */
@Component
@Slf4j
@Getter
public class GlobalVariable {
    public static final Random RANDOM = new Random();

    // 节点数量
    public static int VERTEX_NUM;

    // 划分的每个子图的大小
    public static int SUB_GRAPH_SIZE;

    // 索引的类型
    public static IndexType INDEX_TYPE;

    // 分支数
    public static int BRANCH;

    // 地图
    public static MapInfo MAP_INFO;

    // 分布
    public static Distribution DISTRIBUTE;

    // 移动对象数量
    public static int CAR_NUM;

    public static List<Car> CARS;

    public static String vertexUrl = Constants.BASE_URL + "{0}/USA-road-d.{0}.branch-{1}.avg-{2}.txt";

    public static String edgeUrl = Constants.BASE_URL + "{0}/{0}_Edge.txt";

    public static String carUrl = Constants.BASE_URL + "{0}/CarFile.{0}.CarNum-{1}.Distribute-{2}.txt";

}
