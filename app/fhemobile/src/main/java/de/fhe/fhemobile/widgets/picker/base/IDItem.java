package de.fhe.fhemobile.widgets.picker.base;

/**
 * Created by paul on 12.03.15.
 */
public class IDItem {
    public IDItem(String _Name, String _Id) {
        mName = _Name;
        mId   = _Id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String _name) {
        mName = _name;
    }

    public String getId() {
        return mId;
    }

    public void setId(String _id) {
        mId = _id;
    }

    private String mName;
    private String mId;
}
