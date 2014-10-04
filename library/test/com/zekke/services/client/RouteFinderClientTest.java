package com.zekke.services.client;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import android.content.Context;

import com.zekke.services.client.data.GeoPoint;
import com.zekke.services.client.data.Place;
import com.zekke.services.client.data.Route;
import com.zekke.services.client.impl.RouteFinderRestClient;
import com.zekke.services.http.RequestException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class RouteFinderClientTest {

    private RouteFinderClient routeFinderClient;
    private Context context;

    @Before
    public void setup() {
        context = Robolectric.application;
        Robolectric.getFakeHttpLayer().interceptHttpRequests(false); // allow real http calls
        routeFinderClient = new RouteFinderRestClient(context);
        assertThat(routeFinderClient).isNotNull();
    }

    @Test
    public void testFindRoute() throws Exception {
        final Route expected = new Route();
        expected.setPath(buildExpectedRoute());
        expected.setDistance(159.2452279695584);

        routeFinderClient.setOnRouteFoundCallback(new ClientCallbackForResult<Route>() {

            @Override
            public void onResultAcquired(Route result) {
                System.out.println("Result: " + result);
                assertThat(result).isNotNull();
                assertThat(result.getDistance()).isNotNull().isGreaterThan(0).isEqualTo(expected.getDistance());
                assertThat(result.getPath()).isNotNull().isNotEmpty().hasSameSizeAs(expected.getPath()).isEqualTo(expected.getPath());
            }

            @Override
            public void onFailure(RequestException ex) {
                String message = ex.getMessage();
                if (ex.getMessageResId() != null) message = context.getString(ex.getMessageResId());
                System.err.println("Fail: " + ex.getErrorType() + ", " + message);
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