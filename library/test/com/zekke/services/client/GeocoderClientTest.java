package com.zekke.services.client;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import android.content.Context;

import com.zekke.services.client.data.GeoPoint;
import com.zekke.services.client.data.Place;
import com.zekke.services.client.impl.GeocoderRestClient;
import com.zekke.services.http.RequestException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class GeocoderClientTest {

    private GeocoderClient geocoderClient;
    private Context context;

    @Before
    public void setup() {
        context = Robolectric.application;
        Robolectric.getFakeHttpLayer().interceptHttpRequests(false); // allow real http calls
        geocoderClient = new GeocoderRestClient(context);
        assertThat(geocoderClient).isNotNull();
    }
    
    @Test
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
                System.out.println("Result: " + result);
                assertThat(result).isNotNull().isEqualTo(p);
            }

            @Override
            public void onFailure(RequestException ex) {
                String message = ex.getMessage();
                if (ex.getMessageResId() != null) message = context.getString(ex.getMessageResId());
                System.err.println("Fail: " + ex.getErrorType() + ", " + message);
                fail();
            }
        });

        geocoderClient.findPlaceByPosition(point);
        Thread.sleep(20000);
    }

    @Test
    public void testFindNamesFoundInAreaLikeName() throws Exception {
        final List<String> expected = Arrays.asList("Estacionamiento 6", "Estacionamiento 5");

        geocoderClient.setOnNamesFoundInAreaLikeName(new ClientCallbackForResult<List<String>>() {
            @Override
            public void onResultAcquired(List<String> result) {
                System.out.println("Result: " + result);
                assertThat(result).isNotNull().isNotEmpty().isEqualTo(expected);
            }

            @Override
            public void onFailure(RequestException ex) {
                String message = ex.getMessage();
                if (ex.getMessageResId() != null) message = context.getString(ex.getMessageResId());
                System.err.println("Fail: " + ex.getErrorType() + ", " + message);
                fail();
            }
        });

        GeoPoint center = new GeoPoint();
        center.setLatitude(19.05351);
        center.setLongitude(-98.28321);
        geocoderClient.findNamesInAreaLikeName("Estacionamiento", center, 200d);
        Thread.sleep(20000);
    }

    @Test
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
                System.out.println("Result: " + result);
                assertThat(result).isNotNull().isNotEmpty().hasSameSizeAs(places).isEqualTo(places);
            }

            @Override
            public void onFailure(RequestException ex) {
                String message = ex.getMessage();
                if (ex.getMessageResId() != null) message = context.getString(ex.getMessageResId());
                System.err.println("Fail: " + ex.getErrorType() + ", " + message);
                fail();
            }
        });
        geocoderClient.findPlacesByName("Humanidades");
        Thread.sleep(20000);
    }
}