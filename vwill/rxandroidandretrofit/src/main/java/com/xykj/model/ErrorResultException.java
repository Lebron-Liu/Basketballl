package com.xykj.model;

/**
 * Created by Administrator on 2017/7/10.
 */
public class ErrorResultException extends RuntimeException {
    private int what;

    public ErrorResultException(String message, int what) {
        super(message);
        this.what = what;
    }

    public int getWhat() {
        return what;
    }

    @Override
    public String toString() {
        return "ErrorResultException{" +
                "what=" + what +","+getMessage()+"}";
    }
}
