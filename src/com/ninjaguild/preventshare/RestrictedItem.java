/*
    PreventShare
    Copyright (C) 2016  NinjaStix
    ninjastix84@gmail.com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.ninjaguild.preventshare;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class RestrictedItem implements ConfigurationSerializable {

	private final String displayName;
	private final List<String> lore;
	
	public RestrictedItem(final String displayName, final List<String> lore) {
		this.displayName = displayName;
		this.lore = lore;
	}
	
	@SuppressWarnings("unchecked")
	public RestrictedItem(Map<String, Object> data) {
    	this.displayName = (String)data.get("displayName");
		this.lore = (List<String>)data.get("lore");
	}

	protected String getDisplayName() {
		return displayName;
	}
	
	protected List<String> getLore() {
		return lore;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> data = new HashMap<>();
		data.put("displayName", displayName);
		data.put("lore", lore);
		
		return data;
	}
	
//    @Override
//    public int hashCode() {
//        int hash = 1;
//
//        hash = hash * 31 + getDisplayName().hashCode();
//        hash = hash * 31 + getLore().hashCode();
//
//        return hash;
//    }
	
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) {
//            return true;
//        }
//        if (!(obj instanceof RestrictedItem)) {
//            return false;
//        }
//
//        RestrictedItem item = (RestrictedItem)obj;
//        return (getDisplayName().equals(item.getDisplayName()) && getLore().equals(item.getLore()));
//    }
    
    @Override
    public int hashCode() {
    	return new HashCodeBuilder(17, 31).
    			append(getDisplayName()).
    			append(getLore()).
    			hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RestrictedItem)) {
            return false;
        }

        RestrictedItem item = (RestrictedItem)obj;
    	return new EqualsBuilder().
    			append(getDisplayName(), item.getDisplayName()).
    			append(getLore(), item.getLore()).
    			isEquals();
    }

}
