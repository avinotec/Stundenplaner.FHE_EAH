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
 * Fetch module, update database and book notifications for the user tokens that need to be notified
 *
 * @param string $moduleId
 */
function updateDatabaseAndBookNotifications(string & $moduleId): void
{
	/** $db_timetable database connection */
	global $db_timetable;
    global $output;
    global $debug;

	$moduleURL = API_BASE_URL . ENDPOINT_MODULE_DETAIL . $moduleId;
    $moduleData = array();
	// retrieve data for the module from Stundenplan Server (!not the database on this server)
	$jsonString = file_get_contents($moduleURL);
	// second parameter must be true to enable key-value iteration
	$moduleData = json_decode($jsonString, true);

    // validate answer, otherwise skip
	if (!array_key_exists("dataActivity", $moduleData)) {
        if($debug) $output .= sprintf("<br><i>Module %s is empty (this semester).</i><br>", $moduleId);
        if($debug) $output .= sprintf("<br><i>Module %s is empty (this semester).</i><br>", $moduleId);
        return;
    }

    //initialize to set data types
    $localEventSetIDs = array();
    $fetchedEventSetIDs = array();
    $fetchedEventSetIDs = array_keys($moduleData["dataActivity"]);

    $localEventSetIDs = $db_timetable->getEventSetIds($moduleId);

	if ($localEventSetIDs != null && count($localEventSetIDs) > 0) {
		//DELETED EVENT SETS
		//detect deleted event set ids, by detecting the event sets that are no longer provided by Stundenplan Server
		$deletedEventSets = array_diff($localEventSetIDs, $fetchedEventSetIDs);
		$deletedEventSetsString = implode(", ", $deletedEventSets);
        $output .= sprintf("<p>Deleted Event Set Ids: %s</p>", $deletedEventSetsString);

		//delete those event sets in database
		foreach ($deletedEventSets as $deletedEventSetId) {
			$db_timetable->deleteEventSet($deletedEventSetId);
			$eventseriesName = getEventSeriesName($moduleData["dataActivity"][$deletedEventSetId]["activityName"]);
            bookTimetableChangedNotifications($eventseriesName);
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
            if (!empty($resultLocalEventset) && !empty($resultLocalEventset[0])) {
                $output .= "<p><b>Check for changes:</b><br>";

                $jsonLocalEventset = $resultLocalEventset[0]["eventset_data"];

	            //compare local vs fetched event set checksum
	            // in case they are equal, nothing gets changed
				if ($jsonLocalEventset === $fetchedEventsetJSON) {
					// nothing changed since last request
					$output .= sprintf("Event set %s not changed<br>", $resultLocalEventset[0]["eventset_id"]);
				} else {
					$output .= sprintf("Event set %s has changed since %s<br>",
						$resultLocalEventset[0]["eventset_id"],
						$resultLocalEventset[0]["last_changed"]);
					//update database, the data of this event set has changed
					$db_timetable->updateEventSet($eventsetID, $fetchedEventsetJSON);
					bookTimetableChangedNotifications($eventseriesName);
				}
				$output .= "</p>";
			} else {
				//EVENT SET ADDED
				//no local event set with this id found --> event set is new and has to be added
				$db_timetable->insertEventSet($eventsetID, $eventseriesName, $moduleId, $fetchedEventsetJSON);
                bookTimetableChangedNotifications($eventseriesName);

                //if event series corresponds to a new exam (no matter which group),
                // then notify all users subscribing an event series of the same module
                //possible endings for exams: APL, PL, mdl. Prfg.,Wdh.-Prfg., Wdh.-APL
                if (preg_match("/.*(PL)|(Prfg\\.)/", $eventseriesName)) {
                    bookExamAddedNotifications($moduleId, $moduleData["dataModule"]["Name"]);
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

}

/**
 * Book notifications for the tokens that need to be notified about the change of the given event series
 *
 * @param string $eventseriesName The name of the changed events series
 */
function bookTimetableChangedNotifications(string & $eventseriesName): void
{
	global $debug;
	/** $db_timetable database connection */
	global $db_timetable;
    global $output;

	/** @var mysqli_result|null $resultSubscribingUser get tokens subscribing the given event series */
    // Array of token, language, os
	$resultSubscribingUser = $db_timetable->getSubscribingUsers($eventseriesName);

	if ($debug) { $output .= sprintf("<p><b>Tokens subscribing the series %s: </b><br>", $eventseriesName); }

	if ($resultSubscribingUser != null &&$resultSubscribingUser->num_rows > 0) {
        bookNotifications($resultSubscribingUser, $eventseriesName, TIMETABLE_CHANGED);

        if($debug) $output .= sprintf("<br>Notifications need to be sent for event series %s!</p>", $eventseriesName);
	} else {
        if($debug) $output .= sprintf("There are no tokens subscribing %s!</p>", $eventseriesName);
	}
	$resultSubscribingUser->close();
}

/**
 * Book notifications for the tokens that need to be notified about the added exam
 *
 * @param string $moduleId The ID of the module the exam belongs to
 * @param string $moduleName The name of the module the exam belongs to
 */
function bookExamAddedNotifications(string & $moduleId, string & $moduleName): void
{
    global $debug;
    /** $db_timetable database connection */
    global $db_timetable;
    global $output;

    //get tokens subscribing any event series in the given module
	// Array of token, language, os
    $resultSubscribingUser = $db_timetable->getUserSubscribingAnythingInModule($moduleId);

    if ($resultSubscribingUser->num_rows > 0) {
        bookNotifications($resultSubscribingUser, $moduleName, EXAM_ADDED);

        if($debug) $output .= "<br>Notifications need to be sent for module $moduleId!</p>";
    } else {
        if($debug) $output .= "There are no tokens subscribing $moduleId!</p>";
    }
    $resultSubscribingUser->close();
}

/**
 * Book notifications for the $subscribingUsers concerning the $subject
 * @param mysqli_result|null $subscribingUsers Array of token, language, os
 * @param string $subject eventSeriesName or moduleName
 * @param string $type TIMETABLE_CHANGED or EXAM_ADDED
 */
function bookNotifications(?mysqli_result $subscribingUsers, string $subject, string $type): void
{
    /** $db_timetable database connection */
    global $db_timetable;
    global $output;

    while ($subscribingUser = $subscribingUsers->fetch_assoc()) {
        $output .= $subscribingUser["token"] . "<br>";

        if ($subscribingUser["os"] === ANDROID) {
            $db_timetable->insertNotification(
                $subscribingUser["token"],
                $subject,
                $type);

        } elseif ($subscribingUser["os"] == IOS) {
            ;//send Ios Push
        } else {
            $output .= "<p>++++ PUSH wrong OS!! ++++</p>>";
            exit;
        }
    }
}

/**
 * Send Firebase Message
 *
 * @param string $tokens A string containing multiple tokens seperated by comma
 * @param string $subject The event series oder module name the notification is about
 * @param string $title "timetable change" or "exam added"
 * @return void
 */
function sendFCM(string & $tokens, string & $subject, string & $title): void
{
	//header data
	$headers = array('Authorization: key='.SERVER_KEY, 'Content-Type: application/json');

    //prepare data
    $tokenArray = explode(',', $tokens);
    if(empty($tokenArray)){
        return;
    }
    echo "<p> tokenArray";
    print_r($tokenArray);
    echo "</p>";
    $fields = array (
		'registration_ids' => $tokenArray,
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


// Get data from the Stundenplan server (!not the database on this server)

/** @var string $url fetch all available module ids from StundenplanServer */
$url = API_BASE_URL . ENDPOINT_MODULE;
$jsonStringFromStundenplanServer = file_get_contents($url);
//note: second parameter must be true to enable key-value iteration
$moduleIds = json_decode($jsonStringFromStundenplanServer, true);

$output .= sprintf("<p><b> Fetched module ids</b> (Anzahl: %s):<br>", count($moduleIds));

//fetch data of each module
if ($debug) {
	//for debugging: reduce array to size 5
	$moduleIds = array_slice($moduleIds, 3, 7, true);
	print_r($moduleIds);
}

// Check each module for changes
foreach ($moduleIds as $moduleKey => $moduleId) {
	$output .=  $moduleId . ", ";
	// collect all notifications that need to be sent concerning this moduleID
	updateDatabaseAndBookNotifications($moduleId);
}


global $db_timetable;

//iterate over notifications and send them out
$notificationsToSend = $db_timetable->getNotificationsToSent();

foreach ($notificationsToSend as $notificationData) {
    sendFCM($notificationData["tokens"], $notificationData["subject"], $notificationData["tag"]);
    $output .= "<br>Notifications sent for event series or module ".$notificationData["tag"]."!";
}
//TODO: set up php script for cleaning notifications from time to time

$output .= "</p>";


$output .= "<p><i> Ende Script fcm_update_and_send.php</i></b></p>>";
echo $output;
