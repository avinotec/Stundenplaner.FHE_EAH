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
global $app;

// Alle Routen
$app->get('/', function($request, $response, $args) {
    $all_routes = array(
        "POST/changes" => array(
            "Beispiel" => '{"os_id":1,"device_id":"cc4S_VOJB0A:APA91bGLHU9a9cLLjloOLY4KJI6S4qEDx9uIBDQOS0vYaumJqL3X9IUe2krTObu57QqjKsENVBNkfWDHgDY8kiutewz63oKqqxrPtMChAcASBmo_0o5F1w_vAlAZ-l2sUyk3zBTOVxee", "refresh_timestamp": 99999,
                "module_list":[{"module_title":"Data Min./S", "set_title": "SPLUSECBB84"}, {"module_title":"Web Shop Proj./S", "set_title": "SPLUSECBB84"}]}',
            "Info" => "Gibt alle Änderungen für alle abonnierten Module zurück. Post muss genutzt werden. "
        ),
        "GET/changes" => array(
            "Beispiel" => 'changes?os_id= 1&device_id=cc4S_VOJB0A:APA91bGLHU9a9cLLjloOLY4KJI6S4qEDx9uIBDQOS0vYaumJqL3X9IUe2krTObu57QqjKsENVBNkfWDHgDY8kiutewz63oKqqxrPtMChAcASBmo_0o5F1w_vAlAZ-l2sUyk3zBTOVxee&refresh_timestamp=9999999',
            "Info" => "Gibt alle Änderungen für alle abonnierten Module zurück.GET. "
        ),
        "POST/delete_device" => array(
            "Beispiel" => '{"device_id":"dG2X-Q9nbxY:APA91bFHdBrGiQ5JUNHgVCN1TqdrAEY1EAx0rcQeWyX_WxN8JyCoUqUHfG0r13oP4kN5eJ4rbuABfhXO8q2A0qi5H1QiZ6eXJEileSUbvxI8dWX0iwhRlIBFq7ajllAM61Na_RWDQ65D"}',
            "Info" => "Löscht ein Device"
        )
    );
    return $this->response->withJson(mb_convert_encoding($all_routes, 'UTF-8', 'UTF-8'));
});
// Dieser Call gibt alle Änderung zurück, welche die abonnierten Module betreffen
$app->post('/changes', function($request, $response) {
    // Temp Var für alle validen vom Nutzer abonnierten Module
    $input    = $request->getParsedBody();
    $response = array(
        "error" => false
    );
    // Check Array
    if (!is_array($input)) {
        $response['error']      = true;
        $response['error_text'] = "no valid data";
        return $this->response->withJson($response, 400);
    }
    if (!isset($input['device_id'])) {
        $response['error']      = true;
        $response['error_text'] = "no device token";
        return $this->response->withJson($response, 400);
    }
    if (!isset($input['os_id'])) {
        $response['error']      = true;
        $response['error_text'] = "no os_id";
        return $this->response->withJson($response, 400);
    }
    if (!isset($input['module_list'])) {
        $response['error']      = true;
        $response['error_text'] = "no modul array";
        return $this->response->withJson($response, 400);
    }
    // Prüfung der time Value
    if (!isset($input['refresh_timestamp']) OR !is_numeric($input['refresh_timestamp'])) {
        $input['refresh_timestamp'] = 0;
    }
    // Crypte die Device ID
    $input['device_id'] = $this->encryption_class->encryptString($input['device_id']);
    // Gibt DB Id des Nutzers zurück
    $id                 = add_check_user($this, $input);
    // Alle eventes löschen für das device
    delete_all_events_user($this, $id);
    // Alle events wieder hinzufügen
    add_all_events_user($this, $input, $id);
    // Hol den ältesten Modul Eintrag aus der DB
    $oldest_module_event = $this->db->single("SELECT import FROM `module_events` ORDER BY `module_events`.`import` ASC LIMIT 1");
    // Alle Änderung aus der 'Changes' DB holen, für die abonnierten Module
    $sth                 = $this->db->query("SELECT changes.event_splus_key, changes.changes_reason,changes.changes_reason_text,changes.new_event_json,changes.change_date,changes.set_splus_key, module.module_splus_key FROM changes INNER JOIN (SELECT device_module.set_splus_key FROM device_module WHERE device_module.device_id = ? GROUP BY device_module.set_splus_key) device_module ON device_module.set_splus_key = changes.set_splus_key INNER JOIN (SELECT module_events.module_event_splus_key, module_events.module_id FROM module_events) module_events ON changes.event_splus_key = module_events.module_event_splus_key INNER JOIN (SELECT module.id, module.module_splus_key FROM module) module ON module_events.module_id = module.id WHERE Unix_timestamp(changes.change_date) > ? GROUP BY changes.id ORDER BY changes.change_date ASC", array(
        $id,
        $input['refresh_timestamp']
    ));
    // Check ob es Änderungen gibt
    if (is_array($sth) AND count($sth) > 0) {
        // Es gibt Ändeurngen
        // Ändere den Value new:event_json in ein array, wenn er ein json ist
        foreach ($sth as $num => $data) {
            if (!empty($data['new_event_json']) AND is_array(json_decode($data['new_event_json'], true))) {
                $sth[$num]['new_event_json'] = build_return_data(json_decode($data['new_event_json'], true));
            }
        }
        $response['counter']        = count($sth);
        $response['oldest_db_time'] = strtotime($oldest_module_event);
        $response['changes']        = $sth;
        return $this->response->withJson(mb_convert_encoding($response, 'UTF-8', 'UTF-8'), 200);
    } else {
        // Keine Änderungen
        $response['counter']        = 0;
        $response['oldest_db_time'] = strtotime($oldest_module_event);
        $response['changes']        = array();
        return $this->response->withJson($response, 200);
    }
});
// Dieser Call gibt alle Änderung zurück, welche die abonnierten Module betreffen
// Dabei wird nur die Änderungshistorie zurück gegeben
$app->get('/changes', function($request, $response, $args) {
    // Teste ob device_id verfügbar ist:
    $args = $request->getQueryParams();
    if (!isset($args['device_id']) OR empty($args['device_id'])) {
        $return['error']      = true;
        $return['error_text'] = "Keine valide device_id";
        return $this->response->withJson(mb_convert_encoding($return, 'UTF-8', 'UTF-8'), 400);
    }
    // Testen der os_id
    if (!isset($args['os_id']) OR empty($args['os_id'])) {
        $return['error']      = true;
        $return['error_text'] = "Keine valide os_id";
        return $this->response->withJson(mb_convert_encoding($return, 'UTF-8', 'UTF-8'), 400);
    }
    // Prüfung der time Value
    if (!isset($args['refresh_timestamp']) OR !is_numeric($args['refresh_timestamp'])) {
        $args['refresh_timestamp'] = 0;
    }
    // Crypte die Device ID
    $args['device_id']   = $this->encryption_class->encryptString($args['device_id']);
    // Gibt DB Id des Nutzers zurück
    $id                  = add_check_user($this, $args);
    // Hol den ältesten Modul Eintrag aus der DB
    $oldest_module_event = strtotime($this->db->single("SELECT import FROM `module_events` ORDER BY `module_events`.`import` ASC LIMIT 1"));
    // Alle Änderung aus der 'Changes' DB holen, für die abonnierten Module
    $sth                 = $this->db->query("SELECT changes.event_splus_key, changes.changes_reason,changes.changes_reason_text,changes.new_event_json,changes.change_date,changes.set_splus_key, module.module_splus_key FROM changes INNER JOIN (SELECT device_module.set_splus_key FROM device_module WHERE device_module.device_id = ? GROUP BY device_module.set_splus_key) device_module ON device_module.set_splus_key = changes.set_splus_key INNER JOIN (SELECT module_events.module_event_splus_key, module_events.module_id FROM module_events) module_events ON changes.event_splus_key = module_events.module_event_splus_key INNER JOIN (SELECT module.id, module.module_splus_key FROM module) module ON module_events.module_id = module.id WHERE Unix_timestamp(changes.change_date) > ? GROUP BY changes.id ORDER BY changes.change_date ASC", array(
        $id,
        $args['refresh_timestamp']
    ));
    // Check ob es Änderungen gibt
    if (is_array($sth) AND count($sth) > 0) {
        // Es gibt Ändeurngen
        // Ändere den Value new:event_json in ein array, wenn er ein json ist
        foreach ($sth as $num => $data) {
            if (!empty($data['new_event_json']) AND is_array(json_decode($data['new_event_json'], true))) {
                $sth[$num]['new_event_json'] = build_return_data(json_decode($data['new_event_json'], true));
            }
        }
        $return['counter']        = count($sth);
        $return['oldest_db_time'] = $oldest_module_event;
        $return['changes']        = $sth;
        return $this->response->withJson(mb_convert_encoding($return, 'UTF-8', 'UTF-8'), 200);
    } else {
        // Keine Änderungen
        $return['counter']        = 0;
        $return['oldest_db_time'] = $oldest_module_event;
        $return['changes']        = array();
        return $this->response->withJson($return, 200);
    }
});
// Kann genutzt werden, um ein Nutzer komplett aus der DB zu entfernen
$app->post('/delete_device', function($request, $response) {
    $input = $request->getParsedBody();
    // Check Array
    if (!is_array($input)) {
        $response['error']      = true;
        $response['error_text'] = "no valid data";
        return $this->response->withJson($response, 400);
    }
    // Check Device ID
    if (!isset($input['device_id'])) {
        $response['error']      = true;
        $response['error_text'] = "no device token";
        return $this->response->withJson($response, 400);
    }
    // Check os_id
    if (!isset($input['os_id'])) {
        $response['error']      = true;
        $response['error_text'] = "no os id";
        return $this->response->withJson($response, 400);
    }
    // Crypte die Device ID
    $input['device_id'] = $this->encryption_class->encryptString($input['device_id']);
    // Gibt DB Id des Nutzers zurück
    $id                 = add_check_user($this, $input);
    // Alle eventes löschen für das device
    
    delete_all_events_user($this, $id);
    // Nutzer löschen
    $this->db->query("DELETE FROM `device` WHERE `device`.`id` = ?", array(
        $id
    ));
    // Rückgabe
    $return['error'] = false;
    $return['info']  = "Nutzer erfolgreich geloescht, falls er vorhanden war.";
    return $this->response->withJson(mb_convert_encoding($return, 'UTF-8', 'UTF-8'), 200);
});
// Diese Funktion checkt, ob ein Nutzer bereits in der DB verfügbar ist.
// Wenn nicht wird er angelegt
// Rückgabe ist die DB-ID des Nutzers
function add_check_user($var, $input) {
    $sth = $var->db->row("SELECT Count(*) as count, id FROM `device` WHERE `device_id` = ?", array(
        $input['device_id']
    ));
    // var Count wird geprüft, wenn diese 0 ist, gibt es dieses Device noch nicht
    if ($sth['count'] == 0) {
        // Einfügen
        $sth = $var->db->query("INSERT INTO `device` (`device_id`, `os_id`) VALUES (?,?)", array(
            $input['device_id'],
            $input['os_id']
        ));
        $id  = $var->db->lastInsertId();
    } elseif ($sth['count'] > 0) {
        // Schon verfügbar
        $id = $sth['id'];
    }
    return $id;
}
// Löschung aller Events eines Nutzers
function delete_all_events_user($var, $id) {
    $sth = $var->db->query("DELETE FROM `device_module` WHERE device_id= ?", array(
        $id
    ));
    return true;
}
// Hinzufügung aller Events eines Users
function add_all_events_user($var, $input, $id) {
    $temp_full_title = array();
    foreach ($input['module_list'] as $num => $modul) {
        // Löschung des # vor dem set_title
        $modul['set_title'] = str_replace("#", "", $modul['set_title']) ;
        // Check ob full Title verfügbar sind und set_splus_key
        if (isset($modul['module_title']) AND !empty($modul['module_title']) AND isset($modul['set_title']) AND !empty($modul['set_title'])) {
            // Doppelte genannte Modulnamen ausschließen
            if (in_array($modul['module_title'] . $modul['set_title'], $temp_full_title)) {
                continue;
            }
            $temp_full_title[] = $modul['module_title'] . $modul['set_title'];
            // Nutzer abonniert Modul/Event
            $sth               = $var->db->query("INSERT INTO `device_module` (`device_id`, `module_event_name`, `set_splus_key`) VALUES (?,?,?)", array(
                $id,
                $modul['module_title'],
               $modul['set_title']
            ));
        } else {
            // full_title  oder setid nicht valide
            continue;
        }
    }
    return true;
}
// Erstellung eines Wochen-Tags Array nach Vorgabe der FH Erfurt API
function create_week_day_array($all_events) {
    // Create week array
    $begin             = new DateTime(date('c', $all_events[0]['startDate']));
    $end = new DateTime(date('c', end($all_events)['startDate']));
    $return_array      = array();
    $week_temp         = array();
    $week_days_array   = array();
    $week_days_array[] = array(
        "dayInWeek" => 0,
        "name" => "Sonntag",
        "events" => array()
    );
    $week_days_array[] = array(
        "dayInWeek" => 1,
        "name" => "Montag",
        "events" => array()
    );
    $week_days_array[] = array(
        "dayInWeek" => 2,
        "name" => "Dienstag",
        "events" => array()
    );
    $week_days_array[] = array(
        "dayInWeek" => 3,
        "name" => "Mittwoch",
        "events" => array()
    );
    $week_days_array[] = array(
        "dayInWeek" => 4,
        "name" => "Donnerstag",
        "events" => array()
    );
    $week_days_array[] = array(
        "dayInWeek" => 5,
        "name" => "Freitag",
        "events" => array()
    );
    $week_days_array[] = array(
        "dayInWeek" => 6,
        "name" => "Samstag",
        "events" => array()
    );
    for ($i = $begin; $i <= $end; $i->modify('+1 day')) {
        // Ist Wochenzahl schon im Temp array
        if (!in_array($i->format("W"), $week_temp)) {
            $return_array[] = array(
                "weekInYear" => $i->format("W"),
                "year" => $i->format("o"),
                "weekdays" => $week_days_array
            );
            $week_temp[]    = $i->format("W");
        }
    }
    foreach ($all_events as $num => $data) {
        $week_event = date('W', strtotime($data['date']));
        $day_event  = date('w', strtotime($data['date']));
        // Such im  $return_array und füg ein
        foreach ($return_array as $num2 => $data2) {
            if ($data2['weekInYear'] == $week_event) {
                foreach ($data2['weekdays'] as $num3 => $data3) {
                    if ($data3['dayInWeek'] == $day_event) {
                        $return_array[$num2]['weekdays'][$num3]['events'][] = $data;
                        break 2;
                    }
                }
            }
        }
    }
    return $return_array;
}
// Diese Funktion erstellt ein Rückgabe Array, welche die Rückgabe der FH Erfurt gleicht
function build_return_data($array) {
    $date = date("d.m.Y", $array['startDate']);
    $re   = array(
        "startDate" => $array['startDate'],
        "title" => $array['title'],
        "shortTitle" => $array['shortTitle'],
        "date" => $date,
        "dayOfWeek" => $array['dayOfWeek'],
        "weekOfYear" => date("W", $array['startDate']),
        "startTime" => $array['startTime'],
        "endTime" => $array['endTime'],
        "lecturer" => $array['lecturer'],
        "room" => $array['room'],
        "uid" => $array['uid']
    );
    return $re;
} 