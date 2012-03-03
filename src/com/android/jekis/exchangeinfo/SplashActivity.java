package com.android.jekis.exchangeinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SplashActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Create an object of type SplashHandler.
        SplashHandler mHandler = new SplashHandler();
        // Set the layout for this activity.
        this.setContentView(R.layout.splash);
        // Create a Message object.
        Message msg = new Message();
        // Assign a unique code to the message.
        // Later, this code will be used to identify the message in Handler
        // class.
        msg.what = 0;
        // Send the message with a delay.
        mHandler.sendMessageDelayed(msg, 2000);
    }

    // Handler class implementation to handle the message.
    private class SplashHandler extends Handler {

        // This method is used to handle received messages.
        @Override
        public void handleMessage(Message msg) {
            // Switch to identify the message by its code.
            switch (msg.what) {
                default:
                case 0:
                    super.handleMessage(msg);

                    // Create an intent to start the new activity.
                    // Our intention is to start MainActivity.
                    Intent intent = new Intent();
                    intent.setClass(SplashActivity.this, Exchangeinfo.class);
                    SplashActivity.this.startActivity(intent);
                    // Finish the current activity.
                    SplashActivity.this.finish();
            }
        }
    }
}
