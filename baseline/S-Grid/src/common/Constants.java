package common;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public interface Constants {
    String BASE_URL = "/Users/zhoutao/Documents/AHGIndexData/";

    //    String BASE_URL = "/opt/AHGIndexData/";
    String RANDOM_DISTRIBUTE = "Random",
            NORMAL_DISTRIBUTE = "Normal",
            ZIPF_DISTRIBUTE = "Zipf";

    double NORMAL_PER = 0.7;        //正态分布的中心占比

    Random RANDOM = new Random();   //随机

    int CAR_SPEED_LIMIT = 33;

    String MAP_NY = "NY",
            MAP_COL = "COL",
            MAP_CAL = "CAL",
            MAP_E = "E",
            MAP_CTR = "CTR",
            MAP_USA = "USA";

    Map<String, Integer> MAP2VERTEXNUM = new HashMap<>() {
        {
            put(MAP_NY, 264_346);
            put(MAP_COL, 435_666);
            put(MAP_CAL, 1_890_815);
            put(MAP_E, 3_598_623);
            put(MAP_CTR, 14_081_816);
            put(MAP_USA, 23_947_347);
        }
    };

    /*
     * Map Information:
     * Map    VertexSize    EdgeSize      VertexSize/50
     * NY     264,346       733,846        5,286.92
     * COL    435,666       1,057,066      8,713.32
     * CAL    1,890,815     4,657,742      37,816.2
     * E      3,598,623     8,778,114      71,972.46
     * CTR    14,081,816    34,292,496

     * USA    23,947,347    58,333,344     478,8946.94
     *
     * */
}
