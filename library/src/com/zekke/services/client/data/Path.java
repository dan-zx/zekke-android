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

public class Path extends SerializableIdSupport<Long> implements Serializable {

    private static final long serialVersionUID = 7251495687147664173L;
    private static final int HASH_PRIME = 32;

    private Place fromPlace;
    private Place toPlace;
    private Double distance;

    public Place getFromPlace() {
        return fromPlace;
    }

    public void setFromPlace(Place fromPlace) {
        this.fromPlace = fromPlace;
    }

    public Place getToPlace() {
        return toPlace;
    }

    public void setToPlace(Place toPlace) {
        this.toPlace = toPlace;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    @Override
    public int hashCode() {
        int hash = HashCodeConstants.INIT_HASH;
        hash = HASH_PRIME * hash + (getId() != null ? getId().hashCode() : HashCodeConstants.NULL_HASH);
        hash = HASH_PRIME * hash + (fromPlace != null ? fromPlace.hashCode() : HashCodeConstants.NULL_HASH);
        hash = HASH_PRIME * hash + (toPlace != null ? toPlace.hashCode() : HashCodeConstants.NULL_HASH);
        hash = HASH_PRIME * hash + (distance != null ? distance.hashCode() : HashCodeConstants.NULL_HASH);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        final Path other = (Path)obj;
        if ((getId()  == null) ? (other.getId() != null) : !getId().equals(other.getId())) return false;
        if ((fromPlace == null) ? (other.fromPlace != null) : !fromPlace.equals(other.fromPlace)) return false;
        if ((toPlace == null) ? (other.toPlace != null) : !toPlace.equals(other.toPlace)) return false;
        if ((distance == null) ? (other.distance != null) : !distance.equals(other.distance)) return false;
        
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("Edge{id=").append(getId())
                .append(", fromPlace=").append(fromPlace).append(", toPlace=")
                .append(toPlace).append(", distance=").append(distance)
                .append('}').toString();
    }
}