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
	final
	int os_id;

	@SerializedName("device_id")
	final
	String device_id;

	@SerializedName("refresh_timestamp")
	final
	long  refresh_timestamp;

	@SerializedName("module_list")
	final
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
		final
		String setID;
		@SerializedName("module_title")
		final
		String moduleTitle;
	}


}
