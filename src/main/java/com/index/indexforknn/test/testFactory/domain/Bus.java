package com.index.indexforknn.test.testFactory.domain;

import org.springframework.stereotype.Component;

/**
 * TODO
 * 2022/3/20 zhoutao
 */
@Component
public class Bus implements Car {
    public Bus() {
        register();
    }

    @Override
    public String supportType() {
        return "Bus";
    }

}
