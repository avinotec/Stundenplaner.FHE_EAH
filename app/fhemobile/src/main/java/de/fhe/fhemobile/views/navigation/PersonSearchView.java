package de.fhe.fhemobile.views.navigation;

import android.content.Context;
import android.util.AttributeSet;

import androidx.fragment.app.FragmentManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.models.navigation.Person;
import de.fhe.fhemobile.widgets.picker.PersonPicker;
import de.fhe.fhemobile.widgets.picker.base.OnItemChosenListener;

/**
 * Created by Nadja 03.01.2022
 */
public class PersonSearchView extends SearchView {

    public PersonSearchView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public PersonSearchView(final Context context) {
        super(context);
    }


    public void initializeView(final FragmentManager _Manager) {
        mPersonPicker.setFragmentManager(_Manager);
        mPersonPicker.toggleEnabled(false);
        mPersonPicker.setOnItemChosenListener(mPersonListener);

        super.initializeView();
    }


    public void setViewListener(final IViewListener _Listener) {
        mViewListener = _Listener;
    }

    @Override
    public SearchView.IViewListener getViewListener(){ return mViewListener;}


    public void setPersonItems(final List<Person> _Items) {
        Collections.sort(_Items, new Comparator<Person>() {
            @Override
            public int compare(final Person o1, final Person o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        mPersonPicker.setItems(_Items);
        mPersonPicker.toggleEnabled(true);
    }


    /**
     * Reset Person Picker to "please select"
     */
    public void resetPersonPicker() {
        mPersonPicker.reset(true);
    }

    /**
     * Set displayed value of the Person Picker
     *
     * @param text to display
     */
    public void setPersonDisplayValue(final String text) {
        mPersonPicker.setDisplayValue(text);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPersonPicker = (PersonPicker) findViewById(R.id.navigationPickerPerson);
    }


    //Returns the selected building
    private final OnItemChosenListener mPersonListener = new OnItemChosenListener() {
        @Override
        public void onItemChosen(final String _ItemId, final int _ItemPos) {
            if (mViewListener != null) {
                mViewListener.onPersonChosen(_ItemId);
            }
        }
    };



    public interface IViewListener extends SearchView.IViewListener{
        void onPersonChosen(String _person);
    }

    private IViewListener mViewListener;
    private PersonPicker mPersonPicker;

}


