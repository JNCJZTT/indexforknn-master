package Baseline.base.controller;

import Baseline.base.domain.GlobalVariable;
import Baseline.base.service.GlobalVariableService;
import Baseline.base.service.api.IKnnService;
import Baseline.base.service.api.IndexService;
import Baseline.base.service.dto.IndexDTO;
import Baseline.base.service.dto.KnnDTO;
import Baseline.base.service.dto.UpdateDTO;
import Baseline.base.service.dto.result.ResultDTO;
import Baseline.base.service.factory.ServiceFactory;
import Baseline.base.service.utils.DistributionUtil;
//import baseline.ODIN.service.ODINKnnService;
//import baseline.ODIN.service.dto.ODINkNNDTO;
//import baseline.ODIN.service.dto.result.ODINkNNEdge;
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
