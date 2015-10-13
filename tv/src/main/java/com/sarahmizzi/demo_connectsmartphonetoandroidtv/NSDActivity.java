package com.sarahmizzi.demo_connectsmartphonetoandroidtv;

/*
    Tutorial: http://developer.android.com/training/connect-devices-wirelessly/nsd.html
 */

import android.os.Bundle;
import android.app.Activity;

public class NSDActivity extends Activity {

    public TVNsdHelper mTVNsdHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nsd);

        mTVNsdHelper = new TVNsdHelper(getApplicationContext());
        mTVNsdHelper.initializeRegistrationListener();
        mTVNsdHelper.registerService(9000);
    }

    @Override
    protected void onPause() {
        mTVNsdHelper.unregisterService();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mTVNsdHelper != null) {
            mTVNsdHelper.initializeRegistrationListener();
            mTVNsdHelper.registerService(9000);
        }
    }

    @Override
    protected void onDestroy() {
        mTVNsdHelper.unregisterService();
        super.onDestroy();
    }
}
