package com.index.indexforknn.test.testCase.controller;

import com.index.indexforknn.test.testCase.service.TestService;
import com.index.indexforknn.base.domain.annotation.CostTime;
import com.index.indexforknn.base.domain.enumeration.TimeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * TestController
 * 2022/2/15 zhoutao
 */
@RestController
@RequestMapping(value = "/test")
public class TestController {
    @Autowired
    TestService testService;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public void testApi() throws IOException {

    }

    @CostTime(msg = "测试时间", timeType = TimeType.microseconds)
    @RequestMapping(value = "/testTime", method = RequestMethod.GET)
    public String testTime() {
        int a = 2;
        return "TestTime";
    }


}
