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
package de.fhe.fhemobile.fragments.joboffers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.Nullable;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.views.joboffers.JobOffersWebView;

/**
 * Created by Nadja on 30.03.2022
 */
public class JobOffersFragment extends FeatureFragment {

    private JobOffersWebView mView;

    public JobOffersFragment(){
        // Required empty public constructor
    }

    public static JobOffersFragment newInstance(){
        final JobOffersFragment fragment = new JobOffersFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        mView = (JobOffersWebView) inflater.inflate(R.layout.fragment_joboffers_webview, container, false);
        return mView;
    }

    /**
     * Return the web view object of the fragment,
     * needed for back button behavior
     * @return web view displayed in the fragment
     */
    public WebView getWebView(){
        return getView().findViewById(R.id.webview_joboffers);
    }
}