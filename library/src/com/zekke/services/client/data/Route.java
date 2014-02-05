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
import java.util.ArrayList;
import java.util.List;

import com.zekke.services.util.HashCodeConstants;

public class Route implements Serializable {

    private static final long serialVersionUID = -2680714702892382586L;
    private static final int HASH_PRIME = 17;

    private Double distance;
    private List<Place> path = new ArrayList<Place>(0);

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public List<Place> getPath() {
        return path;
    }

    public void setPath(List<Place> path) {
        this.path = path;
    }

    @Override
    public int hashCode() {
        int hash = HashCodeConstants.INIT_HASH;
        hash = HASH_PRIME * hash + (distance != null ? distance.hashCode() : HashCodeConstants.NULL_HASH);
        hash = HASH_PRIME * hash + (path != null ? path.hashCode() : HashCodeConstants.NULL_HASH);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        final Route other = (Route)obj;
        if ((distance == null) ? (other.distance != null) : !distance.equals(other.distance)) return false;
        if ((path == null) ? (other.path != null) : !path.equals(other.path)) return false;

        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("Route{distance=").append(distance)
                .append(", path=").append(path).append('}').toString();
    }
}