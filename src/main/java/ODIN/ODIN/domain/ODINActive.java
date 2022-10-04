package ODIN.ODIN.domain;

import ODIN.base.domain.Node;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * ODINActive
 * 2022/2/17 zhoutao
 */
@Getter
@Setter
public class ODINActive {


    private String currentClusterName = null;

    private Map<String, List<Node>> highestBorderInfo;

    public ODINActive(ODINVertex activeVertex) {
        highestBorderInfo = new HashMap<>();

    }


}
