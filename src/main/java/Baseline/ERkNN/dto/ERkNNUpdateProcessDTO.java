package Baseline.ERkNN.dto;

import Baseline.ERkNN.domain.ERkNNVariable;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ODINUpdateProcessDTO
 * 2022/4/19 zhoutao
 */
public class ERkNNUpdateProcessDTO {
    private Set<Integer> change2ActiveSet;
    private Set<Integer> change2InActiveSet;

    public ERkNNUpdateProcessDTO() {
        change2ActiveSet = new HashSet<>();
        change2InActiveSet = new HashSet<>();
    }

    public void change2Active(Integer name) {
        change2ActiveSet.add(name);
    }

    public void change2InActive(Integer name) {
        change2InActiveSet.add(name);
    }

    public Set<Integer> mergeChangedActive() {
        Set<Integer> changedActiveSet = change2ActiveSet.stream()
                .filter(name -> ERkNNVariable.INSTANCE.getVertex(name).isActive()).collect(Collectors.toSet());
        changedActiveSet.addAll(change2InActiveSet.stream()
                .filter(name -> !ERkNNVariable.INSTANCE.getVertex(name).isActive()).collect(Collectors.toSet()));
        change2ActiveSet = null;
        change2InActiveSet = null;
        return changedActiveSet;
    }
}
