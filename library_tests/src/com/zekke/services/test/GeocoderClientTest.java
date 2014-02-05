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
package com.zekke.services.test;

import java.util.Arrays;
import java.util.List;

import android.test.AndroidTestCase;
import android.util.Log;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.zekke.services.client.ClientCallbackForResult;
import com.zekke.services.client.GeocoderClient;
import com.zekke.services.client.data.GeoPoint;
import com.zekke.services.client.data.Place;
import com.zekke.services.config.ServiceClientsModule;
import com.zekke.services.http.RequestException;

public class GeocoderClientTest extends AndroidTestCase {

    private static final String TAG = "GeocoderClientTest";

    private GeocoderClient geocoderClient;

    public void setUp() throws Exception {
        Injector injector = Guice.createInjector(new ServiceClientsModule(getContext()));
        geocoderClient = injector.getInstance(GeocoderClient.class);
    }

    public void testFindPlaceByPosition() throws Exception {
        final Place p = new Place();
        p.setId(129L);
        p.setName("CIRIA - 2");
        GeoPoint point = new GeoPoint();
        point.setLatitude(19.054012874144);
        point.setLongitude(-98.283082544804);
        p.setPosition(point);

        geocoderClient.setOnPlaceFoundByPositionCallback(new ClientCallbackForResult<Place>() {

            @Override
            public void onResultAcquired(Place result) {
                Log.i(TAG, "Result: " + result);
                assertNotNull(result);
                assertEquals(p, result);
            }

            @Override
            public void onFailure(RequestException ex) {
                String message = ex.getMessage();
                if (ex.getMessageResId() != null) {
                    message = getContext().getString(ex.getMessageResId());
                }
                Log.e(TAG, "Fail: " + ex.getErrorType() + ", " + message);
                fail();
            }
        });

        geocoderClient.findPlaceByPosition(point);
        Thread.sleep(20000);
    }

    public void testFindNamesFoundInAreaLikeName() throws Exception {
        final List<String> expected = Arrays.asList("Estacionamiento 6", "Estacionamiento 5");

        geocoderClient.setOnNamesFoundInAreaLikeName(new ClientCallbackForResult<List<String>>() {
            @Override
            public void onResultAcquired(List<String> result) {
                Log.i(TAG, "Result: " + result);
                assertNotNull(result);
                assertFalse(result.isEmpty());
                assertEquals(expected, result);
            }

            @Override
            public void onFailure(RequestException ex) {
                String message = ex.getMessage();
                if (ex.getMessageResId() != null) {
                    message = getContext().getString(ex.getMessageResId());
                }
                Log.e(TAG, "Fail: " + ex.getErrorType() + ", " + message);
                fail();
            }
        });

        GeoPoint center = new GeoPoint();
        center.setLatitude(19.05351);
        center.setLongitude(-98.28321);
        geocoderClient.findNamesInAreaLikeName("Estacionamiento", center, 200d);
        Thread.sleep(20000);
    }

    public void testFindPlacesByName() throws Exception {
        Place p1 = new Place();
        p1.setId(32L);
        p1.setName("Humanidades");
        GeoPoint pp1 = new GeoPoint();
        pp1.setLatitude(19.053500752022);
        pp1.setLongitude(-98.280909955502);
        p1.setPosition(pp1);

        Place p2 = new Place();
        p2.setId(33L);
        p2.setName("Humanidades - 2");
        GeoPoint pp2 = new GeoPoint();
        pp2.setLatitude(19.053264972494);
        pp2.setLongitude(-98.280462026596);
        p2.setPosition(pp2);

        Place p3 = new Place();
        p3.setId(34L);
        p3.setName("Humanidades - 3");
        GeoPoint pp3 = new GeoPoint();
        pp3.setLatitude(19.052899893854);
        pp3.setLongitude(-98.280582726002);
        p3.setPosition(pp3);

        Place p4 = new Place();
        p4.setId(35L);
        p4.setName("Humanidades - 4");
        GeoPoint pp4 = new GeoPoint();
        pp4.setLatitude(19.052547490794);
        pp4.setLongitude(-98.28110575676);
        p4.setPosition(pp4);

        final List<Place> places = Arrays.asList(p1, p2, p3, p4);
        geocoderClient.setOnPlacesFoundByNameCallback(new ClientCallbackForResult<List<Place>>() {

            @Override
            public void onResultAcquired(List<Place> result) {
                Log.i(TAG, "Result: " + result);
                assertNotNull(result);
                assertFalse(result.isEmpty());
                assertEquals(places.size(), result.size());
                assertEquals(places, result);
            }

            @Override
            public void onFailure(RequestException ex) {
                String message = ex.getMessage();
                if (ex.getMessageResId() != null) {
                    message = getContext().getString(ex.getMessageResId());
                }
                Log.e(TAG, "Fail: " + ex.getErrorType() + ", " + message);
                fail();
            }
        });
        geocoderClient.findPlacesByName("Humanidades");
        Thread.sleep(20000);
    }
}