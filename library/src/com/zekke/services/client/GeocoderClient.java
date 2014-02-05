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
package com.zekke.services.client;

import java.util.List;

import com.zekke.services.client.data.GeoPoint;
import com.zekke.services.client.data.Place;

public interface GeocoderClient {

    void findPlaceByPosition(GeoPoint position);

    void findPlacesByName(String name);

    void findNamesInAreaLikeName(String name, GeoPoint center, Double radius);

    void setOnPlaceFoundByPositionCallback(ClientCallbackForResult<Place> callback);

    void setOnPlacesFoundByNameCallback(ClientCallbackForResult<List<Place>> callback);

    void setOnNamesFoundInAreaLikeName(ClientCallbackForResult<List<String>> callback);
}