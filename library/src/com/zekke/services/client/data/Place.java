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

public class Place extends SerializableIdSupport<Long> implements Serializable {

    private static final long serialVersionUID = -3178624604142829172L;
    private static final int HASH_PRIME = 43;

    private String name;
    private GeoPoint position;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GeoPoint getPosition() {
        return position;
    }

    public void setPosition(GeoPoint position) {
        this.position = position;
    }

    @Override
    public int hashCode() {
        int hash = HashCodeConstants.INIT_HASH;
        hash = HASH_PRIME * hash + (getId() != null ? getId().hashCode() : HashCodeConstants.NULL_HASH);
        hash = HASH_PRIME * hash + (name != null ? name.hashCode() : HashCodeConstants.NULL_HASH);
        hash = HASH_PRIME * hash + (position != null ? position.hashCode() : HashCodeConstants.NULL_HASH);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        final Place other = (Place)obj;
        if ((getId()  == null) ? (other.getId() != null) : !getId().equals(other.getId())) return false;
        if ((name == null) ? (other.name != null) : !name.equals(other.name)) return false;
        if ((position == null) ? (other.position != null) : !position.equals(other.position)) return false;
        
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("Place{id=").append(getId())
                .append(", name=").append(name).append(", position=")
                .append(position).append('}').toString();
    }
}