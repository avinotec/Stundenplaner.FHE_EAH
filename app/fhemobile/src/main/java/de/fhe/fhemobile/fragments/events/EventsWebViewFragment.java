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

package de.fhe.fhemobile.fragments.events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.views.events.EventsWebView;

/**
 * Created by Nadja on 07.09.21
 * Anzeige des Veranstaltungskalenders der Hochschul-Homepage
 */
public class EventsWebViewFragment extends FeatureFragment {

    public EventsWebViewFragment() {
        // Required empty public constructor
    }

    public static de.fhe.fhemobile.fragments.events.EventsWebViewFragment newInstance() {
        final de.fhe.fhemobile.fragments.events.EventsWebViewFragment fragment = new de.fhe.fhemobile.fragments.events.EventsWebViewFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (EventsWebView) inflater.inflate(R.layout.fragment_events_webview, container, false);
        return mView;
    }

    /**
     * Return the webview object of the fragment
     * Needed for back button behavior
     * @return webview displayed in the fragment
     */
    public WebView getWebView(){
        return getView().findViewById(R.id.eventsWebView);
    }


    private EventsWebView mView;
}
