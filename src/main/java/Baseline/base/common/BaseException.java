package Baseline.base.common;

/**
 * BaseException
 * 2022/2/12 zhoutao
 */
public class BaseException extends Exception {

    // serialVersionUID
    private static final long serialVersionUID = 8243127099991355146L;

    /**
     * build error
     *
     * @param msg  errMsg
     **/
    public BaseException(String msg) {
        super(msg);
    }

    /**
     * build error
     *
     * @param ex   exception
     */
    public BaseException(Exception ex) {
        super(ex);
    }
}
