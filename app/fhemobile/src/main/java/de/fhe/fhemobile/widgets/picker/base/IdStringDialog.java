package de.fhe.fhemobile.widgets.picker.base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

/**
 * Created by paul on 12.03.15.
 */
public class IdStringDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //create the list view
        ListView listView = new ListView(mContext);
        listView.setAdapter(mItemAdapter);
        listView.setOnItemClickListener(mItemClickListener);

        //Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mTitle);
        builder.setView(listView);

        return builder.create();
    }

    //onStart---------------------------------------------------------------------------------------
    @Override
    public void onStart() {
        super.onStart();

    }

    //initDialog------------------------------------------------------------------------------------
    public void initDialog(Context _Context, List<IDItem> _Items, String _Title) {
        mContext     = _Context;
        mItems       = _Items;
        mItemAdapter = new ArrayAdapter<>(mContext, android.R.layout.select_dialog_item);

        //fill the adapter
        for (IDItem item : mItems) {
            mItemAdapter.add(item.getName());
        }

        mTitle = _Title;
    }

    public void setOnItemChosenListener(OnItemChosenListener _Listener) {
        mListener = _Listener;
    }

    //onDismiss-------------------------------------------------------------------------------------
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mListener != null) {
                mListener.onItemChosen(mItems.get(position).getId(), position);
            }
            dismiss();
        }
    };


    private Context              mContext;
    private List<IDItem>         mItems;
    private String               mTitle;
    private ArrayAdapter<String> mItemAdapter;
    private OnItemChosenListener mListener;
}
