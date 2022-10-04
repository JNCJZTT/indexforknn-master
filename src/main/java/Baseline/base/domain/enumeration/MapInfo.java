package Baseline.base.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * MapInfo
 * 2022/2/10 zhoutao
 */
@AllArgsConstructor
@Getter
public enum MapInfo {
    BJ(2_280_770),
    NY(264_346),
    COL(435_666),
    FLA(1_070_376),
    CAL(1_890_815),
    EUSA(3_598_623),
    CUSA(14_081_816),
    USA(23_947_347);

    private int size;

}
