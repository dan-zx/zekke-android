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
package com.zekke.services.config;

import com.zekke.services.client.GeocoderClient;
import com.zekke.services.client.RouteFinderClient;
import com.zekke.services.client.impl.GeocoderRestClient;
import com.zekke.services.client.impl.RouteFinderRestClient;

import android.content.Context;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;

public class ServiceClientsModule implements Module {

    private final Context applicationContext;

    public ServiceClientsModule(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void configure(Binder binder) { }

    @Provides
    public GeocoderClient provideGeocoderClient() {
        GeocoderClient geocoderClient = new GeocoderRestClient(applicationContext);
        return geocoderClient;
    }

    @Provides
    public RouteFinderClient provideRouteFinderClient() {
        RouteFinderClient routeFinderClient = new RouteFinderRestClient(applicationContext);
        return routeFinderClient;
    }
}