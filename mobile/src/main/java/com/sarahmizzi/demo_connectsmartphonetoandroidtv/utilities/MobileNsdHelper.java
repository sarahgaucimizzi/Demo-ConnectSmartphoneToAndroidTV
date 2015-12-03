package com.sarahmizzi.demo_connectsmartphonetoandroidtv.utilities;

/*
    Tutorial: http://developer.android.com/training/connect-devices-wirelessly/nsd.html
 */

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import com.sarahmizzi.demo_connectsmartphonetoandroidtv.fragments.ConnectFragment;
import com.sarahmizzi.demo_connectsmartphonetoandroidtv.MainActivity;

/**
 * Created by Sarah on 22-Sep-15.
 */
public class MobileNsdHelper {
    public final String TAG = MainActivity.class.getSimpleName();
    public final String SERVICE_TYPE = "_http._tcp.";
    public String mServiceName = "Wemote";
    public NsdServiceInfo mService;

    Context mContext;
    NsdManager mNsdManager;
    NsdManager.ResolveListener mResolveListener;
    NsdManager.DiscoveryListener mDiscoveryListener;

    public MobileNsdHelper(Context context) {
        mContext = context;
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }

    public void initializeDiscoveryListener() {
        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                Log.d(TAG, "Service discovery success" + service);
                if (!service.getServiceType().equals(SERVICE_TYPE)) {
                    Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
                }
                /*else if (service.getServiceName().equals(mServiceName)) {
                    Log.d(TAG, "Same machine: " + mServiceName);
                } */
                else if (service.getServiceName().contains(mServiceName)){
                    mNsdManager.resolveService(service, mResolveListener);
                }

                // Update list
                /*if((!NSDActivity.mDeviceList.contains(service.getServiceName())) && (!service.getServiceName().equals(mServiceName))) {
                    NSDActivity.mDeviceList.add(service.getServiceName());
                }*/
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                Log.e(TAG, "service lost" + service);
                mService = service;
                // Update list
                //NSDActivity.mDeviceList.remove(NSDActivity.mDeviceList.indexOf(service.getServiceName()));
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }
        };
    }

    public void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e(TAG, "Resolve failed" + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.e(TAG, "Resolve Succeeded. " + serviceInfo);

                if (serviceInfo.getServiceName().equals(mServiceName)) {
                    Log.d(TAG, "Same IP.");
                }
                mService = serviceInfo;
                ConnectFragment connectFragment = new ConnectFragment();
                connectFragment.startConnection(serviceInfo.getHost(), serviceInfo.getPort());
            }
        };
    }

    public void discoverServices() {
        mNsdManager.discoverServices(
                SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }

    public void stopDiscovery() {
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
    }

    public NsdServiceInfo getChosenServiceInfo() {
        return mService;
    }

    public void tearDown() {
        stopDiscovery();
    }
}
