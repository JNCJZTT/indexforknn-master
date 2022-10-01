package com.index.indexforknn.ahg.common.status;

import lombok.NoArgsConstructor;

/**
 * Juge cluster whether a suitable active cluster based on active nodes size
 * 2022/4/25 zhoutao
 */
@NoArgsConstructor
public enum AhgClusterUpdateStatus {
    NEED_TO_MERGE,
    NEED_TO_SPLIT,
    SUITABLE;
}
