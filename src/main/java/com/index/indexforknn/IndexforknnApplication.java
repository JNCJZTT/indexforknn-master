package com.index.indexforknn;

import com.index.indexforknn.test.testDB.domain.TestEntity;
import com.index.indexforknn.test.testDB.repository.TestEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 启动类，禁用数据库
 **/
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class})
public class IndexforknnApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(IndexforknnApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        testDB();
    }

//    private void testDB() {
//        List<TestEntity> list=testEntityRepository.findTop2ByOrderById();
//        System.out.println(list);
//    }

//    private void showConnection() throws SQLException {
//        log.info(dataSource.toString());
//        Connection conn = dataSource.getConnection();
//        log.info(conn.toString());
//        conn.close();
//    }
//
//    private void showData() {
//        jdbcTemplate.queryForList("SELECT * FROM KNN_TEST_TABLE")
//                .forEach(row -> log.info(row.toString()));
//    }


}
