package com.xykj.model;

/**
 * 结果为{result:1}或者{result:-1}这种类型的数据
 */
public class ErrorResult {
    private int result;
    private String extras;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    @Override
    public String toString() {
        return "ErrorResult{" +
                "result=" + result +
                ", extras='" + extras + '\'' +
                '}';
    }
}
