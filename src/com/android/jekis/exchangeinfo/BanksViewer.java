package com.android.jekis.exchangeinfo;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class BanksViewer extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.banks);

        TableLayout tl = (TableLayout) findViewById(R.id.banksTable);
        
        for (int i = 0; i < 5; i++) {
        	TableRow row = createRow("Victoriabank " + i, "11.80", "12.01");
            tl.addView(row);
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
