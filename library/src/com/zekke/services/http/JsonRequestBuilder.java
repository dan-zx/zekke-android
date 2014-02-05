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
package com.zekke.services.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.zekke.services.R;
import com.zekke.services.util.StringUtils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class JsonRequestBuilder extends RequestBuilder {

    private static final String TAG = "JsonRequestBuilder";

    public JsonRequestBuilder(String url) {
        super(url);
    }

    @Override
    public JsonRequestBuilder setMethod(RequestMethod type) {
        return (JsonRequestBuilder) super.setMethod(type);
    }

    @Override
    public JsonRequestBuilder addHeader(HeaderField field, String value) {
        return (JsonRequestBuilder) super.addHeader(field, value);
    }

    @Override
    public JsonRequestBuilder addFormParam(String name, Object value) {
        return (JsonRequestBuilder) super.addFormParam(name, value);
    }

    @Override
    public JsonRequestBuilder setTimeout(int timeout) {
        return (JsonRequestBuilder) super.setTimeout(timeout);
    }

    @Override
    public JsonRequestBuilder setDefaultHeaders() {
        super.setDefaultHeaders();
        headers.add(new BasicHeader(HeaderField.ACCEPT.getValue(), ContentType.APPLICATION_JSON.getValue()));
        headers.add(new BasicHeader(HeaderField.ACCEPT_CHARSET.getValue(), HTTP.UTF_8));
        return this;
    }

    public <T> T callForResult(Class<T> resultType) throws RequestException {
        String json = null;

        try {
            setUpHeaders();
            setUpFormParams();
            Log.d(TAG, "URL: " + request.getURI());
            HttpResponse response = httpClient.execute(request);
            Log.d(TAG, "Status code: " + response.getStatusLine().getStatusCode());
            HttpEntity entity = response.getEntity();
            json = entity != null ? EntityUtils.toString(entity, HTTP.UTF_8) : null;
            if ((response.getStatusLine().getStatusCode() == HttpStatus.SC_OK
                    || response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED)
                    && !StringUtils.isNullOrBlank(json)) {
                return new Gson().fromJson(json, resultType);
            }
        } catch (ClientProtocolException ex) {
            Log.e(TAG, "Developer bug :(", ex);
            throw new RequestException.Builder()
                    .setMessageResource(R.string.http_protocol_error)
                    .setErrorType(ex)
                    .build();
        } catch (SocketTimeoutException ex) {
            Log.e(TAG, "Timeout", ex);
            throw new RequestException.Builder()
                    .setMessageResource(R.string.timeout_error)
                    .setErrorType(ex)
                    .build();
        } catch (ConnectTimeoutException ex) {
            Log.e(TAG, "Timeout", ex);
            throw new RequestException.Builder()
                    .setMessageResource(R.string.timeout_error)
                    .setErrorType(ex)
                    .build();
        } catch (UnsupportedEncodingException ex) {
            Log.e(TAG, "Developer bug :(", ex);
            throw new RequestException.Builder()
                    .setMessageResource(R.string.params_encoding_error)
                    .setErrorType(ex)
                    .build();
        } catch (IOException ex) {
            Log.e(TAG, "Developer bug :(", ex);
            throw new RequestException.Builder()
                    .setMessageResource(R.string.unknown_error)
                    .setErrorType(ex)
                    .build();
        } catch (JsonSyntaxException ex) {
            Log.e(TAG, "Developer bug :(", ex);
            throw new RequestException.Builder()
                    .setMessageResource(R.string.unknown_error)
                    .setErrorType(ex)
                    .build();
        } catch (ParseException ex) {
            Log.e(TAG, "Developer bug :(", ex);
            throw new RequestException.Builder()
                    .setMessageResource(R.string.parse_response_error)
                    .setErrorType(ex)
                    .build();
        } catch (Exception ex) {
            Log.e(TAG, "Developer bug :(", ex);
            throw new RequestException.Builder()
                    .setMessageResource(R.string.unknown_error)
                    .setErrorType(ex)
                    .build();
        }

        if (!StringUtils.isNullOrBlank(json)) {
            Log.e(TAG, "Server error: " + json);
            try {
                throw new Gson().fromJson(json, RequestException.class);
            } catch (JsonSyntaxException ex) {
                throw new RequestException.Builder()
                        .setMessageResource(R.string.unknown_error)
                        .setErrorType(ex)
                        .build();
            }
        }

        return null;
    }

    public <T> T callForResult(TypeToken<T> resultType) throws RequestException {
        String json = null;
        try {
            setUpHeaders();
            setUpFormParams();
            Log.d(TAG, "URL: " + request.getURI());
            HttpResponse response = httpClient.execute(request);
            Log.d(TAG, "Status code: " + response.getStatusLine().getStatusCode());
            HttpEntity entity = response.getEntity();
            json = entity != null ? EntityUtils.toString(entity, HTTP.UTF_8) : null;
            if ((response.getStatusLine().getStatusCode() == HttpStatus.SC_OK
                    || response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED)
                    && !StringUtils.isNullOrBlank(json)) {
                return new Gson().fromJson(json, resultType.getType());
            }
        } catch (ClientProtocolException ex) {
            Log.e(TAG, "Developer bug :(", ex);
            throw new RequestException.Builder()
                    .setMessageResource(R.string.http_protocol_error)
                    .setErrorType(ex)
                    .build();
        } catch (SocketTimeoutException ex) {
            Log.e(TAG, "Timeout", ex);
            throw new RequestException.Builder()
                    .setMessageResource(R.string.timeout_error)
                    .setErrorType(ex)
                    .build();
        } catch (ConnectTimeoutException ex) {
            Log.e(TAG, "Timeout", ex);
            throw new RequestException.Builder()
                    .setMessageResource(R.string.timeout_error)
                    .setErrorType(ex)
                    .build();
        } catch (UnsupportedEncodingException ex) {
            Log.e(TAG, "Developer bug :(", ex);
            throw new RequestException.Builder()
                    .setMessageResource(R.string.params_encoding_error)
                    .setErrorType(ex)
                    .build();
        } catch (IOException ex) {
            Log.e(TAG, "Developer bug :(", ex);
            throw new RequestException.Builder()
                    .setMessageResource(R.string.unknown_error)
                    .setErrorType(ex)
                    .build();
        } catch (JsonSyntaxException ex) {
            Log.e(TAG, "Developer bug :(", ex);
            throw new RequestException.Builder()
                    .setMessageResource(R.string.unknown_error)
                    .setErrorType(ex)
                    .build();
        } catch (ParseException ex) {
            Log.e(TAG, "Developer bug :(", ex);
            throw new RequestException.Builder()
                    .setMessageResource(R.string.parse_response_error)
                    .setErrorType(ex)
                    .build();
        } catch (Exception ex) {
            Log.e(TAG, "Developer bug :(", ex);
            throw new RequestException.Builder()
                    .setMessageResource(R.string.unknown_error)
                    .setErrorType(ex)
                    .build();
        }

        if (!StringUtils.isNullOrBlank(json)) {
            try {
                Log.e(TAG, "Server error: " + json);
                throw new Gson().fromJson(json, RequestException.class);
            } catch (JsonSyntaxException ex) {
                throw new RequestException.Builder()
                        .setMessageResource(R.string.unknown_error)
                        .setErrorType(ex)
                        .build();
            }
        }

        return null;
    }
}