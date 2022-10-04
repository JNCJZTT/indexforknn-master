package Baseline.VTree.service.dto;

import Baseline.VTree.domain.VtreeVariable;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * TODO
 * 2022/9/18 zhoutao
 */
public class VTreeUpdateProcessDTO {
    private Set<Integer> change2ActiveSet;
    private Set<Integer> change2InActiveSet;

    public VTreeUpdateProcessDTO() {
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
                .filter(name -> VtreeVariable.INSTANCE.getVertex(name).isActive()).collect(Collectors.toSet());
        changedActiveSet.addAll(change2InActiveSet.stream()
                .filter(name -> !VtreeVariable.INSTANCE.getVertex(name).isActive()).collect(Collectors.toSet()));
        change2ActiveSet = null;
        change2InActiveSet = null;
        return changedActiveSet;
    }
}
