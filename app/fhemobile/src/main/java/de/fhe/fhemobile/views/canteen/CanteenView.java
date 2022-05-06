/*
 *  Copyright (c) 2019-2021 Ernst-Abbe-Hochschule Jena
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
package de.fhe.fhemobile.views.canteen;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.canteen.CanteenPagerAdapter;
import de.fhe.fhemobile.utils.UserSettings;


/**
 * Created by Nadja on 05.05.2022
 */
public class CanteenView extends LinearLayout {

    public CanteenView(Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mNoCanteensSelectedText = (TextView) findViewById(R.id.tv_canteen_no_canteens_selected);
    }

    public void initializeView(final FragmentManager _Manager, final Lifecycle _Lifecycle){

        if(UserSettings.getInstance().getSelectedCanteenIds().size() > 0){
            final ViewPager2 viewPager = findViewById(R.id.viewpager_canteen);
            viewPager.setAdapter(new CanteenPagerAdapter(_Manager, _Lifecycle));
        } else{
            mNoCanteensSelectedText.setVisibility(VISIBLE);
        }
    }

    TextView mNoCanteensSelectedText;

}
