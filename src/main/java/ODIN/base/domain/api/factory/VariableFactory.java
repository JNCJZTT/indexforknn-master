package ODIN.base.domain.api.factory;

import ODIN.ODIN.domain.ODINVariable;
import ODIN.base.domain.enumeration.IndexType;
import ODIN.base.domain.GlobalVariable;
import ODIN.base.domain.api.Variable;
//import com.odin.ERknn.domain.ERkNNVariable;
//import com.odin.TenIndex.domain.TenIndexVariable;
//import com.odin.SGrid.domain.SGridVariable;
//import com.odin.SIMknn.domain.SIMkNNVariable;
//import com.odin.vtree.domain.VtreeVariable;


import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 * 2022/5/22 zhoutao
 */
public class VariableFactory {
    private static final Map<IndexType, Variable> variableFactory = new HashMap<>();

    static {
        variableFactory.put(IndexType.ODIN, ODINVariable.INSTANCE);
//        variableFactory.put(IndexType.SGRID, SGridVariable.INSTANCE);
//        variableFactory.put(IndexType.VTREE, VtreeVariable.INSTANCE);
//        variableFactory.put(IndexType.SIMKNN, SIMkNNVariable.INSTANCE);
//        variableFactory.put(IndexType.ERKNN, ERkNNVariable.INSTANCE);
//        variableFactory.put(IndexType.TENINDEX, TenIndexVariable.INSTANCE);
    }

    public static Variable getVariable() {
        return variableFactory.get(GlobalVariable.INDEX_TYPE);
    }

}
