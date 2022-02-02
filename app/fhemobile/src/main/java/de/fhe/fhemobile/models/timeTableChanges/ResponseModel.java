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
package de.fhe.fhemobile.models.timeTableChanges;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import de.fhe.fhemobile.vos.timetable.TimeTableEventVo;

public class ResponseModel {

	@SerializedName("error")
	private Boolean error;
	@SerializedName("counter")
	private Integer counter;
	@SerializedName("oldest_db_time")
	private long oldestDbTime;
	@SerializedName("changes")
	private List<Change> changes = new ArrayList<Change>();

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

	public List<Change> getChanges() {
		return changes;
	}

	public void setChanges(final List<Change> changes) {
		this.changes = changes;
	}
	public class Change {

		@SerializedName("event_splus_key")
		private String eventSplusKey;
		@SerializedName("changes_reason")
		private Integer changesReason;
		@SerializedName("changes_reason_text")
		private String changesReasonText;
		@SerializedName("new_event_json")
		private TimeTableEventVo newEventJson;
		@SerializedName("change_date")
		private String changeDate;
		@SerializedName("set_splus_key")
		private String setSplusKey;
		@SerializedName("modul_splus_key")
		private String modulSplusKey;


		public String getEventSplusKey() {
			return eventSplusKey;
		}

		public void setEventSplusKey(final String eventSplusKey) {
			this.eventSplusKey = eventSplusKey;
		}

		public Integer getChangesReason() {
			return changesReason;
		}

		public void setChangesReason(final Integer changesReason) {
			this.changesReason = changesReason;
		}

		public String getChangesReasonText() {
			return changesReasonText;
		}

		public void setChangesReasonText(final String changesReasonText) {
			this.changesReasonText = changesReasonText;
		}

		public TimeTableEventVo getNewEventJson() {
			return newEventJson;
		}

		public void setNewEventJson(final TimeTableEventVo newEventJson) {
			this.newEventJson = newEventJson;
		}

		public String getChangeDate() {
			return changeDate;
		}

		public void setChangeDate(final String changeDate) {
			this.changeDate = changeDate;
		}

		public String getSetSplusKey() {
			return setSplusKey;
		}

		public void setSetSplusKey(final String setSplusKey) {
			this.setSplusKey = setSplusKey;
		}

		public String getModulSplusKey() {
			return modulSplusKey;
		}

		public void setModulSplusKey(final String modulSplusKey) {
			this.modulSplusKey = modulSplusKey;
		}

	}

}
