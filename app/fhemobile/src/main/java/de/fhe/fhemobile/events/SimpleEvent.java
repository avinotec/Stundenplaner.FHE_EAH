/*
 * Copyright (c) 2014-2019 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fhe.fhemobile.events;

public class SimpleEvent implements Event {

	private String type;
	@Override public String getType() { return type; }
	public void setType(String type) { 
		this.type = type; 
	}
	
	protected Object source;
	@Override public Object getSource() {
		return source;
	}
	@Override public void setSource(Object source) {
		this.source = source;
	}
	
	public SimpleEvent(String type) {
		this.type = type;
	}

}
