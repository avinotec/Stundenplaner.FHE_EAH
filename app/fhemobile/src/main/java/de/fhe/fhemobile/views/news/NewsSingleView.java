package de.fhe.fhemobile.views.news;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.TextView;

import de.fhe.fhemobile.R;

/**
 * Created by paul on 23.01.14.
 */
public class NewsSingleView extends FrameLayout {

    public NewsSingleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void setTitle(String _Title) {
        mTitle.setText(_Title);
    }

    public void setText(String _Text) {
        String htmlData = "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />" + _Text;

//        mText.getSettings().setJavaScriptEnabled(true);
        mText.loadDataWithBaseURL("file:///android_asset/", htmlData, "text/html", "UTF-8", "");
    }

    public void setAuthor(String _Author) {
        mAuthor.setText(_Author);
    }

    public void setPubDate(String _Date) {
        mPubDate.setText(_Date);
    }

    public void setCategories(String _Categories) {
        mCategories.setText(_Categories);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mTitle = (TextView) findViewById(R.id.newsSingleTitle);
        mText = (WebView) findViewById(R.id.newsSingleText);
        mAuthor = (TextView) findViewById(R.id.newsSingleAuthor);
        mPubDate = (TextView) findViewById(R.id.newsSinglePubDate);
        mCategories = (TextView) findViewById(R.id.newsSingleCategories);

    }

    private Context mContext;

    private TextView mTitle;
    private WebView mText;
    private TextView mAuthor;
    private TextView mPubDate;
    private TextView mCategories;
}
