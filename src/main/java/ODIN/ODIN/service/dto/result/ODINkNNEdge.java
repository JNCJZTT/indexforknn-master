package ODIN.ODIN.service.dto.result;

import ODIN.ODIN.domain.ODINVariable;
import ODIN.base.common.constants.Constants;
import ODIN.base.domain.Edge;
import ODIN.base.domain.GlobalVariable;
import ODIN.base.domain.Node;
import lombok.Data;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TODO
 * 2022/9/11 zhoutao
 */
@Data
@Getter
public class ODINkNNEdge extends Edge {
    private int cur = 0;

    private double queryTime;

    private Set<Integer> kCars;


    public ODINkNNEdge(Edge edge) {
        super(edge.getFrom(), edge.getTo(), edge.getDis());
    }

    public void update() {
        cur += Constants.CAR_SPEED;
        if (cur > getDis()) {
            Node node = ODINVariable.INSTANCE.getVertex(getTo()).getRandomEdge();
            this.setFrom(getTo());
            this.setTo(node.getName());
            this.setDis(node.getDis());
            cur = 0;
        }
    }

    public void setTopKnn() {

        kCars = new HashSet<>();

        Node[] fromNodes = ODINVariable.INSTANCE.getVertex(getFrom()).getKCars();
        if (getDis() == 0) {
            kCars.addAll(Arrays.stream(fromNodes).map(Node::getName
            ).collect(Collectors.toList()));
            return;
        }
        Node[] toNodes = ODINVariable.INSTANCE.getVertex(getTo()).getKCars();
        int off = getDis() - cur;
        int i = 0, j = 0, dis1, dis2;
        Node node1, node2;
        while (GlobalVariable.K > kCars.size()) {
            while (kCars.contains(fromNodes[i].getName())) {
                i++;
            }
            while (kCars.contains(toNodes[j].getName())) {
                j++;
            }
            node1 = fromNodes[i];
            node2 = toNodes[j];
            dis1 = node1.getDis() + cur;
            dis2 = node2.getDis() + off;
            if (node1.getName() == node2.getName()) {
                kCars.add(node1.getName());
                i++;
                j++;
            } else if (dis1 < dis2) {
                kCars.add(node1.getName());
                i++;
            } else {
                kCars.add(node2.getName());
                j++;
            }
        }
    }
}
