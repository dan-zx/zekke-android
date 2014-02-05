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
import com.zekke.services.client.RouteFinderClient;
import com.zekke.services.client.data.GeoPoint;
import com.zekke.services.client.data.Place;
import com.zekke.services.client.data.Route;
import com.zekke.services.config.ServiceClientsModule;
import com.zekke.services.http.RequestException;

public class RouteFinderClientTest extends AndroidTestCase {

    private static final String TAG = "RouteFinderClientTest";

    private RouteFinderClient routeFinderClient;

    public void setUp() throws Exception {
        Injector injector = Guice.createInjector(new ServiceClientsModule(getContext()));
        routeFinderClient = injector.getInstance(RouteFinderClient.class);
    }

    public void testFindRoute() throws Exception {
        final Route expected = new Route();
        expected.setPath(buildExpectedRoute());
        expected.setDistance(159.2452279695584);

        routeFinderClient.setOnRouteFoundCallback(new ClientCallbackForResult<Route>() {

            @Override
            public void onResultAcquired(Route result) {
                Log.i(TAG, "Result: " + result);
                assertNotNull(result);
                // Distance
                assertNotNull(result.getDistance());
                assertTrue(result.getDistance() > 0);
                assertEquals(expected.getDistance(), result.getDistance());

                // Route
                assertNotNull(result.getPath());
                assertFalse(result.getPath().isEmpty());
                assertEquals(expected.getPath().size(), result.getPath().size());
                assertEquals(expected.getPath(), result.getPath());
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
        Place root = expected.getPath().get(expected.getPath().size() - 1);
        Place target = expected.getPath().get(0);
        routeFinderClient.findRoute(root.getPosition(), target.getPosition());
        Thread.sleep(20000);
    }

    private List<Place> buildExpectedRoute() {
        Place p9 = new Place();
        p9.setId(9l);
        p9.setName("Sorteo UDLA");
        GeoPoint point9 = new GeoPoint();
        point9.setLatitude(19.056403601544);
        point9.setLongitude(-98.282444179058);
        p9.setPosition(point9);

        Place p8 = new Place();
        p8.setId(8l);
        p8.setName("Estacionamiento 9 - Entrada");
        GeoPoint point8 = new GeoPoint();
        point8.setLatitude(19.05643655937);
        point8.setLongitude(-98.283058404922);
        p8.setPosition(point8);

        Place p7 = new Place();
        p7.setId(7l);
        p7.setName("Planta Fisica");
        GeoPoint point7 = new GeoPoint();
        point7.setLatitude(19.056456841105);
        point7.setLongitude(-98.283441960812);
        p7.setPosition(point7);

        Place p6 = new Place();
        p6.setId(6l);
        p6.setName("Acceso Principal");
        GeoPoint point6 = new GeoPoint();
        point6.setLatitude(19.056865010501);
        point6.setLongitude(-98.283720910549);
        p6.setPosition(point6);

        return Arrays.asList(p9, p8, p7, p6);
    }
}