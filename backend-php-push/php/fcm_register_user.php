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

//Get os
const ANDROID = "android";
const IOS = "ios";

if (isset($_REQUEST['os']))
	$os = htmlentities($_REQUEST['os']);

// Plausibilisierung der Apps
if ($os == ANDROID || $os == IOS)
	; // ok
else {
	error_log(print_r("OS-Type: ".$os, true));
	exit;
}

// intialisieren, damit haben wir den Typ festgelegt
$fcmToken = "";
$subscribedEventSets = "";
$subscribedEventSetsArray = "";

if ($os == ANDROID) {
	//Get token and subscriptions send from App
	// Alle übergebenen Parameter entwerten, um SQL-Injections zu unterbinden.
	$fcmToken = htmlentities( $_REQUEST["fcm_token"]??null);
	$subscribedEventSets = htmlentities( $_REQUEST["eventset_ids"]??null);

	if(!is_null($subscribedEventSets) && !is_null($fcmToken) ){
		$subscribedEventSetsArray = json_decode($subscribedEventSets, true);
	} else {
		error_log(print_r("subscribedEventSetId or fcmToken is null!", true));
		echo "subscribedEventSetId or fcmToken is null!";
		return "subscribedEventSetId or fcmToken is null!";
	}
	/*
	error_log("#---->");
	error_log(print_r('Token: '.$fcmToken, true));
	error_log("<----#");
	*/

} else if ($os == IOS){
	/* //Code aus Hochschule Hof
	$entitybody = file_get_contents("php://input");
	$fullJSON = json_decode($entitybody, true);
	$lectureArray = $fullJSON["vorlesung_id"]??null;
	$pushToken = $fullJSON["fcm_token"]??null;
	$language = $fullJSON["language"]??null; */
} else {
	error_log(print_r("Keine OS Zuordnung!", true));
	echo "Keine OS Zuordnung!";
	return("Keine OS Zuordnung!");
}


// Check if a null value is given, to prevent null entries in the database.
// Null values can happen if a user opens the scipt in a browser window.
if(is_null($subscribedEventSetsArray) || is_null($fcmToken)){
	echo "There is a null value!";
	error_log(print_r("There is a null value!", true));
	return "There is a null value!";
}


// if ($debug) { echo "\nToken:". $fcmToken ."\n\n subscribedEventSets: ". $subscribedEventSets ."\n";}

// Ab hier sollten also das Token, die Vorlesungen valide sein.
$os = 0 ; // Android
$lang = "DE";


// ----------------- DB entry ----------------------------------------------

//Clear database from the given token
// erst einmal alle Vorlesungen löschen, dann später entsprechend wieder hinzufügen.
// Wenn eine Vorlesung abgewählt ist, bekommen wir das sonst nicht mit.
$sqldelete = "DELETE FROM fcm_user WHERE token = \"$fcmToken\""; /* AND os = \"$os\"*/
$con->query($sqldelete);

//Add token and subscribed eventset IDs to database to register user
for ($i = 0; $i < count($subscribedEventSetsArray); $i++) {

	$activity_id = htmlspecialchars($subscribedEventSetsArray[$i]['activity_id']);

	//	if ($debug) { echo "\nactivity_id: $activity_id\n"; }

	// this way you can add null values to cells w/o adding the letters "NULL" as a string.
	// Also you arn't getting crazy by concatenating strings like in the previous version :)
	$stmt = $con->prepare("INSERT INTO fcm_user (token, eventset_id, os, lang) VALUES (?, ?, ?, ?)");

//TODO
	// ss stands for the sequence of string and integer variables.
	$stmt->bind_param('ssss', $fcmToken, $activity_id, $os, $lang);
	/* //Code der Hochschule Hof
	$stmt = $con->prepare("INSERT INTO fcm_nutzer (token, vorlesung_id, os, language) VALUES (?, ?, ?, ?)");
	// ssis stands for the sequence of string and integer variables.
	$stmt->bind_param('ssis', $pushToken, $vorlesung_id, $os, $language);
	*/

	$stmt->execute();
}

//Close sql-connection
$con->close();
// error_log(print_r("DONE!", true));
return("Funktioniert!");
