package com.andreseptian.entities;

public class Result {

    private String categoryName;
    private String field;
    private String result;

    public Result(String categoryName, String field, String result) {
        this.categoryName = categoryName;
        this.field = field;
        this.result = result;
    }

    public Result() {
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getField() {
        return field;
    }

    public String getResult() {
        return result;
    }

}
