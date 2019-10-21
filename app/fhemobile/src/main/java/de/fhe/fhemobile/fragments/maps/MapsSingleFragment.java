package de.fhe.fhemobile.fragments.maps;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.BaseActivity;
import de.fhe.fhemobile.models.maps.MapsModel;
import de.fhe.fhemobile.views.maps.MapsSingleView;
import de.fhe.fhemobile.vos.maps.MapCollectionVo;
import de.fhe.fhemobile.vos.maps.MapVo;

public class MapsSingleFragment extends Fragment {

    public MapsSingleFragment() {
        // Required empty public constructor
    }

    public static MapsSingleFragment newInstance(int _MapId) {
        MapsSingleFragment fragment = new MapsSingleFragment();
        
        Bundle args = new Bundle();
        args.putInt(ARGS_MAP_ID, _MapId );

        fragment.setArguments( args );
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setHasOptionsMenu(true);
        
        if (getArguments() != null) {
            int mapId = getArguments().getInt(ARGS_MAP_ID);
            mMap = MapsModel.getInstance().getMaps().get(mapId);
        }

        if (savedInstanceState != null) {
            mCurrentMapIndex = savedInstanceState.getInt(SAV_MAP_INDEX);
//            Log.d(LOG_TAG, "onCreate ");
        }
        else {
            mCurrentMapIndex = 0;
        }

    }
    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (MapsSingleView) inflater.inflate(R.layout.fragment_maps_single, container, false);

        MapVo map;

        if (mMap.getMaps().size() > 1) {
            map = mMap.getMaps().get(mCurrentMapIndex);
            mView.initializeView(map);
        }
        else {
            map = mMap.getMaps().get(0);
            mView.initializeView(map);
        }

        updateActionBarTitle(map.getName());

        return mView;
    }

    //onCreateOptionsMenu---------------------------------------------------------------------------
    @Override
    public void onCreateOptionsMenu(Menu _menu, MenuInflater _inflater) {

        // Inflate the menu; this adds items to the action bar if it is present.
        _menu.clear();
        
        if (mMap.getMaps().size() > 1) {
            _inflater.inflate(R.menu.menu_maps, _menu);
        }

        super.onCreateOptionsMenu(_menu, _inflater);
    }

    //onOptionsItemSelected-------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem _item) {
        switch (_item.getItemId()) {
            case R.id.action_up:
                if (mCurrentMapIndex < mMap.getMaps().size() - 1) {
                    mCurrentMapIndex++;
                    MapVo map = mMap.getMaps().get(mCurrentMapIndex);

                    mView.initializeView(map);
                    updateActionBarTitle(map.getName());
                }
                return true;

            case R.id.action_down:
                if (mCurrentMapIndex > 0) {
                    mCurrentMapIndex--;
                    MapVo map = mMap.getMaps().get(mCurrentMapIndex);

                    mView.initializeView(map);
                    updateActionBarTitle(map.getName());
                }
                return true;

            //other item
            default:
                return super.onOptionsItemSelected(_item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(SAV_MAP_INDEX, mCurrentMapIndex);
//        Log.d(LOG_TAG, "onSaveInstanceState ");
    }

    private void updateActionBarTitle(String _Title) {
        ((BaseActivity) getActivity()).getSupportActionBar().setTitle(_Title);
    }

    private static final String LOG_TAG = MapsSingleFragment.class.getSimpleName();

    private static final String ARGS_MAP_ID   = "argMapId";

    private static final String SAV_MAP_INDEX = "savMapIndex";

    private MapsSingleView  mView;

    private MapCollectionVo mMap;
    private int             mCurrentMapIndex;

}
