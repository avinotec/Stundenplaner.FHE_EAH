package de.fhe.fhemobile.vos.maps;

import java.util.List;

/**
 * Created by paul on 04.03.15.
 */
public class MapCollectionVo {

    public MapCollectionVo(String _name, List<MapVo> _maps) {
        mName = _name;
        mMaps = _maps;
    }

    public String getName() {
        return mName;
    }

    public void setName(String _name) {
        mName = _name;
    }

    public List<MapVo> getMaps() {
        return mMaps;
    }

    public void setMaps(List<MapVo> _maps) {
        mMaps = _maps;
    }

    private String      mName;
    private List<MapVo> mMaps;
}
