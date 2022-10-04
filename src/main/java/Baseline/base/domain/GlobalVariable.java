package Baseline.base.domain;

import Baseline.base.common.constants.Constants;
import Baseline.base.domain.api.Variable;
import Baseline.base.domain.enumeration.Distribution;
import Baseline.base.domain.enumeration.IndexType;
import Baseline.base.domain.enumeration.MapInfo;
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

    public static int COMPUTE_NUM;

    public static Variable variable;

    public static String vertexUrl = Constants.BASE_URL + "{0}/METIS-{0}-{1}branches-{2}.txt";

    public static String edgeUrl = Constants.BASE_URL + "{0}/original-{0}.txt";

    public static String carUrl = Constants.BASE_URL + "{0}/ObjectsFile.{0}.ObjectsNum-{1}.Distribute-{2}.txt";

}
