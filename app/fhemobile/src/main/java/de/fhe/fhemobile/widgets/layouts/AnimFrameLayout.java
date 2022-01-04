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
package de.fhe.fhemobile.widgets.layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by paul on 03.03.15.
 */
public class AnimFrameLayout extends FrameLayout {
    //AnimFrameLayout-------------------------------------------------------------------------------
    public AnimFrameLayout(final Context _context, final AttributeSet _attrs) {
        super(_context, _attrs);
    }

// --Commented out by Inspection START (01.11.2019 00:18):
//    //getXFraction----------------------------------------------------------------------------------
//    public float getXFraction() {
//        return getX() / getWidth(); // TODO: guard divide-by-zero
//    }
// --Commented out by Inspection STOP (01.11.2019 00:18)

// --Commented out by Inspection START (01.11.2019 00:19):
//    //setXFraction----------------------------------------------------------------------------------
//    public void setXFraction(float _xFraction) {
//        final int width = getWidth();
//        setX((width > 0) ? (_xFraction * width) : -9999);
//    }
// --Commented out by Inspection STOP (01.11.2019 00:19)

// --Commented out by Inspection START (01.11.2019 00:19):
//    //getYFraction----------------------------------------------------------------------------------
//    public float getYFraction() {
//        return getY() / getHeight(); // TODO: guard divide-by-zero
//    }
// --Commented out by Inspection STOP (01.11.2019 00:19)

// --Commented out by Inspection START (01.11.2019 00:19):
//    //setYFraction----------------------------------------------------------------------------------
//    public void setYFraction(float _yFraction) {
//        final int height = getHeight();
//        setY((height > 0) ? (_yFraction * height) : -9999);
//    }
// --Commented out by Inspection STOP (01.11.2019 00:19)
}
