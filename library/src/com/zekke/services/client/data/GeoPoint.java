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
package com.zekke.services.client.data;

import java.io.Serializable;

import com.zekke.services.util.HashCodeConstants;

public class GeoPoint implements Serializable {

    private static final long serialVersionUID = -2333340992671095314L;
    private static final int HASH_PRIME = 31;

    private Double latitude;
    private Double longitude;

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    @Override
    public int hashCode() {
        int hash = HashCodeConstants.INIT_HASH;
        hash = HASH_PRIME * hash + (latitude != null ? latitude.hashCode() : HashCodeConstants.NULL_HASH);
        hash = HASH_PRIME * hash + (longitude != null ? longitude.hashCode() : HashCodeConstants.NULL_HASH);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        final GeoPoint other = (GeoPoint) obj;
        if ((latitude == null) ? (other.latitude != null) : !latitude.equals(other.latitude)) return false;
        if ((longitude == null) ? (other.longitude != null) : !longitude.equals(other.longitude)) return false;

        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("GeoPoint{latitude=")
                .append(latitude).append(", longitude=").append(longitude)
                .append('}').toString();
    }
}