<?php

/*
  ~ Copyright (c) 2019 EAH Jena
  ~ This program is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU General Public License as published by
  ~ the Free Software Foundation, either version 3 of the License, or
  ~ (at your option) any later version.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with this program.  If not, see <http://www.gnu.org/licenses/>.
  */


/* 
#####################
Setzten der ini-values 
#####################
*/
ini_set('max_execution_time', 2500);
ini_set('memory_limit', '2512M');
// Lade Composer
require 'vendor/autoload.php';
// Import aller relevanten Variablen
require_once(dirname(__FILE__) . "/../settings/settings.php");
/*
#####################
Import aller Skripte.
#####################
*/
// Zustaendig, um alle Studiengänge zu sammeln und in die DB einzupflegen.
// Nicht mehr verfügbare Studiengänge werden gelöscht.
require_once(__DIR__ . "/src/get_all_studiengang.php");
// Sammelt alle Stundenplaene Daten
require_once(__DIR__ . "/src/get_all_stundenplane_je_semester_set.php");
// Enthaelt verschiedene Funktionen, um alle Events von Sets zu aktualisieren und um Änderungen zu finden
require_once(__DIR__ . "/src/module.php");
// Eine PDO Klasse, um standardisierte DB Anfrage zu nutzen
require_once(dirname(__FILE__) . "/../general_functions/PDO.class.php");
// Klasse um Kalenderdatein iCal in ein Array umzuwandeln
require_once(__DIR__ . "/src/iCal.php");
// Enthält Funktionen um Push nachrichten zu versenden
require_once(__DIR__ . "/src/push_notification.php");
// Funktion um die Device_Token zu verschlüsseln
require_once(dirname(__FILE__) . "/../general_functions/encryption.php");
// Funktion  um die aktuelle DB Time zu ermitteln
require_once(__DIR__ . "/src/get_db_time.php");
// Abgewandeltes file_get_contents
require_once(__DIR__ . "/src/file_contents_exist.php");
// Funktion  um die DB zu überprüfen, wenn Db nicht vorhanden wird sie angelegt.
require_once(__DIR__ . "/src/check_valid_db.php");
/*
#####################
Skript Vorbereitung
#####################
*/
// Globale Var, welche angibt ob dies der erste Durchgang dieses Skriptes ist.
// Wenn 1 -> ist der erste Durchgang. Es werden keine Einträge in die Changes DB gemacht und keine Push. Not. werden versendet.
// Diese Variable wird von der check_valid_db.php angepasst
$first_start = 0;
// Check ob es möglich ist zu DB eine Verbindung herzustellen.
check_db_con();
// Check ob die Datenbank vorhanden ist
// Wenn nicht wird diese angelegt
check_db_available();
// Erstellung des Firebase Clients
// Infos hierzu: https://github.com/EdwinHoksberg/php-fcm
$firebase_client      = new \Fcm\FcmClient(apiToken, senderId);
// Konfiguaration
// Variablen kommen aus /setting/settings.php
// Link zur OPML Datei:
$opml_file_link       = OPMLLink;
// Link zum Rest Start von SPlus
$normal_link_to_splus = SPlusDomain;
/*
#####################
Skript Start 
#####################
*/
$log                  = new PDOLog();
$log->write("Cron Start " . date('c'), DBName . md5(DBPassword));
/* 
Hole den aktuellen Db Timestamnp.
Wird genutzt, um nicht mehr verfügbare Studiengänge usw zu löschen
*/
$db_time     = get_db_time();
/*
Sammlung alle Studiengänge , alle Semester der Studiengänge und alle Semester Sets der jeweiligen Studiengänge.
*/
get_all_studiengang_start($opml_file_link, $db_time);
/*
Sammlung aller Stundenpläne (.ical files) je Semester Set
*/
get_all_stundenplane_je_semester_set($normal_link_to_splus);
/*
Erstellung einer Modul und Modul-Event Datenbank mit den Stundenplänen aller Semester_Sets
Hierbei werden gelöschte, veränderte und neu hinzugefügt Events für jedes Modul erkannt.
Nach der Erkennung wird eine Push Nachricht an die Nutzer geschickt, welche sich für dieses Modul eingeschrieben haben
*/
get_module_and_module_event($db_time);
// Ende
$log->write("Cron End" . date('c'), DBName . md5(DBPassword));
exit;
?> 