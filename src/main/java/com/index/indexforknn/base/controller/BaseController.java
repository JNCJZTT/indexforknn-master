package com.index.indexforknn.base.controller;

import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.service.GlobalVariableService;
import com.index.indexforknn.base.service.IndexService;
import com.index.indexforknn.base.service.dto.IndexDTO;
import com.index.indexforknn.base.service.factory.IndexServiceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO
 * 2022/3/20 zhoutao
 */
@RestController
@RequestMapping()
@Slf4j
public class BaseController {
    private IndexService indexService;

    @Autowired
    private GlobalVariableService globalVariableService;

    /**
     * 构建索引
     */
    @RequestMapping(value = "/build", method = RequestMethod.POST)
    public String buildIndex(@RequestBody IndexDTO index) {
        initGlobalVariable(index);
        indexService.buildIndex(index);
        return "Success~!";
    }

    /**
     * 初始化全局变量
     */
    private void initGlobalVariable(IndexDTO index) {
        globalVariableService.initGlobalVariable(index);
        indexService = IndexServiceFactory.getIndexService(GlobalVariable.INDEX_TYPE);
    }

}
