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

import android.content.Context;
import android.net.Uri;

import com.zekke.services.R;
import com.zekke.services.client.ClientCallbackForResult;
import com.zekke.services.client.RouteFinderClient;
import com.zekke.services.client.data.GeoPoint;
import com.zekke.services.client.data.Route;
import com.zekke.services.client.internal.ClientAsyncTaskForResult;
import com.zekke.services.http.JsonRequestBuilder;
import com.zekke.services.http.RequestMethod;

public class RouteFinderRestClient extends BaseRestClient implements RouteFinderClient {

    private ClientCallbackForResult<Route> onRouteFoundCallback;

    public RouteFinderRestClient(Context context) {
        super(context);
    }

    @Override
    public void findRoute(GeoPoint rootPosition, GeoPoint targetPosition) {
        new OnFindRouteAsyncTask(onRouteFoundCallback, getContext())
                .execute(rootPosition, targetPosition);
    }

    @Override
    public void setOnRouteFoundCallback(ClientCallbackForResult<Route> callback) {
        onRouteFoundCallback = callback;
    }

    private static class OnFindRouteAsyncTask extends ClientAsyncTaskForResult<Route> {

        private final Uri.Builder uriBuilder;
        private final String rootLatitudeParamName;
        private final String rootLongitudeParamName;
        private final String targetLatitudeParamName;
        private final String targetLongitudeParamName;

        public OnFindRouteAsyncTask(ClientCallbackForResult<Route> callback, Context context) {
            super(callback);
            uriBuilder = Uri.parse(context.getString(R.string.zekke_services_host))
                    .buildUpon()
                    .appendEncodedPath(context.getString(R.string.route_finder_find_route_resource));
            rootLatitudeParamName = context.getResources().getStringArray(R.array.route_finder_find_route_args)[0];
            rootLongitudeParamName = context.getResources().getStringArray(R.array.route_finder_find_route_args)[1];
            targetLatitudeParamName = context.getResources().getStringArray(R.array.route_finder_find_route_args)[2];
            targetLongitudeParamName = context.getResources().getStringArray(R.array.route_finder_find_route_args)[3];
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                GeoPoint rootPosition = (GeoPoint) params[0];
                GeoPoint targetPosition = (GeoPoint) params[1];
                if (rootPosition != null) {
                    if (rootPosition.getLatitude() != null) {
                        uriBuilder.appendQueryParameter(rootLatitudeParamName, rootPosition.getLatitude().toString());
                    }
                    if (rootPosition.getLongitude() != null) {
                        uriBuilder.appendQueryParameter(rootLongitudeParamName, rootPosition.getLongitude().toString());
                    }
                }

                if (targetPosition != null) {
                    if (targetPosition.getLatitude() != null) {
                        uriBuilder.appendQueryParameter(targetLatitudeParamName, targetPosition.getLatitude().toString());
                    }
                    if (targetPosition.getLongitude() != null) {
                        uriBuilder.appendQueryParameter(targetLongitudeParamName, targetPosition.getLongitude().toString());
                    }
                }

                return new JsonRequestBuilder(uriBuilder.build().toString())
                        .setMethod(RequestMethod.GET)
                        .setDefaultHeaders()
                        .callForResult(Route.class);
            } catch (Exception ex) {
                return ex;
            }
        }
    }
}