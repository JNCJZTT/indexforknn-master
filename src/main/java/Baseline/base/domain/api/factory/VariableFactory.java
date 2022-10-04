package Baseline.base.domain.api.factory;

import Baseline.ERkNN.domain.ERkNNVariable;
import Baseline.base.domain.GlobalVariable;
import Baseline.base.domain.api.Variable;
import Baseline.base.domain.enumeration.IndexType;
import Baseline.SGrid.domain.SGridVariable;
import Baseline.SIMkNN.domain.SIMkNNVariable;
import Baseline.TenIndex.domain.TenIndexVariable;
import Baseline.VTree.domain.VtreeVariable;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 * 2022/5/22 zhoutao
 */
public class VariableFactory {
    private static final Map<IndexType, Variable> variableFactory = new HashMap<>();

    static {
        variableFactory.put(IndexType.SGrid, SGridVariable.INSTANCE);
        variableFactory.put(IndexType.VTree, VtreeVariable.INSTANCE);
        variableFactory.put(IndexType.SIMkNN, SIMkNNVariable.INSTANCE);
        variableFactory.put(IndexType.ERkNN, ERkNNVariable.INSTANCE);
        variableFactory.put(IndexType.TenIndex, TenIndexVariable.INSTANCE);
    }

    public static Variable getVariable() {
        return variableFactory.get(GlobalVariable.INDEX_TYPE);
    }

}
