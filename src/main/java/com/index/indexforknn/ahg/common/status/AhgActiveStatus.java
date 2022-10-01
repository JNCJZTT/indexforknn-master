package com.index.indexforknn.ahg.common.status;

import lombok.NoArgsConstructor;

/**
 * AhgActiveStatus
 * cluster active status
 *
 * 2022/4/25 zhoutao
 */
@NoArgsConstructor
public enum AhgActiveStatus {
    PARENT_ACTIVE,
    SON_ACTIVE,
    CURRENT_ACTIVE;
}
