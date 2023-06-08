package com.example.box.Entity;

public class SideDishType {
    private int sideDishTypeId;
    private String sideDishTypeName;

    public SideDishType(int sideDishTypeId, String sideDishTypeName) {
        this.sideDishTypeId = sideDishTypeId;
        this.sideDishTypeName = sideDishTypeName;
    }

    public String getSideDishTypeName() {
        return sideDishTypeName;
    }

    public void setSideDishTypeName(String sideDishTypeName) {
        this.sideDishTypeName = sideDishTypeName;
    }

    public int getSideDishTypeId() {
        return sideDishTypeId;
    }

    public void setSideDishTypeId(int sideDishTypeId) {
        this.sideDishTypeId = sideDishTypeId;
    }
}
