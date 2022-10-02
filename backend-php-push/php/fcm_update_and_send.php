<?php
/****************************************************************************
 *  Copyright (c) 2014-2022 Ernst-Abbe-Hochschule Jena
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
 *****************************************************************************/

declare(strict_types=1);

require_once 'define.php';
require_once 'utils.php';

/* wird vom Cron-Job aus aufgerufen */

require_once 'fcm_connect_db.php';

$debug = true;
if ($debug) error_log(print_r("Begin Script fcm_update_and_send.php", true));
$output = "";



// ---------------------------------------------------------------------------
// ---------------- functions ------------------------------------------------
// ---------------------------------------------------------------------------

function fetchModuleAndUpdateDatabase(string $module_id): void {
    /** @var mysqli $db_timetable database connection */
    global $db_timetable;

	$module_url = API_BASE_URL . ENDPOINT_MODULE_DETAIL . $module_id;
	// second parameter must be true to enable key-value iteration
	$module_data = json_decode(file_get_contents($module_url), true);

    if(!array_key_exists("dataActivity", $module_data)){ return; }

	$fetched_eventset_ids = array_keys($module_data["dataActivity"]);
	$local_eventset_ids = $db_timetable->query("SELECT eventset_id FROM event_sets WHERE module_id = '$module_id'");

	if($local_eventset_ids != null){
	    //DELETED EVENT SETS
	    //detect deleted event set ids
	    $deleted_eventsets = array_diff($local_eventset_ids, $fetched_eventset_ids);
    	echo "<p>Deleted Event Set Ids: " . $deleted_eventsets . "</p>";
    	//delete those event sets in database
    	foreach($deleted_eventsets as $deletedEventSetId){
            $db_timetable->query("DELETE FROM event_sets WHERE eventset_id = '$deletedEventSetId'");
            $eventseries_name = getEventSeriesName($module_data["dataActivity"][$deletedEventSetId]["activityName"]);
            sendNotification($eventseries_name);
    	}

        //CHANGED and ADDED EVENT SERIES
    	// per module, check each event set for changes
    	foreach ($module_data["dataActivity"] as $eventset_id=>$eventset_data){
    		$fetched_eventset_json = json_encode($eventset_data);
            $eventseries_name = getEventSeriesName($eventset_data["activityName"]);
    		//get local event set
    		$result_local_eventset = $db_timetable->query("SELECT * FROM event_sets WHERE eventset_id = '$eventset_id'");

            //EVENT SET probably CHANGED
    		if ($result_local_eventset != null && $result_local_eventset -> num_rows > 0){
    		    //compare local vs fetched event set checksum
                $json_local_eventset = string($result_local_eventset[0]["eventset_data"]);
    			if ($json_local_eventset !== $fetched_eventset_json){
    				echo "Eventset " . $result_local_eventset[0]["eventset_id"] . " has changed since " . $result_local_eventset[0]["last_changed"];
    				//update database
                    $db_timetable->query("UPDATE event_sets SET eventset_data = '$fetched_eventset_json'
    							WHERE eventset_id = '$eventset_id'");
                    sendNotification($eventseries_name, $db_timetable);
    			}
    		}
    		//EVENT SET ADDED
    		//no local event set with this id found --> event set is new and has to be added
    		else {
    		    //note: use ' for the values not ` to avoid "unknown column in field list" error in MySQL
                $db_timetable->query("INSERT INTO event_sets(`eventset_id`, `eventseries`, `module_id`, `eventset_data`, `last_changed`)
    			    		VALUES ('$eventset_id', '$eventseries_name', '$module_id', '$fetched_eventset_json', SYSDATE())");
                sendNotification($eventseries_name, $db_timetable);
                //todo: if eventseries is an exam, notify users with eventseries names belonging to the same module
    		}

    		$result_local_eventset->close();
	    }
	}
	//new module -> needs to be added
	else{
	    foreach ($module_data["dataActivity"] as $eventset_id=>$eventset_data){
	        $eventseries_name = getEventSeriesName($eventset_data["activityName"]);
            $fetched_eventset_json = json_encode($eventset_data);
	        //note: use ' for the values not ` to avoid "unknown column in field list" error in MySQL
            $db_timetable->query("INSERT INTO event_sets(`eventset_id`, `eventseries`, `module_id`, `eventset_data`, `last_changed`)
                		VALUES ('$eventset_id', '$eventseries_name', '$module_id', '$fetched_eventset_json', SYSDATE())");
	    }
	}

}

function sendNotification(string $eventseries_name): void {
	global $debug;
    /** @var mysqli $db_timetable database connection */
    global $db_timetable;

    //get tokens subscribing the given eventseries
	$result_subscribing_user = $db_timetable->query("SELECT token, os FROM fcm_user WHERE eventseries_name = '$eventseries_name'");
	$token_array = array(array(), array());

	//Collect tokens into $token_array
    if($debug) echo "<p><b>Tokens subscribing" . $eventseries_name . ": </b><br>";
	if ($result_subscribing_user -> num_rows > 0) {
	    while ($subscribing_user = $result_subscribing_user->fetch_assoc()) {
            echo $subscribing_user["token"] . ", ";

            if($subscribing_user["os"] === ANDROID){
                //send android push
                sendFCM($subscribing_user["token"], $eventseries_name);

            } else if($subscribing_user["os"] == IOS){
                //send Ios Push
            } else {
                error_log(print_r("++++ PUSH wrong OS!! ++++<br>\n", true));
                exit;
            }
	    }

		echo "<br>Notifications for the Event Series $eventseries_name sent!</p>";

	} else{
	    echo "There are no tokens subscribing the Event Series <b>$eventseries_name</b>!</p>";
	}
	$result_subscribing_user->close();
}


function sendFCM(string $token, string $language, string $eventseries_name): void{
    //header data
    $headers = array('Authorization: key='.SERVER_KEY, 'Content-Type: application/json');

    //notification content
    if($language === "DE"){
        $title = "Stundenplanänderung";
        $message = $eventseries_name . " hat sich geändert. Bitte aktualisiere deinen Stundenplan!";
    } else{
        $title = "Timetable Change";
        $message = $eventseries_name . " changed. Please update your schedule!";
    }

    //prepare data
    $fields = array (
        'token' => $token,
        'notification' => array('title' => $title, 'body' => $message, 'sound' => 'default')
    );

    //initiate curl request
    $cRequest = curl_init();
    curl_setopt($cRequest, CURLOPT_URL, FCM_URL);
    curl_setopt($cRequest, CURLOPT_POST, true);
    curl_setopt($cRequest, CURLOPT_HTTPHEADER, $headers);
    curl_setopt($cRequest, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($cRequest, CURLOPT_POSTFIELDS, json_encode($fields));

    // execute curl request
    curl_exec($cRequest);
    //close curl request
    curl_close($cRequest);

}


// main

// -------------- Detect changed event sets and update database ----------------------------------

//fetch all available module ids from StundenplanServer
$url = API_BASE_URL . ENDPOINT_MODULE;
//note: second parameter must be true to enable key-value iteration
$jsonStringFromStundenplanServer = file_get_contents($url);
$module_ids = json_decode( $jsonStringFromStundenplanServer, true);

//fetch data of each module
$output .= "<p> Module Ids (" . count($module_ids) . "):</b><br>";

if ( count($module_ids) <= 0 ) {
	// no modules found

	//TODO
	// echo nothing found
	// exit?
}


$module_ids = array_slice($module_ids, 0, 5, true); //for debug: reduce array to size 5
foreach($module_ids as $key=>$module_id){
    $output .=  $module_id . ", ";
    fetchModuleAndUpdateDatabase($module_id);
}
$output .= "</p>";


error_log(print_r("Script fcm_update_and_send.php Ende,".$output, true));

echo $output;
