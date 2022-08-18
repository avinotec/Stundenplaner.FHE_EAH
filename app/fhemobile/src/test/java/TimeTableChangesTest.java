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

import static de.fhe.fhemobile.utils.myschedule.MyScheduleUtils.groupByModuleId;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.fhe.fhemobile.utils.myschedule.MyScheduleUtils;
import de.fhe.fhemobile.vos.myschedule.ModuleVo;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventSeriesVo;

@RunWith(Parameterized.class)
public class TimeTableChangesTest{

    List<MyScheduleEventSeriesVo> mEventSeriesVos;
    ModuleVo mModule;
    List<MyScheduleEventSeriesVo> mExpectedUpdatedEventSeriesVos;


    public TimeTableChangesTest(@NonNull String folder){
        System.out.println("Testcase: " + folder);
        try {
            final Type listType = new TypeToken<List<MyScheduleEventSeriesVo>>(){}.getType();

            Path pathEventSeriesVos = Paths.get(TimeTableChangesTest.class.getClassLoader().getResource(
                    folder + "/subscribed_eventseries_list.json").toURI());
            final BufferedReader readerEventSeries = Files.newBufferedReader(pathEventSeriesVos);
            mEventSeriesVos = (new Gson()).fromJson(readerEventSeries, listType);

            Path pathExpectedEventSeries = Paths.get(TimeTableChangesTest.class.getClassLoader().getResource(
                    folder + "/subscribed_eventseries_list_updated.json").toURI());
            final BufferedReader readerUpdatedEventSeries = Files.newBufferedReader(pathExpectedEventSeries);
            mExpectedUpdatedEventSeriesVos = (new Gson()).fromJson(readerUpdatedEventSeries, listType);

            Path pathModule = Paths.get(TimeTableChangesTest.class.getClassLoader().getResource(
                    folder + "/module_12345.json").toURI());
            final BufferedReader readerModule = Files.newBufferedReader(pathModule);
            mModule = (new Gson()).fromJson(readerModule, ModuleVo.class);

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Parameterized.Parameters
    public static List<Object[]> testCaseFolders(){
        return Arrays.asList(new Object[][]{
                {"test_addition"},
                {"test_deletion"},
                {"test_edition"},
                {"test_exam_added"}
        });
    }

    @Test
    public void test(){
        //ARRANGE
        Map<String, Map<String, MyScheduleEventSeriesVo>> modules = groupByModuleId(mEventSeriesVos);

        //ACT
        List<MyScheduleEventSeriesVo> updatedEventSeriesVos =
                MyScheduleUtils.getUpdateSubscribedEventSeries(modules.get("12345"), mModule.getEventSets());

        //ASSERT
        Gson gson = new Gson();
        for(int i = 0; i < updatedEventSeriesVos.size(); i++){
            MyScheduleEventSeriesVo actual = updatedEventSeriesVos.get(i);
            MyScheduleEventSeriesVo expected = mExpectedUpdatedEventSeriesVos.get(i);

            //assert
            Assert.assertEquals(gson.toJson(expected.getEvents()), gson.toJson(actual.getEvents()));
        }


    }


}
