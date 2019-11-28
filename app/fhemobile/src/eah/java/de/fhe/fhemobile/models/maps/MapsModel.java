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

package de.fhe.fhemobile.models.maps;

import java.util.ArrayList;
import java.util.List;

import de.fhe.fhemobile.vos.maps.MapCollectionVo;
import de.fhe.fhemobile.vos.maps.MapVo;

/**
 * Created by paul on 23.02.14.
 */
public class MapsModel {


    private static MapsModel ourInstance = null;
    private final List<MapCollectionVo> mMaps;

    public static MapsModel getInstance() {
        if(ourInstance == null) {
            ourInstance = new MapsModel();
        }
        return ourInstance;
    }

    final public List<MapCollectionVo> getMaps() {
        return mMaps;
    }

    private MapsModel() {
        mMaps = new ArrayList<>();

        // -----------------------------------------------------------------------------------------
        List<MapVo> campusOverViewMaps = new ArrayList<>();
        campusOverViewMaps.add(new MapVo("Campus Übersicht", "campus_overview.jpg"));
        MapCollectionVo campusOverview = new MapCollectionVo("Campus Übersicht", campusOverViewMaps);

        
        // -----------------------------------------------------------------------------------------

        final List<MapVo> building3Maps = new ArrayList<>();
        building3Maps.add(new MapVo("Haus 3-2-1",       "Haus3-0_Start2.jpeg"));
        building3Maps.add(new MapVo("Haus 3-2-1 - KG",  "Haus3-1_KG.jpeg"));
        building3Maps.add(new MapVo("Haus 3-2-1 - EG",  "Haus3-2_EG.jpeg"));
        building3Maps.add(new MapVo("Haus 3-2-1 - OG1", "Haus3-3_OG1.jpeg"));
        building3Maps.add(new MapVo("Haus 3-2-1 - OG2", "Haus3-4_OG2.jpeg"));
        building3Maps.add(new MapVo("Haus 3-2-1 - OG3", "Haus3-5_OG3.jpeg"));
        building3Maps.add(new MapVo("Haus 3-2-1 - OG4", "Haus3-6_OG4.jpeg"));
        MapCollectionVo building3 = new MapCollectionVo("Haus 3-2-1", building3Maps);


        // -----------------------------------------------------------------------------------------

        final List<MapVo> building4Maps = new ArrayList<>();
        building4Maps.add(new MapVo("Haus 4",       "Haus4-0_Start.jpeg"));
        building4Maps.add(new MapVo("Haus 4 - KG",  "Haus4-1_KG.jpeg"));
        building4Maps.add(new MapVo("Haus 4 - EG",  "Haus4-2_EG.jpeg"));
        building4Maps.add(new MapVo("Haus 4 - OG1", "Haus4-3_OG1.jpeg"));
        building4Maps.add(new MapVo("Haus 4 - OG2", "Haus4-4_OG2.jpeg"));
        building4Maps.add(new MapVo("Haus 4 - OG3", "Haus4-5_OG3.jpeg"));
        MapCollectionVo building4 = new MapCollectionVo("Haus 4", building4Maps);


        // -----------------------------------------------------------------------------------------

        final List<MapVo> building5Maps = new ArrayList<>();
        building5Maps.add(new MapVo("Haus 5",       "Haus5-0_Start.jpeg"));
        building5Maps.add(new MapVo("Haus 5 - KG",  "Haus5-1_KG.jpeg"));
        building5Maps.add(new MapVo("Haus 5 - EG",  "Haus5-2_EG.jpeg"));
        building5Maps.add(new MapVo("Haus 5 - OG1", "Haus5-3_OG1.jpeg"));
        building5Maps.add(new MapVo("Haus 5 - OG2", "Haus5-4_OG2.jpeg"));
        building5Maps.add(new MapVo("Haus 5 - OG3", "Haus5-5_OG3.jpeg"));
        MapCollectionVo building5 = new MapCollectionVo("Haus 5", building5Maps);


        // -----------------------------------------------------------------------------------------

        //final List<MapVo> hitAulaMaps = new ArrayList<>();
        //hitAulaMaps.add(new MapVo("HIT - Aula", "HIT-Aula.jpg"));
        //MapCollectionVo hitAula = new MapCollectionVo("HIT - Aula", hitAulaMaps);

        
        // -----------------------------------------------------------------------------------------

        //final List<MapVo> hitCampusMaps = new ArrayList<>();
        //hitCampusMaps.add(new MapVo("HIT - Campus", "HIT-Campus.jpg"));
        //MapCollectionVo hitCampus = new MapCollectionVo("HIT - Campus", hitCampusMaps);
        
        mMaps.add(campusOverview);
        mMaps.add(building3);
        mMaps.add(building4);
        mMaps.add(building5);
        //mMaps.add(hitAula);
        //mMaps.add(hitCampus);
    }

}
