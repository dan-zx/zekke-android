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
package com.zekke.services.client.internal;

import com.zekke.services.client.ClientCallback;
import com.zekke.services.http.RequestException;

import android.os.AsyncTask;

public abstract class ClientAsyncTask extends AsyncTask<Object, Void, Object> {

    private final ClientCallback callback;

    public ClientAsyncTask(ClientCallback callback) {
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        if (callback != null) {
            callback.onPreExecute();
        }
    }

    @Override
    protected void onPostExecute(Object result) {
        if (result != null && callback != null) {
            callback.onFailure((RequestException) result);
        }
    }

    protected ClientCallback getCallback() {
        return callback;
    }
}