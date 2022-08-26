/*
 *  Copyright (c) 2014-2022 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
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

package de.fhe.fhemobile.views.navigation;

import static de.fhe.fhemobile.utils.Define.Navigation.FLOORCONNECTION_TYPE_ELEVATOR;
import static de.fhe.fhemobile.utils.Define.Navigation.FLOORCONNECTION_TYPE_STAIR;
import static de.fhe.fhemobile.utils.Define.Navigation.cellgrid_height;
import static de.fhe.fhemobile.utils.Define.Navigation.cellgrid_width;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.vos.navigation.BuildingExitVo;
import de.fhe.fhemobile.models.navigation.Cell;
import de.fhe.fhemobile.models.navigation.FloorConnectionCell;
import de.fhe.fhemobile.vos.navigation.RoomVo;
import de.fhe.fhemobile.vos.navigation.Complex;
import de.fhe.fhemobile.utils.navigation.BuildingFloorKey;


/**
 * Created by Nadja 12.12.2020
 */
public class NavigationView extends LinearLayout {

    public NavigationView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public NavigationView(final Context context) {
        super(context);
    }


    public void initializeView(final RoomVo startRoom, final RoomVo destRoom){

        //set start and dest
        mTextStart.setText(getResources().getString(R.string.navigation_start) +"   "+ startRoom.getRoomName());
        mTextDest.setText(getResources().getString(R.string.navigation_dest) +"   "+ destRoom.getRoomName());

        //set size of a floor plan cell
        final int displayWidth = getResources().getDisplayMetrics().widthPixels;
        cellHeight = cellWidth = displayWidth / (double) cellgrid_width;

        //set listeners for buttons navigating between the floorplans
        mButtonPrevPlan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (mViewListener != null) {
                    mViewListener.onPrevPlanClicked();
                }
            }
        });

        mButtonNextPlan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (mViewListener != null) {
                    mViewListener.onNextPlanClicked();
                }
            }
        });
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mButtonPrevPlan = (Button) findViewById(R.id.btn_navigation_previousplan);
        mButtonNextPlan = (Button) findViewById(R.id.btn_navigation_nextplan);

        mFloorPlanImageView = (ImageView) findViewById(R.id.imageview_floorplan);

        mNavigationLayout = (RelativeLayout) findViewById(R.id.layout_navigation_placeholder);

        mTextStart = (TextView) findViewById(R.id.tv_navigation_start);
        mTextDest = (TextView) findViewById(R.id.tv_navigation_dest);
        mTextCurrentFloorPlan = (TextView) findViewById(R.id.tv_navigation_current_floorplan);

        mBackButton = (Button) findViewById(R.id.btn_navigation_back);
    }

    /**
     * Removes old floorplan and route and sets new image
     * @param image new floorplan drawable
     */
    public void drawFloorPlanImage(final Drawable image, final BuildingFloorKey currentFloorPlan){

        removeRoute();
        mNavigationLayout.addView(mFloorPlanImageView);

        mFloorPlanImageView.setImageDrawable(image);
        mFloorPlanImageView.setX(0);
        mFloorPlanImageView.setY(0);

        //rescale floorplan to match display width
        final ViewGroup.LayoutParams layoutParams = mFloorPlanImageView.getLayoutParams();
        layoutParams.width = (int) Math.round(cellWidth*cellgrid_width);
        //intrinsicHeight returns the height of the original floor plan image in the resources
        final int floorPlanImageHeight = image.getIntrinsicHeight();
        //Dreisatz: imageheight_new / cellheight = intrinsicHeight / cellheight_original
        //      cellHeight_original = intrinsicHeight / cellgridHeight
        layoutParams.height = (int) Math.round(floorPlanImageHeight * cellHeight / (floorPlanImageHeight/(float)cellgrid_height)) ;
        mFloorPlanImageView.setLayoutParams(layoutParams);

        //TODO: find solution that also works when rotating the device
        //fits image height and width (aspect ratio of the original resource is not maintained -> width and height must be set properly)
        mFloorPlanImageView.setScaleType(ImageView.ScaleType.FIT_XY);


        mTextCurrentFloorPlan.setText(
                getResources().getString(R.string.navigation_current_floorplan) +" "+
                        getResources().getString(R.string.navigation_building) + " "+
                        currentFloorPlan.getComplex().toString().replace("_", "-") + ", "+
                        getResources().getString(R.string.navigation_floor) + " "+
                        currentFloorPlan.getFloorString());

    }


    public void setViewListener(final IViewListener _Listener) {
        mViewListener = _Listener;
    }

    public void setOnClickListener(final OnClickListener _Listener){
        mBackButton.setOnClickListener(_Listener);
    }

    public interface IViewListener {
        void onPrevPlanClicked();
        void onNextPlanClicked();
    }

    public void togglePrevPlanButtonEnabled(final boolean _Enabled){
        mButtonPrevPlan.setEnabled(_Enabled);
        if(_Enabled){
            mButtonPrevPlan.setBackgroundResource(R.drawable.buttonshape_rectangle);
        } else {
            mButtonPrevPlan.setBackgroundResource(R.drawable.buttonshape_rectangle_disabled);
        }
    }

    public void toggleNextPlanButtonEnabled(final boolean _Enabled){
        mButtonNextPlan.setEnabled(_Enabled);
        if(_Enabled){
            mButtonNextPlan.setBackgroundResource(R.drawable.buttonshape_rectangle);
            mBackButton.setVisibility(GONE);
        }else{
            mButtonNextPlan.setBackgroundResource(R.drawable.buttonshape_rectangle_disabled);
            mBackButton.setVisibility(VISIBLE);

        }
    }

    /**
     * Removes route icons from the floor plan
     */
    public void removeRoute(){
        //Remove views from layouts before redrawing
        if (mNavigationLayout != null) mNavigationLayout.removeAllViews();
    }


    /**
     * Convert cell position from unit "cell number" to a unit for displaying
     * @param x position in cells
     * @return x in the displaying unit
     */
    private int convertCellCoordX(final int x){
        return (int) Math.round( x * cellWidth);
    }

    /**
     * Convert the cell position from unit cellnumber to a unit for displaying
     * @param y position in cells
     * @return y in the displaying unit
     */
    private int convertCellCoordY(final int y){
        return (int) Math.round(y * cellHeight);
    }

    /**
     * Display all cells of the route
     * @param currentlyDisplayed BuildingFloorKey describing the currently displayed floorplan and route
     * @param cellsToWalk Map of all cells to walk (complete route)
     */
    public void drawAllPathCells(final BuildingFloorKey currentlyDisplayed, final Map<BuildingFloorKey, ArrayList<Cell>> cellsToWalk) {
        try {
            final ArrayList<Cell> cellList = cellsToWalk.get(currentlyDisplayed);
            for(final Cell cell : cellList) {
                if (cell instanceof FloorConnectionCell) {
                    drawFloorConnection((FloorConnectionCell) cell);
                } else if (cell instanceof BuildingExitVo) {
                    drawExit((BuildingExitVo) cell);
                } else {
                    drawPathCell(cell);
                }
            }
        } catch (final RuntimeException e) {
            Log.e(TAG, "error drawing path cells:",e);
        }
    }

    /**
     * Draw start location
     * @param startRoom start room of the route
     */
    public void drawStartLocation(final RoomVo startRoom) {
        try {
            //add start icon
            final ImageView startIcon = new ImageView(getContext());
            startIcon.setImageResource(R.drawable.start_icon);
            if (mNavigationLayout != null) mNavigationLayout.addView(startIcon);
            else Log.d(TAG, "Layout holding navigation route is null");
            fitOneCell(startIcon);
            startIcon.setX(convertCellCoordX(startRoom.getXCoordinate()));
            startIcon.setY(convertCellCoordY(startRoom.getYCoordinate()));
        } catch (final RuntimeException e) {
            Log.e(TAG, "error drawing start room:", e);
        }
    }

    /**
     * Draw destination location
     * @param destRoom destination room of the route
     */
    public void drawDestinationLocation(final RoomVo destRoom) {
        try {
            //add destination icon
            final ImageView destinationIcon = new ImageView(getContext());
            destinationIcon.setImageResource(R.drawable.destination_icon);
            if (mNavigationLayout != null) mNavigationLayout.addView(destinationIcon);
            fitOneCell(destinationIcon);
            destinationIcon.setX(convertCellCoordX(destRoom.getXCoordinate()));
            destinationIcon.setY(convertCellCoordY(destRoom.getYCoordinate()));
        } catch (final RuntimeException e) {
            Log.e(TAG,"error drawing destination room:", e);
        }
    }

    /**
     * Add a cell icon to display a walkable cell of the calculated route
     * @param cell to draw
     */
    private void drawPathCell(final Cell cell){
        final ImageView pathCellIcon = new ImageView(getContext());
        pathCellIcon.setImageResource(R.drawable.path_cell_icon);
        if (mNavigationLayout != null) mNavigationLayout.addView(pathCellIcon);
        else Log.d(TAG, "Layout holding navigation route is null");

        fitOneCell(pathCellIcon);
        pathCellIcon.setX(convertCellCoordX(cell.getXCoordinate()));
        pathCellIcon.setY(convertCellCoordY(cell.getYCoordinate()));
    }

    /**
     * Draw floorconnection icon corresponding to the floorconnection type (stairs, elevator)
     * @param fCell {@link FloorConnectionCell} to draw
     */
    private void drawFloorConnection(final FloorConnectionCell fCell) {
        final Complex complex = fCell.getComplex();
        final String floor = fCell.getFloorString();

        if (fCell.getComplex() == complex
                && fCell.getFloorString().equals(floor)) {

            if (fCell.getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_STAIR)) {
                final ImageView stairIcon = new ImageView(getContext());
                stairIcon.setImageResource(R.drawable.stairs_icon);
                if (mNavigationLayout != null) mNavigationLayout.addView(stairIcon);
                else Log.d(TAG, "Layout holding navigation route is null");

                fitOneCell(stairIcon);
                stairIcon.setX(convertCellCoordX(fCell.getXCoordinate()));
                stairIcon.setY(convertCellCoordY(fCell.getYCoordinate()));
            }

            if (fCell.getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_ELEVATOR)) {
                final ImageView elevatorIcon = new ImageView(getContext());
                elevatorIcon.setImageResource(R.drawable.elevator_icon);
                if (mNavigationLayout != null) mNavigationLayout.addView(elevatorIcon);
                else Log.d(TAG, "Layout holding navigation route is null");

                fitOneCell(elevatorIcon);
                elevatorIcon.setX(convertCellCoordX(fCell.getXCoordinate()));
                elevatorIcon.setY(convertCellCoordY(fCell.getYCoordinate()));
            }

            //bridge is ignored
//            if (fCell.getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_BRIDGE)) {
//                ImageView bridgeIcon = new ImageView(this);
//                bridgeIcon.setImageResource(R.drawable.bridge_icon);
//                if (navigationLayout != null) navigationLayout.addView(bridgeIcon);
//
//                fitOneCell(bridgeIcon);
//                bridgeIcon.setX(convertCellCoordX(fCell.getXCoordinate()));
//                bridgeIcon.setY(convertCellCoordY(fCell.getYCoordinate()));
//            }
        }
    }

    /**
     * Draw exit icon corresponding to the exitCell
     * @param buildingExitCell exit to display
     */
    private void drawExit(final BuildingExitVo buildingExitCell){
        final ImageView exitIcon = new ImageView(getContext());
        exitIcon.setImageResource(R.drawable.exit_icon);
        if (mNavigationLayout != null) mNavigationLayout.addView(exitIcon);
        else Log.d(TAG, "Layout holding navigation route is null");

        fitOneCell(exitIcon);
        exitIcon.setX(convertCellCoordX(buildingExitCell.getXCoordinate()));
        exitIcon.setY(convertCellCoordY(buildingExitCell.getYCoordinate()));
    }


    /**
     * Make the icon image fit exactly one cell
     * @param icon ImageView containing the icon (view must be added to parent view before calling)
     */
    private void fitOneCell(final ImageView icon){
        //visibility set GONE to avoid shortly displaying falsely sized icons
        icon.setVisibility(GONE);

        //post-Method needed because otherwise icon is not really drawn yed and thus layoutParams are null
        icon.post(new Runnable() {
            @Override
            public void run() {
                //set height and width of the ImageView
                final ViewGroup.LayoutParams layoutParams = icon.getLayoutParams();
                layoutParams.height = (int) Math.round(cellHeight*0.95); //fill 95% of the cell
                layoutParams.width = (int) Math.round(cellWidth*0.95);
                icon.setLayoutParams(layoutParams);
                //make picture fit image height and width (aspect ratio of the resource is not maintained -> width and height must be set properly)
                icon.setScaleType(ImageView.ScaleType.FIT_XY);

                //visibility was set GONE before to avoid shortly displaying falsely sized icons
                icon.setVisibility(VISIBLE);
            }
        });
    }


    IViewListener mViewListener;

    private Button mButtonPrevPlan;
    private Button mButtonNextPlan;
    private Button mBackButton;

    private ImageView mFloorPlanImageView;
    private RelativeLayout mNavigationLayout;

    private TextView mTextStart;
    private TextView mTextDest;
    private TextView mTextCurrentFloorPlan;

    double cellWidth;
    double cellHeight;

    private static final String TAG = NavigationView.class.getSimpleName();
}
