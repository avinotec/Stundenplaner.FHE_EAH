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


/* wird vom Cron-Job aus aufgerufen */

declare(strict_types=1);

require_once 'define.php';
require_once 'config.php';
require_once 'TimetableDb.php';

require_once 'utils.php';


/** @var TimetableDb $db_timetable database connection */
global $db_timetable;

//Get or create database connection
initDbConnection();


/* we can request debug output to better find errors */
$debug = false;
$debug = true;	  // gegebenenfalls auskommentieren

if ($debug) error_log("Begin Script fcm_update_and_send.php") ;

// collect output and echo only once
$output = "";

// ---------------------------------------------------------------------------
// ---------------- functions ------------------------------------------------
// ---------------------------------------------------------------------------

function fetchModuleAndUpdateDatabase(string $module_id): void
{
	/** @var TimetableDb $db_timetable database connection */
	global $db_timetable;
    global $output;

	$module_url = API_BASE_URL . ENDPOINT_MODULE_DETAIL . $module_id;
    $module_data = array();
	// second parameter must be true to enable key-value iteration
	$module_data = json_decode(file_get_contents($module_url), true);

	if(!array_key_exists("dataActivity", $module_data)){ return; }

    //initialize to set data types
    $local_eventset_ids = array();
    $fetched_eventset_ids = array();
    $fetched_eventset_ids = array_keys($module_data["dataActivity"]);
    $local_eventset_ids = $db_timetable->getEventSetIds($module_id);
	if($local_eventset_ids != null && count($local_eventset_ids) > 0){
		//DELETED EVENT SETS
		//detect deleted event set ids
		$deleted_eventsets = array_diff($local_eventset_ids, $fetched_eventset_ids);
        $output .= "<p>Deleted Event Set Ids: ". implode(", ", $deleted_eventsets) . "</p>";

		//delete those event sets in database
		foreach($deleted_eventsets as $deletedEventSetId){
			$db_timetable->deleteEventSet($deletedEventSetId);
			$eventseries_name = getEventSeriesName($module_data["dataActivity"][$deletedEventSetId]["activityName"]);
			sendNotification($eventseries_name);
		}

		//CHANGED and ADDED EVENT SERIES
		// per module, check each event set for changes
		foreach ($module_data["dataActivity"] as $eventset_id=>$eventset_data){
			$fetched_eventset_json = json_encode($eventset_data);
            // change escaped slashes ("\/") to "/"
            $fetched_eventset_json = stripslashes($fetched_eventset_json);
            $eventseries_name = getEventSeriesName($eventset_data["activityName"]);
            //get local event set
            $result_local_eventset = $db_timetable->getEventSet($eventset_id);

            //EVENT SET probably CHANGED
            if (!is_null($result_local_eventset) && count($result_local_eventset) > 0){
                $output .= "<p><b>Check for changes:</b><br>";
                //compare local vs fetched event set checksum
                $json_local_eventset = $result_local_eventset[0]["eventset_data"];
				if ($json_local_eventset !== $fetched_eventset_json){
                    $output .= "Event set " . $result_local_eventset[0]["eventset_id"]
                        . " has changed since " . $result_local_eventset[0]["last_changed"] . "<br>";
					//update database
					$db_timetable->updateEventSet($eventset_id, $fetched_eventset_json);
					sendNotification($eventseries_name);
				}
                $output .= "</p>";
			}
			//EVENT SET ADDED
			//no local event set with this id found --> event set is new and has to be added
			else {
				$db_timetable->insertEventSet($eventset_id, $eventseries_name, $module_id, $fetched_eventset_json);
				sendNotification($eventseries_name);
				//todo: if eventseries is an exam, notify users with eventseries names belonging to the same module
			}
		}
	}
	//new module -> needs to be added
	else {
		foreach ($module_data["dataActivity"] as $eventset_id => $eventset_data) {
			$eventseries_name = getEventSeriesName($eventset_data["activityName"]);
			$fetched_eventset_json = json_encode($eventset_data);
			$db_timetable->insertEventSet($eventset_id, $eventseries_name, $module_id, $fetched_eventset_json);
        }
	}

}

function sendNotification(string $eventseries_name): void
{
	global $debug;
	/** @var TimetableDb $db_timetable database connection */
	global $db_timetable;
    global $output;

	//get tokens subscribing the given event series
	$result_subscribing_user = $db_timetable->getSubscribingUsers($eventseries_name);

	//Collect tokens into $token_array
	if ($debug) $output .= "<p><b>Tokens subscribing the series " . $eventseries_name . ": </b><br>";
	if ($result_subscribing_user->num_rows > 0) {
		while ($subscribing_user = $result_subscribing_user->fetch_assoc()) {
            $output .= $subscribing_user["token"] . "<br>";

			if($subscribing_user["os"] === ANDROID){
				//send android push
				sendFCM($subscribing_user["token"], $subscribing_user["language"],$eventseries_name);

			} elseif($subscribing_user["os"] == IOS){
				//send Ios Push
			} else {
				$output .= "<p>++++ PUSH wrong OS!! ++++</p>>";
				exit;
			}
		}

        $output .= "<br>Notifications for the event series $eventseries_name sent!</p>";

	} else {
        $output .= "There are no tokens subscribing $eventseries_name!</p>";
	}
	$result_subscribing_user->close();
}


function sendFCM(string $token, string $language, string $eventseries_name): void{
	//header data
	$headers = array('Authorization: key='.SERVER_KEY, 'Content-Type: application/json');

	//notification content
	if($language === LANG_DE){
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


// ---------------------------------------------------------------------------
// ---------------- main ------------------------------------------------
// ---------------------------------------------------------------------------

// -------------- Detect changed event sets and update database ----------------------------------

//fetch all available module ids from StundenplanServer
$url = API_BASE_URL . ENDPOINT_MODULE;
$jsonStringFromStundenplanServer = file_get_contents($url);
//note: second parameter must be true to enable key-value iteration
$module_ids = json_decode($jsonStringFromStundenplanServer, true);

$output .= "<p><b> Fetched module ids</b> (Anzahl: " . count($module_ids) . "):<br>";

//fetch data of each module
if ($debug) {
    //for debugging: reduce array to size 5
    $module_ids = array_slice($module_ids, 0, 5, true);
}
foreach($module_ids as $key=>$module_id){
	$output .=  $module_id . ", ";
	fetchModuleAndUpdateDatabase($module_id);
}
$output .= "</p>";


$output .= "<p><i> Ende Script fcm_update_and_send.php</i></b></p>>";
echo $output;
