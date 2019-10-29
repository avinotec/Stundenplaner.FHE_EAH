package de.fhe.fhemobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.fragments.maps.MapsSingleFragment;
import de.fhe.fhemobile.models.maps.MapsModel;

public class MapsSingleActivity extends BaseActivity {
    private static final String TAG = "MapsSingleActivity";
    public static final String STATE_MAPS_ID = "stateMapsId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseContent(R.layout.activity_maps_single);

        mModel = MapsModel.getInstance();

        if (savedInstanceState != null) {
            mMapId = savedInstanceState.getInt(STATE_MAPS_ID);
        }
        else {
            Intent intent = getIntent();
            mMapId = intent.getIntExtra(EXTRA_MAP_ID, -1);
        }

        try {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, MapsSingleFragment.newInstance(mMapId))
                    .commit();

            getSupportActionBar().setTitle(MapsModel.getInstance().getMaps().get(mMapId).getName());
        }
        catch (Exception e){
            Log.e(TAG, "Fehler beim Aufrufen des Fragments: ",e );
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_MAPS_ID, mMapId);
    }

    private static final String LOG_TAG = MapsSingleActivity.class.getSimpleName();


    public static final String EXTRA_MAP_ID = "extraMapId";

    private MapsModel           mModel;

    private Integer             mMapId;

}
