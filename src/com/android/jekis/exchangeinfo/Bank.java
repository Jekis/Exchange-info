package com.android.jekis.exchangeinfo;

import java.util.HashMap;
import java.util.Map;

public class Bank {
    private String name;
    public Map<String, Currency> currencies = new HashMap<String, Currency>();

    public Bank(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void addCurrency(String charcode) {
        Currency currency = new Currency(charcode);
        this.currencies.put(currency.getCharcode(), currency);
    }

    public void addCurrency(String charcode, float buyPrice, float sellPrice) {
        Currency currency = new Currency(charcode, buyPrice, sellPrice);
        this.currencies.put(currency.getCharcode(), currency);
    }

    public Currency getCurrency(String charcode) {
        return this.currencies.get(charcode);
    }
}
