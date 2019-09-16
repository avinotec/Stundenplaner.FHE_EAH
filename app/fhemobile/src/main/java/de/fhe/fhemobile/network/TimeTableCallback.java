package de.fhe.fhemobile.network;

import de.fhe.fhemobile.vos.timetable.FlatDataStructure;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TimeTableCallback<T> implements Callback<T> {
	public TimeTableCallback(FlatDataStructure data) {
		this.data=data;
	}
	FlatDataStructure data;

	public FlatDataStructure getData() {
		return data;
	}


	@Override
	public void onResponse(Call<T> call, Response<T> response) {

	}

	@Override
	public void onFailure(Call<T> call, Throwable t) {

	}
}
