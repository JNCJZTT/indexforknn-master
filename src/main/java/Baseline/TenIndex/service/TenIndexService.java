package Baseline.TenIndex.service;

import Baseline.TenIndex.domain.TenIndexVariable;
import Baseline.TenIndex.domain.TenIndexVertex;
import Baseline.TenIndex.service.dto.TenIndexUpdateProcessDTO;
import Baseline.TenIndex.service.graph.TenIndexActiveService;
import Baseline.TenIndex.service.graph.TenIndexCarService;
import Baseline.TenIndex.service.graph.TenIndexVertexService;
import Baseline.base.domain.GlobalVariable;
import Baseline.base.domain.api.Vertex;
import Baseline.base.domain.enumeration.IndexType;
import Baseline.base.service.api.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * TODO
 * 2022/9/30 zhoutao
 */
@Service
public class TenIndexService extends IndexService {
    @Autowired
    private TenIndexVertexService vertexService;

    @Autowired
    private TenIndexActiveService activeService;

    private Stack<Integer> treeStack;

    private TenIndexVertex root;

    public TenIndexService() {
        register();
    }

    @Override
    protected void build() {
        buildStack();
        buildTree();
        activeService.buildActive();
        buildCKNN();
    }

    @Override
    protected void update() {
        TenIndexUpdateProcessDTO processDTO = ((TenIndexCarService) carService).getUpdateProcessDTO();
        for (Integer name : processDTO.getChange2InActiveSet()) {
            TenIndexVariable.INSTANCE.getVertex(name).buildCKNN();
        }
    }

    @Override
    public IndexType supportType() {
        return IndexType.TenIndex;
    }


    private void buildStack() {
        treeStack = new Stack<>();
        int minDegree = 1;
        HashSet<Integer> allKeys = TenIndexVariable.INSTANCE.getVertices()
                .stream().map(Vertex::getName).collect(Collectors.toCollection(HashSet::new));

        while (!allKeys.isEmpty()) {
            Iterator<Integer> allKeysIter = allKeys.iterator();

            while (allKeysIter.hasNext()) {
                Integer vertexName = allKeysIter.next();
                TenIndexVertex vertex = TenIndexVariable.INSTANCE.getVertex(vertexName);
                if (vertex.getDegree() <= minDegree) {
                    vertexService.buildClique(vertex);

                    treeStack.add(vertexName);
                    allKeysIter.remove();
                }
            }
            minDegree++;
        }
    }

    private void buildTree() {

        root = TenIndexVariable.INSTANCE.getVertex(treeStack.pop());
        root.setRoot();

        while (!treeStack.empty()) {

            Integer vertexName = treeStack.pop();
            TenIndexVertex vertex = TenIndexVariable.INSTANCE.getVertex(vertexName);
            vertex.setParent();
        }
    }

    private void buildCKNN() {
        HashSet<Integer> allKeys = TenIndexVariable.INSTANCE.getVertices()
                .stream().map(Vertex::getName).collect(Collectors.toCollection(HashSet::new));
        int maxTreeLevel = 0;

        for (int i = 0; i < GlobalVariable.VERTEX_NUM; i++) {
            if (TenIndexVariable.INSTANCE.getVertex(i).getTreeLevel() > maxTreeLevel) {
                maxTreeLevel = TenIndexVariable.INSTANCE.getVertex(i).getTreeLevel();
            }
        }
        System.out.println("MaxTreeLevel=" + maxTreeLevel);
        while (!allKeys.isEmpty()) {
            Iterator<Integer> allKeysIter = allKeys.iterator();

            while (allKeysIter.hasNext()) {
                Integer Name = allKeysIter.next();
                TenIndexVertex vertex = TenIndexVariable.INSTANCE.getVertex(Name);

                if (vertex.getTreeLevel() == maxTreeLevel) {

                    vertex.buildCKNN();
                    allKeysIter.remove();
                }
            }
            maxTreeLevel--;
        }
    }


}
