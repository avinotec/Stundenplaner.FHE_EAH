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
package de.fhe.fhemobile.network;

import de.fhe.fhemobile.vos.timetable.FlatDataStructure;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *
 * @param <T>
 */
public class TimeTableCallback<T> implements Callback<T> {
	/**
	 *
	 * @param data
	 */
	public TimeTableCallback(FlatDataStructure data) {
		this.data=data;
	}

	/**
	 *
	 */
	final private FlatDataStructure data;

	/**
	 *
	 * @return
	 */
	public FlatDataStructure getData() {
		return data;
	}

	/**
	 *
	 * @param call
	 * @param response
	 */
	@Override
	public void onResponse(Call<T> call, Response<T> response) {

	}

	/**
	 *
	 * @param call
	 * @param t
	 */
	@Override
	public void onFailure(Call<T> call, Throwable t) {

	}
}
