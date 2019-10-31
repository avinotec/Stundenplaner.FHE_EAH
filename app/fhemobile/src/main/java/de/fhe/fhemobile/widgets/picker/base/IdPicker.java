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
package de.fhe.fhemobile.widgets.picker.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

import de.fhe.fhemobile.R;

/**
 * Created by paul on 12.03.15.
 */
public abstract class IdPicker extends LinearLayout{

    //##############################################################################################
    //                                     Abstract methods
    //##############################################################################################

    /**
     *
     * @param _Position
     * @return
     */
    protected abstract String getId(int _Position);

    /**
     *
     * @param _Position
     * @return
     */
    protected abstract String getName(int _Position);

    /**
     *
     * @return
     */
    protected abstract int getCount();

    //##############################################################################################
    //                                       Base methods
    //##############################################################################################

    protected IdPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.IdPicker, 0, 0);

        try {
            //left button
            mHeadline = a.getString(R.styleable.IdPicker_idpHeadline);

        } finally {
            a.recycle();
        }

        if (mHeadline == null) {
            mHeadline = "";
        }

        preInitialize();
    }

    public void setFragmentManager(FragmentManager _Manager) {
        mFragmentManager = _Manager;
    }

    public void setOnItemChosenListener(OnItemChosenListener _Listener) {
        mListener = _Listener;
    }

    public void setHeadline(String _Headline) {
        mHeadline = _Headline;
    }

    public void toggleEnabled(boolean _Enabled) {
        mEnabled = _Enabled;
    }

    public void reset(boolean _Enabled) {
        mContentView.setText(mContext.getResources().getString(R.string.timetable_input));
        toggleEnabled(_Enabled);
    }

    private void preInitialize() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_id_picker, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mHeadlineView = (TextView) findViewById(R.id.idPickerHeadline);
        mContentView  = (TextView) findViewById(R.id.idPickerContent);

        onPostFinishInflate();
    }

    private void onPostFinishInflate() {
        mContentView.setOnClickListener(mClickListener);

        mHeadlineView.setText(mHeadline);
    }

    private void openStringPicker() {

        IdStringDialog dialog = new IdStringDialog();
        dialog.setOnItemChosenListener(mItemChosenListener);
        dialog.initDialog(mContext, buildItemList(), mHeadline);
        dialog.show(mFragmentManager, "idStringDialog");
    }

    private List<IDItem> buildItemList() {
        ArrayList<IDItem> items = new ArrayList<>();

        int amountOfItems = getCount();

        for (int i = 0; i < amountOfItems; i++) {
            items.add(new IDItem(getName(i), getId(i)));
        }

        return items;
    }

    private final OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mEnabled) {
                openStringPicker();
            }
        }
    };

    private final OnItemChosenListener mItemChosenListener = new OnItemChosenListener() {
        @Override
        public void onItemChosen(String _ItemId, int _ItemPos) {
            mContentView.setText(getName(_ItemPos));


            if (mListener != null) {
                mListener.onItemChosen(_ItemId, _ItemPos);
            }
        }
    };
    public void setDisplayValue(String text){
        mContentView.setText(text);
    }

    private final Context              mContext;
    private FragmentManager      mFragmentManager;
    private OnItemChosenListener mListener;

    private String               mHeadline;
    private boolean              mEnabled = true;

    private TextView             mHeadlineView;
    private TextView             mContentView;
}
