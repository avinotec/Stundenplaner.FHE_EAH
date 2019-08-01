package de.fhe.fhemobile.fragments.news;



import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        NewsCategoriesFragment fragment = new NewsCategoriesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (NewsCategoriesView) inflater.inflate(R.layout.fragment_news_categories, container, false);
        mView.initView(mViewsListener);

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

    private NewsCategoriesView.ViewListener mViewsListener = new NewsCategoriesView.ViewListener() {
        @Override
        public void onNewsCategoryChosen(Integer _Id, Integer _Position) {
            UserSettings.getInstance().setChosenNewsCategory(String.valueOf(_Id));
            NewsModel.getInstance().setChosenNewsItemPosition(_Position);
        }
    };

    private NewsCategoriesView mView;
}
