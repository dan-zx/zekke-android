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
package com.zekke.android.activity;

import javax.inject.Inject;

import com.zekke.android.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;

import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class RequirementsCheckingActivity extends RoboSherlockActivity {

    private static final String TAG = RequirementsCheckingActivity.class.getSimpleName();
    private static final int GOOGLE_PLAY_SERVICES_UNAVAILABLE_REQUEST_CODE = 9;

    @Inject private ConnectivityManager connectivityManager;

    @Override
    protected void onResume() {
        if (isConnectedToInternet()) {
            checkIfGooglePlayServicesAvailable();
        }
        super.onResume();
    }

    private boolean isConnectedToInternet() {
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isOnline = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if (!isOnline) {
            Log.e(TAG, "Device is not connected to internet");
            new AlertDialog.Builder(this)
                    .setTitle(R.string.network_connectivity_dialog_title)
                    .setMessage(R.string.network_connectivity_dialog_messege)
                    .setPositiveButton(R.string.network_connectivity_dialog_positive_button_label, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_SETTINGS));
                        }
                    })
                    .setOnCancelListener(new OnCancelActivity(this))
                    .show();
        } else {
            Log.i(TAG, "Device is connected to internet");
        }

        return isOnline;
    }

    private void checkIfGooglePlayServicesAvailable() {
        int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (errorCode != ConnectionResult.SUCCESS) {
            switch (errorCode) {
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
            }

            Dialog googlePlayServicesErrorDialog = GooglePlayServicesUtil.getErrorDialog(
                    errorCode,
                    this,
                    GOOGLE_PLAY_SERVICES_UNAVAILABLE_REQUEST_CODE, new OnCancelActivity(this));
            googlePlayServicesErrorDialog.show();
        } else {
            startMapRoutesActivity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GOOGLE_PLAY_SERVICES_UNAVAILABLE_REQUEST_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        startMapRoutesActivity();
                        break;
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void startMapRoutesActivity() {
        Log.i(TAG, "Google Play services APK is up-to-date, enabled and valid");
        startActivity(new Intent(this, MapRoutesActivity.class));
        finish();
    }

    private static class OnCancelActivity implements DialogInterface.OnCancelListener {

        private Activity activity;

        public OnCancelActivity(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            dialog.dismiss();
            activity.finish();
            activity = null;
        }
    }
}