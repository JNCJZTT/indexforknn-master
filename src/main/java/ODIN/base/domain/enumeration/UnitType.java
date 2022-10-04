package ODIN.base.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Unit Type
 * 2022/2/16 zhoutao
 */
@AllArgsConstructor
@Getter
public enum UnitType {
    Second(1000_000_000, "s"),
    MilliSecond(1000_000, "ms"),
    microseconds(1000, "us"),

    B(1, "B"),
    KB(1000, "KB"),
    MB(1000_000, "MB"),
    GB(1000_000_000, "GB");

    private int quantity;

    private String name;
}
