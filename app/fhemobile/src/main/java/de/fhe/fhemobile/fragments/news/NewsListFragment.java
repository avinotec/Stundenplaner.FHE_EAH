package de.fhe.fhemobile.fragments.news;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.SettingsActivity;
import de.fhe.fhemobile.activities.news.NewsSingleActivity;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.models.news.NewsModel;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.utils.feature.FeatureId;
import de.fhe.fhemobile.views.news.NewsListView;

public class NewsListFragment extends FeatureFragment {

    public NewsListFragment() {
        // Required empty public constructor
    }

    public static NewsListFragment newInstance() {
        NewsListFragment fragment = new NewsListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(NewsModel.getInstance().getNewsItems() == null) {
            NetworkHandler.getInstance().fetchNewsData();
        }

        if (getArguments() != null) {

        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (NewsListView) inflater.inflate(R.layout.fragment_news_list, container, false);
        mView.init(mViewListener);

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mView.registerModelListener();
    }

    @Override
    public void onPause() {
        super.onPause();

        mView.deregisterModelListener();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        menu.clear();
        inflater.inflate(R.menu.main, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    //onOptionsItemSelected-------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem _item) {
        switch (_item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                intent.putExtra(SettingsActivity.EXTRA_SETTINGS_ID, FeatureId.NEWS);
                startActivity(intent);
                return true;

            //other item
            default:
                return super.onOptionsItemSelected(_item);
        }
    }

    private final NewsListView.ViewListener mViewListener = new NewsListView.ViewListener() {
        @Override
        public void onNewsItemClick(Integer _Id) {
            Intent intent = new Intent(getActivity(), NewsSingleActivity.class);
            intent.putExtra(NewsSingleActivity.EXTRA_NEWS_ITEM, NewsModel.getInstance().getNewsItems()[_Id]);
            startActivity(intent);
        }
    };

    private NewsListView mView;

}
