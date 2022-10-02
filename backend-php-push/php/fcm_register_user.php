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

require_once 'fcm_connect_db.php';
/** @var TimetableDb $db_timetable database connection */
global $db_timetable;

/* we can request debug output to better find errors */
$debug = false;

if (isset( $_REQUEST['debug']))
	$debug = htmlentities($_REQUEST['debug']);

if ($debug){
	mysqli_report(MYSQLI_REPORT_ALL);

	ini_set('mysql.trace_mode',  'On' );
	ini_set('mysqli.trace_mode',  'On' );

	//ini_set('error_reporting', E_ALL | E_STRICT | E_DEPRECATED | E_NOTICE | E_PARSE );
	error_reporting(E_ALL);

	ini_set('display_errors', 'On' );
	ini_set('display_startup_errors', 'On' ) ;

	//ini_set('allow_call_time_pass_reference', 'On' );
	ini_set('html_errors', 'On' ) ;

	ini_set('mysql.log_queries_not_using_indexes','on');
	ini_set('mysql.log_slow_admin_statements','on');
	ini_set('mysql.slow_query_log','on');
	ini_set('mysql.log_error_verbosity','3');

	ini_set('mysqli.log_queries_not_using_indexes','on');
	ini_set('mysqli.log_slow_admin_statements','on');
	ini_set('mysqli.slow_query_log','on');
	ini_set('mysqli.log_error_verbosity','3');

	// E_NOTICE ist sinnvoll um uninitialisierte oder
	// falsch geschriebene Variablen zu entdecken
	error_reporting(E_ERROR | E_WARNING | E_PARSE | E_NOTICE | E_STRICT );

}


//Get or create database connection
try {
    initDbConnection();
} catch (Exception $e) {
    echo "Database is not available: " .  $e->getMessage() . "<br>";
    exit();
}
if ($debug) {
    echo "<p>";
    print_r($_REQUEST);
    echo "</p>";
}


// ----------------- Get data from app ----------------------------------------------

// initialize to set data types
$os = ANDROID;
$language = "DE";

//get os
if (isset($_REQUEST['os']))
	$os = htmlentities($_REQUEST['os']);

//get language
if(isset($_REQUEST['language']))
    $language = htmlentities($_REQUEST['language']);
if($language !== "DE" && $language !== "EN"){
    error_log("Language: ".$language);
    exit;
}

// initialize to set data types
$fcm_token = "";
$array_subscribed_eventseries = array();

if ($os === ANDROID) {
	//Get token and subscriptions send from app
	// Alle übergebenen Parameter entwerten, um SQL-Injections zu unterbinden.
	$fcm_token = htmlentities( $_REQUEST["fcm_token"]??null);
    $array_subscribed_eventseries =  $_REQUEST["eventseries_names"]??null;

} elseif ($os === IOS){
	//
} else {
    echo "Keine OS Zuordnung!";
    error_log("Keine OS Zuordnung! OS-Type: ".$os);
    exit;
}


// Check if a null value is given, to prevent null entries in the database.
// Null values can happen if a user opens the script in a browser window.
if(is_null($array_subscribed_eventseries) || is_null($fcm_token)){
	echo "<br> array_subscribed_eventseries or fcm token is null! <br>";
	error_log("arraySubscribedEventSeries or fcm token is null!");
	return;
}


// ----------------- DB entry for user to register ----------------------------------------------

//Clear database from potential previous registrations with the given token
$db_timetable->deleteUser($fcm_token);
//Add token and subscribed event series names to database to register user
foreach ($array_subscribed_eventseries as $subscribed_eventseries) {
    $subscribed_eventseries = htmlentities($subscribed_eventseries);

	if($debug) echo "<br> Register user $fcm_token for event series: $subscribed_eventseries <br>";

	$db_timetable->insertUser($fcm_token, $subscribed_eventseries, $os, $language);
}

//TODO kannst Du das gebrauchen oder stört das?
echo 'Done. EAH Jena';
