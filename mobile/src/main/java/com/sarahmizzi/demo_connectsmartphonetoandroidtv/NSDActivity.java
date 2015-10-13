package com.sarahmizzi.demo_connectsmartphonetoandroidtv;

/*
    Tutorial: http://developer.android.com/training/connect-devices-wirelessly/nsd.html
 */

import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class NSDActivity extends Activity {

    public MobileNsdHelper mMobileNsdHelper;

    public static List<String> mDeviceList;
    private RecyclerView mRecyclerView;
    private DevicesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nsd);
        mMobileNsdHelper = new MobileNsdHelper(getApplicationContext());

        // Start service discovery
        mMobileNsdHelper.initializeNsd();

        // Setup recycler view
        setupRecyclerView();
    }

    @Override
    protected void onPause() {
        if (mMobileNsdHelper != null) {
            mMobileNsdHelper.tearDown();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMobileNsdHelper != null) {
            mMobileNsdHelper.registerService(9000); //mConnection.getLocalPort());
            mMobileNsdHelper.discoverServices();
        }
        setupRecyclerView();
    }

    @Override
    protected void onDestroy() {
        mMobileNsdHelper.tearDown();
        //mConnection.tearDown();
        super.onDestroy();
    }

    public void setupRecyclerView() {
        // Create new instance of array list
        mDeviceList = new ArrayList<>();

        // Find recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new DevicesAdapter(this, mDeviceList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Refresh every second, delay by half a second
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.refresh(mDeviceList);
                new Handler().postDelayed(this, 1000);
            }
        }, 500);
    }
}

