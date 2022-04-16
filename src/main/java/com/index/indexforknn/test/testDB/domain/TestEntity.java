package com.index.indexforknn.test.testDB.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * TODO
 * 2022/3/7 zhoutao
 */
//@Entity
//@Table(name = "KNN_TEST_TABLE")
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
public class TestEntity implements Serializable {
//    @Id
//    @GeneratedValue
    private String id;

    private String name;
}
