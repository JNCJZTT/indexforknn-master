package ODIN.base.service.graph;

import ODIN.base.service.factory.ServiceFactory;
import ODIN.base.common.constants.Constants;
import ODIN.base.domain.Car;
import ODIN.base.domain.GlobalVariable;
import ODIN.base.domain.Node;
import ODIN.base.domain.api.Vertex;
import ODIN.base.domain.enumeration.IndexType;
import ODIN.base.service.api.IBaseService;
import ODIN.base.service.api.IVariableService;
import ODIN.base.service.dto.UpdateDTO;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * CarService
 * 2022/4/19 zhoutao
 */
@Service
public abstract class CarService implements IBaseService {

    protected IVariableService variableService;

    /**
     * batchUpdateRandomCarLocation
     *
     * @param updateDTO updateDTO
     */
    public void batchUpdateRandomCarLocation(UpdateDTO updateDTO) {
        int carNum = updateDTO.getUpdateNum();
        initUpdateDTO(updateDTO);
        List<Vertex> vertices =GlobalVariable.variable.getVertices();

        for (int i = 0; i < carNum; i++) {
            Car car = GlobalVariable.CARS.get(GlobalVariable.RANDOM.nextInt(GlobalVariable.CAR_NUM));
            int dis = car.getActiveDis() - Constants.CAR_SPEED;
            if (dis < 0) {
                int originalActive = car.getActive();
                Node node = vertices.get(originalActive).getRandomEdge();
                car.setActiveInfo(node);
                updateActive(originalActive, car.getActive(), car);
            } else {
                car.setActiveDis(dis);
            }
        }
    }

    protected abstract void initUpdateDTO(UpdateDTO updateDTO);

    protected abstract void updateActive(int originalActiveName, int activeName, Car car);


    public abstract IndexType supportType();

    public void register() {
        ServiceFactory.register(supportType(), this);
    }


}
