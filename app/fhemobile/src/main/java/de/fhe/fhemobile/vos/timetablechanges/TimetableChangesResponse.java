/*
 *  Copyright (c) 2014-2019 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
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
package de.fhe.fhemobile.vos.timetablechanges;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TimetableChangesResponse {

	public Boolean getError() {
		return error;
	}

	public void setError(final Boolean error) {
		this.error = error;
	}

	public Integer getCounter() {
		return counter;
	}

	public void setCounter(final Integer counter) {
		this.counter = counter;
	}

	public long getOldestDbTime() {
		return oldestDbTime;
	}

	public void setOldestDbTime(final Integer oldestDbTime) {
		this.oldestDbTime = oldestDbTime;
	}

	public List<TimetableChange> getChanges() {
		return changes;
	}

	public void setChanges(final List<TimetableChange> changes) {
		this.changes = changes;
	}

	@SerializedName("error")
	private Boolean error;

	@SerializedName("counter")
	private Integer counter;

	@SerializedName("oldest_db_time")
	private long oldestDbTime;

	@SerializedName("changes")
	private List<TimetableChange> changes = new ArrayList<TimetableChange>();
}
