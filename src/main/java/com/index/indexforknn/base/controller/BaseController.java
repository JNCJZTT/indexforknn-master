package com.index.indexforknn.base.controller;

import com.index.indexforknn.ahg.service.AhgKnnService;
import com.index.indexforknn.ahg.service.dto.AhgKnnDTO;
import com.index.indexforknn.ahg.service.dto.result.AhgKnnEdge;
import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.service.GlobalVariableService;
import com.index.indexforknn.base.service.api.IKnnService;
import com.index.indexforknn.base.service.api.IndexService;
import com.index.indexforknn.base.service.dto.IndexDTO;
import com.index.indexforknn.base.service.dto.KnnDTO;
import com.index.indexforknn.base.service.dto.result.ResultDTO;
import com.index.indexforknn.base.service.dto.UpdateDTO;
import com.index.indexforknn.base.service.factory.ServiceFactory;
import com.index.indexforknn.base.service.utils.DistributionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * BaseController
 * 2022/3/20 zhoutao
 */
@RestController
@RequestMapping()
@Slf4j
public class BaseController {
    private IndexService indexService;

    @Autowired
    private GlobalVariableService globalVariableService;

    private IKnnService knnService;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test() {
        return "API is connected";
    }

    /**
     * buildIndex
     *
     * @param index Index related parameters
     */
    @RequestMapping(value = "/build", method = RequestMethod.POST)
    public ResultDTO buildIndex(@RequestBody IndexDTO index) {
        initGlobalVariable(index);
        indexService.buildIndex(index);
        return indexService.buildResult(index);
    }

    @RequestMapping(value = "/ahg/knn", method = RequestMethod.POST)
    public @ResponseBody
    List<AhgKnnEdge> ahgKnn(@RequestBody AhgKnnDTO knnDTO) {
        globalVariableService.initKnnVariable(knnDTO);
        knnService = ServiceFactory.getAhgKnnService();
        ((AhgKnnService) knnService).initAhgKnn(knnDTO);
        ((AhgKnnService) knnService).batchAhgKnnSerach();
        ((AhgKnnService) knnService).updateKnnPosition();
//        if (knnDTO.getQueryName() > -1 && knnDTO.getQueryName() < GlobalVariable.VERTEX_NUM) {
//            knnService.knnSearch(knnDTO.getQueryName());
//        } else {
//            knnService.knnSearch(DistributionUtil.getVertexName());
//        }

        return ((AhgKnnService) knnService).getQueryEdges();
    }


    @RequestMapping(value = "/knn", method = RequestMethod.POST)
    public @ResponseBody
    ResultDTO knn(@RequestBody KnnDTO knnDTO) {
        globalVariableService.initKnnVariable(knnDTO);
        knnService = ServiceFactory.getKnnService();
        if (knnDTO.getQueryName() > -1 && knnDTO.getQueryName() < GlobalVariable.VERTEX_NUM) {
            knnService.knnSearch(knnDTO.getQueryName());
        } else {
            knnService.knnSearch(DistributionUtil.getVertexName());
        }
        return knnService.buildResult(knnDTO);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public void update(@RequestBody UpdateDTO updateDTO) {
        indexService.updateCar(updateDTO);
    }

    /**
     * initGlobalVariable
     */
    private void initGlobalVariable(IndexDTO index) {
        globalVariableService.initGlobalVariable(index);
        indexService = ServiceFactory.getIndexService();
    }

}
