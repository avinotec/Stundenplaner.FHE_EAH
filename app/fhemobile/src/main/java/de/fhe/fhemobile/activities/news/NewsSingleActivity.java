package de.fhe.fhemobile.activities.news;

import android.content.Intent;
import android.os.Bundle;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.BaseActivity;
import de.fhe.fhemobile.fragments.news.NewsSingleViewFragment;
import de.fhe.fhemobile.vos.news.NewsItemVo;

public class NewsSingleActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseContent(R.layout.activity_news_single);

        Intent intent = getIntent();
        NewsItemVo newsItem = intent.getParcelableExtra(EXTRA_NEWS_ITEM);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, NewsSingleViewFragment.newInstance(newsItem))
                .commit();
    }

    public static final String EXTRA_NEWS_ITEM   = "extraNewsItem";

}
