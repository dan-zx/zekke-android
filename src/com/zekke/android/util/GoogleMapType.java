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
package com.zekke.android.util;

import com.zekke.android.R;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;

public enum GoogleMapType {

    NORMAL(GoogleMap.MAP_TYPE_NORMAL, "roadmap"),
    SATELLITE(GoogleMap.MAP_TYPE_SATELLITE, "satellite"),
    TERRAIN(GoogleMap.MAP_TYPE_TERRAIN, "terrain"),
    HYBRID(GoogleMap.MAP_TYPE_HYBRID, "hybrid");

    private final int androidApiType;
    private final String staticMapApiType;

    GoogleMapType(int androidApiType, String staticMapApiType) {
        this.androidApiType = androidApiType;
        this.staticMapApiType = staticMapApiType;
    }

    public int getAndroidApiType() {
        return androidApiType;
    }

    public String getStaticMapApiType() {
        return staticMapApiType;
    }

    public String toString(Context context) {
        return context.getResources().getStringArray(R.array.google_map_type_strings)[ordinal()];
    }
}