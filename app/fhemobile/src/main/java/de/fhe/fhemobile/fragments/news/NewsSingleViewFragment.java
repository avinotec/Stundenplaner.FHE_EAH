package de.fhe.fhemobile.fragments.news;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.views.news.NewsSingleView;
import de.fhe.fhemobile.vos.news.NewsItemVo;

public class NewsSingleViewFragment extends Fragment {

    public NewsSingleViewFragment() {
        // Required empty public constructor
    }

    public static NewsSingleViewFragment newInstance(NewsItemVo _NewsItem) {
        NewsSingleViewFragment fragment = new NewsSingleViewFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_NEWS_ITEM, _NewsItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNewsItem = getArguments().getParcelable(ARG_NEWS_ITEM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (NewsSingleView) inflater.inflate(R.layout.fragment_news_single_view, container, false);

        mView.setTitle(mNewsItem.getTitle());
        mView.setText(mNewsItem.getEncoded());
        mView.setAuthor(mNewsItem.getAuthor());
        mView.setPubDate(mNewsItem.getPubDate());
        mView.setCategories("");

        return mView;
    }

    private static final String ARG_NEWS_ITEM = "argNewsItem";

    private NewsSingleView mView;

    private NewsItemVo mNewsItem;

}
