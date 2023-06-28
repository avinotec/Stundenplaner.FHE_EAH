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
package de.fhe.fhemobile.views.canteen;

import static de.fhe.fhemobile.Main.getAppContext;
import static de.fhe.fhemobile.utils.Define.Canteen.SP_KEY_CANTEEN;
import static de.fhe.fhemobile.utils.Define.Canteen.SP_CANTEEN;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.StickyHeaderAdapter;
import de.fhe.fhemobile.events.CanteenChangeEvent;
import de.fhe.fhemobile.events.Event;
import de.fhe.fhemobile.events.EventListener;
import de.fhe.fhemobile.models.canteen.CanteenModel;
import de.fhe.fhemobile.utils.UserSettings;
import de.fhe.fhemobile.utils.Utils;
import de.fhe.fhemobile.utils.headerlistview.HeaderListView;
import de.fhe.fhemobile.vos.canteen.CanteenDishVo;
import de.fhe.fhemobile.vos.canteen.CanteenMenuDayVo;
import de.fhe.fhemobile.vos.canteen.CanteenVo;
import de.fhe.fhemobile.widgets.stickyHeaderList.CanteenImageRowItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.CanteenRowItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.DefaultHeaderItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.IHeaderItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.IRowItem;

/**
 * Created by paul on 23.01.14
 * Edited by Nadja on 05.05.2022
 */
public class CanteenMenuView extends LinearLayout {

    private static final String TAG = CanteenMenuView.class.getSimpleName();

    public interface ViewListener {
    }

    public CanteenMenuView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mErrorText          = (TextView)        findViewById(R.id.tv_canteen_error);
        mMenuDaysListView   = (HeaderListView)  findViewById(R.id.lv_canteen_menu);
        mCanteenNameText    = (TextView)        findViewById(R.id.tv_canteen_title);
    }

    public void initializeView(final String _CanteenId){
        mCanteenId = _CanteenId;
        final CanteenVo selectedCanteen = UserSettings.getInstance().getSelectedCanteen(mCanteenId);
        if (selectedCanteen == null)
            return;
        mCanteenNameText.setText(selectedCanteen.getCanteenName());

        mErrorText.setVisibility(VISIBLE);
        initMenuDaysList();
    }

    public void registerModelListener() {
        CanteenModel.getInstance().addListener(
                CanteenChangeEvent.getReceivedCanteenMenuEventWithCanteenId(mCanteenId),
                mReceivedCanteenMenuEventListener);
    }

    public void deregisterModelListener() {
        CanteenModel.getInstance().removeListener(
                CanteenChangeEvent.getReceivedCanteenMenuEventWithCanteenId(mCanteenId),
                mReceivedCanteenMenuEventListener);
    }

    /**
     * Initial population of menu days list with list loaded from shared preferences
     */
    public void initMenuDaysList(){
        List<CanteenMenuDayVo> menuDaysList = getCanteenMenuDaysFromSharedPreferences();
        populateMenuDaysList(menuDaysList);
    }

    /**
     * Load menu from {@link CanteenModel} or from shared preferences if menu is empty.
     * If menu is not empty, use the list of {@link CanteenMenuDayVo}s to populate listview,
     * otherwise show an error text.
     */
    public void populateMenuDaysList(){
        List<CanteenMenuDayVo> menuDaysList = CanteenModel.getInstance().getMenu(mCanteenId);

        //if menu empty, load from shared preferences
        if (menuDaysList == null || menuDaysList.isEmpty()){
            menuDaysList = getCanteenMenuDaysFromSharedPreferences();
            showUpdateFailedToast();
        }

        populateMenuDaysList(menuDaysList);
    }

    /**
     * Use the given list of {@link CanteenMenuDayVo}s to populate the listview.
     *
     * @param menuDaysList List of {@link CanteenMenuDayVo}
     */
    private void populateMenuDaysList(List<CanteenMenuDayVo> menuDaysList) {
        //if non-empty, populate view
        if(menuDaysList != null && !menuDaysList.isEmpty()) {
            mMenuDaysListView.setVisibility(VISIBLE);

            mErrorText.setVisibility(GONE);

            final ArrayList<IHeaderItem> sectionList = new ArrayList<>();

            final DefaultHeaderItem headerSection = new DefaultHeaderItem("", false);
            headerSection.addItem(new CanteenImageRowItem(R.drawable.th_canteen));

            sectionList.add(headerSection);

            //populate view
            for (final CanteenMenuDayVo collection : menuDaysList) {
                final ArrayList<IRowItem> rowItems = new ArrayList<>();

                for (final CanteenDishVo canteenItem : collection.getItems()) {
                    rowItems.add(new CanteenRowItem(canteenItem));
                }

                sectionList.add(new DefaultHeaderItem(collection.getHeadline(), true, rowItems));
            }
            final StickyHeaderAdapter mAdapter = new StickyHeaderAdapter(getContext(), sectionList);
            mMenuDaysListView.setAdapter(mAdapter);
        }
        //if list is empty, show error text
        else {
            mErrorText.setVisibility(VISIBLE);
            mMenuDaysListView.setVisibility(GONE);
        }
    }

    /**
     * Load menu from shared preferences
     * @return List of {@link CanteenMenuDayVo}s
     */
    private List<CanteenMenuDayVo> getCanteenMenuDaysFromSharedPreferences() {
        List<CanteenMenuDayVo> menuDaysList = new ArrayList<>();
        final SharedPreferences sharedPreferences = getAppContext().getSharedPreferences(SP_CANTEEN, Context.MODE_PRIVATE);
        final String json = sharedPreferences.getString(SP_KEY_CANTEEN + mCanteenId, "");
        final Gson gson = new Gson();
        if(!json.isEmpty()){
            final Type listType = new TypeToken<ArrayList<CanteenMenuDayVo>>(){}.getType();
            menuDaysList = gson.fromJson(json, listType);
            Log.d(TAG, "Menu for " + mCanteenId + " loaded from Shared Preferences");
        }
        return menuDaysList;
    }

    private static void showUpdateFailedToast(){
        Utils.showToast(R.string.canteen_update_failed);
    }


    private final EventListener mReceivedCanteenMenuEventListener = new EventListener() {
        @Override
        public void onEvent(final Event event) {
            populateMenuDaysList();
        }
    };

    private String          mCanteenId;

    private HeaderListView  mMenuDaysListView;
    private TextView        mCanteenNameText;

    TextView                mErrorText;

}
