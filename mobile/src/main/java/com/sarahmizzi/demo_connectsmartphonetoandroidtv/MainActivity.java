package com.sarahmizzi.demo_connectsmartphonetoandroidtv;

import android.app.Activity;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity{


    private final String TAG = MainActivity.class.getSimpleName();
    private String SERVICE_NAME = "Client Device";
    private String SERVICE_TYPE = "_http._tcp.";

    private InetAddress hostAddress;
    private int hostPort;
    private NsdManager mNsdManager;
    private NsdManager.DiscoveryListener mDiscoveryListener;

    public List<String> mDeviceList;
    private RecyclerView mRecyclerView;
    private DevicesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup recycler view
        setupRecyclerView();

        // Start service discovery
        startServiceDiscovery();
    }

    @Override
    protected void onPause() {
        if(mNsdManager != null){
            // mNsdManager.stopServiceDiscovery(mDiscoveryListener);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mNsdManager != null){
            // mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
        }
    }

    @Override
    protected void onDestroy() {
        if(mNsdManager != null){
            // mNsdManager.stopServiceDiscovery(mDiscoveryListener);
        }
        super.onDestroy();
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

    private void startServiceDiscovery(){
        mNsdManager = (NsdManager) getSystemService(NSD_SERVICE);
        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            // Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started.");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                // A service was found! Do something with it.
                Log.d(TAG, "Service discovery success : " + service);
                Log.d(TAG, "Host = " + service.getServiceName());
                Log.d(TAG, "port = " + String.valueOf(service.getPort()));

                if (!service.getServiceType().equals(SERVICE_TYPE)) {
                    // Service type is the string containing the protocol and
                    // transport layer for this service.
                    Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
                } else if (service.getServiceName().equals(SERVICE_NAME)) {
                    // The name of the service tells the user what they'd be
                    // connecting to. It could be "Bob's Chat App".
                    Log.d(TAG, "Same machine: " + SERVICE_NAME);
                } else {
                    // Add item to dialog
                    mDeviceList.add(service.getServiceName());

                    Log.d(TAG, "Diff Machine : " + service.getServiceName());
                    // connect to the service and obtain serviceInfo
                    mNsdManager.resolveService(service, new NsdManager.ResolveListener() {

                        @Override
                        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                            // Called when the resolve fails. Use the error code to debug.
                            Log.e(TAG, "Resolve failed: " + errorCode);
                            Log.e(TAG, "Service: " + serviceInfo);
                        }

                        @Override
                        public void onServiceResolved(NsdServiceInfo serviceInfo) {
                            Log.d(TAG, "Resolve Succeeded. " + serviceInfo);

                            if (serviceInfo.getServiceName().equals(SERVICE_NAME)) {
                                Log.d(TAG, "Same IP.");
                                return;
                            }

                            // Obtain port and IP
                            hostPort = serviceInfo.getPort();
                            hostAddress = serviceInfo.getHost();
                        }
                    });
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available. Internal bookkeeping code goes here.
                Log.e(TAG, "Service lost: " + service);
                mDeviceList.remove(mDeviceList.indexOf(service.getServiceName()));
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code: " + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code: " + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }
        };
        mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }

    private void setupRecyclerView(){
        // Create new instance of array list
        mDeviceList = new ArrayList<>();

        // Find recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new DevicesAdapter(this, mDeviceList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Refresh every 5 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.refresh(mDeviceList);
            }
        }, 1000);
    }
}
