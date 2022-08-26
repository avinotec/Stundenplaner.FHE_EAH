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

package de.fhe.fhemobile.fragments.news;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.views.news.NewsWebView;

/**
 * Created by Nadja on 06.09.21
 * for displaying News-Browser-Webpage using WebView
 */
public class NewsWebViewFragment extends FeatureFragment {

    private NewsWebView mView;

    public NewsWebViewFragment() {
        // Required empty public constructor
    }

    public static NewsWebViewFragment newInstance() {
        final NewsWebViewFragment fragment = new NewsWebViewFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


/*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
*/

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (NewsWebView) inflater.inflate(R.layout.fragment_news_webview, container, false);
        return mView;
    }

    /**
     * Return the web view object of the fragment,
     * Needed for back button behavior
     * @return web view displayed in the fragment
     */
    public WebView getWebView(){
        return getView().findViewById(R.id.webview_news);
    }
}
