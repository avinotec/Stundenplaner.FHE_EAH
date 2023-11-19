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
package de.fhe.fhemobile.fragments;

import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

/**
 * Created by paul on 18.03.15.
 */
public abstract class FeatureFragment extends Fragment {

    protected String mFeatureTag;

    /**
     * Tag of the fragment by which it can be found and which is used for logging
     * @param tag The fragment's tag
     */
    public FeatureFragment(final String tag) {
        this.mFeatureTag = tag;
    }

    public String getFeatureTag() {
        return mFeatureTag;
    }

    public void onRestoreActionBar(final ActionBar _ActionBar) {

    }

    protected void setActionBarTitle(final String _Title) {
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(_Title);
        }
    }

    public void setActionBarTitle(@StringRes final int _Title) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(_Title);
    }

}
