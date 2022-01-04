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
package de.fhe.fhemobile.activities;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import de.fhe.fhemobile.R;

/**
 * This activity should be implemented by every Activity in the App, which should use the Toolbar.
 * One of the methods getLayoutResource() or getLayoutView should be overwritten and provided with
 * working references, to be able to fill the container.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base);

        mToolbar    = (Toolbar)     findViewById(R.id.toolbar);
        mContainer  = (FrameLayout) findViewById(R.id.baseActivityContainer);
        mTabHost    = (TabHost)     findViewById(R.id.baseActivityTabHost);
        mShadow     = (ImageView)   findViewById(R.id.baseActivityShadow);
        mSpinner    = (Spinner)     findViewById(R.id.toolbarSpinner);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        mActionBar = getSupportActionBar();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTabHost.setup();
        mTabHost.getTabWidget().setDividerDrawable(null);
        mTabHost.setBackground(mToolbar.getBackground().getConstantState().newDrawable());

    }

    //onResume--------------------------------------------------------------------------------------
    @Override
    protected void onResume() {
        super.onResume();

        final TabWidget widget = mTabHost.getTabWidget();
        // Change tab text color to preferred color
        for (int i = 0; i < widget.getChildCount(); i++) {
            final View v = widget.getChildAt(i);
            final TextView tv = (TextView) v.findViewById(android.R.id.title);
            if (tv == null) {
                continue;
            }
            int mTabSelectedTextColor = 0xFFFFFFFF;
            tv.setTextColor(mTabSelectedTextColor);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.tab_text_size));
            v.setBackgroundResource(R.drawable.tab_indicator_ab_material);
        }
//        ((TextView) mTabHost.getCurrentTabView().findViewById(android.R.id.title)).setTextColor(mTabUnselectedTextColor);
    }

    //onPause---------------------------------------------------------------------------------------
/*
    @Override
    protected void onPause() {
        super.onPause();
    }
*/

    //onDestroy-------------------------------------------------------------------------------------
/*
    @Override
     protected void onDestroy() {
        super.onDestroy();
    }
*/

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // das zurück gehen mit dem Pfeil oben links
//        if (item.getItemId() == android.R.id.home) {
//        }

        return super.onOptionsItemSelected(item);
    }


// --Commented out by Inspection START (01.11.2019 00:18):
//    protected void setActionBarIcon(int iconRes) {
//        mToolbar.setNavigationIcon(iconRes);
//    }
// --Commented out by Inspection STOP (01.11.2019 00:18)

    protected void setBaseContent(final int _layoutResource) {
        View mContent = View.inflate(this, _layoutResource, null);
        mContainer.addView(mContent);
    }

// --Commented out by Inspection START (01.11.2019 00:18):
//    protected void setBaseContent(View _layoutView) {
//        mContent = _layoutView;
//        mContainer.addView(_layoutView);
//    }
// --Commented out by Inspection STOP (01.11.2019 00:18)

    protected void showTabs(final boolean _show) {
        mTabHost.setVisibility(_show ? View.VISIBLE : View.GONE);
    }

    protected void showShadow(final boolean _show) {
        mShadow.setVisibility(_show ? View.VISIBLE : View.GONE);
    }

// --Commented out by Inspection START (01.11.2019 00:18):
//    protected TabHost getTabHost() {
//        return mTabHost;
//    }
// --Commented out by Inspection STOP (01.11.2019 00:18)

// --Commented out by Inspection START (01.11.2019 00:17):
//    protected void toggleToolbarSpinner(boolean _show) {
//        mActionBar.setDisplayShowTitleEnabled(!_show);
//        mSpinner.setVisibility(_show ? View.VISIBLE : View.GONE);
//    }
// --Commented out by Inspection STOP (01.11.2019 00:17)

    protected void setSpinnerListener(final AdapterView.OnItemSelectedListener _listener) {
        mSpinner.setOnItemSelectedListener(_listener);
    }

    protected void setSpinnerAdapter(final SpinnerAdapter _adapter) {
        mSpinner.setAdapter(_adapter);
    }

    protected void setSelectedSpinnerItem(final int _index) {
        mSpinner.setSelection(_index);
    }

    protected void setSelectedSpinnerItem(final int _index, final boolean _animate) {
        mSpinner.setSelection(_index, _animate);
    }

// --Commented out by Inspection START (01.11.2019 00:18):
//    protected int getSelectedSpinnerItem() {
//        return mSpinner.getSelectedItemPosition();
//    }
// --Commented out by Inspection STOP (01.11.2019 00:18)

    private static final String LOG_TAG = BaseActivity.class.getSimpleName();

    protected Toolbar   mToolbar;
    protected ActionBar mActionBar;

    private FrameLayout mContainer;
    private TabHost     mTabHost;
    private ImageView   mShadow;
    private Spinner     mSpinner;

    // --Commented out by Inspection (01.11.2019 00:18):private int         mTabUnselectedTextColor = 0xFFFFFF00;
    // --Commented out by Inspection (01.11.2019 00:18):private boolean     mAdsEnabled             = false;
}
