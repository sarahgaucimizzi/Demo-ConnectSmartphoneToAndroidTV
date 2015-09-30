package com.sarahmizzi.demo_connectsmartphonetoandroidtv;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity{
    public MobileNsdHelper mMobileNsdHelper;

    public static List<String> mDeviceList;
    private RecyclerView mRecyclerView;
    private DevicesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMobileNsdHelper = new MobileNsdHelper(getApplicationContext());

        // Start service discovery
        mMobileNsdHelper.initializeNsd();

        // Setup recycler view
        setupRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        if(mMobileNsdHelper != null){
            mMobileNsdHelper.tearDown();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mMobileNsdHelper != null){
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

    public void setupRecyclerView(){
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
