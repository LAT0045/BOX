package com.example.box.Entity;

public class SideDish {
    private int sideDishId;
    private String sideDishName;
    private Double sideDishPrice;

    public SideDish(int sideDishId, String sideDishName, Double sideDishPrice) {
        this.sideDishId = sideDishId;
        this.sideDishName = sideDishName;
        this.sideDishPrice = sideDishPrice;
    }

    public int getSideDishId() {
        return sideDishId;
    }

    public void setSideDishId(int sideDishId) {
        this.sideDishId = sideDishId;
    }

    public String getSideDishName() {
        return sideDishName;
    }

    public void setSideDishName(String sideDishName) {
        this.sideDishName = sideDishName;
    }

    public Double getSideDishPrice() {
        return sideDishPrice;
    }

    public void setSideDishPrice(Double sideDishPrice) {
        this.sideDishPrice = sideDishPrice;
    }
}
