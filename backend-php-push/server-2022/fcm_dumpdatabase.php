<!DOCTYPE html>
<html lang="de">
<head>
	<meta charset="utf-8"/>
	<title>FCM Dump Database</title>
</head>

<body>
<h1>Willkommen auf dem Stundenplan Push Server! DEBUG - DumpDatabase</h1>

<?php

require_once 'TimetableDb.php';

$GLOBALS['DEBUGDATABASE'] = false;
$GLOBALS['DEBUGDATABASE'] = true;


if ($GLOBALS['DEBUGDATABASE']) {
	
    echo '<h1>Table event_sets</h1>';
    
	$result = debugDisplayAllEventSets();
    
    print_r( $result );

    echo '<p></p>';
	echo '<p></p>';
	echo '<p></p>';
	
	echo '<h1>Table fcm_user</h1>';
	
	$result = debugDisplayAllFcmUser();
	
	print_r( $result );
	
}

