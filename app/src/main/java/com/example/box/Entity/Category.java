package com.example.box.Entity;

import java.util.List;

public class Category<Type> {
    private String categoryName;
    List<?> list;

    public Category(String categoryName, List<?> list) {
        this.categoryName = categoryName;
        this.list = list;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }
}
