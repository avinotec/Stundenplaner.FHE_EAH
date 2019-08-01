package de.fhe.fhemobile.vos.maps;

/**
 * Created by paul on 23.02.14.
 */
public class MapVo {

    public MapVo() {
    }

    public MapVo(String _name, String _imageUrl) {
        mName = _name;
        mImageUrl = _imageUrl;
    }

    public String getName() {
        return mName;
    }

    public void setName(String _name) {
        mName = _name;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String _imageUrl) {
        mImageUrl = _imageUrl;
    }

    private String mName;
    private String mImageUrl;
}
