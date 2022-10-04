package Baseline.base.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TODO
 * 2022/9/11 zhoutao
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Edge {
    private int from;
    private int to;
    private int dis;
}
