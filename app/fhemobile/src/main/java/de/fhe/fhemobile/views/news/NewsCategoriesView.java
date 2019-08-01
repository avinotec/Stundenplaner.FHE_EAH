package de.fhe.fhemobile.views.news;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.news.NewsCategoryAdapter;
import de.fhe.fhemobile.events.Event;
import de.fhe.fhemobile.events.EventListener;
import de.fhe.fhemobile.models.news.NewsModel;

/**
 * Created by paul on 12.02.14.
 */
public class NewsCategoriesView extends FrameLayout {

    public interface ViewListener {
        void onNewsCategoryChosen(Integer _Id, Integer _Position);
    }

    public NewsCategoriesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mModel = NewsModel.getInstance();

        mModel.addListener(NewsModel.ChangeEvent.RECEIVED_CATEGORY_ITEMS, mCategoryItemsListener);
    }

    public void initView(ViewListener _Listener) {
        mViewListener = _Listener;
    }

    public void destroy() {
        mModel.removeListener(NewsModel.ChangeEvent.RECEIVED_CATEGORY_ITEMS, mCategoryItemsListener);

        mViewListener = null;
        mCategoryItemsListener = null;
    }

    public void initContent() {
        mAdapter = new NewsCategoryAdapter(mContext, mModel.getCategoryItems());
        mCategoryListView.setAdapter(mAdapter);
        mCategoryListView.setOnItemClickListener(mCategorySelectListener);
        mCategoryListView.setItemChecked(mModel.getChosenNewsItemPosition(), true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mCategoryListView = (ListView) findViewById(R.id.newsCategoryListView);
    }

    private AdapterView.OnItemClickListener mCategorySelectListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mViewListener.onNewsCategoryChosen((int) mAdapter.getItemId(position), position);
        }
    };

    private EventListener mCategoryItemsListener = new EventListener() {
        @Override
        public void onEvent(Event event) {
            initContent();
        }
    };

    private Context             mContext;

    private NewsModel           mModel;

    private ViewListener        mViewListener;
    private NewsCategoryAdapter mAdapter;

    private ListView            mCategoryListView;
}
