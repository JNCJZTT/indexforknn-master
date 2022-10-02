package com.index.indexforknn.base.domain.api.factory;

import com.index.indexforknn.ahg.domain.AhgVariable;
import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.domain.api.Variable;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.sgrid.domain.SGridVariable;
import com.index.indexforknn.tenstar.domain.TenStarVariable;
import com.index.indexforknn.vtree.domain.VtreeVariable;
import com.index.indexforknn.er.domain.ErVariable;
import com.index.indexforknn.sim.domain.SimVariable;


import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 * 2022/5/22 zhoutao
 */
public class VariableFactory {
    private static final Map<IndexType, Variable> variableFactory = new HashMap<>();

    static {
        variableFactory.put(IndexType.AHG, AhgVariable.INSTANCE);
        variableFactory.put(IndexType.SGRID, SGridVariable.INSTANCE);
        variableFactory.put(IndexType.VTREE, VtreeVariable.INSTANCE);
        variableFactory.put(IndexType.SIM, SimVariable.INSTANCE);
        variableFactory.put(IndexType.ER, ErVariable.INSTANCE);
        variableFactory.put(IndexType.TENSTAR, TenStarVariable.INSTANCE);
    }

    public static Variable getVariable() {
        return variableFactory.get(GlobalVariable.INDEX_TYPE);
    }

}
