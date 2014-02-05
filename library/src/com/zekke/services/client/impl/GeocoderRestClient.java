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
package com.zekke.services.client.impl;

import java.util.List;

import android.content.Context;
import android.net.Uri;

import com.google.gson.reflect.TypeToken;

import com.zekke.services.R;
import com.zekke.services.client.ClientCallbackForResult;
import com.zekke.services.client.GeocoderClient;
import com.zekke.services.client.data.GeoPoint;
import com.zekke.services.client.data.Place;
import com.zekke.services.client.internal.ClientAsyncTaskForResult;
import com.zekke.services.http.JsonRequestBuilder;
import com.zekke.services.http.RequestMethod;
import com.zekke.services.util.StringUtils;

public class GeocoderRestClient extends BaseRestClient implements GeocoderClient {

    private ClientCallbackForResult<Place> onPlaceFoundByPositionCallback;
    private ClientCallbackForResult<List<Place>> onPlacesFoundByNameCallback;
    private ClientCallbackForResult<List<String>> onNamesFoundInAreaLikeNameCallback;

    public GeocoderRestClient(Context context) {
        super(context);
    }

    @Override
    public void findPlaceByPosition(GeoPoint position) {
        new OnFindPlaceByPositionAsyncTask(onPlaceFoundByPositionCallback, getContext())
                .execute(position);
    }

    @Override
    public void findPlacesByName(String name) {
        new OnFindPlacesByNameAsyncTask(onPlacesFoundByNameCallback, getContext())
                .execute(name);
    }

    @Override
    public void findNamesInAreaLikeName(String name, GeoPoint center, Double radius) {
        new OnNamesFoundByNameAndLocationAsyncTask(onNamesFoundInAreaLikeNameCallback, getContext())
                .execute(name, center, radius);
    }

    @Override
    public void setOnPlaceFoundByPositionCallback(ClientCallbackForResult<Place> callback) {
        onPlaceFoundByPositionCallback = callback;
    }

    @Override
    public void setOnPlacesFoundByNameCallback(ClientCallbackForResult<List<Place>> callback) {
        onPlacesFoundByNameCallback = callback;
    }

    @Override
    public void setOnNamesFoundInAreaLikeName(ClientCallbackForResult<List<String>> callback) {
        onNamesFoundInAreaLikeNameCallback = callback;
    }

    private static class OnFindPlaceByPositionAsyncTask extends ClientAsyncTaskForResult<Place> {

        private final Uri.Builder uriBuilder;
        private final String latParamName;
        private final String lngParamName;

        public OnFindPlaceByPositionAsyncTask(ClientCallbackForResult<Place> callback, Context context) {
            super(callback);
            uriBuilder = Uri.parse(context.getString(R.string.zekke_services_host))
                    .buildUpon()
                    .appendEncodedPath(context.getString(R.string.geocoder_find_by_position_resource));
            latParamName = context.getResources().getStringArray(R.array.geocoder_find_by_position_args)[0];
            lngParamName = context.getResources().getStringArray(R.array.geocoder_find_by_position_args)[1];
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                GeoPoint position = (GeoPoint) params[0];
                if (position != null) {
                    if (position.getLatitude() != null) {
                        uriBuilder.appendQueryParameter(latParamName, position.getLatitude().toString());
                    }
                    if (position.getLongitude() != null) {
                        uriBuilder.appendQueryParameter(lngParamName, position.getLongitude().toString());
                    }
                }

                return new JsonRequestBuilder(uriBuilder.build().toString())
                        .setMethod(RequestMethod.GET)
                        .setDefaultHeaders()
                        .callForResult(Place.class);
            } catch (Exception ex) {
                return ex;
            }
        }
    }

    private static class OnFindPlacesByNameAsyncTask extends ClientAsyncTaskForResult<List<Place>> {

        private final Uri.Builder uriBuilder;

        public OnFindPlacesByNameAsyncTask(ClientCallbackForResult<List<Place>> callback, Context context) {
            super(callback);
            uriBuilder = Uri.parse(context.getString(R.string.zekke_services_host))
                    .buildUpon()
                    .appendEncodedPath(context.getString(R.string.geocoder_find_like_name_resource));
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                String name = StringUtils.scapeSpacesForUrl((String) params[0]);
                String url = String.format(uriBuilder.build().toString(), name);
                return new JsonRequestBuilder(url)
                        .setMethod(RequestMethod.GET)
                        .setDefaultHeaders()
                        .callForResult(new TypeToken<List<Place>>() {
                        });
            } catch (Exception ex) {
                return ex;
            }
        }
    }

    private static class OnNamesFoundByNameAndLocationAsyncTask extends ClientAsyncTaskForResult<List<String>> {

        private final Uri.Builder uriBuilder;
        private final String latParamName;
        private final String lngParamName;
        private final String radParamName;

        public OnNamesFoundByNameAndLocationAsyncTask(ClientCallbackForResult<List<String>> callback, Context context) {
            super(callback);
            latParamName = context.getResources().getStringArray(R.array.geocoder_find_names_in_area_like_name_args)[0];
            lngParamName = context.getResources().getStringArray(R.array.geocoder_find_names_in_area_like_name_args)[1];
            radParamName = context.getResources().getStringArray(R.array.geocoder_find_names_in_area_like_name_args)[2];
            uriBuilder = Uri.parse(context.getString(R.string.zekke_services_host))
                    .buildUpon()
                    .appendEncodedPath(context.getString(R.string.geocoder_find_names_in_area_like_name_resource));
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                String name = StringUtils.scapeSpacesForUrl((String) params[0]);
                GeoPoint position = (GeoPoint) params[1];
                Double radius = (Double) params[2];
                if (position != null) {
                    if (position.getLatitude() != null) {
                        uriBuilder.appendQueryParameter(latParamName, position.getLatitude().toString());
                    }
                    if (position.getLongitude() != null) {
                        uriBuilder.appendQueryParameter(lngParamName, position.getLongitude().toString());
                    }
                }

                if (radius != null) {
                    uriBuilder.appendQueryParameter(radParamName, radius.toString());
                }
                String url = String.format(uriBuilder.build().toString(), name);

                return new JsonRequestBuilder(url)
                        .setMethod(RequestMethod.GET)
                        .setDefaultHeaders()
                        .callForResult(new TypeToken<List<String>>() {
                        });
            } catch (Exception ex) {
                return ex;
            }
        }
    }
}