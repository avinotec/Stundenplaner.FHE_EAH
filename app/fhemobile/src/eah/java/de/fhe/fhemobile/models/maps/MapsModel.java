/*
 *  Copyright (c) 2014-2022 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
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

import static de.fhe.fhemobile.utils.Define.Maps.BUILDING_03_02_01_FLOOR_00;
import static de.fhe.fhemobile.utils.Define.Maps.BUILDING_03_02_01_FLOOR_01;
import static de.fhe.fhemobile.utils.Define.Maps.BUILDING_03_02_01_FLOOR_02;
import static de.fhe.fhemobile.utils.Define.Maps.BUILDING_03_02_01_FLOOR_03;
import static de.fhe.fhemobile.utils.Define.Maps.BUILDING_03_02_01_FLOOR_04;
import static de.fhe.fhemobile.utils.Define.Maps.BUILDING_03_02_01_FLOOR_UG1;
import static de.fhe.fhemobile.utils.Define.Maps.BUILDING_03_02_01_GROUND;
import static de.fhe.fhemobile.utils.Define.Maps.BUILDING_04_FLOOR_00;
import static de.fhe.fhemobile.utils.Define.Maps.BUILDING_04_FLOOR_01;
import static de.fhe.fhemobile.utils.Define.Maps.BUILDING_04_FLOOR_02;
import static de.fhe.fhemobile.utils.Define.Maps.BUILDING_04_FLOOR_03;
import static de.fhe.fhemobile.utils.Define.Maps.BUILDING_04_FLOOR_UG1;
import static de.fhe.fhemobile.utils.Define.Maps.BUILDING_04_GROUND;
import static de.fhe.fhemobile.utils.Define.Maps.BUILDING_05_FLOOR_00;
import static de.fhe.fhemobile.utils.Define.Maps.BUILDING_05_FLOOR_01;
import static de.fhe.fhemobile.utils.Define.Maps.BUILDING_05_FLOOR_02;
import static de.fhe.fhemobile.utils.Define.Maps.BUILDING_05_FLOOR_03;
import static de.fhe.fhemobile.utils.Define.Maps.BUILDING_05_FLOOR_3Z;
import static de.fhe.fhemobile.utils.Define.Maps.BUILDING_05_FLOOR_UG1;
import static de.fhe.fhemobile.utils.Define.Maps.BUILDING_05_FLOOR_UG2;
import static de.fhe.fhemobile.utils.Define.Maps.BUILDING_05_GROUND;

import java.util.ArrayList;
import java.util.List;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.vos.maps.MapCollectionVo;
import de.fhe.fhemobile.vos.maps.MapVo;

/**
 * Created by paul on 23.02.14.
 */
public final class MapsModel {


    private static MapsModel ourInstance = null;
    private final List<MapCollectionVo> mMaps;

    /** singleton */
    public static MapsModel getInstance() {
        if(ourInstance == null) {
            ourInstance = new MapsModel();
        }
        return ourInstance;
    }

    public final List<MapCollectionVo> getMaps() {
        return mMaps;
    }

    private MapsModel() {
        mMaps = new ArrayList<>();

        // -----------------------------------------------------------------------------------------
        final List<MapVo> campusOverViewMaps = new ArrayList<>();
        campusOverViewMaps.add(new MapVo(R.string.campus_overview, "overview.png"));
        final MapCollectionVo campusOverview = new MapCollectionVo(R.string.campus_overview, campusOverViewMaps);

        
        // -----------------------------------------------------------------------------------------

        final List<MapVo> building321Maps = new ArrayList<>();
        building321Maps.add(new MapVo(R.string.building_03_02_01_ground,BUILDING_03_02_01_GROUND+".png"));
        building321Maps.add(new MapVo(R.string.building_03_02_01_floor_ug1,  BUILDING_03_02_01_FLOOR_UG1 +".png"));
        building321Maps.add(new MapVo(R.string.building_03_02_01_floor_00,  BUILDING_03_02_01_FLOOR_00+".png"));
        building321Maps.add(new MapVo(R.string.building_03_02_01_floor_01, BUILDING_03_02_01_FLOOR_01+".png"));
        building321Maps.add(new MapVo(R.string.building_03_02_01_floor_02, BUILDING_03_02_01_FLOOR_02+".png"));
        building321Maps.add(new MapVo(R.string.building_03_02_01_floor_03, BUILDING_03_02_01_FLOOR_03+".png"));
        building321Maps.add(new MapVo(R.string.building_03_02_01_floor_04, BUILDING_03_02_01_FLOOR_04+".png"));
        final MapCollectionVo building321 = new MapCollectionVo(R.string.building_03_02_01, building321Maps);


        // -----------------------------------------------------------------------------------------

        final List<MapVo> building4Maps = new ArrayList<>();
        building4Maps.add(new MapVo(R.string.building_04_ground,  BUILDING_04_GROUND+".png"));
        building4Maps.add(new MapVo(R.string.building_04_floor_ug1,  BUILDING_04_FLOOR_UG1 +".png"));
        building4Maps.add(new MapVo(R.string.building_04_floor_00,  BUILDING_04_FLOOR_00+".png"));
        building4Maps.add(new MapVo(R.string.building_04_floor_01,  BUILDING_04_FLOOR_01+".png"));
        building4Maps.add(new MapVo(R.string.building_04_floor_02,  BUILDING_04_FLOOR_02+".png"));
        building4Maps.add(new MapVo(R.string.building_04_floor_03,  BUILDING_04_FLOOR_03+".png"));

        final MapCollectionVo building4 = new MapCollectionVo(R.string.building_04, building4Maps);


        // -----------------------------------------------------------------------------------------

        final List<MapVo> building5Maps = new ArrayList<>();
        building5Maps.add(new MapVo(R.string.building_05_floor_ground,  BUILDING_05_GROUND+".png"));
        building5Maps.add(new MapVo(R.string.building_05_floor_ug2,  BUILDING_05_FLOOR_UG2+".png"));
        building5Maps.add(new MapVo(R.string.building_05_floor_ug1,  BUILDING_05_FLOOR_UG1+".png"));
        building5Maps.add(new MapVo(R.string.building_05_floor_00,  BUILDING_05_FLOOR_00+".png"));
        building5Maps.add(new MapVo(R.string.building_05_floor_01,  BUILDING_05_FLOOR_01+".png"));
        building5Maps.add(new MapVo(R.string.building_05_floor_02,  BUILDING_05_FLOOR_02+".png"));
        building5Maps.add(new MapVo(R.string.building_05_floor_03,  BUILDING_05_FLOOR_03 +".png"));
        building5Maps.add(new MapVo(R.string.building_05_floor_3Z,  BUILDING_05_FLOOR_3Z +".png"));
        final MapCollectionVo building5 = new MapCollectionVo(R.string.building_05, building5Maps);
        // -----------------------------------------------------------------------------------------
/*
TODO Haus 6
        final List<MapVo> building6Maps = new ArrayList<>();
        building5Maps.add(new MapVo(R.string.building_06_floor_ground,  BUILDING_06_GROUND+".png"));
        building5Maps.add(new MapVo(R.string.building_06_floor_ug2,  BUILDING_06_FLOOR_UG2+".png"));
        building5Maps.add(new MapVo(R.string.building_06_floor_ug1,  BUILDING_06_FLOOR_UG1+".png"));
*/

        mMaps.add(campusOverview);
        mMaps.add(building321);
        mMaps.add(building4);
        mMaps.add(building5);
        //mMaps.add(building6); TODO Haus 6
    }

}
