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

package de.fhe.fhemobile.adapters.canteen;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

import de.fhe.fhemobile.fragments.canteen.CanteenDayFragmentNEW;
import de.fhe.fhemobile.vos.canteen.CanteenDayVo;

/**
 * Created by Nadja in 03/2022
 */
public class CanteenPagerAdapter extends FragmentStateAdapter {

    public CanteenPagerAdapter(final FragmentManager fm,
                               final Lifecycle lifecycle,
                               final List<CanteenDayVo> canteenDayItems){
        super(fm, lifecycle);
        mData = canteenDayItems;
    }

    /**
     * Return the Fragment associated with a specified position.
     * @param position
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return CanteenDayFragmentNEW.newInstance(mData.get(position));
    }

    /**
     * Return the number of views available.
     * @return
     */
    @Override
    public int getItemCount() {
        return mData.size();
    }


    private final List<CanteenDayVo> mData;
}
