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


// Hier sind mehrere Funktionen enthalten
// All umfassend wird get_module_and_module_event von der start.php ausgeführt
// Hierbei werden die neu eingelesenen Ical Dateien aus den Set Datenbanken geholt, um die Module und Modul_Event DB zu aktualisieren
// Gleichzeitig wird geprüft, ob Änderung an den Events aufgetreten sind
// Wenn Änderung gefunden werden, werden die Push B. versendet und eine Änderungshinweis in der DB Tabelle changes erstellt.
// get_module_and_module_event  ist hierbei eine Funktion, welche mehrere Unterfunktionen ansteuert.

/*
#####################
Oberfunktion
#####################
*/
function get_module_and_module_event($db_time) {
    // neue DB Con
    $DB            = new Db(DBHost, DBPort, DBName, DBUser, DBPassword);
    // Hol alle ical Stundenplan Datensätze aus der DB
    $all_ical_json = get_all_ical();
    // Foreachloop durch Stundenplan Array
    foreach ($all_ical_json as $num => $data) {
        // Json -> Array
        $json_data = json_decode($data['ical_json'], true);
        // Check ob $json_data ein Array mit Inhalt ist.
        // Wenn nicht wird dieses Set uebersprungen
        if (!is_array($json_data) OR empty($json_data)) {
            continue;
        }
        // Foreach Loop durch alle Events eines Stundenplanes
        foreach ($json_data as $date => $data2) {
            // Prüfung ob Event ein Array ist
            if (is_array($data2)) {
                // Foreach Loop durch das einzelne Event Array
                foreach ($data2 as $num2 => $data3) {
                    // Check ob das Event ein Modul ist
                    $module_name = substr($data3['uid'] . '-', 0, strpos($data3['uid'], '-'));
                    // Wenn kein valides Modul, dann continue
                    if ($module_name == false) {
                        continue;
                    } else {
                        // Ist ein valides Event
                        $module_id = check_modul($module_name, $DB);
                        // Jedes Event des Modules überprüfen via Foreach Loop
                        // Checke ob das Event des Modules schon verfügbar ist/war, für dieses Set ($data['splus_key'])
                        check_module_event($module_id, $module_name, $data3, $data['splus_key'], $DB);
                    }
                }
            }
        }
    }
    // entferne alte Events, welche nicht mehr im Stundenplan vorkommen -> welche eine alte event_change_date haben
    remove_old_events($db_time, $DB);
    // entferne alte Module, welche nicht mehr im Stundenplan vorkommen -> welche eine alte event_change_date haben und keine Events mehr unter sich hat
    remove_old_module($db_time, $DB);
}

/*
#####################
Unterfunktionen
#####################
*/

// Hole alle ical_json
// Check alte Module und Modul_Events und bereitet Push Not. vor
function check_module_event($module_id, $module_name, $module_event_data, $set_splus_key, $DB) {
    global $first_start;
    // Check, ob das Event schon in der DB verfügbar ist
    $result = $DB->row("SELECT * FROM `module_events` WHERE `module_id` = ? AND  `module_event_splus_key` = ? AND `set_splus_key` = ?  ;", array(
        $module_id,
        $module_event_data['uid'],
        $set_splus_key
    ));
    if (!is_array($result) OR $result === false) {
        // Wenn Event nicht verfügbar ist, wird es eingefügt
        $DB->query("INSERT INTO `module_events` (`module_id`, `module_event_splus_key`, `change_date`,`startDate`, `title`, `shortTitle`, `date`, `dayOfWeek`, `startTime`, `endTime`, `lecturer`, `room`,`set_splus_key`) VALUES ( ?, ?, CURRENT_TIMESTAMP,  ?,?,?,?,?,?,?,?,?,?)", array(
            $module_id,
            $module_event_data['uid'],
            $module_event_data['startDate'],
            $module_event_data['title'],
            $module_event_data['shortTitle'],
            $module_event_data['date'],
            $module_event_data['dayOfWeek'],
            $module_event_data['startTime'],
            $module_event_data['endTime'],
            $module_event_data['lecturer'],
            $module_event_data['room'],
            $set_splus_key
        ));
        // Neues Event in Log schreiben, soweit es nicht der erste Start ist.
        if ($first_start == 0) {
            $log = new PDOLog();
            $log->write(" Start: {$first_start} Function check_module_event neues Event " . json_encode($module_event_data), DBName . md5(DBPassword));
            // Push Not. wird versendet, für neu eingefügtes Event
            send_push_notifications($module_id, $DB->lastInsertId(), $set_splus_key, array(
                "Neues Event"
            ), $module_event_data, array());
        }
    } else {
        // Das Event war/ist schon in der DB
        // send_push = 0, wenn es zu 1 wird,gab es eine Event Änderung
        $send_push       = 0;
        // Array um die möglichen Gründe zu sammeln, warum und wie ein Event 'verändert' wurden ist
        $grund_aenderung = array();
        //Überprüfung Date
        if (strtotime($result['date']) <> strtotime($module_event_data['date'])) {
            $date              = $module_event_data['date'];
            // Push new date
            $send_push         = 1;
            $grund_aenderung[] = "Datum";
        } else {
            $date = $result['date'];
        }
        //Überprüfung startTime
        if ($result['startTime'] <> $module_event_data['startTime']) {
            $startTime         = $module_event_data['startTime'];
            // Push new startTime
            $send_push         = 1;
            $grund_aenderung[] = "Startzeit";
        } else {
            $startTime = $result['startTime'];
        }
        //Überprüfung endTime
        if ($result['endTime'] <> $module_event_data['endTime']) {
            $endTime           = $module_event_data['endTime'];
            // Push new endTime
            $send_push         = 1;
            $grund_aenderung[] = "Endzeit";
        } else {
            $endTime = $result['endTime'];
        }
        //Überprüfung room
        if ($result['room'] <> $module_event_data['room']) {
            // Check room wiht only Int value numbers 
            if (preg_match('/\d/', $result['room']) AND preg_replace('/[^0-9]/', '', $result['room']) <> preg_replace('/[^0-9]/', '', $module_event_data['room'])) {
                $room              = $module_event_data['room'];
                // Push new room
                $send_push         = 1;
                $grund_aenderung[] = "Raum";
            } else {
                $room = $result['room'];
            }
        } else {
            $room = $result['room'];
        }
        // Event aktualisieren und Push B. senden
        if ($send_push == 1) {
            $DB->query("UPDATE `module_events` SET `change_date` = CURRENT_TIMESTAMP,`startDate` = ?, `title` = ?, `shortTitle` = ?, `date` = ?, `dayOfWeek` = ?, `startTime` = ?, `endTime` = ?, `lecturer` = ?, `room` = ?  WHERE `id` = ?", array(
                $module_event_data['startDate'],
                $module_event_data['title'],
                $module_event_data['shortTitle'],
                $module_event_data['date'],
                $module_event_data['dayOfWeek'],
                $module_event_data['startTime'],
                $module_event_data['endTime'],
                $module_event_data['lecturer'],
                $module_event_data['room'],
                $result['id']
            ));
            $log = new PDOLog();
            $log->write("Event Änderung: " . json_encode($grund_aenderung) . "  ALT:" . json_encode($result) . "  || NEU:" . json_encode($module_event_data), DBName . md5(DBPassword));
            // Sende Push B. für diese Event Änderung
            send_push_notifications($module_id, $result['id'], $set_splus_key, $grund_aenderung, $module_event_data, $result);
        } else {
            // Gab keine Änderung dieses Events
            // Nur die Changetime wird neu gesetzt
            $DB->query("UPDATE `module_events` SET `change_date` = CURRENT_TIMESTAMP WHERE `id` = ?", array(
                $result['id']
            ));
        }
    }
}
// Überprüfung,  ob ein Modul schon in der DB vorhanden ist
// Wenn nein, dann wird es eingefügt
// Rückgabe ist die DB id für dieses Moduls
function check_modul($module_name, $DB) {
    // Pruefe ob schon Verfübar in der DB
    $result = $DB->row("SELECT Count(`id`) as count, id FROM `module` WHERE module_splus_key = ?", array(
        $module_name
    ));
    // Wenn nein
    if ($result['count'] == 0) {
        // Einfügen
        $DB->query("INSERT INTO `module` (`module_splus_key`, `change_date`) VALUES (?, CURRENT_TIMESTAMP)", array(
            $module_name
        ));
        // Rückgabe der Modul DB ID
        return $DB->lastInsertId();
    } else {
        // Schon verfügbar
        // Update change_date
        $DB->query("UPDATE `module` SET `change_date` = CURRENT_TIMESTAMP WHERE `id` = ?", array(
            $result['id']
        ));
        // Rückgabe der Modul DB ID
        return $result['id'];
    }
}
// Hol alle Ical Datein aus der DB
// Wenn es keine Results gibt mach nen Log und beende das Skript
function get_all_ical() {
    // Neue DB Con
    $DB     = new Db(DBHost, DBPort, DBName, DBUser, DBPassword);
    // Holt alle Ical Json Stundepläne aus der DB, welche nicht leer sind.
    // Eine ical_json ist leer, wenn keine ical für dieses Semester erhalten worden ist.
    $result = $DB->query("SELECT id , ical_json, splus_key FROM `semester_set` WHERE ical_json != '' ;");
    $DB->closeConnection();
    // Wenn isset and is_array dann Rückgabe des Arrays
    if (isset($result) AND is_array($result)) {
        return $result;
    }
    // Wenn False dann Errorlog & Exit
    else {
        $log = new PDOLog();
        $log->write("Keine Result für die Function: get_all_ical", DBName . md5(DBPassword));
        exit;
    }
}
function remove_old_events($db_time, $DB) {
    // Hol alle Events welche älter sind als $db_time (start des Skriptes)
    // Diese Events sind nicht mehr Verfügbar und werden entfernt
    // Dabei werden nur die Events gelöscht, wenn deren Set in semester set verfügbar ist und eine gefüllte ical_json hat.
    // Mit diesem Umgang wird verschmieden, das unendlich viele Events gelöscht werden, wenn in einem Durchlauf nicht alle Ical Datein vom EAH Jena Server abgerufen werden konnte
    $DB     = new Db(DBHost, DBPort, DBName, DBUser, DBPassword);
    // Alle Events welche älter als $db_time sind
    $result = $DB->query("SELECT * FROM `module_events` WHERE change_date < ? ;", array(
        $db_time
    ));
    // Prüf ob $result ein array ist
    if (isset($result) AND is_array($result)) {
        foreach ($result as $num => $data) {
            // Prüfung ob semester set verfügbar ist und eine Ical_ Json hat 
            $result_set = $DB->single("SELECT count(*) count FROM `semester_set` WHERE splus_key = ? AND ical_json != ''", array(
                $data['set_splus_key']
            ));
            // Wenn $result_set > 0 ist kann das Event mit einer Push Benachrichtigung gelöscht werden
            // Sonst wird nix gemacht
            if ($result_set > 0) {
                // Push Event ist nicht mehr enthalten
                $log = new PDOLog();
                $log->write("Event nicht mehr im Stundenplan: " . json_encode($data), DBName . md5(DBPassword));
                // Entferne Event aus der DB
                $result = $DB->query("DELETE FROM `module_events` WHERE `id` = ?", array(
                    $data['id']
                ));
                //Sende Push B. für diese Eventänderung
                send_push_notifications($data['module_id'], $data['id'], $data['set_splus_key'], array(
                    "Event Entfernt"
                ), array(), $data);
            }
        }
    }
    return;
}

function remove_old_module($db_time, $DB) {
    // Löscht alle alten Module, welche zu alt sind und keine Events mehr unter sicht hat
    $DB     = new Db(DBHost, DBPort, DBName, DBUser, DBPassword);
    // Alle Module welche älter als $db_time sind
    $result = $DB->query("SELECT * FROM `module` WHERE change_date < ? ;", array(
        $db_time
    ));
    // Prüf ob $result ein array ist
    if (isset($result) AND is_array($result)) {
        foreach ($result as $num => $data) {
            // Prüfung ob das Modul noch aktive Events hat verfügbar ist und eine Ical_ Json hat 
            $result_set = $DB->single("SELECT count(*) count FROM `module_events` WHERE `module_id` = ?", array(
                $data['id']
            ));
            // Wenn $result_set == 0 ist kann das Modul gelöscht werden
            // Sonst wird nix gemacht
            if ($result_set == 0) {
                // Entferne Module aus der DB
                $result = $DB->query("DELETE FROM `module` WHERE `id` = ?", array(
                    $data['id']
                ));
            }
        }
    }
    return;
}
