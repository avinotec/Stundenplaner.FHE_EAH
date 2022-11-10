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


/* we can request debug output to better find errors */
$debug = false;
$debug = true;	  // gegebenenfalls auskommentieren

if ($debug) { error_log("Begin Script fcm_update_and_send.php"); }

// collect output and echo only once
$output = "";

// ---------------------------------------------------------------------------
// ---------------- functions ------------------------------------------------
// ---------------------------------------------------------------------------
/**
 * Fetch module, update database and collect user tokens to send notifications to
 *
 * @param string $moduleId
 * @return array An array containing an entry ["token" => <token>, "eventseries" => <name>]
 *              for every notification that needs to be sent
 */
function updateDatabaseAndGetNotifications(string & $moduleId): array
{
	/** $db_timetable database connection */
	global $db_timetable;
    global $output;

    //collect notifications here
    $uncalledNotifications = array();

	$moduleURL = API_BASE_URL . ENDPOINT_MODULE_DETAIL . $moduleId;
	/** @var array $moduleData */
	$moduleData = array();
	// retrieve data for the module from Stundenplan Server
	$jsonString = file_get_contents($moduleURL);
	// second parameter must be true to enable key-value iteration
	$moduleData = json_decode($jsonString, true);

    // validate answer, otherwise skip
	if (!array_key_exists("dataActivity", $moduleData)) {
        $output .= sprintf("<br><i>Module %s is empty (this semester).</i><br>", $moduleId);
        return $uncalledNotifications;
    }

    //initialize to set data types
	/** @var array $localEventsetIDs */
	$localEventsetIDs = array();
	/** @var array $fetchedEventsetIDs */
	$fetchedEventsetIDs = array();
    $fetchedEventsetIDs = array_keys($moduleData["dataActivity"]);

    $localEventsetIDs = $db_timetable->getEventSetIds($moduleId);

	if ($localEventsetIDs != null && count($localEventsetIDs) > 0) {
		//DELETED EVENT SETS
		//detect deleted event set ids, no longer provided by Stundenplan Server
		$deletedEventSets = array_diff($localEventsetIDs, $fetchedEventsetIDs);
		$deletedEventSetsString = implode(", ", $deletedEventSets);
        $output .= sprintf("<p>Deleted Event Set Ids: %s</p>", $deletedEventSetsString);

		//delete those event sets in database
		foreach ($deletedEventSets as $deletedEventSetId) {
			$db_timetable->deleteEventSet($deletedEventSetId);
			$eventseriesName = getEventSeriesName($moduleData["dataActivity"][$deletedEventSetId]["activityName"]);
            $uncalledNotifications = array_merge($uncalledNotifications, getTimetableChangeNotifications($eventseriesName));
		}

		//CHANGED and ADDED EVENT SERIES
		// per module, check each event set for changes
		foreach ($moduleData["dataActivity"] as $eventsetID => $eventsetData) {
			$fetchedEventsetJSON = json_encode($eventsetData);
            // change escaped slashes ("\/") to "/"
            $fetchedEventsetJSON = stripslashes($fetchedEventsetJSON);
			/** @var String $eventseriesName */
			$eventseriesName = getEventSeriesName($eventsetData["activityName"]);
            //get local event set
            $resultLocalEventset = $db_timetable->getEventSet($eventsetID);

            //EVENT SET probably CHANGED
            if (!is_null($resultLocalEventset) && !empty($resultLocalEventset) && !empty($resultLocalEventset[0])) {
                $output .= "<p><b>Check for changes:</b><br>";

                $jsonLocalEventset = $resultLocalEventset[0]["eventset_data"];

	            //compare local vs fetched event set checksum
	            // in case they are equal, nothing changed
				if ($jsonLocalEventset === $fetchedEventsetJSON) {
					// nothing changed since last request
					$output .= sprintf("Event set %s not changed<br>", $resultLocalEventset[0]["eventset_id"]);
				} else {
					$output .= sprintf("Event set %s has changed since %s<br>",
						$resultLocalEventset[0]["eventset_id"],
						$resultLocalEventset[0]["last_changed"]);
					//update database, this eventsetID changed
					$db_timetable->updateEventSet($eventsetID, $fetchedEventsetJSON);
					$uncalledNotifications = array_merge($uncalledNotifications, getTimetableChangeNotifications($eventseriesName));
				}
				$output .= "</p>";
			} else {
				//EVENT SET ADDED
				//no local event set with this id found --> event set is new and has to be added
				$db_timetable->insertEventSet($eventsetID, $eventseriesName, $moduleId, $fetchedEventsetJSON);
                $uncalledNotifications = array_merge($uncalledNotifications, getTimetableChangeNotifications($eventseriesName));

                //if event series corresponds to a new exam (no matter which group),
                // then notify all users subscribing an event series of the same module
                //possible endings for exams: APL, PL, mdl. Prfg.,Wdh.-Prfg., Wdh.-APL
                if (preg_match("/.*(PL)|(Prfg\\.)/", $eventseriesName)) {
                    //$uncalledNotifications = array_merge($uncalledNotifications, getExamAddedNotifications($moduleId, $moduleData["dataModule"]["Name"]));
	                $uncalledNotifications = array_merge($uncalledNotifications, getExamAddedNotifications($moduleId));
                }
			}
		}
	} else {
		//new module -> needs to be added
		foreach ($moduleData["dataActivity"] as $eventsetID => $eventsetData) {
			$eventseriesName = getEventSeriesName($eventsetData["activityName"]);
			$fetchedEventsetJSON = json_encode($eventsetData);
			$db_timetable->insertEventSet($eventsetID, $eventseriesName, $moduleId, $fetchedEventsetJSON);
        }
	}

    return $uncalledNotifications;

}

/**
 * Get array containing the tokens that need to be notified about the change of the given event series
 *
 * @param string $eventseriesName The name of the changed events series
 * @return array An array containing an entry ["tag" => <Exam added | Timetable change>, "token" => <token>, "eventseries" => <name>]
 *              for every notification that needs to be sent
 */
function getTimetableChangeNotifications(string & $eventseriesName): array
{
	global $debug;
	/** $db_timetable database connection */
	global $db_timetable;
    global $output;

    //collect notification data
    // entries have structure ["tag" => <Exam added | Timetable change>, "token" => <token>, "eventseries" => <name>]
    $uncalledNotificationsAndroid = array();

	/** @var mysqli_result|null $resultSubscribingUser get tokens subscribing the given event series */
	$resultSubscribingUser = $db_timetable->getSubscribingUsers($eventseriesName);

	if ($debug) { $output .= sprintf("<p><b>Tokens subscribing the series %s: </b><br>", $eventseriesName); }

	if ($resultSubscribingUser != null &&$resultSubscribingUser->num_rows > 0) {

		while ($subscribingUser = $resultSubscribingUser->fetch_assoc()) {
			$output .= $subscribingUser["token"] . "<br>";

			if ($subscribingUser["os"] === ANDROID) {
                $uncalledNotificationsAndroid[] = array(
                    "tag" => "Timetable change",
                    "token" => $subscribingUser["token"],
                    "eventseries" => $eventseriesName);

			} elseif ($subscribingUser["os"] == IOS) {
				;//send Ios Push
			} else {
				$output .= "<p>++++ PUSH wrong OS!! ++++</p>>";
				exit;
			}
		}

        $output .= sprintf("<br>Notifications need to be sent for event series %s!</p>", $eventseriesName);

	} else {
        $output .= sprintf("There are no tokens subscribing %s!</p>", $eventseriesName);
	}
	$resultSubscribingUser->close();

    return $uncalledNotificationsAndroid;
}

/**
 * Get array containing the tokens that need to be notified about the added exam
 *
 * @param string $moduleId The ID of the module the exam belongs to
 * @return array An array containing an entry ["tag" => <Exam added | Timetable change>, "token" => <token>, "eventseries" => <name>]
 *              for every notification that needs to be sent
 */
function getExamAddedNotifications(string & $moduleId): array
{
    global $debug;
    /** $db_timetable database connection */
    global $db_timetable;
    global $output;

    //collect notification data
    // entries have structure ["tag" => <Exam added | Timetable change>, "token" => <token>, "eventseries" => <name>]
    $uncalledNotificationsAndroid = array();

    //get tokens subscribing any event series in the given module
	// Array von token, language, os
    $resultSubscribingUser = $db_timetable->getUserSubscribingAnythingInModule($moduleId);

    if ($resultSubscribingUser->num_rows > 0) {

        while ($subscribingUser = $resultSubscribingUser->fetch_assoc()) {
            $output .= $subscribingUser["token"] . "<br>";

            if ($subscribingUser["os"] === ANDROID) {
                $uncalledNotificationsAndroid[] = array(
                    "tag" => "Exam added",
                    "token" => $subscribingUser["token"],
                    "eventseries" => $moduleId);

            } elseif ($subscribingUser["os"] == IOS) {
                ;//send Ios Push
            } else {
                $output .= "<p>++++ PUSH wrong OS!! ++++</p>>";
                exit;
            }
        }

        $output .= "<br>Notifications need to be sent for module $moduleId!</p>";

    } else {
        $output .= "There are no tokens subscribing $moduleId!</p>";
    }
    $resultSubscribingUser->close();

    return $uncalledNotificationsAndroid;
}

/**
 * Send Firebase Message
 *
 * @param string $token
 * @param string $subject The event series oder module name the notification is about
 * @param string $title "timetable change" or "exam added"
 * @return void
 */
function sendFCM(string & $token, string & $subject, string & $title): void
{
	//header data
	$headers = array('Authorization: key='.SERVER_KEY, 'Content-Type: application/json');

    //prepare data
	$fields = array (
		'to' => $token,
		'notification' => array(
            'title' => $title,
            'body' => $subject,
            'sound' => 'default'
        )
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


// a) get the Stundenplan from the server

/** @var string $url fetch all available module ids from StundenplanServer */
$url = API_BASE_URL . ENDPOINT_MODULE;
$jsonStringFromStundenplanServer = file_get_contents($url);
//note: second parameter must be true to enable key-value iteration
$moduleIDs = json_decode($jsonStringFromStundenplanServer, true);

$output .= sprintf("<p><b> Fetched module ids</b> (Anzahl: %s):<br>", count($moduleIDs));

//fetch data of each module
if ($debug) {
	//for debugging: reduce array to size 5
	$moduleIDs = array_slice($moduleIDs, 3, 7, true);
	print_r($moduleIDs);
}

// alle Module durchgehen
foreach ($moduleIDs as $key => $moduleID) {
	$output .=  $moduleID . ", ";
	// vermerke alle Benachrichtigungen zu einem moduleID
	$notificationsToSend = array_unique(updateDatabaseAndGetNotifications($moduleID));

    if ($debug) print_r($notificationsToSend);
	// gehe alle Benachrichtigungen durch und sende eine Benachrichtigung
    foreach ($notificationsToSend as $key => $notificationsData) {

		//TODO müssen wir je geänderter ID wirklich einen eigenen Aufruf machen?
	    //TODO können wir die Aufrufe nicht bündeln? Nicht kriegsentscheidend.
        sendFCM($notificationsData["token"], $notificationsData["eventseries"], $notificationsData["tag"]);
        $output .= "<br>Notifications sent for event series or module ".$notificationsData["tag"]."!";
    }
}

//TODO
// besser ist es, wenn wir das in 2 Schleifen machen
// Erst feststellen, dass es eine Benachrichtigung gibt.
// und die Benachrichtigung in einer zweiten Schleife machen, nämlich nach erfolgreichem Versenden vermerken, dass
// benachrichtigt wurde. Insofern brauchen wir noch ein Flag, dass nicht benachrichtigte noch zu benachrichtigen sind.
// man kann dann anschließend gerne noch aufräumen, alles was benachrichtigt wurde dann löschen.
// Das nennt sich Wiederanlaufsteuerung, wenn mittendrin in der Konstruktion was zusammenbricht, weiß man nicht,
// ob alle Benachrichtigungen versendet worden sind.


$output .= "</p>";


$output .= "<p><i> Ende Script fcm_update_and_send.php</i></b></p>>";
echo $output;
