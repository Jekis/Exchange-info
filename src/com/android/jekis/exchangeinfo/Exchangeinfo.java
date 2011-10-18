package com.android.jekis.exchangeinfo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class Exchangeinfo extends Activity {
	// Current deal operation
	private String mOperation;

	// Current deal currency
	private String mCurrency;

	// Form submit button
	private Button mSubmit;

	// String resource parts, to compose relevant button text
	private String[] mSubmitResourceParts = { "", "" };

	// Radio group for selecting deal operation
	private RadioGroup mOperationRadios;

	// Radio group for selecting deal currency
	private RadioGroup mCurrencyRadios;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Get submit button
        mSubmit = (Button) findViewById(R.id.submit);
        mSubmit.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	// Store form values as user settings for this application
            	storeFormSettings();

                // Show banks list view
                Intent i = new Intent(Exchangeinfo.this, BanksViewer.class);
//                i.putExtra("", "");
                Exchangeinfo.this.startActivity(i);
            }
        });

        // Get form radio groups
        mOperationRadios = (RadioGroup) findViewById(R.id.radioGroupOperation);
        mCurrencyRadios = (RadioGroup) findViewById(R.id.radioGroupCurrency);

        // Set listeners for radio groups
        mOperationRadios.setOnCheckedChangeListener(radioGroupListener);
        mCurrencyRadios.setOnCheckedChangeListener(radioGroupListener);

        // Set default checked options
        restoreFormSettings();
    }
    
    private void storeFormSettings() {
    	SharedPreferences.Editor editor = getAppPreferences().edit();
    	editor.putInt("operationChecked", mOperationRadios.getCheckedRadioButtonId());
    	editor.putInt("currencyChecked", mCurrencyRadios.getCheckedRadioButtonId());
    	editor.commit();
    }

    private void restoreFormSettings() {
    	SharedPreferences prefs = getAppPreferences();
        mOperationRadios.check(prefs.getInt("operationChecked", R.id.radio_sell));
        mCurrencyRadios.check(prefs.getInt("currencyChecked", R.id.radio_usd));
    }

    public SharedPreferences getAppPreferences() {
    	return getPreferences(MODE_PRIVATE);
    }

    private void composeSubmitButtonText(int part, String resuorcePart){
    	mSubmitResourceParts[part] = resuorcePart;
    	setSubmitButtonText();
    }

    /**
     * Set submit button text.
     *
     * Compose string resource id from two parts and set this string as the button text.
     * */
    private void setSubmitButtonText(){
    	if (mSubmitResourceParts[0].length() > 0 && mSubmitResourceParts[1].length() > 0) {
	    	String resourceName = mSubmitResourceParts[0] + "_" + mSubmitResourceParts[1];
	
	    	if (resourceName.equals("buy_usd")) {
	        	mSubmit.setText(R.string.btn_buy_usd);
	    	}
	    	else if (resourceName.equals("buy_eur")) {
	        	mSubmit.setText(R.string.btn_buy_eur);
	    	}
	    	else if (resourceName.equals("sell_usd")) {
	        	mSubmit.setText(R.string.btn_sell_usd);
	    	}
	    	else if (resourceName.equals("sell_eur")) {
	        	mSubmit.setText(R.string.btn_sell_eur);
	    	}
    	}
    }

    /**
     * Radio groups listener
     * */
    private OnCheckedChangeListener radioGroupListener = new OnCheckedChangeListener() {
		public void onCheckedChanged(RadioGroup radioGroup, int checkedId){
			switch (checkedId) {
			case R.id.radio_buy:
				mOperation = "buy";
				composeSubmitButtonText(0, "buy");
				break;
			case R.id.radio_sell:
				mOperation = "sell";
				composeSubmitButtonText(0, "sell");
				break;
			case R.id.radio_usd:
				mCurrency = "USD";
				composeSubmitButtonText(1, "usd");
				break;
			case R.id.radio_eur:
				mCurrency = "EUR";
				composeSubmitButtonText(1, "eur");
				break;
			}
		}
    };
}