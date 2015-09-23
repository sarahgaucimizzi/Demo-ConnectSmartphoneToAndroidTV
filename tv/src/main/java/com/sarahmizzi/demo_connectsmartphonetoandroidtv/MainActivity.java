/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.sarahmizzi.demo_connectsmartphonetoandroidtv;

import android.app.Activity;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.util.Log;

/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    private final String TAG = MainActivity.class.getSimpleName();
    private String SERVICE_NAME = "Server Device";
    private String SERVICE_TYPE = "_http._tcp.";
    private NsdManager mNsdManager;
    private NsdManager.RegistrationListener mRegistrationListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);
        registerService(9000);

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
                String mServiceName = serviceInfo.getServiceName();
                SERVICE_NAME = mServiceName;
                Log.d(TAG, "Registered name : " + mServiceName);
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
                // Service has been unregistered. This only happens when you call NsdManager.unregisterService() and pass in this listener.
                Log.d(TAG, "Service unregistered: " + serviceInfo.getServiceName());
            }
        };
    }

    @Override
    protected void onPause() {
        if (mNsdManager != null){
            mNsdManager.unregisterService(mRegistrationListener);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mNsdManager != null){
            registerService(9000);
        }
    }

    @Override
    protected void onDestroy() {
        if(mNsdManager != null){
            mNsdManager.unregisterService(mRegistrationListener);
        }
        super.onDestroy();
    }

    public void registerService(int port){
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName(SERVICE_NAME);
        serviceInfo.setServiceType(SERVICE_TYPE);
        serviceInfo.setPort(port);
        mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
    }
}
