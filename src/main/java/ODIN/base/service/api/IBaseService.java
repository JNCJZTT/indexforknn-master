package ODIN.base.service.api;

import ODIN.base.domain.enumeration.IndexType;
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
