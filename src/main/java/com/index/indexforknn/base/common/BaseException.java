package com.index.indexforknn.base.common;

/**
 * TODO
 * 2022/2/12 zhoutao
 */
public class BaseException extends Exception {

    // 序列化UID
    private static final long serialVersionUID = 8243127099991355146L;

    /**
     * 构造异常
     *
     * @param msg  异常讯息
     **/
    public BaseException(String msg) {
        super(msg);
    }

    /**
     * 构造异常
     *
     * @param ex   异常来源
     */
    public BaseException(Exception ex) {
        super(ex);
    }
}
