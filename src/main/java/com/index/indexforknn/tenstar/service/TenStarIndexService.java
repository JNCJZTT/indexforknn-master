package com.index.indexforknn.tenstar.service;

import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.domain.api.Vertex;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.api.IndexService;
import com.index.indexforknn.tenstar.domain.TenStarVariable;
import com.index.indexforknn.tenstar.domain.TenStarVertex;
import com.index.indexforknn.tenstar.service.dto.TenStarUpdateProcessDTO;
import com.index.indexforknn.tenstar.service.graph.TenStarActiveService;
import com.index.indexforknn.tenstar.service.graph.TenStarCarService;
import com.index.indexforknn.tenstar.service.graph.TenStarVertexService;
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
public class TenStarIndexService extends IndexService {
    @Autowired
    private TenStarVertexService vertexService;

    @Autowired
    private TenStarActiveService activeService;

    private Stack<Integer> treeStack;

    private TenStarVertex root;

    public TenStarIndexService() {
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
        TenStarUpdateProcessDTO processDTO = ((TenStarCarService) carService).getUpdateProcessDTO();
        for (Integer name : processDTO.getChange2InActiveSet()) {
            TenStarVariable.INSTANCE.getVertex(name).buildCKNN();
        }
    }

    @Override
    public IndexType supportType() {
        return IndexType.TENSTAR;
    }

    //构造索引
    private void buildStack() {
        treeStack = new Stack<>();
        int minDegree = 1;
        HashSet<Integer> allKeys = TenStarVariable.INSTANCE.getVertices()
                .stream().map(Vertex::getName).collect(Collectors.toCollection(HashSet::new));

        while (!allKeys.isEmpty()) {
            Iterator<Integer> allKeysIter = allKeys.iterator();

            while (allKeysIter.hasNext()) {
                Integer vertexName = allKeysIter.next();
                TenStarVertex vertex = TenStarVariable.INSTANCE.getVertex(vertexName);
                if (vertex.getDegree() <= minDegree) {
                    vertexService.buildClique(vertex);                    //构建团

                    treeStack.add(vertexName);                //进栈
                    allKeysIter.remove();
                }
            }
            minDegree++;
        }
    }

    private void buildTree() {
        //栈顶作为根节点
        root = TenStarVariable.INSTANCE.getVertex(treeStack.pop());
        root.setRoot();
        //构建树
        while (!treeStack.empty()) {
            //如果栈不为空
            Integer vertexName = treeStack.pop();                       //出栈
            TenStarVertex vertex = TenStarVariable.INSTANCE.getVertex(vertexName);
            vertex.setParent();                                        //寻找父亲
        }
    }

    private void buildCKNN() {
        HashSet<Integer> allKeys = TenStarVariable.INSTANCE.getVertices()
                .stream().map(Vertex::getName).collect(Collectors.toCollection(HashSet::new));
        int maxTreeLevel = 0;
        //找到最大的层高
        for (int i = 0; i < GlobalVariable.VERTEX_NUM; i++) {
            if (TenStarVariable.INSTANCE.getVertex(i).getTreeLevel() > maxTreeLevel) {
                maxTreeLevel = TenStarVariable.INSTANCE.getVertex(i).getTreeLevel();
            }
        }
        System.out.println("MaxTreeLevel=" + maxTreeLevel);
        while (!allKeys.isEmpty()) {
            Iterator<Integer> allKeysIter = allKeys.iterator();

            while (allKeysIter.hasNext()) {
                Integer Name = allKeysIter.next();
                TenStarVertex vertex = TenStarVariable.INSTANCE.getVertex(Name);
                //当层高等于最大层高时
                if (vertex.getTreeLevel() == maxTreeLevel) {

                    vertex.buildCKNN();
                    allKeysIter.remove();
                }
            }
            maxTreeLevel--;
        }
    }


}
