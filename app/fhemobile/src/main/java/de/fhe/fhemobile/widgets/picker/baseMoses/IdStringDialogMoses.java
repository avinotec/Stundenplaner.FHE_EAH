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
package de.fhe.fhemobile.widgets.picker.baseMoses;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.List;

import de.fhe.fhemobile.widgets.picker.base.OnItemChosenListener;

/**
 * Created by paul on 12.03.15.
 */
public class IdStringDialogMoses extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        //create the list view
        final ListView listView = new ListView(mContext);
        listView.setAdapter(mItemAdapter);
        listView.setOnItemClickListener(mItemClickListener);

        //Create the dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mTitle);
        builder.setView(listView);

        return builder.create();
    }

    //initDialog------------------------------------------------------------------------------------
    public void initDialog(final Context _Context, final List<IdItemMoses> _Items, final String _Title) {
        mContext = _Context;
        mItems = _Items;
        mItemAdapter = new ArrayAdapter<>(mContext, android.R.layout.select_dialog_item);

        //fill the adapter
        for (final IdItemMoses item : mItems) {
            mItemAdapter.add(item.getName());
        }

        mTitle = _Title;
    }

    public void setOnItemChosenListener(final OnItemChosenListenerMoses _Listener) {
        mListener = _Listener;
    }

    private final AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(
                final AdapterView<?> parent,
                final View view,
                final int position,
                final long id
        ) {
            String studyProgramName = mItems.get(position).getId();
            Integer studyProgramId = mItems.get(position).getmStudyProgramId();

            Log.e("cz studyProgramName", studyProgramName);

            if (mListener != null) {
                mListener.onItemChosenMoses(
                        studyProgramName,
                        position,
                        studyProgramId
                );
            }
            dismiss();
        }
    };


    private Context mContext;
    List<IdItemMoses> mItems;
    private String mTitle;
    private ArrayAdapter<String> mItemAdapter;
    OnItemChosenListenerMoses mListener;
}
