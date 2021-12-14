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

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.models.navigation.Cell;
import de.fhe.fhemobile.models.navigation.Exit;
import de.fhe.fhemobile.models.navigation.FloorConnectionCell;
import de.fhe.fhemobile.models.navigation.Room;
import de.fhe.fhemobile.utils.navigation.NavigationUtils;


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


    public void initializeView(Room startRoom, Room destRoom){

        //set start and dest
        mTextViewStart.setText(startRoom.getRoomName());
        mTextViewDest.setText(destRoom.getRoomName());

        //set size of a floor plan cell
        int displayWidth = getResources().getDisplayMetrics().widthPixels;
        cellWidth = displayWidth / cellgrid_width;
        cellHeight = cellWidth;

        //set listeners for buttons navigating between the floorplans
        mButtonPrevPlan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewListener != null) {
                    mViewListener.onPrevPlanClicked();
                }
            }
        });

        mButtonPrevPlan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewListener != null) {
                    mViewListener.onNextPlanClicked();
                }
            }
        });

    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mButtonPrevPlan = (Button) findViewById(R.id.navigationButtonPrevPlan);
        mButtonNextPlan = (Button) findViewById(R.id.navigationButtonNextPlan);

        mFloorPlanImageView = (ImageView) findViewById(R.id.imageview_floorplan);

        mTextViewStart = (TextView) findViewById(R.id.navigation_text_start_value);
        mTextViewDest = (TextView) findViewById(R.id.navigation_text_dest_value);
    }


    public void setFloorPlanImage(Drawable image){

        //Remove views from layouts before redrawing
        if (mNavigationLayout != null) mNavigationLayout.removeAllViews();

        mFloorPlanImageView.setImageDrawable(image);
        mFloorPlanImageView.setX(0);
        mFloorPlanImageView.setY(0);

        //rescale floorplan to match display width
        ViewGroup.LayoutParams layoutParams = mFloorPlanImageView.getLayoutParams();
        layoutParams.width = (int) Math.round(cellWidth*cellgrid_width);
        //intrinsicHeight returns the height of the original floor plan image in the resources
        int floorPlanImageHeight = image.getIntrinsicHeight();
        //Dreisatz: imageheight_new / cellheight = intrinsicHeight / cellheight_original
        //      cellHeight_original = intrinsicHeight / cellgridHeight
        layoutParams.height = (int) Math.round(floorPlanImageHeight * cellHeight / (floorPlanImageHeight/cellgrid_height)) ;
        mFloorPlanImageView.setLayoutParams(layoutParams);

        //TODO: find solution that also works when rotating the device
        //fits image height and width (aspect ratio of the original resource is not maintained -> width and height must be set properly)
        mFloorPlanImageView.setScaleType(ImageView.ScaleType.FIT_XY);

    }


    public void setViewListener(final IViewListener _Listener) {
        mViewListener = _Listener;
    }

    public interface IViewListener {
        void onPrevPlanClicked();
        void onNextPlanClicked();
    }

    /**
     * Convert cell position from unit "cell number" to a unit for displaying
     * @param x position in cells
     * @return x in the displaying unit
     */
    private int convertCellCoordX(int x){
        return (int) Math.round( x * cellWidth);
    }

    /**
     * Convert the cell position from unit cellnumber to a unit for displaying
     * @param y position in cells
     * @return y in the displaying unit
     */
    private int convertCellCoordY(int y){
        return (int) Math.round(y * cellHeight);
    }

    /**
     * Draw all path cells of the calculated route
     * @param complex that is displayed (floorplan)
     * @param floor that is displayed (floorplan)
     */
    public void drawAllPathCells(NavigationUtils.Complex complex, String floor,
                                  ArrayList<Cell> cellsToWalk) {
        try {
            for (int j = 0; j < cellsToWalk.size(); j++) {
                if (cellsToWalk.get(j).getComplex().equals(complex)
                        && cellsToWalk.get(j).getFloorString().equals(floor)) {

                    if(cellsToWalk.get(j) instanceof FloorConnectionCell){
                        drawFloorConnection((FloorConnectionCell) cellsToWalk.get(j));
                    }
                    else if(cellsToWalk.get(j) instanceof Exit){
                        drawExit((Exit) cellsToWalk.get(j));
                    }
                    else{
                        drawPathCell(cellsToWalk.get(j));
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "error drawing path cells:",e);
        }
    }

    /**
     * Draw start location
     * @param complex that is displayed (floorplan)
     * @param floor that is displayed (floorplan)
     * @param startRoom start room of the route
     */
    public void drawStartLocation(NavigationUtils.Complex complex, String floor, Room startRoom) {
        try {
            //add start icon
            ImageView startIcon = new ImageView(getContext());
            startIcon.setImageResource(R.drawable.start_icon);
            if (mNavigationLayout != null) mNavigationLayout.addView(startIcon);
            fitOneCell(startIcon);
            startIcon.setX(convertCellCoordX(startRoom.getXCoordinate()));
            startIcon.setY(convertCellCoordY(startRoom.getYCoordinate()));
        } catch (Exception e) {
            Log.e(TAG, "error drawing start room:", e);
        }
    }

    /**
     * Draw destination location
     * @param complex that is displayed (floorplan)
     * @param floor that is displayed (floorplan)
     * @param destRoom destination room of the route
     */
    public void drawDestinationLocation(NavigationUtils.Complex complex, String floor, Room destRoom) {
        try {
            //add destination icon
            ImageView destinationIcon = new ImageView(getContext());
            destinationIcon.setImageResource(R.drawable.destination_icon);
            if (mNavigationLayout != null) mNavigationLayout.addView(destinationIcon);
            fitOneCell(destinationIcon);
            destinationIcon.setX(convertCellCoordX(destRoom.getXCoordinate()));
            destinationIcon.setY(convertCellCoordY(destRoom.getYCoordinate()));
        } catch (Exception e) {
            Log.e(TAG,"error drawing destination room:", e);
        }
    }

    /**
     * Add a cell icon to display a walkable cell of the calculated route
     * @param cell to draw
     */
    private void drawPathCell(Cell cell){
        ImageView pathCellIcon = new ImageView(getContext());
        pathCellIcon.setImageResource(R.drawable.path_cell_icon);
        if (mNavigationLayout != null) mNavigationLayout.addView(pathCellIcon);

        fitOneCell(pathCellIcon);
        pathCellIcon.setX(convertCellCoordX(cell.getXCoordinate()));
        pathCellIcon.setY(convertCellCoordY(cell.getYCoordinate()));
    }

    /**
     * Draw floorconnection icon corresponding to the floorconnection type (stairs, elevator)
     * @param fCell {@link FloorConnectionCell} to draw
     */
    private void drawFloorConnection(FloorConnectionCell fCell) {
        NavigationUtils.Complex complex = fCell.getComplex();
        String floor = fCell.getFloorString();

        if (fCell.getComplex().equals(complex)
                && fCell.getFloorString().equals(floor)) {

            if (fCell.getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_STAIR)) {
                ImageView stairIcon = new ImageView(getContext());
                stairIcon.setImageResource(R.drawable.stairs_icon);
                if (mNavigationLayout != null) mNavigationLayout.addView(stairIcon);

                fitOneCell(stairIcon);
                stairIcon.setX(convertCellCoordX(fCell.getXCoordinate()));
                stairIcon.setY(convertCellCoordY(fCell.getYCoordinate()));
            }

            if (fCell.getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_ELEVATOR)) {
                ImageView elevatorIcon = new ImageView(getContext());
                elevatorIcon.setImageResource(R.drawable.elevator_icon);
                if (mNavigationLayout != null) mNavigationLayout.addView(elevatorIcon);

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
     * @param exitCell exit to display
     */
    private void drawExit(Exit exitCell){
        ImageView exitIcon = new ImageView(getContext());
        exitIcon.setImageResource(R.drawable.exit_icon);
        if (mNavigationLayout != null) mNavigationLayout.addView(exitIcon);

        fitOneCell(exitIcon);
        exitIcon.setX(convertCellCoordX(exitCell.getXCoordinate()));
        exitIcon.setY(convertCellCoordY(exitCell.getYCoordinate()));
    }

    /**
     * Make the Icon Picture fit exactly one cell
     * @param icon ImageView containing the icon (view must be added to parent view before calling)
     */
    private void fitOneCell(ImageView icon){
        //set height and width of the ImageView
        ViewGroup.LayoutParams layoutParams = icon.getLayoutParams();
        layoutParams.height = (int) Math.round(cellHeight*0.95); //fill 95% of the cell
        layoutParams.width = (int) Math.round(cellWidth*0.95);
        icon.setLayoutParams(layoutParams);
        //make picture fit image height and width (aspect ratio of the resource is not maintained -> width and height must be set properly)
        icon.setScaleType(ImageView.ScaleType.FIT_XY);
    }


    private IViewListener mViewListener;

    private Button mButtonPrevPlan;
    private Button mButtonNextPlan;

    private ImageView mFloorPlanImageView;
    private RelativeLayout mNavigationLayout;

    private TextView mTextViewStart;
    private TextView mTextViewDest;

    private double cellWidth;
    private double cellHeight;

    private String TAG = "NavigationView"; //$NON-NLS
}
