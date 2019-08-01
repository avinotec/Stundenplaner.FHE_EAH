package de.fhe.fhemobile.widgets.layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by paul on 03.03.15.
 */
public class AnimFrameLayout extends FrameLayout {
    //AnimFrameLayout-------------------------------------------------------------------------------
    public AnimFrameLayout(Context _context, AttributeSet _attrs) {
        super(_context, _attrs);
    }

    //getXFraction----------------------------------------------------------------------------------
    public float getXFraction() {
        return getX() / getWidth(); // TODO: guard divide-by-zero
    }

    //setXFraction----------------------------------------------------------------------------------
    public void setXFraction(float _xFraction) {
        final int width = getWidth();
        setX((width > 0) ? (_xFraction * width) : -9999);
    }

    //getYFraction----------------------------------------------------------------------------------
    public float getYFraction() {
        return getY() / getHeight(); // TODO: guard divide-by-zero
    }

    //setYFraction----------------------------------------------------------------------------------
    public void setYFraction(float _yFraction) {
        final int height = getHeight();
        setY((height > 0) ? (_yFraction * height) : -9999);
    }
}
