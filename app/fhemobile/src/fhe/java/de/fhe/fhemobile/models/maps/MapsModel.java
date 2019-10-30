/*
 * Copyright (c) 2014 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.fhe.fhemobile.models.maps;

import java.util.ArrayList;
import java.util.List;

import de.fhe.fhemobile.vos.maps.MapCollectionVo;
import de.fhe.fhemobile.vos.maps.MapVo;

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

        final List<MapVo> placesList = new ArrayList<>();
        placesList.add(new MapVo("Standorte FH Erfurt", "map_standorte.png"));
        MapCollectionVo places = new MapCollectionVo("Standorte FH Erfurt", placesList);

        final List<MapVo> altonaerList = new ArrayList<>();
        altonaerList.add(new MapVo("Campus Altonaer Straße", "map_campus.png"));
        MapCollectionVo altonaer = new MapCollectionVo("Campus Altonaer Straße", altonaerList);

        final List<MapVo> steinplatzList = new ArrayList<>();
        steinplatzList.add(new MapVo("Steinplatz 2", "map_steinplatz.png"));
        MapCollectionVo steinplatz = new MapCollectionVo("Steinplatz 2", steinplatzList);

        final List<MapVo> leipzigerList = new ArrayList<>();
        leipzigerList.add(new MapVo("Leipziger Straße 77", "map_leipziger.png"));
        MapCollectionVo leipziger = new MapCollectionVo("Leipziger Straße 77", leipzigerList);

        final List<MapVo> schlueterList = new ArrayList<>();
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
