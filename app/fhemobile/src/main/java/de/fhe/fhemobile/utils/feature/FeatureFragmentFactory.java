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
package de.fhe.fhemobile.utils.feature;

import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.fragments.events.EventsWebViewFragment;
import de.fhe.fhemobile.fragments.impressum.ImpressumFragment;
import de.fhe.fhemobile.fragments.maps.MapsDialogFragment;
import de.fhe.fhemobile.fragments.mensa.MensaFoodFragment;
import de.fhe.fhemobile.fragments.mensa.MensaFragmentNEW;
import de.fhe.fhemobile.fragments.mytimetable.MyTimeTableCalendarFragment;
import de.fhe.fhemobile.fragments.navigation.NavigationDialogFragment;
import de.fhe.fhemobile.fragments.news.NewsWebViewFragment;
import de.fhe.fhemobile.fragments.semesterdata.SemesterDataFragment;
import de.fhe.fhemobile.utils.TimeTableFactory;

/**
 * Created by paul on 18.03.15.
 */
public class FeatureFragmentFactory {

    public static FeatureFragment getFeaturedFragment(final int _FeatureId) {
        final FeatureFragment fragment;

        //TODO Features
        switch (_FeatureId) {
            //case Features.FeatureId.PHONEBOOK:       fragment = PhonebookSearchFragment.newInstance();   break;
            //todo: change back to old MensaFoodFragment for deploy because new Mensa layout is missing settings
            //case Features.FeatureId.MENSA:           fragment = MensaFoodFragment.newInstance();         break;
            case Features.FeatureId.MENSA:           fragment = MensaFragmentNEW.newInstance();         break;
            case Features.FeatureId.MAPS:            fragment = MapsDialogFragment.newInstance();              break;
            case Features.FeatureId.SEMESTER_DATA:   fragment = SemesterDataFragment.newInstance();      break;
            //case FeatureId.SEMESTER_DATA:   fragment = SemesterDataWebViewFragment.newInstance();      break; //display from Browser/as Webview - Nadja 07.09.21
            case Features.FeatureId.IMPRESS:         fragment = ImpressumFragment.newInstance();         break;
            case Features.FeatureId.TIMETABLE:       fragment = TimeTableFactory.getTimeTableFragment(); break;
            case Features.FeatureId.MYTIMETABLE:     fragment = MyTimeTableCalendarFragment.newInstance();       break;
            case Features.FeatureId.EVENTS:          fragment = EventsWebViewFragment.newInstance();       break; //added by Nadja 07.09.21
            //case Features.FeatureId.NAVIGATION:      fragment = NavigationDialogFragmentOLD.newInstance();       break; //added by Nadja 09.09.21
            case Features.FeatureId.NAVIGATION:      fragment = NavigationDialogFragment.newInstance();       break; //added by Nadja 02.12.21
            //case FeatureId.NAVIGATION:      fragment = ComingSoonFragment.newInstance(); break;
            case Features.FeatureId.NEWS:
            //default:                        fragment = NewsListFragment.newInstance();
            default:                        fragment = NewsWebViewFragment.newInstance(); //display from Browser/as Webview - Nadja 6.9.21

        }

        return fragment;
    }

}
