package com.android.jekis.exchangeinfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class Exchangeinfo extends Activity {
    private static Exchangeinfo instance;

    // Current deal operation.
    private String mOperation;

    // Current deal currency
    private String mCurrency;

    // Form submit button.
    private Button mSubmit;

    // String resource parts, to compose relevant button text.
    private String[] mSubmitResourceParts = { "", "" };

    // Radio group for selecting deal operation.
    private RadioGroup mOperationRadios;

    // Radio group for selecting deal currency.
    private RadioGroup mCurrencyRadios;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        this.setContentView(R.layout.main);

        // Get submit button
        this.mSubmit = (Button) this.findViewById(R.id.submit);
        this.mSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Store form values as user settings for this application.
                Exchangeinfo.this.storeFormSettings();

                // Show banks list view.
                Intent i = new Intent(Exchangeinfo.this, BanksViewer.class);

                SharedPreferences prefs = Exchangeinfo.this.getAppPreferences();

                // Send currency param.
                String currencyCharcode = "EUR";
                if (prefs.getInt("currencyChecked", R.id.radio_usd) == R.id.radio_usd) {
                    currencyCharcode = "USD";
                }
                i.putExtra("currencyCharcode", currencyCharcode);

                // Send operation param.
                String operationType = "buy";
                if (prefs.getInt("operationChecked", R.id.radio_sell) == R.id.radio_sell) {
                    operationType = "sell";
                }
                i.putExtra("operationType", operationType);

                Exchangeinfo.this.startActivity(i);
            }
        });

        // Get form radio groups.
        this.mOperationRadios = (RadioGroup) this.findViewById(R.id.radioGroupOperation);
        this.mCurrencyRadios = (RadioGroup) this.findViewById(R.id.radioGroupCurrency);

        // Set listeners for radio groups.
        this.mOperationRadios.setOnCheckedChangeListener(this.radioGroupListener);
        this.mCurrencyRadios.setOnCheckedChangeListener(this.radioGroupListener);

        // Set default checked options.
        this.restoreFormSettings();
    }

    public static Context getContext() {
        return instance;
    }

    private void storeFormSettings() {
        SharedPreferences.Editor editor = this.getAppPreferences().edit();
        editor.putInt("operationChecked", this.mOperationRadios.getCheckedRadioButtonId());
        editor.putInt("currencyChecked", this.mCurrencyRadios.getCheckedRadioButtonId());
        editor.commit();
    }

    private void restoreFormSettings() {
        SharedPreferences prefs = this.getAppPreferences();
        this.mOperationRadios.check(prefs.getInt("operationChecked", R.id.radio_sell));
        this.mCurrencyRadios.check(prefs.getInt("currencyChecked", R.id.radio_usd));
    }

    public SharedPreferences getAppPreferences() {
        return this.getPreferences(MODE_PRIVATE);
    }

    private void composeSubmitButtonText(int part, String resuorcePart) {
        this.mSubmitResourceParts[part] = resuorcePart;
        this.setSubmitButtonText();
    }

    /**
     * Set submit button text.
     * Compose string resource id from two parts and set this string as the
     * button text.
     */
    private void setSubmitButtonText() {
        if (this.mSubmitResourceParts[0].length() > 0 && this.mSubmitResourceParts[1].length() > 0) {
            String resourceName = this.mSubmitResourceParts[0] + "_" + this.mSubmitResourceParts[1];

            if (resourceName.equals("buy_usd")) {
                this.mSubmit.setText(R.string.btn_buy_usd);
            } else if (resourceName.equals("buy_eur")) {
                this.mSubmit.setText(R.string.btn_buy_eur);
            } else if (resourceName.equals("sell_usd")) {
                this.mSubmit.setText(R.string.btn_sell_usd);
            } else if (resourceName.equals("sell_eur")) {
                this.mSubmit.setText(R.string.btn_sell_eur);
            }
        }
    }

    /**
     * Radio groups listener
     */
    private OnCheckedChangeListener radioGroupListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            switch (checkedId) {
            case R.id.radio_buy:
                Exchangeinfo.this.mOperation = "buy";
                Exchangeinfo.this.composeSubmitButtonText(0, "buy");
                break;
            case R.id.radio_sell:
                Exchangeinfo.this.mOperation = "sell";
                Exchangeinfo.this.composeSubmitButtonText(0, "sell");
                break;
            case R.id.radio_usd:
                Exchangeinfo.this.mCurrency = "USD";
                Exchangeinfo.this.composeSubmitButtonText(1, "usd");
                break;
            case R.id.radio_eur:
                Exchangeinfo.this.mCurrency = "EUR";
                Exchangeinfo.this.composeSubmitButtonText(1, "eur");
                break;
            }
        }
    };
}
