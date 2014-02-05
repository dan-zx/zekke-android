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
package com.zekke.android.activity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import roboguice.inject.InjectFragment;
import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.zekke.android.R;
import com.zekke.android.activity.handler.MyLocationListener;
import com.zekke.android.util.Constants;
import com.zekke.android.util.GeoUtils;
import com.zekke.android.util.GoogleMapType;
import com.zekke.android.widget.SimpleSuggestionsAdapter;
import com.zekke.services.client.ClientCallbackForResult;
import com.zekke.services.client.GeocoderClient;
import com.zekke.services.client.RouteFinderClient;
import com.zekke.services.client.data.GeoPoint;
import com.zekke.services.client.data.Place;
import com.zekke.services.client.data.Route;
import com.zekke.services.http.RequestException;
import com.zekke.services.util.StringUtils;

public class MapRoutesActivity extends RoboSherlockFragmentActivity {

    private static final String TAG = MapRoutesActivity.class.getSimpleName();

    private static final GoogleMapType DEFAULT_MAP_TYPE = GoogleMapType.NORMAL;
    private static final double DEFAULT_MAP_LAT = 19.05348807571192;
    private static final double DEFAULT_MAP_LNG = -98.2831871509552;
    private static final float DEFAULT_MAP_ZOOM = 17f;
    private static final int MY_LOCATION_PADDING = 40;

    private GoogleMap map;
    private List<Marker> markers;
    private Marker routeRootMarker;
    private Marker routeTargetMarker;
    private LocationClient locationClient;
    private SearchView searchView;
    private SimpleSuggestionsAdapter suggestionsAdapter;
    private Polyline routeLine;

    @InjectFragment(R.id.map_fragment) private SupportMapFragment mapFragment;
    @InjectView(R.id.loading_spinner)  private ProgressBar loadingSpinner;
    @Inject                            private LocationManager locationManager;
    @Inject                            private GeocoderClient geocoderClient;
    @Inject                            private RouteFinderClient routeFinderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_routes_activity);
        setUpMap();
        setUpLocationClient();
        setUpSuggestionsAdapter();
        setUpGeocoderClient();
        setUpRouteFinderClient();
    }

    @Override
    protected void onResume() {
        if (map != null) {
            GoogleMapType type = GoogleMapType.valueOf(getSharedPreferences().getString(Constants.PREF_KEY_MAP_LAST_TYPE, DEFAULT_MAP_TYPE.name()));

            if (map.getMapType() != type.getAndroidApiType()) {
                map.setMapType(type.getAndroidApiType());
            }
        }

        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!locationClient.isConnected()) {
            locationClient.connect();
        }
    }

    @Override
    protected void onStop() {
        if (locationClient.isConnected()) {
            locationClient.disconnect();
        }

        super.onStop();
    }

    @Override
    protected void onPause() {
        if (map != null) {
            getSharedPreferences().edit()
                    .putString(Constants.PREF_KEY_MAP_LAST_LAT, Double.toString(map.getCameraPosition().target.latitude))
                    .putString(Constants.PREF_KEY_MAP_LAST_LNG, Double.toString(map.getCameraPosition().target.longitude))
                    .putFloat(Constants.PREF_KEY_MAP_LAST_ZOOM, map.getCameraPosition().zoom)
                    .commit();
        }

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.map_routes, menu);
        setUpSearchView(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.my_location_menu_item:
                if (!isMyLocationEnabled()) {
                    Toast.makeText(getApplicationContext(), R.string.my_location_diseabled, Toast.LENGTH_LONG).show();
                } else {
                    Location location = locationClient.getLastLocation();
                    if (location != null) {
                        LatLng[] bounds = GeoUtils.boundingLatLng(new LatLng(location.getLatitude(), location.getLongitude()), location.getAccuracy());
                        map.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(bounds[0], bounds[1]), MY_LOCATION_PADDING));
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.my_location_unavailable, Toast.LENGTH_LONG).show();
                    }
                }
                return true;
            case R.id.find_route_menu_item:
                if (routeRootMarker != null && routeTargetMarker != null) {
                    GeoPoint rootPosition = new GeoPoint();
                    rootPosition.setLatitude(routeRootMarker.getPosition().latitude);
                    rootPosition.setLongitude(routeRootMarker.getPosition().longitude);
                    GeoPoint targetPosition = new GeoPoint();
                    targetPosition.setLatitude(routeTargetMarker.getPosition().latitude);
                    targetPosition.setLongitude(routeTargetMarker.getPosition().longitude);
                    routeFinderClient.findRoute(rootPosition, targetPosition);
                } else if (routeRootMarker == null && routeTargetMarker != null) {
                    Toast.makeText(getApplicationContext(), R.string.no_route_root_marker_selected, Toast.LENGTH_LONG).show();
                } else if (routeRootMarker != null && routeTargetMarker == null) {
                    Toast.makeText(getApplicationContext(), R.string.no_route_target_marker_selected, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_route_markers_selected, Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.settings_menu_item:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setUpMap() {
        if (map == null) {
            map = mapFragment.getMap();
            double lat = Double.parseDouble(getSharedPreferences().getString(Constants.PREF_KEY_MAP_LAST_LAT, Double.toString(DEFAULT_MAP_LAT)));
            double lng = Double.parseDouble(getSharedPreferences().getString(Constants.PREF_KEY_MAP_LAST_LNG, Double.toString(DEFAULT_MAP_LNG)));
            GoogleMapType type = GoogleMapType.valueOf(getSharedPreferences().getString(Constants.PREF_KEY_MAP_LAST_TYPE, DEFAULT_MAP_TYPE.name()));
            float zoom = getSharedPreferences().getFloat(Constants.PREF_KEY_MAP_LAST_ZOOM, DEFAULT_MAP_ZOOM);
            map.setMapType(type.getAndroidApiType());
            map.getUiSettings().setMyLocationButtonEnabled(false);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoom));
            map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng point) {
                    GeoPoint position = new GeoPoint();
                    position.setLatitude(point.latitude);
                    position.setLongitude(point.longitude);
                    geocoderClient.findPlaceByPosition(position);
                }
            });
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(final Marker marker) {
                    marker.showInfoWindow();
                    new AlertDialog.Builder(MapRoutesActivity.this)
                            .setTitle(R.string.route_marker_dialog_title)
                            .setItems(R.array.route_marker_dialog_options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            if (routeRootMarker != null) {
                                                routeRootMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                            }
                                            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                                            routeRootMarker = marker;
                                            break;
                                        case 1:
                                            if (routeTargetMarker != null) {
                                                routeTargetMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                            }
                                            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                            routeTargetMarker = marker;
                                            break;
                                    }
                                }
                            })
                            .show();
                    return true;
                }
            });
            markers = new ArrayList<Marker>();
        }
    }

    private void setUpLocationClient() {
        MyLocationListener myLocationListener = new MyLocationListener();
        locationClient = new LocationClient(this, myLocationListener, myLocationListener);
    }

    private void setUpSuggestionsAdapter() {
        MatrixCursor cursor = new MatrixCursor(new String[]{BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1});
        suggestionsAdapter = new SimpleSuggestionsAdapter(getSupportActionBar().getThemedContext(), cursor);
    }

    private void setUpSearchView(Menu menu) {
        final MenuItem searchViewMenuItem = menu.findItem(R.id.search_places_menu_item);
        searchView = (SearchView) searchViewMenuItem.getActionView();
        searchView.setSuggestionsAdapter(suggestionsAdapter);
        searchView.setQueryHint(getString(R.string.search_places_menu_title));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!StringUtils.isNullOrBlank(query)) {
                    searchViewMenuItem.collapseActionView();
                    searchView.setQuery(null, false);
                    geocoderClient.findPlacesByName(query);
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!StringUtils.isNullOrBlank(newText) && newText.length() > 1) { // The query should be at least 2 characters length
                    double radius = GeoUtils.distanceBetween(map.getProjection().getVisibleRegion().farLeft, map.getProjection().getVisibleRegion().farRight);
                    GeoPoint center = new GeoPoint();
                    center.setLatitude(map.getCameraPosition().target.latitude);
                    center.setLongitude(map.getCameraPosition().target.longitude);
                    geocoderClient.findNamesInAreaLikeName(newText, center, radius);
                }
                return false;
            }
        });
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionClick(int position) {
                Cursor c = (Cursor) suggestionsAdapter.getItem(position);
                String query = c.getString(c.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
                searchViewMenuItem.collapseActionView();
                searchView.setQuery(null, false);
                geocoderClient.findPlacesByName(query);
                return true;
            }

            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }
        });
    }

    private void setUpGeocoderClient() {
        geocoderClient.setOnPlaceFoundByPositionCallback(new ClientCallbackForResult<Place>() {
            @Override
            public void onPreExecute() {
                showLoadingSpinner();
            }

            @Override
            public void onResultAcquired(Place result) {
                putMarkerOnMap(result);
                hideLoadingSpinner();
            }

            @Override
            public void onFailure(RequestException ex) {
                hideLoadingSpinner();
                Log.e(TAG, "Search for place failed", ex);
                if (ex.getMessageResId() != null) {
                    Toast.makeText(getApplicationContext(), ex.getMessageResId(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        geocoderClient.setOnPlacesFoundByNameCallback(new ClientCallbackForResult<List<Place>>() {
            @Override
            public void onPreExecute() {
                showLoadingSpinner();
            }

            @Override
            public void onResultAcquired(List<Place> result) {
                putMarkersOnMap(result);
                hideLoadingSpinner();
            }

            @Override
            public void onFailure(RequestException ex) {
                hideLoadingSpinner();
                Log.e(TAG, "Search for places failed", ex);
                if (ex.getMessageResId() != null) {
                    Toast.makeText(getApplicationContext(), ex.getMessageResId(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        geocoderClient.setOnNamesFoundInAreaLikeName(new ClientCallbackForResult<List<String>>() {
            @Override
            public void onPreExecute() {
                showLoadingSpinner();
            }

            @Override
            public void onResultAcquired(List<String> result) {
                setPlaceNameSuggestions(result);
                hideLoadingSpinner();
            }

            @Override
            public void onFailure(RequestException ex) {
                hideLoadingSpinner();
                Log.e(TAG, "Search for place names failed", ex);
                if (ex.getMessageResId() != null) {
                    Toast.makeText(getApplicationContext(), ex.getMessageResId(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void setUpRouteFinderClient() {
        routeFinderClient.setOnRouteFoundCallback(new ClientCallbackForResult<Route>() {
            @Override
            public void onPreExecute() {
                showLoadingSpinner();
            }

            @Override
            public void onResultAcquired(Route result) {
                hideLoadingSpinner();
                drawRouteOnMap(result);
            }

            @Override
            public void onFailure(RequestException ex) {
                hideLoadingSpinner();
                if (ex.getMessageResId() != null) {
                    Toast.makeText(getApplicationContext(), ex.getMessageResId(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private SharedPreferences getSharedPreferences() {
        return getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    private boolean isMyLocationEnabled() {
        try {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            Log.d(TAG, "Location manager is unavailable");
            return false;
        }
    }

    private void showLoadingSpinner() {
        if (loadingSpinner.getVisibility() == View.GONE) {
            loadingSpinner.setVisibility(View.VISIBLE);
        }
    }

    private void hideLoadingSpinner() {
        if (loadingSpinner.getVisibility() == View.VISIBLE) {
            loadingSpinner.setVisibility(View.GONE);
        }
    }

    private void putMarkerOnMap(Place place) {
        if (map != null) {
            if (place != null) {
                clearRoute();
                Marker marker = map.addMarker(new MarkerOptions()
                        .position(new LatLng(place.getPosition().getLatitude(), place.getPosition().getLongitude()))
                        .title(place.getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                markers.add(marker);
                map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                marker.showInfoWindow();
            } else {
                Toast.makeText(getApplicationContext(), R.string.no_place_found_in_location, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void putMarkersOnMap(List<Place> places) {
        if (map != null) {
            if (places != null && !places.isEmpty()) {
                clearMarkers();
                clearRoute();
                for (Place place : places) {
                    markers.add(map.addMarker(new MarkerOptions()
                            .position(new LatLng(place.getPosition().getLatitude(), place.getPosition().getLongitude()))
                            .title(place.getName())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))));
                }
                Marker lastMarker = markers.get(markers.size() - 1);
                map.animateCamera(CameraUpdateFactory.newLatLng(lastMarker.getPosition()));
                lastMarker.showInfoWindow();
            } else {
                Toast.makeText(getApplicationContext(), R.string.no_places_found_by_given_name, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void drawRouteOnMap(Route route) {
        if (map != null) {
            if (route != null) {
                clearRoute();
                PolylineOptions pathOptions = new PolylineOptions()
                        .color(Color.RED);
                for (Place pathPlace : route.getPath()) {
                    pathOptions.add(new LatLng(pathPlace.getPosition().getLatitude(), pathPlace.getPosition().getLongitude()));
                }

                routeLine = map.addPolyline(pathOptions);
                Toast.makeText(getApplicationContext(), getString(R.string.route_distance, route.getDistance()), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), R.string.no_route_found, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setPlaceNameSuggestions(List<String> names) {
        if (names != null) {
            MatrixCursor cursor = new MatrixCursor(new String[]{BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1});
            for (int i = 0; i < names.size(); i++) {
                cursor.addRow(new String[]{String.valueOf(i + 1), names.get(i)});
            }

            suggestionsAdapter.changeCursor(cursor);
        }
    }

    private void clearMarkers() {
        for (Marker marker : markers) {
            marker.remove();
        }

        routeRootMarker = null;
        routeTargetMarker = null;
        markers.clear();
    }

    private void clearRoute() {
        if (routeLine != null) {
            routeLine.remove();
            routeLine = null;
        }
    }
}