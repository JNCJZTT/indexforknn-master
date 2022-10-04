package ODIN.ODIN.common.status;

import lombok.NoArgsConstructor;

/**
 * ODINActiveStatus
 * cluster active status
 *
 * 2022/4/25 zhoutao
 */
@NoArgsConstructor
public enum ODINActiveStatus {
    PARENT_ACTIVE,
    SON_ACTIVE,
    CURRENT_ACTIVE;
}
