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

// All diese Funktion in diesem Skript kümmern sich, um das Einlesen und Verarbeiten der OPML Datei

// Übergeordnete Funktion, welche mehrere Unterfunktiopnen nacheinander ansteuert
function get_all_studiengang_start($opml_file_link, $db_time) {
    // Holet die OPML Datei und wandelt diese in ein Array
    $array_all_studiengang = all_studiengang($opml_file_link);
    // Sammelt alle Studiengänge, Semester und Semestert Sets zusammen und prüft die Verfügbrkeit in der DB
    check_newer_or_invalid_studiengang($array_all_studiengang);
    // Entfernung von alten Semester Sets
    remove_old_semester_sets($db_time);
    // Entfernung von alten Semestern
    remove_old_semester($db_time);
    // Entfernung von alten Studiengängen 
    remove_old_studiengang($db_time);
}
/*
#####################
Unterfunktionen
#####################
*/
// Holt die OPML Datei und wandelt diese in ein Array
function all_studiengang($link) {
    $omp = file_contents_exist($link);
    // // Wenn die omp Datei geholt werden konnte
    if ($omp) {
        $xml   = simplexml_load_string($omp, "SimpleXMLElement");
        $json  = json_encode($xml);
        $array = json_decode($json, TRUE);
    }
    // Wenn Array dann true
    if ($omp AND isset($array) AND is_array($array)) {
        return $array;
    } else {
        // Wenn fehlerhaft dann stopp das Skript mit einem Error
        // Dies ist nötig, damit keine Daten gelöscht werden, falls die OPML Datei einmal nicht geholt werden konnte
        $log = new PDOLog();
        $log->write("Keine Result für die Function: all_studiengang mit folgenden Link: {$link}");
        exit;
    }
}
// Sammelt alle Studiengänge, Semester und Semestert Sets zusammen und prüft die Verfügbrkeit in der DB
// Diese Funktion geht mit mehreren Foreach-Schleifen durch das OPML Array und sucht nach allen Studiengängen, Semestern und Sets
function check_newer_or_invalid_studiengang($array) {
    $log = new PDOLog();
    foreach ($array['body']['outline'] as $num => $studiengang_data) {
        // Check and Get studiengang name
        if (isset($studiengang_data['@attributes']) AND isset($studiengang_data['@attributes']['text']) AND is_string($studiengang_data['@attributes']['text'])) {
            // valid
            $studiengang_name = $studiengang_data['@attributes']['text'];
        } else {
            // invalid studiengang name
            //$log->write("Invalid studiengang name for element : {$num}");
            continue;
        }
        // Importiere diesen studiengang
        // Fals dieser studiengang schon vorhanden ist, wird er aktualisiert
        $studiengang_id = import_studiengang_id($studiengang_name);
        // Checke ob dieser studiengang verfügbare Semester hat
        if (!isset($studiengang_data['outline']) OR !is_array($studiengang_data['outline'])) {
            //$log->write("Keine Semester für : {$studiengang_name }");
            continue;
        }
        // Foreach loop für jedes  Studiengang Semester
        foreach ($studiengang_data['outline'] as $num1 => $semester_data) {
            // Checkt, ob dieses Semester ein validen Text hat
            if (!isset($semester_data['@attributes']) OR !isset($semester_data['@attributes']['text'])) {
                //$log->write("Keine Semestertext für: {$studiengang_name } - {$num1}");
                continue;
            }
            // Holen des Semester Name
            else {
                $semester_name = $semester_data['@attributes']['text'];
            }
            // Importiere dieses Semester
            // Fals dieses Semester schon vorhanden ist, wird er aktualisiert
            $semester_id = import_semester_id($studiengang_id, $semester_name);
            // Checke ob dieses Semester min. ein Set besitzt
            if (!isset($semester_data['outline']) OR !is_array($semester_data['outline'])) {
                // Dieses Semester hat keine Semestersets
                //$log->write("Keine Sets für: {$studiengang_name } - {$semester_name}");
                continue;
            } else {
                // Dieses Semester hat Semestersets
                // Foreach loop durch jedes Set
                foreach ($semester_data['outline'] as $num2 => $set_data) {
                    // Check ob ein Set Text gesetzt ist
                    if (!isset($set_data['@attributes']['text'])) {
                        //$log->write("Keine Setstext für: {$studiengang_name } - {$semester_name} - {$num2}");
                        continue;
                    }
                    $set_name = $set_data['@attributes']['text'];
                    if (!isset($set_data['outline']) OR !is_array($set_data['outline'])) {
                        //$log->write("Kein valides Set: {$studiengang_name } - {$semester_name} - {$set_name}");
                        continue;
                    }
                    // Holt den SPlus Key aus diesem Array
                    $splus_key = get_splus_key($set_data['outline']);
                    // Wenn es ein valider SPlus Key ist wird er in die DB geschrieben
                    if ($splus_key) {
                        // Import des SPlus Keys in die DB
                        import_set_id($studiengang_id, $semester_id, $set_name, $splus_key);
                    }
                }
            }
        }
    }
    return;
}
/*
Diese Funktion erhält ein Array und versucht den SPlus Key zu finden
*/
function get_splus_key($array) {
    // Foreach über das Array
    foreach ($array as $num => $set_links) {
        // Check ob es ein key @attributes und @attributes['text'] gibt
        if (isset($set_links['@attributes']) AND isset($set_links['@attributes']['text'])) {
            $link_name = $set_links['@attributes']['text'];
            // Checke ob der $link_name = ical_URL_google ist und ob  $set_links['@attributes']['url'] vorhanden ist
            if ($link_name == "ical_URL_google" AND isset($set_links['@attributes']['url'])) {
                $link_url = $set_links['@attributes']['url'];
                // Wenn eine ID in der URL ist, wird aus dieser via substr der SPlus Key herraus geholt
                if (($pos = strpos($link_url, "id=")) !== FALSE) {
                    // Rückgabe eines validen SPlus Keys
                    return substr($link_url, $pos + 3);
                }
            } else {
                continue;
            }
        }
    }
    // Rückgabe fals
    return false;
}
// Checkt, ob dieser Studiengang schon in der DB ist
// Wenn nein wird er eingefügt
// Rückgabe = studiengang ID
function import_studiengang_id($name) {
    // Aufbau DB Con
    $DB     = new Db(DBHost, DBPort, DBName, DBUser, DBPassword);
    // DB query, ob dieser studiengang schon in DB ist
    $result = $DB->row("SELECT id FROM `studiengang` WHERE `studiengang_name` = ? ", array(
        $name
    ));
    // Wenn Array, dann ist der studiengang schon vorhanden
    if (is_array($result)) {
        // Update Change Date
        $DB->query("UPDATE `studiengang` SET `change_date` = CURRENT_TIMESTAMP WHERE `studiengang`.`id` = ?", array(
            $result['id']
        ));
        $DB->closeConnection();
        // Return studiengang ID
        return $result['id'];
    }
    // Ist noch nicht in DB
    else {
        // Einfügen des Studienganges
        $DB->query("INSERT INTO `studiengang` (`studiengang_name`) VALUES (?)", array(
            $name
        ));
        // Rückgabe studiengang ID
        return $DB->lastInsertId();
    }
}
// Check ob dieses Semester schon in der DB ist
// Wenn nein, wird es eingefügt
// Rückgabe = Semestser ID
function import_semester_id($studiengang_id, $semester_name) {
    // Aufbau DB Con
    $DB     = new Db(DBHost, DBPort, DBName, DBUser, DBPassword);
    // DB query, ob dieses Semester schon in DB ist
    $result = $DB->row("SELECT id FROM `semester` WHERE `studiengang_id` = ? AND semester_name = ?;", array(
        $studiengang_id,
        $semester_name
    ));
    // Wenn Array, dann ist dieses Semester schon vorhanden
    if (is_array($result)) {
        // Update Change Date
        $DB->query("UPDATE `semester` SET `change_date` = CURRENT_TIMESTAMP WHERE `semester`.`id` = ?", array(
            $result['id']
        ));
        $DB->closeConnection();
        // Return Semester ID
        return $result['id'];
    } else {
        // Semester ist noch nicht in DB
        // Einfügen des Semesters
        $DB->query("INSERT INTO `semester` (`studiengang_id`, `semester_name`) VALUES (?,?)", array(
            $studiengang_id,
            $semester_name
        ));
        // Rückgabe Semester ID
        return $DB->lastInsertId();
    }
}
// Check ob dieses Set schon in der DB ist
// Wenn nein wird es eingefügt
// Rückgabe = Set ID
function import_set_id($studiengang_id, $semester_id, $set_name, $splus_key) {
    // Aufbau DB Con
    $DB     = new Db(DBHost, DBPort, DBName, DBUser, DBPassword);
    // DB query, ob dieses Semester-Set schon in DB ist
    $result = $DB->row("SELECT id FROM `semester_set` WHERE `semester_id` = ? AND set_name = ? AND `splus_key` = ? ;", array(
        $semester_id,
        $set_name,
        $splus_key
    ));
    // Wenn Array, dann ist dieses Set schon vorhanden
    if (is_array($result)) {
        // Update Change Date
        $DB->query("UPDATE `semester_set` SET `change_date` = CURRENT_TIMESTAMP, `change_date_json` = CURRENT_TIMESTAMP WHERE `semester_set`.`id` = ?", array(
            $result['id']
        ));
        $DB->closeConnection();
        // Rückgabe Set ID
        return $result['id'];
    } else {
        // Set ist noch nicht in DB
        // Einfügen des Semesters
        $DB->query("INSERT INTO `semester_set` (`semester_id`, `set_name`, `splus_key`) VALUES (?,?,?)", array(
            $semester_id,
            $set_name,
            $splus_key
        ));
        // Rückgabe Set ID
        return $DB->lastInsertId();
    }
}
// Nicht mehr verfügbare Studiengänge aus DB entfernen
function remove_old_studiengang($db_time) {
    $log    = new PDOLog();
    $DB     = new Db(DBHost, DBPort, DBName, DBUser, DBPassword);
    // Holen aller Studiengänge, welche ein zu altes change_date haben
    $result = $DB->query("SELECT * FROM `studiengang` WHERE `change_date` < ? ", array(
        $db_time
    ));
    if ($result AND is_array($result)) {
        // Jeden Studiengang löschen, welcher zu alt ist
        foreach ($result as $num => $data) {
            $DB->query("DELETE FROM `studiengang` WHERE id = ?", array(
                $data['id']
            ));
            $log->write("{$data['studiengang_name']} removed - studiengang", DBName . md5(DBPassword));
        }
    }
    $DB->closeConnection();
    return;
}
// Nicht mehr verfügbare Semester aus DB entfernen
function remove_old_semester($db_time) {
    $log    = new PDOLog();
    $DB     = new Db(DBHost, DBPort, DBName, DBUser, DBPassword);
    // Holen aller Semester, welche ein zu altes change_date haben
    $result = $DB->query("SELECT * FROM `semester` WHERE `change_date` < ? ", array(
        $db_time
    ));
    if ($result AND is_array($result)) {
        // Jeden Semester löschen, welches zu alt ist
        foreach ($result as $num => $data) {
            $DB->query("DELETE FROM `semester` WHERE id = ?", array(
                $data['id']
            ));
            $log->write("{$data['semester_name']} removed - Semester", DBName . md5(DBPassword));
        }
    }
    $DB->closeConnection();
    return;
}
// Nicht mehr verfügbare Sets aus DB entfernen
// Hierbei werden auch alle Änderungshinweise, aus der Tabelle changes, dieses Sets aus der Datenbank gelöscht 
// Darüber hinaus auch alle Abonnements aus der Device_module Tabelle. 
// Auch alle Events werden gelöscht
// Wenn somit ein neues Semester gestartet wird, werden alle Beziehungen aus dem Vorsemester aus der Datenbank entfernt.
function remove_old_semester_sets($db_time) {
    $log    = new PDOLog();
    $DB     = new Db(DBHost, DBPort, DBName, DBUser, DBPassword);
    $result = $DB->query("SELECT * FROM `semester_set` WHERE `change_date` < ? ", array(
        $db_time
    ));
    if ($result AND is_array($result)) {
        foreach ($result as $num => $data) {
            // Set löschen
            $DB->query("DELETE FROM `semester_set` WHERE id = ?", array(
                $data['id']
            ));
            $log->write("{$data['set_name']} removed - Semester", DBName . md5(DBPassword));
            // Alle Änderungshinweise für dieses Set löschen
            $DB->query("DELETE FROM `changes` WHERE `set_splus_key` = ?", array(
                $data['splus_key']
            ));
            // Alle Abonnements welche sich auf dieses Set beziehen werden gelöscht
            $DB->query("DELETE FROM `device_module` WHERE `set_splus_key` = ?", array(
                $data['splus_key']
            ));
            // Alle Event dieses Set werden gelöscht 
            $DB->query("DELETE FROM `module_events` WHERE `set_splus_key` = ?", array(
                $data['splus_key']
            ));
            // Alle Module dieses Set werden gelöscht
            // Diese werden durch remove_old_events() gelöscht
        }
    }
    $DB->closeConnection();
    return;
}
?> 