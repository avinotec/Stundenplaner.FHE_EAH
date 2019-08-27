package de.fhe.fhemobile.network;

import de.fhe.fhemobile.vos.timetable.FlatDataStructure;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TimeTableCallback<T> implements Callback<T> {
	public TimeTableCallback(FlatDataStructure data) {
		this.data=data;
	}
	FlatDataStructure data;

	public FlatDataStructure getData() {
		return data;
	}

	@Override
	public void success(T t, Response response) {

	}

	@Override
	public void failure(RetrofitError error) {

	}
}
