package com.android.jekis.exchangeinfo;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class BanksViewer extends Activity {
    private String currencyCharcode;
    private String userOperationType;
    private Map<String, Object> banksDataMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.banks);

        // Get intent extra paramaters.
        this.currencyCharcode = this.getIntent().getStringExtra("currencyCharcode");
        this.userOperationType = this.getIntent().getStringExtra("operationType");
        new BackgroundAsyncTask().execute();
    }

    /**
     * Loads list of banks.
     */
    private void loadBanksList() {
        // Get banks data.
        CursMdExchangeSiteParser cursMd = new CursMdExchangeSiteParser();
        String sortingField = "buyPrice";
        if (this.userOperationType.equals("buy")) {
            sortingField = "sellPrice";
        }
        this.banksDataMap = cursMd.getBanksData(this.currencyCharcode, sortingField);
    }

    /**
     * Shows list of banks.
     */
    private void showBanksList() {
        TableLayout tl = (TableLayout) this.findViewById(R.id.banksTable);

        // Loop through banks.
        @SuppressWarnings("unchecked")
        ArrayList<Bank> banks = (ArrayList<Bank>) this.banksDataMap.get("banks");
        for (int i = 0; i < banks.size(); i++) {
            Bank bank = banks.get(i);
            Currency currency = bank.getCurrency(this.currencyCharcode);
            if (this.userOperationType.equals("buy")) {
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

    private class BackgroundAsyncTask extends AsyncTask<Void, Boolean, Void> {
        ProgressDialog pd;

        @Override
        protected Void doInBackground(Void... params) {
            BanksViewer.this.loadBanksList();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            this.pd.dismiss();
            BanksViewer.this.showBanksList();
        }

        @Override
        protected void onPreExecute() {
            String title = BanksViewer.this.getString(R.string.progressDialogTitle);
            String message = BanksViewer.this.getString(R.string.progressDialogMessage);
            this.pd = ProgressDialog.show(BanksViewer.this, title, message, true, false);
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
        }
    }
}
