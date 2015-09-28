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
    public ServerNsdHelper mNsdHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNsdHelper = new ServerNsdHelper(getApplicationContext());
        mNsdHelper.registerService(9000);
    }

    @Override
    protected void onPause() {
        if (mNsdHelper!= null){
            mNsdHelper.unregisterService();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mNsdHelper != null){
            mNsdHelper.registerService(9000);
        }
    }

    @Override
    protected void onDestroy() {
        if(mNsdHelper!= null){
           mNsdHelper.unregisterService();
        }
        super.onDestroy();
    }


}
