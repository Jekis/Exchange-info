package com.android.jekis.exchangeinfo;

public class Currency {
    private String charcode;
    public float buyPrice;
    public float sellPrice;

    public Currency(String charcode) {
        this.charcode = charcode;
    }

    public String getCharcode() {
        return this.charcode;
    }

    public Currency(String charcode, float buyPrice, float sellPrice) {
        this.charcode = charcode;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }
}
