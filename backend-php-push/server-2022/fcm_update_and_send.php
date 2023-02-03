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
function updateDatabaseAndBookNotifications(string &$moduleId): void
{
    /** $db_timetable database connection */
    global $db_timetable;
    global $output;
    global $debug;

    $moduleURL = API_BASE_URL . ENDPOINT_MODULE_DETAIL . $moduleId;
    $moduleData = array();
    // retrieve data for the module from Stundenplan Server (!not the database on this server)
    $jsonString = file_get_contents($moduleURL);
	
	if ( $jsonString === false )
	{
		$output .= "(E500) updateDatabaseAndBookNotifications: failed reading from '$moduleURL'.".date("Y.m.d H:s");
		return;
	}
	
    // second parameter must be true to enable key-value iteration
    $moduleData = json_decode($jsonString, true);

    // validate answer, otherwise skip
    if (!array_key_exists("dataActivity", $moduleData)) {
        $moduleName = "";
        if (array_key_exists("dataModule", $moduleData)){
            $moduleName = $moduleData["dataModule"]["Name"];
        }
        if ($debug) $output .= sprintf("Module %s (%s) is empty (at least this semester).<br>", $moduleId, $moduleName);
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
        if (count($deletedEventSets) > 0) {
            $output .= sprintf("Deleted Event Set Ids: %s <br>", implode(", ", $deletedEventSets));
        }

        //delete those event sets in database
        foreach ($deletedEventSets as $deletedEventSetId) {
            $db_timetable->deleteEventSet($deletedEventSetId);
            $eventSeriesName = getEventSeriesName($moduleData["dataActivity"][$deletedEventSetId]["activityName"]);
            bookTimetableChangedNotifications($eventSeriesName);
        }

        //CHANGED and ADDED EVENT SERIES
        // per module, check each event set for changes
        foreach ($moduleData["dataActivity"] as $eventsetID => $eventsetData) {
            $fetchedEventSetJson = json_encode($eventsetData);
            // change escaped slashes ("\/") to "/"
//            $fetchedEventSetJson = stripslashes($fetchedEventSetJson);
            $eventSeriesName = getEventSeriesName($eventsetData["activityName"]);
            //get local event set
            //array of eventset_id, eventseries, module_id, eventset_data, last_changed
            $resultLocalEventSet = $db_timetable->getEventSet($eventsetID);

            //EVENT SET probably CHANGED
            if (!empty($resultLocalEventSet) && !empty($resultLocalEventSet[0])) {
                $output .= "<ul>";

                $localEventSetJson = $resultLocalEventSet[0]["eventset_data"];

                //compare local vs fetched event set checksum
                // in case they are equal, nothing gets changed
                if ($localEventSetJson === $fetchedEventSetJson) {
                    // nothing changed since last request
//                    if ($debug) $output .= sprintf("<li> Event set %s has not changed. </li>", $resultLocalEventSet[0]["eventset_id"]);
                } else {
                    $output .= sprintf("<li> Event set %s has changed since %s.</li>",
                        $resultLocalEventSet[0]["eventset_id"],
                        $resultLocalEventSet[0]["last_changed"]);
                    file_put_contents('D:/Downloads/json_compare/'.$eventsetID.'_local.json', $localEventSetJson);
                    file_put_contents('D:/Downloads/json_compare/'.$eventsetID.'_fetched.json', $fetchedEventSetJson);
                    //update database, the data of this event set has changed
                    $db_timetable->updateEventSet($eventsetID, $fetchedEventSetJson);
                    bookTimetableChangedNotifications($eventSeriesName);
                }
                $output .= "</ul>";
            } else {
                //EVENT SET ADDED
                //no local event set with this id found --> event set is new and has to be added
                if ($debug) $output .= sprintf("<li> Event set %s has been added. ", $eventsetID);
                $db_timetable->insertEventSet($eventsetID, $eventSeriesName, $moduleId, $fetchedEventSetJson);
                bookTimetableChangedNotifications($eventSeriesName);
                if($debug) $output .= "</li>";

                //if event series corresponds to a new exam (no matter which group),
                // then notify all users subscribing an event series of the same module
                //possible endings for exams: APL, PL, mdl. Prfg.,Wdh.-Prfg., Wdh.-APL
                if (preg_match("/.*(PL)|(Prfg\\.)/", $eventSeriesName)) {
                    if ($debug) $output .= sprintf("<li> Exam %s has been added for module %s. ", $eventsetID, $moduleData["dataModule"]["Name"]);
                    bookExamAddedNotifications($moduleId, $moduleData["dataModule"]["Name"]);
                    if($debug) $output .= "</li>";
                }
            }
        }
    } else {
        //new module -> needs to be added
        foreach ($moduleData["dataActivity"] as $eventsetID => $eventsetData) {
            $eventSeriesName = getEventSeriesName($eventsetData["activityName"]);
            $fetchedEventSetJson = json_encode($eventsetData);
            $db_timetable->insertEventSet($eventsetID, $eventSeriesName, $moduleId, $fetchedEventSetJson);
        }
    }

}

/**
 * Book notifications for the tokens that need to be notified about the change of the given event series
 *
 * @param string $eventSeriesName The name of the changed events series
 */
function bookTimetableChangedNotifications(string &$eventSeriesName): void
{
    global $debug;
    /** $db_timetable database connection */
    global $db_timetable;
    global $output;

    /** @var mysqli_result|null $resultSubscribingUser get tokens subscribing the given event series */
    // Array of token, language, os
    $resultSubscribingUser = $db_timetable->getSubscribingUsers($eventSeriesName);

    if ($resultSubscribingUser != null && $resultSubscribingUser->num_rows > 0) {
        if ($debug) $output .= sprintf("There are notifications that need to be sent for event series %s!", $eventSeriesName);

        bookNotifications($resultSubscribingUser, $eventSeriesName, TIMETABLE_CHANGED);
    } else {
        if ($debug) $output .= sprintf("There are no tokens subscribing %s!", $eventSeriesName);
    }
    $resultSubscribingUser->close();
}

/**
 * Book notifications for the tokens that need to be notified about the added exam
 *
 * @param string $moduleId The ID of the module the exam belongs to
 * @param string $moduleName The name of the module the exam belongs to
 */
function bookExamAddedNotifications(string &$moduleId, string &$moduleName): void
{
    global $debug;
    /** $db_timetable database connection */
    global $db_timetable;
    global $output;

    //get tokens subscribing any event series in the given module
	/** @var array $mysqli_result Array of token, language, os */
	$resultSubscribingUser = $db_timetable->getUserSubscribingAnythingInModule($moduleId);

    if ($resultSubscribingUser->num_rows > 0) {
        bookNotifications($resultSubscribingUser, $moduleName, EXAM_ADDED);

        if ($debug) $output .= sprintf("There are notifications that need to be sent for module %s!", $moduleId);
    } else {
        if ($debug) $output .= "There are no tokens subscribing $moduleId!";
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

    while ($subscribingUser = $subscribingUsers->fetch_assoc()) {
        $db_timetable->insertNotification(
            $subscribingUser["token"],
            $subject,
            $type,
            $subscribingUser["os"]);
    }
}

/**
 * Send Firebase Message
 *
 * @param string $tokens A string containing multiple tokens seperated by comma
 * @param string $subject The event series oder module name the notification is about
 * @param string $type "1" for timetable changed or "2" for exam added
 */
function sendFCM(string &$tokens, string &$subject, string &$type): void
{
    global $db_timetable;
    //header data
    $headers = array('Authorization: key=' . SERVER_KEY, 'Content-Type: application/json');

    //prepare data
    $tokenArray = explode(',', $tokens);
    if (empty($tokenArray)) {
        return;
    }
    $fields = array(
        'registration_ids' => $tokenArray,
        'data' => array(
            'type' => $type,
            'subject' => $subject
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
    $result = curl_exec($cRequest);
    //close curl request
    curl_close($cRequest);

	if ($result !== false) {
		// successfull sent
		$db_timetable->markNotificationsAsSent( $tokenArray, $subject, $type );
	}
}


// ---------------------------------------------------------------------------
// ---------------- main ------------------------------------------------
// ---------------------------------------------------------------------------

// -------------- Detect changed event sets and update database ----------------------------------


// Get data from the Stundenplan server (!not the database on this server)

/** fetch all available module ids from StundenplanServer */
$url = API_BASE_URL . ENDPOINT_MODULE;
$jsonStringFromStundenplanServer = file_get_contents($url);
//note: second parameter must be true to enable key-value array interpretation
$moduleIds = array_values(json_decode($jsonStringFromStundenplanServer, true));


//fetch data of each module
$output .= sprintf("<b> %s module IDs had been fetched.</b><br>", count($moduleIds));

//DEBUG
if ($debug) {
    $output .= sprintf("<b><i><span style='color:DodgerBlue;'>Fetched module ids: </span></i></b> %s", count($moduleIds));

    //TODO: @Stepping comment out for complete modules fetching (or set $debug to false)
    //DEBUG: for debugging: reduce array to smaller size
//    $moduleIds = array_slice($moduleIds, 20, 2, true);
    $moduleIds = array_slice($moduleIds, 20, 200, true);
    //add Personalmanagement for testing exam added
//    $moduleIds[] = "A615C4E5AF9DAB47C65F7B181CFD4C70";
//
//    $moduleIds[] = "09FF2B2A861392FAD18A0E92574B675B";
//    $moduleIds[] = "E8A862F1C4B17A679D3FB04C6B129735";
    $moduleIds[] = "1C3A91921F135DD40715BB9E399B3F6C";
    $output .= implode(", ", $moduleIds);
} // DEBUG


// Check each module for changes
foreach ($moduleIds as $moduleId) {
    if ($debug) $output .= sprintf("<p style='color:DodgerBlue;'>Module %s:<br><p style='margin-left:40px'> ", $moduleId);

    // collect all notifications that need to be sent concerning this moduleID
    updateDatabaseAndBookNotifications($moduleId);

    if ($debug) $output .= "</p></p>";
}


global $db_timetable;

//iterate over notifications and send them out
$notificationsToSend = $db_timetable->getNotificationsToSendToAndroid();
$output .= sprintf("<br><b> %s notifications to send out.</b><br>", count($notificationsToSend));

if ($debug) $output .= "<b><i><span style='color:DodgerBlue;'>Notifications to send: { </span></i></b> <ul>";
foreach ($notificationsToSend as $notificationData) {
    sendFCM($notificationData["tokens"], $notificationData["subject"], $notificationData["type"]);
    if ($debug) $output .= sprintf("<li>Type %s: %s</li>", $notificationData["type"], $notificationData["subject"]);
}
if ($debug) $output .= "</ul><b><i><span style='color:DodgerBlue;'>}</span></i></b>";


$output .= "<p><br><i><b> Ende Script fcm_update_and_send.php</i></b></p>>";
echo $output;
