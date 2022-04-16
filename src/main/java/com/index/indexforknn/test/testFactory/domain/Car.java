package com.index.indexforknn.test.testFactory.domain;

import com.index.indexforknn.test.testFactory.factory.CarFactory;
import org.springframework.stereotype.Component;

/**
 * TODO
 * 2022/3/20 zhoutao
 */
@Component
public interface Car {
    String supportType();

    default void register() {
        CarFactory.register(supportType(), this);
    }
}
