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

/* wird vom Cron-Job aus aufgerufen */

require_once 'fcm_connect_db.php';
require_once 'server.php';

$debug = 0;

error_log(print_r("Begin Script fcm_update_and_send.php", TRUE));

// -------------- Get data from database ----------------------------------
$queryEventSetIds = "SELECT evenset_id FROM fcm_user GROUP BY evenset_id";
$sqlResult = $con->query($queryEventSetIds);
// collect eventset IDs from database into array
$evenset_ids = array();
echo"\n\Eventset ids: \n\n";
if($sqlResult->num_rows > 0) {
	while($row = $sqlResult->fetch_assoc()) {
		echo "id: ";
		echo $row['evenset_id'];
		echo "\n";
		array_push($evenset_ids, $row['evenset_id']);
	}
}
$sqlResult->close();

// -------------- Get changed eventsets from database ----------------------------------
//todo: detect changes

$queryModuleIds = "SELECT UNIQUE module_id FROM modules";
$sqlResultModuleIds = $con->query($queryEventSetIds);
$sqlResultModuleIds->close();

//close sql connection
$con->close();



// ---------------------------------------------------------------------------
// ---------------- functions ------------------------------------------------
// ---------------------------------------------------------------------------
function sendNotification( & $evenset_id, & $con, & $label) {
	global $debug;
	
	/* label: Beschreibung der Änderung, mehrsprachig, t.b.d. */

	$sql3 = "SELECT token, os FROM fcm_nutzer WHERE evenset_id = '".$evenset_id."'";
	
	$sqlResult3 = $con->query($sql3);
	$tokenArray = array(array(), array());
	
	//Alle Tokens auslesen und in $tokens speichern
	if ($sqlResult3->num_rows > 0) {
		
	    //output data of each row
	    $count = 0;
	    echo("count: ");
	    echo(count($tokenArray));
	    while ($row = $sqlResult3->fetch_assoc()) {
		    echo "Token hinzufügen: $row[os] \n";
		    $tokenArray[$count][0] = $row["token"];
		    $tokenArray[$count][1] = $row["os"];  
	    	$count++;
	    }
		echo("for wird ausgeführt: $count\n");
		//Nachricht senden mit jedem Token aufrufen. Unterscheidung zwischen 0 = Android/GCM und 1 = iOS
		for($i=0; $i < $count; $i++) {
			
			if ($debug) error_log(print_r("++++ PUSH for an os type ++++ <br>", TRUE));
			echo("++++ PUSH as echo ++++<br>\n");                            
			
			// Android
			if ($tokenArray[$i][1] == 0){error_log(print_r("PUSH FCM: " . $evenset_id . " - ".$label." - for token: ".$tokenArray[$i][0], TRUE));
				try {
					// Token, Label
					sendGCM($tokenArray[$i][0], $label);
				} catch (Exception $e) {
					error_log(print_r("catch exception e: ".$e, TRUE));
				}
			}
			// IOS
			/*else if ($tokenArray[$i][1] == 1)
			{
				if ($debug) error_log(print_r("PUSH iOS: " . $evenset_id . " - ".$label." - for token: ".$tokenArray[$i][0], TRUE));     			
				try {
					// Titel, Body, Token
					sendIosPush("Neue Änderung für das Fach", $label, $tokenArray[$i][0]);
				} catch (Exception $e) {
					error_log(print_r("catch exception e: ".$e, TRUE));
				}
			}*/
			else
			{
				error_log(print_r("++++ PUSH wrong OS!! ++++<br>\n", TRUE));	     			
				exit;
			}
			echo("Token: " . $tokenArray[$i][0]."<br>");
		}
		echo "Notification an evenset_id $evenset_id wurde gesendet!<br>\n";
	} else{
	    echo "Es sind keine Tokens für die vorlesungs_id <b>$evenset_id</b> vorhanden!<br>\n";
	}
	$sqlResult3->close();
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
error_log(print_r("Script fcm_update_and_send.php Ende", TRUE));
?>