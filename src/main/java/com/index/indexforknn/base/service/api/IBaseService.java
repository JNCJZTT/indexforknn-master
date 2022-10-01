package com.index.indexforknn.base.service.api;

import com.index.indexforknn.base.domain.enumeration.IndexType;
import org.springframework.stereotype.Service;

/**
 * IBaseService
 * 2022/4/23 zhoutao
 */
@Service
public interface IBaseService {
    IndexType supportType();

    void register();
}
