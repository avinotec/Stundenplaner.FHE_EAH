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
package de.fhe.fhemobile.adapters.myschedule;

import android.view.View;
import android.widget.Button;

import de.fhe.fhemobile.models.myschedule.MyScheduleModel;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventSeriesVo;

public class MyScheduleOverviewAdapter extends AbstractMyScheduleAdapter {

    private static final String TAG = MyScheduleOverviewAdapter.class.getSimpleName();

    public MyScheduleOverviewAdapter(){
        super();
        setRoomVisible(true);
    }


    @Override
    protected View.OnClickListener getAddEventSeriesBtnOnClickListener(
            final MyScheduleEventSeriesVo currentItem, final Button btnAddCourse) {

        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                MyScheduleModel.getInstance().removeFromSubscribedEventSeriesAndUpdateAdapters(currentItem);
            }
        };
    }

}
