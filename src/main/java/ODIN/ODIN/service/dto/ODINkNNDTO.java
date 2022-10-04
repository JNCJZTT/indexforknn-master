package ODIN.ODIN.service.dto;

import ODIN.base.service.dto.KnnDTO;
import lombok.Data;


/**
 * TODO
 * 2022/9/11 zhoutao
 */
@Data
public class ODINkNNDTO extends KnnDTO {
    /**
     * 1 : node Query
     * 2 : Edge Query
     */
    private int queryType;

    private int querySize;

    private boolean reset;
}
