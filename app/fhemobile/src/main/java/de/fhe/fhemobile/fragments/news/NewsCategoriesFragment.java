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

package de.fhe.fhemobile.fragments.news;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.models.news.NewsModel;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.utils.UserSettings;
import de.fhe.fhemobile.views.news.NewsCategoriesView;

public class NewsCategoriesFragment extends Fragment {

    public NewsCategoriesFragment() {
        // Required empty public constructor
    }

    public static NewsCategoriesFragment newInstance() {
        final NewsCategoriesFragment fragment = new NewsCategoriesFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//
//        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (NewsCategoriesView) inflater.inflate(R.layout.fragment_news_categories, container, false);
        mView.initializeView(mViewsListener);

        if(NewsModel.getInstance().getCategoryItems() == null) {
            NetworkHandler.getInstance().fetchAvailableNewsLists();
        }
        else {
            mView.initContent();
        }

        return mView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mView != null) {
            mView.destroy();
        }
    }

    private final NewsCategoriesView.ViewListener mViewsListener = new NewsCategoriesView.ViewListener() {
        @Override
        public void onNewsCategoryChosen(final Integer _Id, final Integer _Position) {
            UserSettings.getInstance().setChosenNewsCategory(String.valueOf(_Id));
            NewsModel.getInstance().setChosenNewsItemPosition(_Position);
        }
    };

    private NewsCategoriesView mView;
}
