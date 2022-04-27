package com.index.indexforknn.base.service.api;

import com.index.indexforknn.base.domain.enumeration.IndexType;

/**
 * IBaseService
 * 2022/4/23 zhoutao
 */
public interface IBaseService {
    IndexType supportType();

    void register();
}
