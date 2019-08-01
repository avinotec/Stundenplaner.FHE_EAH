package de.fhe.fhemobile.adapters.drawer;

/**
 * Created by paul on 18.03.15.
 */
public class DrawerItem {
    public DrawerItem(int _id, String _text) {
        mId = _id;
        mText = _text;
    }

    public int getId() {
        return mId;
    }

    public void setId(int _id) {
        mId = _id;
    }

    public String getText() {
        return mText;
    }

    public void setText(String _text) {
        mText = _text;
    }

    private int    mId;
    private String mText;
}