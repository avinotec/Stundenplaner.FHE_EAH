package de.fhe.fhemobile.models.TimeTableChanges;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RequestModel {

	public RequestModel(int os_id, String device_id, long refresh_timestamp){
		this.os_id=os_id;
		this.device_id=device_id;
		this.refresh_timestamp=refresh_timestamp;
		this.module_list=new ArrayList<>();
	}

	@SerializedName("os_id")
	int os_id;

	@SerializedName("device_id")
	String device_id;

	@SerializedName("refresh_timestamp")
	long  refresh_timestamp;

	@SerializedName("module_list")
	ArrayList<Module> module_list;

	public void addLesson(String setID,String moduleTitle){
		this.module_list.add(new Module(setID,moduleTitle));
	}

	class Module {
		public Module(String setID,String moduleTitle){
			this.setID=setID;
			this.moduleTitle=moduleTitle;
		}
		@SerializedName("set_title")
		String setID;
		@SerializedName("module_title")
		String moduleTitle;
	}


}
