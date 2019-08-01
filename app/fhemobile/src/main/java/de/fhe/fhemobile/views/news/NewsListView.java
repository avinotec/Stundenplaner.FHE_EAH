package de.fhe.fhemobile.views.news;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.applidium.headerlistview.HeaderListView;

import java.util.ArrayList;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.StickyHeaderAdapter;
import de.fhe.fhemobile.adapters.news.NewsListAdapter;
import de.fhe.fhemobile.events.Event;
import de.fhe.fhemobile.events.EventListener;
import de.fhe.fhemobile.models.news.NewsModel;
import de.fhe.fhemobile.vos.news.NewsItemVo;
import de.fhe.fhemobile.widgets.stickyHeaderList.DefaultHeaderItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.IHeaderItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.NewsRowItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.WeatherRowItem;

/**
 * Created by paul on 23.01.14.
 */
public class NewsListView extends LinearLayout {

    public interface ViewListener {
        void onNewsItemClick(Integer _Id);
    }

    public NewsListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mModel   = NewsModel.getInstance();
    }

    public void setViewListener(ViewListener _Listener) {
        mViewListener = _Listener;
    }

    public void init(ViewListener _Listener) {
        mViewListener = _Listener;

        if(mModel.getNewsItems() != null) {
            initializeAdapter();
        }
        else {
            mNewsProgressBar.setVisibility(VISIBLE);
        }

    }

    public void registerModelListener() {
        mModel.addListener(NewsModel.ChangeEvent.RECEIVED_NEWS, mReceivedNewsListener);
        mModel.addListener(NewsModel.ChangeEvent.RECEIVED_EMPTY_NEWS, mReceivedEmptyNewsListener);

    }

    public void deregisterModelListener() {
        mModel.removeListener(NewsModel.ChangeEvent.RECEIVED_NEWS, mReceivedNewsListener);
        mModel.removeListener(NewsModel.ChangeEvent.RECEIVED_EMPTY_NEWS, mReceivedEmptyNewsListener);
    }

    private void initializeAdapter() {

        ArrayList<IHeaderItem> sectionList = new ArrayList<>();

        DefaultHeaderItem headerItem = new DefaultHeaderItem("", false);
        headerItem.addItem(new WeatherRowItem());

        for (NewsItemVo itemVo : mModel.getNewsItems()) {
            headerItem.addItem(new NewsRowItem(itemVo));
        }

        sectionList.add(headerItem);

        mAdapter = new StickyHeaderAdapter(mContext, sectionList);
        mAdapter.setOnItemClickListener(mListItemClickListener);
        mNewsList.setAdapter(mAdapter);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mNewsList        = (HeaderListView) findViewById(R.id.newsListView);
        mNewsProgressBar = (ProgressBar)    findViewById(R.id.newsProgressIndicator);

        // To prevent crashing.
        // See: https://github.com/applidium/HeaderListView/issues/28
        mNewsList.getListView().setId(R.id.listMode);
    }

    private EventListener mReceivedNewsListener = new EventListener() {
        @Override
        public void onEvent(Event event) {
            mNewsProgressBar.setVisibility(GONE);
            initializeAdapter();
        }
    };

    private EventListener mReceivedEmptyNewsListener = new EventListener() {
        @Override
        public void onEvent(Event event) {

        }
    };

    private AdapterView.OnItemClickListener mListItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mViewListener != null) {
                // As the WeatherHeader is interpreted as a Row item we need to decrement the position
                // to get the correct position in the news list and check if the position is not
                // 0 (the WeatherHeader)
                if (position > 0) {
                    mViewListener.onNewsItemClick(--position);
                }
            }
        }
    };

    private Context             mContext;

    private NewsModel           mModel;
    private StickyHeaderAdapter mAdapter;

    private ProgressBar         mNewsProgressBar;

    private HeaderListView      mNewsList;

    private ViewListener        mViewListener;

}
