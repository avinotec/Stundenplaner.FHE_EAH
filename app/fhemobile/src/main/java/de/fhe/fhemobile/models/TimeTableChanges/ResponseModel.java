package de.fhe.fhemobile.models.TimeTableChanges;

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

	public void setError(Boolean error) {
		this.error = error;
	}

	public Integer getCounter() {
		return counter;
	}

	public void setCounter(Integer counter) {
		this.counter = counter;
	}

	public long getOldestDbTime() {
		return oldestDbTime;
	}

	public void setOldestDbTime(Integer oldestDbTime) {
		this.oldestDbTime = oldestDbTime;
	}

	public List<Change> getChanges() {
		return changes;
	}

	public void setChanges(List<Change> changes) {
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

		public void setEventSplusKey(String eventSplusKey) {
			this.eventSplusKey = eventSplusKey;
		}

		public Integer getChangesReason() {
			return changesReason;
		}

		public void setChangesReason(Integer changesReason) {
			this.changesReason = changesReason;
		}

		public String getChangesReasonText() {
			return changesReasonText;
		}

		public void setChangesReasonText(String changesReasonText) {
			this.changesReasonText = changesReasonText;
		}

		public TimeTableEventVo getNewEventJson() {
			return newEventJson;
		}

		public void setNewEventJson(TimeTableEventVo newEventJson) {
			this.newEventJson = newEventJson;
		}

		public String getChangeDate() {
			return changeDate;
		}

		public void setChangeDate(String changeDate) {
			this.changeDate = changeDate;
		}

		public String getSetSplusKey() {
			return setSplusKey;
		}

		public void setSetSplusKey(String setSplusKey) {
			this.setSplusKey = setSplusKey;
		}

		public String getModulSplusKey() {
			return modulSplusKey;
		}

		public void setModulSplusKey(String modulSplusKey) {
			this.modulSplusKey = modulSplusKey;
		}

	}

}