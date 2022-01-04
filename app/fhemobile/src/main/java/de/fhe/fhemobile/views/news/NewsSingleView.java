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

    private TextView mTitle;
    private WebView mText;
    private TextView mAuthor;
    private TextView mPubDate;
    private TextView mCategories;
}
