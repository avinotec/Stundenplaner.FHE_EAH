<?php
/*
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
 */

declare(strict_types=1);

const API_BASE_URL = "https://stundenplanung.eah-jena.de/api/mobileapp/";
const ENDPOINT_MODULE = "v1/module";
const ENDPOINT_MODULE_DETAIL = "v1/module/"; //+{id}

/* wird vom Cron-Job aus aufgerufen */

require_once 'fcm_connect_db.php';
//require_once 'server.php';
require_once 'utils.php';

$debug = 0;
error_log(print_r("Begin Script fcm_update_and_send.php", true));


// -------------- Detect changed event sets and update database ----------------------------------

//fetch al available module ids
$url = API_BASE_URL . ENDPOINT_MODULE;
// second parameter must be true to enable key-value iteration
$module_ids = json_decode(file_get_contents($url), true);

//fetch data of each module
echo "<p> Module Ids (" . count($module_ids) . "):</b><br>";
$module_ids = array_slice($module_ids, 0, 5, true); //for debug: reduce array to size 5
foreach($module_ids as $key=>$module_id){
    echo $module_id . ", ";
    fetchModuleAndUpdateDatabase($module_id, $con);
}
echo "</p>";

// -------------- Get subscribed event set IDS from database ----------------------------------
$resultEventSetIds = $con->query("SELECT DISTINCT eventset_id FROM fcm_user");
// collect eventset IDs from database into array
$evenset_ids = array();
echo "<p>EventSet ids:</b><br>";
if($resultEventSetIds != null) {
	while($row = $resultEventSetIds->fetch_assoc()) {
		echo $row['eventset_id'] . "<br>";
		array_push($evenset_ids, $row['eventset_id']);
	}
	$resultEventSetIds->close();

} else {
	echo "No Event Set Ids found in fcm_user";
}
echo "</p>";


// ---------------------------------------------------------------------------
// ---------------- functions ------------------------------------------------
// ---------------------------------------------------------------------------

function fetchModuleAndUpdateDatabase(&$module_id, &$con){
	$module_url = API_BASE_URL . ENDPOINT_MODULE_DETAIL . $module_id;
	// second parameter must be true to enable key-value iteration
	$module_data = json_decode(file_get_contents($module_url), true);

    if(!array_key_exists("dataActivity", $module_data)){ return; }

	$fetchedEventSetIds = array_keys($module_data["dataActivity"]);
	$resultLocalEventSetIds = $con->query("SELECT eventset_id FROM timetable_data WHERE module_id = `$module_id`");

	if($resultLocalEventSetIds != null){
	    //DELTED EVENT SETS
	    $deletedEventSetIds = array_diff($resultLocalEventSetIds, $fetchedEventSetIds);
    	echo "Deleted Event Sets: " . $deletedEventSetIds;
    	foreach($deletedEventSetIds as $deletedEventSetId){
    	    $con->query("DELETE FROM timetable_data WHERE eventset_id = `$deletedEventSetId`");
    	    //todo: delete subscribed event set in fcm_user and notify user
    	}

        //CHANGED and ADDED EVENT SETS
    	// check each event set of the module for changes
    	foreach ($module_data["dataActivity"] as $eventset_id=>$eventset){
    		$fetched_checksum = hash("md5", json_encode($eventset));
    		//get local event set
    		$queryEventSet = "SELECT * FROM timetable_data WHERE eventset_id = `$eventset_id`";
    		$resultEventSet = $con->query($queryEventSet);

    		if ($resultEventSet != null && $resultEventSet -> num_rows > 0){
    		    //compare local vs fetched event set checksum
    			if ($resultEventSet[0]["md5_checksum"] !== $fetched_checksum){
    				echo "Eventset " . $resultEventSet[0]["eventset_id"] . " has changed since " . $resultEventSet[0]["last_changed"];
    				// update database
    				$con->query("UPDATE timetable_data SET md5_checksum = `$fetched_checksum`
    							WHERE eventset_id = `$eventset_id`");
    			}
    		}
    		//no local event set with this eventsetID found --> event set is new and has to be added
    		else {
    		    $eventseries_name = getEventSeriesName($eventset["activityName"]);
    			$con->query("INSERT INTO timetable_data(`eventset_id`, `eventseries_name`, `module_id`, `md5_checksum`, `last_changed`)
    			    		VALUES ('$eventset_id', '$eventseries_name', '$module_id', '$fetched_checksum', SYSDATE())");
    			//TODO: how to detect and notify users when an events set has been added to an subscribed event series
    		}

    		$resultEventSet->close();
	    }
	}
	//new module -> needs to be added
	else{
	    foreach ($module_data["dataActivity"] as $eventset_id=>$eventset){
	        $eventseries_name = getEventSeriesName($eventset["activityName"]);
	        $fetched_checksum = hash("md5", json_encode($eventset));
	        //note: use ' for the values not ` to avoid "unknown column in field list" error in MySQL
            $con->query("INSERT INTO timetable_data(`eventset_id`, `eventseries_name`, `module_id`, `md5_checksum`, `last_changed`)
                		VALUES ('$eventset_id', '$eventseries_name', '$module_id', '$fetched_checksum', SYSDATE())");
	    }
	}

}

function sendNotification( & $evenset_id, & $con, & $label) {
	global $debug;

	/* label: Beschreibung der Änderung, mehrsprachig, t.b.d. */

	$sql3 = "SELECT token, os FROM fcm_nutzer WHERE evenset_id = '".$evenset_id."'";

	$result3 = $con->query($sql3);
	$tokenArray = array(array(), array());

	//Alle Tokens auslesen und in $tokens speichern
	if ($result3->num_rows > 0) {

	    //output data of each row
	    $count = 0;
	    echo("count: ");
	    echo(count($tokenArray));
	    while ($row = $result3->fetch_assoc()) {
		    echo "Token hinzufügen: $row[os] \n";
		    $tokenArray[$count][0] = $row["token"];
		    $tokenArray[$count][1] = $row["os"];
	    	$count++;
	    }
		echo("for wird ausgeführt: $count\n");
		//Nachricht senden mit jedem Token aufrufen. Unterscheidung zwischen 0 = Android/GCM und 1 = iOS
		for($i=0; $i < $count; $i++) {

			if ($debug) error_log(print_r("++++ PUSH for an os type ++++ <br>", true));
			echo("++++ PUSH as echo ++++<br>\n");

			// Android
			if ($tokenArray[$i][1] == 0){error_log(print_r("PUSH FCM: " . $evenset_id . " - ".$label." - for token: ".$tokenArray[$i][0], true));
				try {
					// Token, Label
					sendGCM($tokenArray[$i][0], $label);
				} catch (Exception $e) {
					error_log(print_r("catch exception e: ".$e, true));
				}
			}
			// IOS
			/*else if ($tokenArray[$i][1] == 1)
			{
				if ($debug) error_log(print_r("PUSH iOS: " . $evenset_id . " - ".$label." - for token: ".$tokenArray[$i][0], true));
				try {
					// Titel, Body, Token
					sendIosPush("Neue Änderung für das Fach", $label, $tokenArray[$i][0]);
				} catch (Exception $e) {
					error_log(print_r("catch exception e: ".$e, true));
				}
			}*/
			else
			{
				error_log(print_r("++++ PUSH wrong OS!! ++++<br>\n", true));
				exit;
			}
			echo("Token: " . $tokenArray[$i][0]."<br>");
		}
		echo "Notification an evenset_id $evenset_id wurde gesendet!<br>\n";
	} else{
	    echo "Es sind keine Tokens für die vorlesungs_id <b>$evenset_id</b> vorhanden!<br>\n";
	}
	$result3->close();
}

//SendGoogleCloudMessage
function sendGCM( & $registration_ids, & $label) {
    //titel und message der Notification
    $title =    "Neue Änderung";
    $message =  "Fach ".$label;

    //FCM URL
    $url = "https://fcm.googleapis.com/fcm/send";
    //$server_key = "AIzaSyAqg-02MqKHK4P9kWTFaeX18AZuKz3-oH8";
    $server_key = "AIzaSyCLUsCj9AxHYigJS_Gfu_ccC_9Y5Ii38zw";

    //prepare data
    $fields = array (
        'registration_ids' => array($registration_ids),
		'data' => array('notification_type' => 'change'),
        'notification' => array('title' => $title, 'body' => $message, 'sound' => 'default')
    );

    $fields = json_encode ( $fields );

    //header data
    $headers = array ('Authorization: key='.$server_key, 'Content-Type: application/json');

    //initiate curl request
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_POST, true);
    curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_POSTFIELDS, $fields);

    // execute curl request
    $result = curl_exec($ch);

    //close curl request
    curl_close($ch);

    echo "Token wurde an Google Firebase übermittelt: ";
    //return output
    return $result;
}
error_log(print_r("Script fcm_update_and_send.php Ende", true));
