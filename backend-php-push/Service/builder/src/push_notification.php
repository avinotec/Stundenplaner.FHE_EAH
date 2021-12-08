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
In dieser Datei werden alle Push Notifications ausgeführt und verwaltet.
Darüber hinaus werden hier die Änderungshinweise in die Tabelle changes geschrieben
*/
/*
#####################
Oberfunktion
#####################
*/
function send_push_notifications($module_id, $event_id, $set_splus_key, $grund_aenderung, $neue_event_daten, $alte_event_daten) {
    global $firebase_client;
    $DB = new Db(DBHost, DBPort, DBName, DBUser, DBPassword);
    // Den Grund der Änderung ID bilden, um in der Datenbank und/oder App besser damit zu handieren.
    // ID :
    // 1 -> Änderung
    // 2 -> Entfernung
    // 3 -> Hinzfügung
    // 4 -> Unbekannt
    // Prüfen ob $grund_aenderung ein Array ist und ein 0tes Element verfügbar ist. 
    if (is_array($grund_aenderung)) {
        // Date, startTime, endTime, room
        if (in_array("Datum", $grund_aenderung) OR in_array("Startzeit", $grund_aenderung) OR in_array("Endzeit", $grund_aenderung) OR in_array("Raum", $grund_aenderung)) {
            $grund_aenderung_id = 1;
        }
        // Entfernt
        elseif (in_array("Event Entfernt", $grund_aenderung)) {
            $grund_aenderung_id = 2;
        }
        // Hinzugefügt
            elseif (in_array("Neues Event", $grund_aenderung)) {
            $grund_aenderung_id = 3;
        }
        // Wenn kein passender Grund übermittelt worden ist.
        else {
            $grund_aenderung_id = 4;
            $grund_aenderung    = array(
                "unknown"
            );
        }
    }
    // Wenn $grund_aenderung kein Array ist
    else {
        $grund_aenderung_id = 4;
        $grund_aenderung    = array(
            "unknown"
        );
    }

    // Neue oder alte Daten nutzen
    // Alte bei Löschun
    // Sonst können neue_event_daten genutzt werden

    if(empty($neue_event_daten)){
            $event_splus_key =  $alte_event_daten['module_event_splus_key'];
            $title =  $alte_event_daten['title'];
            $date = $alte_event_daten['date'];
    }
    else{
        $event_splus_key =  $neue_event_daten['uid'];
        $title =  $neue_event_daten['title'];
        $date = $neue_event_daten['date'];
    }


    // Speicherung der Änderung  in der DB Tabelle changes
    $DB->query("INSERT INTO `changes` ( `module_splus_id`, `event_splus_key`, `changes_reason`, `changes_reason_text`, `new_event_json`,`set_splus_key`) VALUES (?,?,?,?,?,?)", array(
        $module_id,
        $event_splus_key,
        $grund_aenderung_id,
        $grund_aenderung[0],
        json_encode($neue_event_daten),
        $set_splus_key
    ));
    #####################
    # Push Benachrichtigung Start
    #####################   

    // Alle Device Ids aus der DB holen, welche für diese Push B. relevant sind

    $deviceId_array = hol_alle_device_ids_fuer_modul($title, $set_splus_key);
    //Test
    if ($deviceId_array !== false) {
        $log = new PDOLog();
        // Sende Push zu jedem Device

        // Device ID liste bei 100 Ids teilen, um den Requests kein zu halten
        $deviceId_array_chunk = array_chunk($deviceId_array, 100);
        foreach ($deviceId_array_chunk as $deviceId_id) {

               $deviceId_id_string = implode( ",", $deviceId_id ); 
            $log->write("Push an _ {$title} - : " . $deviceId_id_string , DBName . md5(DBPassword));

            // Nur ein Änderungsgrund - wenn $grund_aenderung Anzahl = 1
            if (isset($grund_aenderung) AND is_array($grund_aenderung) AND count($grund_aenderung) == 1) {
                $msg_title = 'Änderung einer Veranstaltung';
                $msg_body = "{$title} - {$date}. Grund: {$grund_aenderung[0]}";
            }
            // Mehre Änderungsgründe
            elseif (isset($grund_aenderung) AND is_array($grund_aenderung) AND count($grund_aenderung) > 1) {
                // implode des Arrays
                $send_string  = implode(", ", $grund_aenderung);
                $msg_title = 'Änderung einer Veranstaltung';
                $msg_body = "{$title} - {$date}. Gründe: { $send_string}";
            }
            // Umbekannter Änderungsgrund
            else {
                $msg_title = 'Änderung einer Veranstaltung';
                $msg_body = "{$title} - {$date}. Grund: unbekannt ";
            }

            // Neue Nachricht erstellen
            $notification = new \Fcm\Push\Notification();
            // Titel und Body hinzufügen
            $notification->setTitle($msg_title);
            $notification->setBody($msg_body);
            // Jedes Device hinzufügen
            $notification->addRecipient($deviceId_id);
            //Nachricht senden
            $response = $firebase_client->send($notification);

            
            $log->write("Response: " . json_encode($response) , DBName . md5(DBPassword));
        }
        
    }
    return;
}

/*
#####################
Unterfunktionen
#####################
*/

// Diese Funktion holt alle Device Ids, welche ein gewisses Modul abonniert hat.
function hol_alle_device_ids_fuer_modul(String $module_title, String $set_splus_key) : array {
    // neue DB Con
    $DB               = new Db(DBHost, DBPort, DBName, DBUser, DBPassword);
    $encryption_class = new Encryption();
    // Hol alle Device IDs aus der DB, welche von der Änderung betroffen sind
    $result           = $DB->query("SELECT device.device_id,device.os_id FROM device INNER JOIN device_module ON device.id = device_module.device_id WHERE device_module.set_splus_key = ? AND LOCATE(device_module.module_event_name,?)>0", array(
        $set_splus_key,
        $module_title
    ));
    // Check, ob es erfolgreiche Treffer gibt
    // Wenn ja wird das $return_array mit den Device Ids gefüllt
    if (isset($result) AND is_array($result) AND count($result) > 0) {
        $retrun_array = array();
        foreach ($result as $num => $data) {
            $retrun_array[] = $encryption_class->decryptString($data['device_id']);
        }
        return $retrun_array;
    }
    return false;
}
