package ODIN;

import ODIN.ODIN.service.dto.ODINkNNDTO;
import ODIN.base.controller.BaseController;

import ODIN.base.service.dto.IndexDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;


/**
 * spring starter
 **/
@Slf4j
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class})
public class ODINApplication implements CommandLineRunner {
//ODINApplication,IndexforknnApplication
    @Autowired
    BaseController controller;

    public static void main(String[] args) {
        SpringApplication.run(ODINApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
/**
 * Error queryName:142095
 */
        IndexDTO indexDTO = new IndexDTO();
        indexDTO.setIndexType("ODIN");
        indexDTO.setBranch(4);
        indexDTO.setSubGraphSize(200);
        indexDTO.setDistribution("RANDOM");
        indexDTO.setMapInfo("NY");
        indexDTO.setCarNum(10000);
        indexDTO.setLeastActiveNum(5);
        indexDTO.setK(10);
        indexDTO.setTimeType("Second");
        indexDTO.setMemory(true);
        indexDTO.setMemoryType("GB");
        System.out.println(controller.buildIndex(indexDTO).getResult());
        ODINkNNDTO knnDTO=new ODINkNNDTO();
        knnDTO.setK(10);
        knnDTO.setQueryName(-2);
        knnDTO.setPrintKnn(false);
        knnDTO.setDijkstra(false);
        knnDTO.setQuerySize(10);
        System.out.println(controller.ahgKnn(knnDTO));





    }
}
