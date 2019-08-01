package de.fhe.fhemobile.views.maps;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.maps.MapsAdapter;
import de.fhe.fhemobile.models.maps.MapsModel;

/**
 * Created by paul on 23.02.14.
 */
public class MapsView extends FrameLayout {

    public interface ViewListener {
        void onMapItemClicked(Integer _Position);
    }

    public MapsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mModel = MapsModel.getInstance();
    }

    public void initializeView(ViewListener _Listener) {
        mViewListener = _Listener;

        mAdapter = new MapsAdapter(mContext, mModel.getMaps());

        mMapsChoiceList.setAdapter(mAdapter);
        mMapsChoiceList.setOnItemClickListener(mMapsClickListener);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mMapsChoiceList = (ListView) findViewById(R.id.mapsChoiceList);

    }

    private AdapterView.OnItemClickListener mMapsClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mViewListener.onMapItemClicked(position);
        }
    };

    private static final String LOG_TAG = MapsView.class.getSimpleName();

    private Context mContext;
    private ViewListener mViewListener;

    private MapsModel mModel;

    private MapsAdapter mAdapter;

    private ListView mMapsChoiceList;
}
