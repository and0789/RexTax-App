package com.task.dynamicregex.entities;

public class Result {

    private String field;

    private String result;

    public Result(String field, String result) {
        this.field = field;
        this.result = result;
    }

    public Result() {
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}
