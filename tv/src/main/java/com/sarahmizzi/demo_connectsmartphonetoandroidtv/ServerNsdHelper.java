package com.sarahmizzi.demo_connectsmartphonetoandroidtv;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import java.net.ServerSocket;

/**
 * Created by Sarah on 28-Sep-15.
 */
public class ServerNsdHelper {
    private final String TAG = MainActivity.class.getSimpleName();
    private String SERVICE_NAME = "Server Device - TV";
    private String SERVICE_TYPE = "_http._tcp.";
    private NsdManager mNsdManager;
    private NsdManager.RegistrationListener mRegistrationListener;
    private String mServiceName;
    private Context mContext;

    public ServerNsdHelper(Context context){
        mContext = context;
    }

    public void initializeRegistrationListener(){
        // Setup registration listener
        mRegistrationListener = new NsdManager.RegistrationListener(){
            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Registration failed! Put debugging code here to determine why.
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Unregistration failed. Put debugging code here to determine why.
            }

            @Override
            public void onServiceRegistered(NsdServiceInfo serviceInfo) {
                mServiceName = serviceInfo.getServiceName();
                SERVICE_NAME = mServiceName;
                Log.d(TAG, "Registered name : " + mServiceName);
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
                // Service has been unregistered. This only happens when you call NsdManager.unregisterService() and pass in this listener.
                Log.e(TAG, "Service unregistered: " + serviceInfo.getServiceName());
            }
        };
    }

    public void registerService(int port){
        initializeRegistrationListener();

        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName(SERVICE_NAME);
        serviceInfo.setServiceType(SERVICE_TYPE);
        serviceInfo.setPort(port);

        mNsdManager = (NsdManager) mContext.getSystemService(Context.NSD_SERVICE);

        mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
    }

    public void unregisterService(){
        mNsdManager.unregisterService(mRegistrationListener);
    }
}
