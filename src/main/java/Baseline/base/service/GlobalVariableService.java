package Baseline.base.service;

import Baseline.base.common.constants.Constants;
import Baseline.base.domain.GlobalVariable;
import Baseline.base.domain.api.factory.VariableFactory;
import Baseline.base.domain.enumeration.Distribution;
import Baseline.base.domain.enumeration.IndexType;
import Baseline.base.domain.enumeration.MapInfo;
import Baseline.base.service.dto.IndexDTO;
import Baseline.base.service.dto.KnnDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * GlobalVariableService
 * 2022/3/26 zhoutao
 */
@Service
@Slf4j
public class GlobalVariableService {

    /**
     * init global variables
     */
    public void initGlobalVariable(IndexDTO index) {
        GlobalVariable.BRANCH = index.getBranch();
        GlobalVariable.SUB_GRAPH_SIZE = index.getSubGraphSize();
        GlobalVariable.INDEX_TYPE = IndexType.valueOf(index.getIndexType());
        GlobalVariable.MAP_INFO = MapInfo.valueOf(index.getMapInfo());
        GlobalVariable.VERTEX_NUM = GlobalVariable.MAP_INFO.getSize();
        GlobalVariable.DISTRIBUTE = Distribution.valueOf(index.getDistribution());
        GlobalVariable.CAR_NUM = index.getCarNum();
        GlobalVariable.DIGIT = String.valueOf(index.getBranch()).length();
        GlobalVariable.variable = VariableFactory.getVariable();
        GlobalVariable.K= index.getK();
        // init file path
        initFileUrl();
    }

    /**
     * init file path
     */
    private void initFileUrl() {
        String mapName = GlobalVariable.MAP_INFO.name();
        String indexName = GlobalVariable.INDEX_TYPE.name();
        // USA-road-d.NY.branch-4.avg-50.txt
        if (indexName.equals("SIMkNN")||indexName.equals("ERkNN")){
            GlobalVariable.vertexUrl = Constants.BASE_URL + "{0}/original-{0}.co";
            GlobalVariable.vertexUrl = MessageFormat.format(GlobalVariable.vertexUrl , mapName);
        }else {
            GlobalVariable.vertexUrl = MessageFormat.format(GlobalVariable.vertexUrl,
                    mapName, GlobalVariable.BRANCH, GlobalVariable.SUB_GRAPH_SIZE);
        }
        // NY_Edge.txt
        GlobalVariable.edgeUrl = MessageFormat.format(GlobalVariable.edgeUrl, mapName);
        // CarFile.NY.CarNum-5,000.Distribute-RANDOM.txt
        GlobalVariable.carUrl = MessageFormat.format(GlobalVariable.carUrl,
                mapName, GlobalVariable.CAR_NUM, GlobalVariable.DISTRIBUTE.name());

        log.info("vertexUrl={}", GlobalVariable.vertexUrl);
        log.info("edgeUrl={}", GlobalVariable.edgeUrl);
        log.info("carUrl={}", GlobalVariable.carUrl);
    }

    /**
     * init knn variables
     */
    public void initKnnVariable(KnnDTO knnDTO) {
        GlobalVariable.K = knnDTO.getK();
    }
}
