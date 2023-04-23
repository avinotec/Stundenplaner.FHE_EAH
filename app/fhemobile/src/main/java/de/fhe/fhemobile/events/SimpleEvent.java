/*
 *  Copyright (c) 2014-2022 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package de.fhe.fhemobile.events;

public class SimpleEvent implements Event {

	protected Object source;
	private final String type;


	public SimpleEvent(final String type) {
		this.type = type;
	}

    @Override
    public String getType() {
        return type;
    }

// --Commented out by Inspection START (29.03.2023 02:26):
//    public void setType(final String type) {
//        this.type = type;
//    }
// --Commented out by Inspection STOP (29.03.2023 02:26)

    @Override
    public Object getSource() {
        return source;
    }

    @Override
    public void setSource(final Object source) {
        this.source = source;
    }

}
