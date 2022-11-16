<?php
# Variablen
// Sollten alle streng vertraulich behandelt werden
// Datenbank Infos
//define('DBHost', '');
const DBHost = '';
//define('DBPort', 3306 );
const DBPort = 3306;
//define('DBName', 'stundenplan');
const DBName = 'stundenplan';
//define('DBUser', '');
const DBUser = '';
//define('DBPassword', '');
const DBPassword = '';
// Firebase Variablen - Zu finden in den Firebase Einstellungen ->  Cloudmessaging| apiToken = Serverschlüssel | senderid = Sender-ID
//define('apiToken', '');
const apiToken = '';
//define('senderId', '');
const senderId = '';
# Weitere Variablen
// Link zur OPML Datei |EAH : http://stundenplanung.eah-jena.de/eahapp.opml
//define('OPMLLink', "http://stundenplanung.eah-jena.de/eahapp.opml");
const OPMLLink = "http://stundenplanung.eah-jena.de/eahapp.opml";
// Domain zum Splus Server (FH Jena = https://stundenplanung.eah-jena.de/ )
//define('SPlusDomain', "https://stundenplanung.eah-jena.de/");
const SPlusDomain = "https://stundenplanung.eah-jena.de/";
// Zwei Secret Keys für die Verschlüsslung der Device Token/Ids
// Beide beliebig lang wählen.
// Empfehlung ist:
// - jeweils 16 Zeichen
// - beliebigen Passwort Generator hierfür nutzen
const SecretKey1 = "";
const SecretKey2 = "";
// Error Echo bei PDO Fehlschlag --> 1 = debug, 0 = produktiv System
const PDOError = 1;
// Slim Framwork Error Anzeige set to false in production
const SlimdisplayErrorDetails = false;
// Zeitzone Einstellung
const TIMEZONE = 'Europe/Berlin';
date_default_timezone_set(TIMEZONE);
