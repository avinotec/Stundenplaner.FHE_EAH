package de.fhe.fhemobile.models.maps;

import java.util.ArrayList;
import java.util.List;

import de.fhe.fhemobile.vos.maps.MapCollectionVo;
import de.fhe.fhemobile.vos.maps.MapVo;

/**
 * Created by paul on 23.02.14.
 */
public class MapsModel {

    public static MapsModel getInstance() {
        if(ourInstance == null) {
            ourInstance = new MapsModel();
        }
        return ourInstance;
    }

    public List<MapCollectionVo> getMaps() {
        return mMaps;
    }

    private MapsModel() {
        mMaps = new ArrayList<>();

        List<MapVo> placesList = new ArrayList<>();
        placesList.add(new MapVo("Standorte FH Erfurt", "map_standorte.png"));
        MapCollectionVo places = new MapCollectionVo("Standorte FH Erfurt", placesList);

        List<MapVo> altonaerList = new ArrayList<>();
        altonaerList.add(new MapVo("Campus Altonaer Straße", "map_campus.png"));
        MapCollectionVo altonaer = new MapCollectionVo("Campus Altonaer Straße", altonaerList);

        List<MapVo> steinplatzList = new ArrayList<>();
        steinplatzList.add(new MapVo("Steinplatz 2", "map_steinplatz.png"));
        MapCollectionVo steinplatz = new MapCollectionVo("Steinplatz 2", steinplatzList);

        List<MapVo> leipzigerList = new ArrayList<>();
        leipzigerList.add(new MapVo("Leipziger Straße 77", "map_leipziger.png"));
        MapCollectionVo leipziger = new MapCollectionVo("Leipziger Straße 77", leipzigerList);

        List<MapVo> schlueterList = new ArrayList<>();
        schlueterList.add(new MapVo("Schlüterstraße 1", "map_schlueter.png"));
        MapCollectionVo schlueter = new MapCollectionVo("Schlüterstraße 1", schlueterList);

        mMaps.add(places);
        mMaps.add(altonaer);
        mMaps.add(steinplatz);
        mMaps.add(leipziger);
        mMaps.add(schlueter);
    }

    private static MapsModel ourInstance = null;

    private List<MapCollectionVo> mMaps;
}
