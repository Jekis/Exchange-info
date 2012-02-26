package com.android.jekis.exchangeinfo;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class BanksViewer extends Activity {
    public static String BANKS_XML = "banks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.banks);

        // Get intent extra paramaters.
        String currencyCharcode = this.getIntent().getStringExtra("currencyCharcode");
        String operationType = this.getIntent().getStringExtra("operationType");

        TableLayout tl = (TableLayout) this.findViewById(R.id.banksTable);

        // Get banks data.
        CursMdExchangeSiteParser cursMd = new CursMdExchangeSiteParser();
        String sortingField = "buyPrice";
        if (operationType.equals("buy")) {
            sortingField = "sellPrice";
        }
        Map<String, Object> banksDataMap = cursMd.getBanksData(currencyCharcode, sortingField);

        // Loop through banks.
        @SuppressWarnings("unchecked")
        ArrayList<Bank> banks = (ArrayList<Bank>) banksDataMap.get("banks");
        for (int i = 0; i < banks.size(); i++) {
            Bank bank = banks.get(i);
            Currency currency = bank.getCurrency(currencyCharcode);
            if (operationType.equals("buy")) {
                // Add bank sell price to highlighted column.
                tl.addView(this.createRow(bank.getName(), Float.toString(currency.sellPrice), Float.toString(currency.buyPrice)));
            } else {
                // Add bank buy price to highlighted column.
                tl.addView(this.createRow(bank.getName(), Float.toString(currency.buyPrice), Float.toString(currency.sellPrice)));
            }
        }

    }

    private TableRow createRow(String bankName, String actualRate, String secondRate) {
        LayoutInflater li = this.getLayoutInflater();
        TableRow row = (TableRow) li.inflate(R.layout.bank_row, null);

        TextView bankNameRateView = (TextView) row.findViewById(R.id.name);
        TextView actualRateView = (TextView) row.findViewById(R.id.actualRate);
        TextView secondRateView = (TextView) row.findViewById(R.id.secondRate);

        bankNameRateView.setText(bankName);
        actualRateView.setText(actualRate);
        secondRateView.setText(secondRate);

        return row;
    }
}
