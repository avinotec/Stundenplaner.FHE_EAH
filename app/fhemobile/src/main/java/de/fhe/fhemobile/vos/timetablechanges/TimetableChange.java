package de.fhe.fhemobile.vos.timetablechanges;

import com.google.gson.annotations.SerializedName;

import de.fhe.fhemobile.vos.timetable.TimeTableEventVo;

public class TimetableChange {

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

}
