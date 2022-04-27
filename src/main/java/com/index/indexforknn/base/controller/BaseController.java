package com.index.indexforknn.base.controller;

import com.index.indexforknn.ahg.service.AhgKnnService;
import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.service.GlobalVariableService;
import com.index.indexforknn.base.service.IndexService;
import com.index.indexforknn.base.service.dto.IndexDTO;
import com.index.indexforknn.base.service.dto.KnnDTO;
import com.index.indexforknn.base.service.dto.ResultDTO;
import com.index.indexforknn.base.service.dto.UpdateDTO;
import com.index.indexforknn.base.service.factory.ServiceFactory;
import com.index.indexforknn.base.service.utils.DistributionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private AhgKnnService ahgKnnService;

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

    @RequestMapping(value = "/knn", method = RequestMethod.POST)
    public @ResponseBody
    ResultDTO knn(@RequestBody KnnDTO knnDTO) {
        globalVariableService.initKnnVariable(knnDTO);
        if (knnDTO.getQueryName() > -1 && knnDTO.getQueryName() < GlobalVariable.VERTEX_NUM) {
            ahgKnnService.knnSearch(knnDTO.getQueryName());
        } else {
            ahgKnnService.knnSearch(DistributionUtil.getVertexName());
        }
        return ahgKnnService.buildResult(knnDTO);
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
