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

package de.fhe.fhemobile.fragments.maps;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.SecondaryActivity;
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

    private static final String TAG = MapsFragment.class.getSimpleName();


    public MapsFragment() {
        // Required empty public constructor
    }

    public static MapsFragment newInstance(final int _MapId) {
        final MapsFragment fragment = new MapsFragment();

        final Bundle args = new Bundle();
        args.putInt(ARGS_MAP_ID, _MapId );

        fragment.setArguments( args );
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //replacement of deprecated setHasOptionsMenu(), onCreateOptionsMenu() and onOptionsItemSelected()
        final MenuHost menuHost = requireActivity();
        final Activity activity = getActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull final Menu menu, @NonNull final MenuInflater menuInflater) {
                menu.clear();
                // Add menu items here
                if (mMap.getMaps().size() > 1) {
                    menuInflater.inflate(R.menu.menu_maps, menu);
                }
            }

            @Override
            public boolean onMenuItemSelected(@NonNull final MenuItem menuItem) {
                // Handle the menu selection
                if(menuItem.getItemId() == R.id.action_up) {
                    if (mCurrentMapIndex < mMap.getMaps().size() - 1) {
                        mCurrentMapIndex++;
                        final MapVo map = mMap.getMaps().get(mCurrentMapIndex);

                        mView.initializeView(map);
                        updateActionBarTitle(getContext().getResources().getString(map.getNameID()));
                    }
                    return true;
                }

                if (menuItem.getItemId() == R.id.action_down) {
                    if (mCurrentMapIndex > 0) {
                        mCurrentMapIndex--;
                        final MapVo map = mMap.getMaps().get(mCurrentMapIndex);

                        mView.initializeView(map);
                        updateActionBarTitle(getContext().getResources().getString(map.getNameID()));
                    }
                    return true;
                }

                return false;
            }
        });

        if (getArguments() != null) {
            final int mapId = getArguments().getInt(ARGS_MAP_ID);
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
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        try {
            mView = (MapsView) inflater.inflate(R.layout.fragment_maps, container, false);
            final MapVo map;

            if (mMap.getMaps().size() > 1) {
                map = mMap.getMaps().get(mCurrentMapIndex);
                mView.initializeView(map);
            }
            else {
                map = mMap.getMaps().get(0);
                mView.initializeView(map);
            }

            updateActionBarTitle(getContext().getResources().getString(map.getNameID()));
        } catch (final Resources.NotFoundException e) {
            mView = new MapsView(getContext(), new AttributeSet() {
                @Override
                public int getAttributeCount() {
                    return 0;
                }

                @Override
                public String getAttributeName(final int i) {
                    return null;
                }

                @Override
                public String getAttributeValue(final int i) {
                    return null;
                }

                @Override
                public String getAttributeValue(final String s, final String s1) {
                    return null;
                }

                @Override
                public String getPositionDescription() {
                    return null;
                }

                @Override
                public int getAttributeNameResource(final int i) {
                    return 0;
                }

                @Override
                public int getAttributeListValue(final String s, final String s1, final String[] strings, final int i) {
                    return 0;
                }

                @Override
                public boolean getAttributeBooleanValue(final String s, final String s1, final boolean b) {
                    return false;
                }

                @Override
                public int getAttributeResourceValue(final String s, final String s1, final int i) {
                    return 0;
                }

                @Override
                public int getAttributeIntValue(final String s, final String s1, final int i) {
                    return 0;
                }

                @Override
                public int getAttributeUnsignedIntValue(final String s, final String s1, final int i) {
                    return 0;
                }

                @Override
                public float getAttributeFloatValue(final String s, final String s1, final float v) {
                    return 0;
                }

                @Override
                public int getAttributeListValue(final int i, final String[] strings, final int i1) {
                    return 0;
                }

                @Override
                public boolean getAttributeBooleanValue(final int i, final boolean b) {
                    return false;
                }

                @Override
                public int getAttributeResourceValue(final int i, final int i1) {
                    return 0;
                }

                @Override
                public int getAttributeIntValue(final int i, final int i1) {
                    return 0;
                }

                @Override
                public int getAttributeUnsignedIntValue(final int i, final int i1) {
                    return 0;
                }

                @Override
                public float getAttributeFloatValue(final int i, final float v) {
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
                public int getIdAttributeResourceValue(final int i) {
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

    @Override
    public void onSaveInstanceState(@NonNull final Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(SAV_MAP_INDEX, mCurrentMapIndex);
//        Log.d(LOG_TAG, "onSaveInstanceState ");
    }

    void updateActionBarTitle(final String _Title) {
        ((SecondaryActivity) getActivity()).getSupportActionBar().setTitle(_Title);
    }

    private static final String ARGS_MAP_ID   = "argMapId";

    private static final String SAV_MAP_INDEX = "savMapIndex";

    MapsView mView;

    MapCollectionVo mMap;
    int             mCurrentMapIndex;

}
