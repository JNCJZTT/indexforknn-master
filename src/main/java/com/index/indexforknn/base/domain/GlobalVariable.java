package com.index.indexforknn.base.domain;

import com.index.indexforknn.base.common.constants.Constants;
import com.index.indexforknn.base.domain.api.Variable;
import com.index.indexforknn.base.domain.enumeration.Distribution;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.domain.enumeration.MapInfo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

/**
 * GlobalVariable
 * 2022/2/11 zhoutao
 */
@Component
@Slf4j
@Getter
public class GlobalVariable {
    public static final Random RANDOM = new Random();

    // vertex num
    public static int VERTEX_NUM;

    // the max vertex num in sub-graph
    public static int SUB_GRAPH_SIZE;

    // index type
    public static IndexType INDEX_TYPE;

    // branch
    public static int BRANCH;

    // map
    public static MapInfo MAP_INFO;

    // distribute
    public static Distribution DISTRIBUTE;

    // k
    public static int K;

    // car-num
    public static int CAR_NUM;

    // cars
    public static List<Car> CARS;

    public static int DIGIT;

    public static Variable variable;

    public static String vertexUrl = Constants.BASE_URL + "{0}/USA-road-d.{0}.branch-{1}.avg-{2}.txt";

    public static String edgeUrl = Constants.BASE_URL + "{0}/{0}_Edge.txt";

    public static String carUrl = Constants.BASE_URL + "{0}/CarFile.{0}.CarNum-{1}.Distribute-{2}.txt";

}
