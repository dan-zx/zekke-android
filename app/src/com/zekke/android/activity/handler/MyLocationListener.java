/*
 * Copyright 2013 ZeKKe Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zekke.android.activity.handler;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;

public class MyLocationListener implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    private static final String TAG = MyLocationListener.class.getSimpleName();

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.getErrorCode() != ConnectionResult.SUCCESS) {
            Log.e(TAG, "Connection Failed");
            switch (connectionResult.getErrorCode()) {
                case ConnectionResult.DEVELOPER_ERROR:
                    Log.e(TAG, "The application is misconfigured");
                    break;
                case ConnectionResult.INTERNAL_ERROR:
                    Log.e(TAG, "An internal error occurred");
                    break;
                case ConnectionResult.INVALID_ACCOUNT:
                    Log.e(TAG, "The client attempted to connect to the service with an invalid account name specified");
                    break;
                case ConnectionResult.LICENSE_CHECK_FAILED:
                    Log.e(TAG, "The application is not licensed to the user");
                    break;
                case ConnectionResult.NETWORK_ERROR:
                    Log.e(TAG, "A network error occurred");
                    break;
                case ConnectionResult.RESOLUTION_REQUIRED:
                    Log.e(TAG, "Completing the connection requires some form of resolution");
                    break;
                case ConnectionResult.SERVICE_DISABLED:
                    Log.e(TAG, "The installed version of Google Play services has been disabled on this device");
                    break;
                case ConnectionResult.SERVICE_INVALID:
                    Log.e(TAG, "The version of the Google Play services installed on this device is not authentic");
                    break;
                case ConnectionResult.SERVICE_MISSING:
                    Log.e(TAG, "Google Play services is missing on this device");
                    break;
                case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                    Log.e(TAG, "The installed version of Google Play services is out of date");
                    break;
                case ConnectionResult.SIGN_IN_REQUIRED:
                    Log.e(TAG, "The client attempted to connect to the service but the user is not signed in");
                    break;
            }
        }
    }

    @Override
    public void onConnected(Bundle dataBundle) {
        Log.d(TAG, "Connected");
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "Disconnected. Please re-connect");
    }
}