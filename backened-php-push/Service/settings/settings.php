<?php
# Variablen
// Sollten alle streng vertraulich behandelt werden
// Datenbank Infos
define('DBHost', '');
define('DBPort', 3306 );
define('DBName', 'stundenplan');
define('DBUser', '');
define('DBPassword', '');
// Firebase Variablen - Zu finden in den Firebase Einstellungen ->  Cloudmessaging| apiToken = Serverschlüssel | senderid = Sender-ID
define('apiToken', '');
define('senderId', '');
# Weitere Variablen
// Link zur OPML Datei |EAH : http://stundenplanung.eah-jena.de/eahapp.opml
define('OPMLLink', "http://stundenplanung.eah-jena.de/eahapp.opml");
// Domain zum Splus Server (FH Jena = https://stundenplanung.eah-jena.de/ )
define('SPlusDomain', "https://stundenplanung.eah-jena.de/");
// Zwei Secret Keys für die Verschlüsslung der Device Token/Ids
// Beide beliebig lang wählen.
// Empfehlung ist:
// - jeweils 16 Zeichen
// - beliebigen Passwort Generator hierfür nutzen
define('SecretKey1', "");
define('SecretKey2', "");
// Error Echo bei PDO Fehlschlag --> 1 = debug, 0 = produktiv System
define('PDOError', 1);
// Slim Framwork Error Anzeige set to false in production
define("SlimdisplayErrorDetails", false);
// Zeitzone Einstellung
define('TIMEZONE', 'Europe/Berlin');
date_default_timezone_set(TIMEZONE);
?> 