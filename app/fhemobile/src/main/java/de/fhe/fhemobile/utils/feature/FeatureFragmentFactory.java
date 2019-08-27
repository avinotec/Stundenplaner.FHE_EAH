package de.fhe.fhemobile.utils.feature;

import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.fragments.impressum.ImpressumFragment;
import de.fhe.fhemobile.fragments.maps.MapsFragment;
import de.fhe.fhemobile.fragments.mensa.MensaFoodFragment;
import de.fhe.fhemobile.fragments.mytimetable.MyTimeTableFragment;
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
            case FeatureId.MYTIMETABLE:     fragment = MyTimeTableFragment.newInstance();       break;
            case FeatureId.NEWS:
            default:                        fragment = NewsListFragment.newInstance();
        }

        return fragment;
    }

}
