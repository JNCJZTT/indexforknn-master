package com.index.indexforknn.test.testCase.domain;

/**
 * TODO
 * 2022/3/12 zhoutao
 */
public class Task {
    public static int id = 1;

    public static void main(String[] args) {
        Task task = new Task();
        task.id++;
        Task task2=new Task();
        System.out.println(task2.id);
    }
}
