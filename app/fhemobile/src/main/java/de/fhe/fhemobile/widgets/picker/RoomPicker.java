package de.fhe.fhemobile.widgets.picker;

import android.content.Context;
import android.util.AttributeSet;

import java.util.List;

import de.fhe.fhemobile.vos.navigation.RoomVo;
import de.fhe.fhemobile.widgets.picker.base.IdPicker;

/**
 * Created by Nadja - 08.12.2021
 */
public class RoomPicker extends IdPicker {

    public RoomPicker(final Context context, final AttributeSet attrs) { super(context, attrs); }

    public void setItems(final List<RoomVo> _items){
        mItems = _items;
        if(mItems == null){
            throw new AssertionError("RoomVo items cannot be null!");
        }
    }

    @Override
    protected String getId(final int _Position) {
        return mItems.get(_Position).getId();
    }

    @Override
    protected String getName(final int _Position) {
        return mItems.get(_Position).getRoomName();
    }

    @Override
    protected int getCount() {
        return mItems.size();
    }

    private List<RoomVo> mItems;
}
