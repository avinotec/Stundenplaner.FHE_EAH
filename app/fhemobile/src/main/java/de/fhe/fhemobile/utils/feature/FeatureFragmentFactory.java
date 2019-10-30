/*
 * Copyright (c) 2014-2019 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fhe.fhemobile.utils.feature;

import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.fragments.impressum.ImpressumFragment;
import de.fhe.fhemobile.fragments.maps.MapsFragment;
import de.fhe.fhemobile.fragments.mensa.MensaFoodFragment;
import de.fhe.fhemobile.fragments.mytimetable.MyTimeTableCalendarFragment;
import de.fhe.fhemobile.fragments.news.NewsListFragment;
import de.fhe.fhemobile.fragments.phonebook.PhonebookSearchFragment;
import de.fhe.fhemobile.fragments.semesterdata.SemesterDataFragment;
import de.fhe.fhemobile.utils.TimeTableFactory;

/**
 * Created by paul on 18.03.15.
 */
public class FeatureFragmentFactory {

    public static FeatureFragment getFeaturedFragment(int _FeatureId) {
        FeatureFragment fragment;

        switch (_FeatureId) {
            case FeatureId.PHONEBOOK:       fragment = PhonebookSearchFragment.newInstance();   break;
            case FeatureId.MENSA:           fragment = MensaFoodFragment.newInstance();         break;
            case FeatureId.MAPS:            fragment = MapsFragment.newInstance();              break;
            case FeatureId.SEMESTER_DATA:   fragment = SemesterDataFragment.newInstance();      break;
            case FeatureId.IMPRESS:         fragment = ImpressumFragment.newInstance();         break;
            case FeatureId.TIMETABLE:       fragment = TimeTableFactory.getTimeTableFragment(); break;
            case FeatureId.MYTIMETABLE:     fragment = MyTimeTableCalendarFragment.newInstance();       break;
            case FeatureId.NEWS:
            default:                        fragment = NewsListFragment.newInstance();
        }

        return fragment;
    }

}
