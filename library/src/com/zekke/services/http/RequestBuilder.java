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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import com.zekke.services.R;
import com.zekke.services.util.StringUtils;

import android.util.Log;

public class RequestBuilder {

    private static final String TAG = "RequestBuilder";
    private static final int DEFAULT_TIMEOUT = 30000;

    protected final String url;
    protected final HttpClient httpClient;
    protected final List<NameValuePair> formParams;
    protected final List<BasicHeader> headers;
    protected HttpRequestBase request;

    public RequestBuilder(String url) {
        httpClient = new DefaultHttpClient();
        HttpParams httpParams = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, DEFAULT_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_TIMEOUT);
        formParams = new ArrayList<NameValuePair>(0);
        headers = new ArrayList<BasicHeader>(0);
        this.url = url;
    }

    public RequestBuilder setMethod(RequestMethod type) {
        switch (type) {
            case GET:
                request = new HttpGet(url);
                break;
            case POST:
                request = new HttpPost(url);
                break;
            case PUT:
                request = new HttpPut(url);
                break;
            case DELETE:
                request = new HttpDelete(url);
                break;
            default: //TODO: complete switch
                request = new HttpGet(url);
                break;
        }

        return this;
    }

    public RequestBuilder addFormParam(String name, Object value) {
        if (!StringUtils.isNullOrBlank(name) && value != null) {
            formParams.add(new BasicNameValuePair(name, value.toString()));
        }

        return this;
    }

    public RequestBuilder addHeader(HeaderField field, String value) {
        headers.add(new BasicHeader(field.getValue(), value));
        return this;
    }
    
    public RequestBuilder setTimeout(int timeout) {
	HttpParams httpParams = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
        HttpConnectionParams.setSoTimeout(httpParams, timeout);
	return this;
    }

    public RequestBuilder setDefaultHeaders() {
        String clientAcceptLanguage = Locale.getDefault().toString().replace('_', '-');
        headers.add(new BasicHeader(HeaderField.ACCEPT_LANGUAGE.getValue(), clientAcceptLanguage));
        return this;
    }

    protected void setUpHeaders() {
        if (!headers.isEmpty()) {
            Log.d(TAG, "Headers: " + headers);
            request.setHeaders(headers.toArray(new Header[headers.size()]));
        }
    }

    protected void setUpFormParams() throws UnsupportedEncodingException {
        if (!formParams.isEmpty() && request instanceof HttpEntityEnclosingRequest) {
            Log.d(TAG, "Form params: " + formParams);
            ((HttpEntityEnclosingRequest) request).setEntity(new UrlEncodedFormEntity(formParams));
        }
    }

    public void call() throws RequestException {
        try {
            setUpHeaders();
            setUpFormParams();
            Log.d(TAG, "URL: " + request.getURI());
            HttpResponse response = httpClient.execute(request);
            Log.d(TAG, "Status code: " + response.getStatusLine().getStatusCode());
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
        } catch (Exception ex) {
            Log.e(TAG, "Developer bug :(", ex);
            throw new RequestException.Builder()
                    .setMessageResource(R.string.unknown_error)
                    .setErrorType(ex)
                    .build();
        }
    }
}