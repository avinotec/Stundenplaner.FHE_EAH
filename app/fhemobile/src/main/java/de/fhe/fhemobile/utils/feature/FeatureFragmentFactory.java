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
import de.fhe.fhemobile.fragments.canteen.CanteenFragment;
import de.fhe.fhemobile.fragments.events.EventsWebViewFragment;
import de.fhe.fhemobile.fragments.imprint.ImprintFragment;
import de.fhe.fhemobile.fragments.joboffers.JobOffersFragment;
import de.fhe.fhemobile.fragments.maps.MapsDialogFragment;
import de.fhe.fhemobile.fragments.myschedule.MyScheduleCalendarFragment;
import de.fhe.fhemobile.fragments.navigation.NavigationDialogFragment;
import de.fhe.fhemobile.fragments.news.NewsWebViewFragment;
import de.fhe.fhemobile.fragments.semesterdata.SemesterDataFragment;
import de.fhe.fhemobile.utils.TimeTableFactory;

/**
 * Created by paul on 18.03.15
 */
public final class FeatureFragmentFactory {

    /* Utility classes have all fields and methods declared as static.
    Creating private constructors in utility classes prevents them from being accidentally instantiated. */
	private FeatureFragmentFactory() {
	}

	public static FeatureFragment getFeaturedFragment(final int _FeatureId) {
        final FeatureFragment fragment;

        switch (_FeatureId) {
            case Features.FeatureId.MYSCHEDULE:     fragment = MyScheduleCalendarFragment.newInstance();  break;
            case Features.FeatureId.CANTEEN:         fragment = CanteenFragment.newInstance();            break;
            case Features.FeatureId.MAPS:            fragment = MapsDialogFragment.newInstance();           break;
            //case FeatureId.NAVIGATION:                fragment = ComingSoonFragment.newInstance(); break;
            case Features.FeatureId.NAVIGATION:      fragment = NavigationDialogFragment.newInstance();     break; //added by Nadja 02.12.21
            case Features.FeatureId.SEMESTER_DATA:   fragment = SemesterDataFragment.newInstance();         break;
            //case FeatureId.SEMESTER_DATA:             fragment = SemesterDataWebViewFragment.newInstance();   break; //display from Browser/as Webview - Nadja 07.09.21
            case Features.FeatureId.EVENTS:          fragment = EventsWebViewFragment.newInstance();        break; //added by Nadja 07.09.21
            //case Features.FeatureId.PHONEBOOK:       fragment = PhonebookSearchFragment.newInstance();   break;
            case Features.FeatureId.JOBOFFERS:       fragment = JobOffersFragment.newInstance();            break; //added by Nadja on 30.03.2022
            case Features.FeatureId.IMPRINT:         fragment = ImprintFragment.newInstance();            break;
            case Features.FeatureId.NEWS:            fragment = NewsWebViewFragment.newInstance();       break; //display as Webview - Nadja 6.9.21
            case Features.FeatureId.TIMETABLE:
            default:                        fragment = TimeTableFactory.getTimeTableFragment();

        }

        return fragment;
    }

}
