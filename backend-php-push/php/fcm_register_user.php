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

/* we can request debug output to better find errors */
$debug=0;
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


require_once 'fcm_connect_db.php';
if ($debug) print_r($_REQUEST);


// ----------------- Get data from app ----------------------------------------------
//BSP SQL-Injection
//$login = $this->mysqli->real_escape_string( $login ) ;

const ANDROID = "android";
const IOS = "ios";
// initialize to set data types
$os = "";
$language = "";

//get os
if (isset($_REQUEST['os']))
	$os = htmlentities($_REQUEST['os']);

//get language
if(isset($_REQUEST['language']))
    $language = htmlentities($_REQUEST['language']);
if($language !== "DE" && $language !== "EN"){
    error_log(print_r("Language: ".$language, true));
    exit;
}

// initialize to set data types
$fcm_token = "";
$json_subscribed_eventseries = "";
$array_subscribed_eventseries = array();

if ($os === ANDROID) {
	//Get token and subscriptions send from app
	// Alle übergebenen Parameter entwerten, um SQL-Injections zu unterbinden.
	$fcm_token = htmlentities( $_REQUEST["fcm_token"]??null);
	$json_subscribed_eventseries = htmlentities( $_REQUEST["eventseries_names"]??null);

	if(!is_null($json_subscribed_eventseries) && !is_null($fcm_token) ){
        //todo: check if double quotes are also needed for the request sent by the app
		$array_subscribed_eventseries = array(json_decode("\"".$json_subscribed_eventseries."\"", true));
        if($debug) echo "<p>subscribed event series: " . $json_subscribed_eventseries . "</p>";
	} else {
		error_log(print_r("jsonSubscribedEventSeries or fcmToken is null!", true));
		echo "jsonSubscribedEventSeries or fcmToken is null!<br>";
		return;
	}
	/*
	error_log("#---->");
	error_log(print_r('Token: '.$fcmToken, true));
	error_log("<----#");
	*/

} else if ($os === IOS){
	/* //Code aus Hochschule Hof
	$entitybody = file_get_contents("php://input");
	$fullJSON = json_decode($entitybody, true);
	$lectureArray = $fullJSON["vorlesung_id"]??null;
	$pushToken = $fullJSON["fcm_token"]??null;
	$language = $fullJSON["language"]??null; */
} else {
    echo "Keine OS Zuordnung!";
    error_log(print_r("Keine OS Zuordnung!", true));
    error_log(print_r("OS-Type: ".$os, true));
    exit;
}


// Check if a null value is given, to prevent null entries in the database.
// Null values can happen if a user opens the script in a browser window.
if(is_null($array_subscribed_eventseries) || is_null($fcm_token)){
    echo "<br> SubScribed Event Series Json: " . $json_subscribed_eventseries . "<br>";
	echo "arraySubscribedEventSeries or fcm token is null! <br>";
	error_log(print_r("arraySubscribedEventSeries or fcm token is null!", true));
	return;
}


// ----------------- DB entry for user to register ----------------------------------------------

//Clear database from potential previous registrations with the given token
$con->query("DELETE FROM fcm_user WHERE token = '$fcm_token' AND os = '$os'");

//Add token and subscribed event series names to database to register user
foreach ($array_subscribed_eventseries as $subscribed_eventseries) {

	if($debug) echo "<br> event series: $subscribed_eventseries <br>";

	// this way you can add null values to cells w/o adding the letters "NULL" as a string.
	$stmt = $con->prepare("INSERT INTO fcm_user (token, eventseries_name, os, language) VALUES (?, ?, ?, ?)");

//TODO
	// ssss stands for the sequence of string and integer variables.
	$stmt->bind_param('ssss', $fcm_token, $subscribed_eventseries, $os, $language);
	$stmt->execute();
}

//Close sql-connection
$con->close();
// error_log(print_r("DONE!", true));
return("Funktioniert!");
