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

package de.fhe.fhemobile.fragments.maps;

import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.BaseActivity;
import de.fhe.fhemobile.models.maps.MapsModel;
import de.fhe.fhemobile.views.maps.MapsView;
import de.fhe.fhemobile.vos.maps.MapCollectionVo;
import de.fhe.fhemobile.vos.maps.MapVo;

/**
 * Fragment for showing and navigating through maps/floorplans of a certain building
 *
 * Edit by Nadja 02.12.2021: rename from MapsSingleFragment to MapsFragment
 */
public class MapsFragment extends Fragment {

    public MapsFragment() {
        // Required empty public constructor
    }

    public static MapsFragment newInstance(int _MapId) {
        MapsFragment fragment = new MapsFragment();
        
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
        }
        else {
            mCurrentMapIndex = 0;
        }

    }
    
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        try {
            mView = (MapsView) inflater.inflate(R.layout.fragment_maps, container, false);
            MapVo map;

            if (mMap.getMaps().size() > 1) {
                map = mMap.getMaps().get(mCurrentMapIndex);
                mView.initializeView(map);
            }
            else {
                map = mMap.getMaps().get(0);
                mView.initializeView(map);
            }

            updateActionBarTitle(getContext().getResources().getString(map.getNameID()));
        }catch (Exception e){
            mView = new MapsView(getContext(), new AttributeSet() {
                @Override
                public int getAttributeCount() {
                    return 0;
                }

                @Override
                public String getAttributeName(int i) {
                    return null;
                }

                @Override
                public String getAttributeValue(int i) {
                    return null;
                }

                @Override
                public String getAttributeValue(String s, String s1) {
                    return null;
                }

                @Override
                public String getPositionDescription() {
                    return null;
                }

                @Override
                public int getAttributeNameResource(int i) {
                    return 0;
                }

                @Override
                public int getAttributeListValue(String s, String s1, String[] strings, int i) {
                    return 0;
                }

                @Override
                public boolean getAttributeBooleanValue(String s, String s1, boolean b) {
                    return false;
                }

                @Override
                public int getAttributeResourceValue(String s, String s1, int i) {
                    return 0;
                }

                @Override
                public int getAttributeIntValue(String s, String s1, int i) {
                    return 0;
                }

                @Override
                public int getAttributeUnsignedIntValue(String s, String s1, int i) {
                    return 0;
                }

                @Override
                public float getAttributeFloatValue(String s, String s1, float v) {
                    return 0;
                }

                @Override
                public int getAttributeListValue(int i, String[] strings, int i1) {
                    return 0;
                }

                @Override
                public boolean getAttributeBooleanValue(int i, boolean b) {
                    return false;
                }

                @Override
                public int getAttributeResourceValue(int i, int i1) {
                    return 0;
                }

                @Override
                public int getAttributeIntValue(int i, int i1) {
                    return 0;
                }

                @Override
                public int getAttributeUnsignedIntValue(int i, int i1) {
                    return 0;
                }

                @Override
                public float getAttributeFloatValue(int i, float v) {
                    return 0;
                }

                @Override
                public String getIdAttribute() {
                    return null;
                }

                @Override
                public String getClassAttribute() {
                    return null;
                }

                @Override
                public int getIdAttributeResourceValue(int i) {
                    return 0;
                }

                @Override
                public int getStyleAttribute() {
                    return 0;
                }
            });
        }




        return mView;
    }

    //onCreateOptionsMenu---------------------------------------------------------------------------
    @Override
    public void onCreateOptionsMenu(Menu _menu, @NonNull MenuInflater _inflater) {

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

        /* Checks use of resource IDs in places requiring constants
            Avoid the usage of resource IDs where constant expressions are required.
            A future version of the Android Gradle Plugin will generate R classes with
            non-constant IDs in order to improve the performance of incremental compilation.
            Issue id: NonConstantResourceId
         */
        final int itemId = _item.getItemId();
        if (itemId == R.id.action_up) {
            if (mCurrentMapIndex < mMap.getMaps().size() - 1) {
                mCurrentMapIndex++;
                MapVo map = mMap.getMaps().get(mCurrentMapIndex);

                mView.initializeView(map);
                updateActionBarTitle(getContext().getResources().getString(map.getNameID()));
            }
            return true;
        } else if (itemId == R.id.action_down) {
            if (mCurrentMapIndex > 0) {
                mCurrentMapIndex--;
                MapVo map = mMap.getMaps().get(mCurrentMapIndex);

                mView.initializeView(map);
                updateActionBarTitle(getContext().getResources().getString(map.getNameID()));
            }
            return true;

            //other item
        }
        return super.onOptionsItemSelected(_item);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(SAV_MAP_INDEX, mCurrentMapIndex);
//        Log.d(LOG_TAG, "onSaveInstanceState ");
    }

    private void updateActionBarTitle(String _Title) {
        ((BaseActivity) getActivity()).getSupportActionBar().setTitle(_Title);
    }

    private static final String LOG_TAG = MapsFragment.class.getSimpleName();

    private static final String ARGS_MAP_ID   = "argMapId";

    private static final String SAV_MAP_INDEX = "savMapIndex";

    private MapsView mView;

    private MapCollectionVo mMap;
    private int             mCurrentMapIndex;

}
