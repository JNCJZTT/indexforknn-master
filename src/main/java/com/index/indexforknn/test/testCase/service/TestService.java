package com.index.indexforknn.test.testCase.service;

import com.index.indexforknn.ahg.domain.AhgVertex;
import com.index.indexforknn.base.service.utils.DistributionUtil;
import com.index.indexforknn.base.service.factory.SpringBeanFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 * 2022/2/15 zhoutao
 */
@Service
@Data
@Slf4j
public class TestService {
    private int testNum = 0;

    public static List list;

    public static void main(String[] args) {
        list = new ArrayList();
        list.add(new AhgVertex());
        AhgVertex vertex = (AhgVertex) (list.get(0));
    }


}
