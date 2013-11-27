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

import com.google.android.gms.maps.model.LatLng;

public abstract class GeoUtils {

    private static final int EARTH_RADIUS = 6378137; //in meters
    private static final double MIN_LAT = Math.toRadians(-90d);  // -PI/2
    private static final double MAX_LAT = Math.toRadians(90d);   //  PI/2
    private static final double MIN_LON = Math.toRadians(-180d); // -PI
    private static final double MAX_LON = Math.toRadians(180d);  //  PI

    public static LatLng[] boundingLatLng(LatLng centerPoint, double distance) {
        double radDist = distance / EARTH_RADIUS;
        double radLat = Math.toRadians(centerPoint.latitude);
        double radLon = Math.toRadians(centerPoint.longitude);

        double minLat = radLat - radDist;
        double maxLat = radLat + radDist;

        double minLon, maxLon;
        if (minLat > MIN_LAT && maxLat < MAX_LAT) {
            double deltaLon = Math.asin(Math.sin(radDist) / Math.cos(radLat));
            minLon = radLon - deltaLon;

            if (minLon < MIN_LON) {
                minLon += 2d * Math.PI;
            }

            maxLon = radLon + deltaLon;

            if (maxLon > MAX_LON) {
                maxLon -= 2d * Math.PI;
            }
        } else {
            minLat = Math.max(minLat, MIN_LAT);
            maxLat = Math.min(maxLat, MAX_LAT);
            minLon = MIN_LON;
            maxLon = MAX_LON;
        }

        return new LatLng[]{new LatLng(Math.toDegrees(minLat), Math.toDegrees(minLon)),
            new LatLng(Math.toDegrees(maxLat), Math.toDegrees(maxLon))};
    }

    public static double distanceBetween(LatLng point1, LatLng point2) {
        double dLat = Math.toRadians(point2.latitude - point1.latitude);
        double dLon = Math.toRadians(point2.longitude - point1.longitude);
        double lat1 = Math.toRadians(point1.latitude);
        double lat2 = Math.toRadians(point2.latitude);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return EARTH_RADIUS * c;
    }
}