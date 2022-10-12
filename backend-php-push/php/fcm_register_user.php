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
require_once 'config.php';
require_once 'TimetableDb.php';

/** $db_timetable database connection */
global $db_timetable;

/* we can request debug output to better find errors */
$debug = false;

// collect output and echo only once
$output = "";

if (isset($_REQUEST['debug']))
	$debug = htmlentities($_REQUEST['debug']);

if ($debug) {
	mysqli_report(MYSQLI_REPORT_ALL);

	ini_set('mysql.trace_mode', 'On');
	ini_set('mysqli.trace_mode', 'On');

	//ini_set('error_reporting', E_ALL | E_STRICT | E_DEPRECATED | E_NOTICE | E_PARSE );
	error_reporting(E_ALL);

	ini_set('display_errors', 'On');
	ini_set('display_startup_errors', 'On');

	//ini_set('allow_call_time_pass_reference', 'On' );
	ini_set('html_errors', 'On');

	ini_set('mysql.log_queries_not_using_indexes', 'on');
	ini_set('mysql.log_slow_admin_statements', 'on');
	ini_set('mysql.slow_query_log', 'on');
	ini_set('mysql.log_error_verbosity', '3');

	ini_set('mysqli.log_queries_not_using_indexes', 'on');
	ini_set('mysqli.log_slow_admin_statements', 'on');
	ini_set('mysqli.slow_query_log', 'on');
	ini_set('mysqli.log_error_verbosity', '3');

	// E_NOTICE ist sinnvoll um uninitialisierte oder
	// falsch geschriebene Variablen zu entdecken
	error_reporting(E_ERROR | E_WARNING | E_PARSE | E_NOTICE | E_STRICT);
}


//Get or create database connection
try {
	initDbConnection();
} catch (Exception $e) {
	$output .= "Database is not available: " .  $e->getMessage() . "<br>";
    echo $output;
	exit();
}
if ($debug) {
    $output .= "<p> DEBUG: " . json_encode($_REQUEST) . "</p>";
}


// ----------------- Get data from app ----------------------------------------------

// initialize to set data types
$os = ANDROID;
$language = LANG_DE;

//get os
if (isset($_REQUEST['os']))
	$os = htmlentities($_REQUEST['os']);

//get language
if (isset($_REQUEST['language']))
	$language = htmlentities($_REQUEST['language']);
if ($language !== LANG_DE && $language !== LANG_EN) {
	error_log("Language: " . $language);
    echo $output;
	exit;
}

// initialize to set data types
$fcmToken = "";
$arraySubscribedEventseries = array();

if ($os === ANDROID) {
	//Get token and subscriptions send from app
	// Alle übergebenen Parameter entwerten, um SQL-Injections zu unterbinden.
    if(isset($_REQUEST["fcmToken"])) {
        $fcmToken = htmlentities($_REQUEST["fcmToken"]);
    } else {
        echo $output;
        exit;
    }
	if(isset($_REQUEST["eventseries_names"])) {
        $arraySubscribedEventseries =  $_REQUEST["eventseries_names"] ?? null;
    }
} elseif ($os === IOS) {
	//
} else {
	$output .= "Keine OS Zuordnung!";
	error_log("Keine OS Zuordnung! OS-Type: " . $os);
    echo $output;
	exit;
}


// Check if a null value is given, to prevent null entries in the database.
// Null values can happen if a user opens the script in a browser window.
if (is_null($arraySubscribedEventseries) || is_null($fcmToken)) {
	$output .= "<br> arraySubscribedEventseries or fcm token is null! <br>";
	error_log("arraySubscribedEventSeries or fcm token is null!");
	echo $output;
    return;
}


// ----------------- DB entry for user to register ----------------------------------------------

//Clear database from potential previous registrations with the given token
$db_timetable->deleteUser($fcmToken);
//Add token and subscribed event series names to database to register user
foreach ($arraySubscribedEventseries as $subscribed_eventseries) {
	$subscribed_eventseries = htmlentities($subscribed_eventseries);

	if ($debug) {
        $output .= "<br>Register user $fcmToken for event series $subscribed_eventseries.<br>";
    }

	$db_timetable->insertUser($fcmToken, $subscribed_eventseries, $os, $language);
}

echo $output;
echo "<p><i><b>Done. EAH Jena</b></i></p>";
