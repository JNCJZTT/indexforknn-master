package com.index.indexforknn.amt.common.status;

import lombok.NoArgsConstructor;

/**
 * Juge cluster whether a suitable active cluster based on active nodes size
 * 2022/4/25 zhoutao
 */
@NoArgsConstructor
public enum AmtClusterUpdateStatus {
    NEED_TO_MERGE,
    NEED_TO_SPLIT,
    SUITABLE;
}
