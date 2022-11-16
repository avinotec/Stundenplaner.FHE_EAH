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



// Holt alle SPlus Keys je Semesterset aus der Datenbank
// Die Kalenderdatei (.ics) wird mittels des SPlus Keys geladen
// Diese Kalenderdatei wird dann mithilfe der 'new Ical' Funktion in ein verständliches Array umgewandelt
// Fügt den Stundenplan als Json Objekt in die DB ein
function get_all_stundenplane_je_semester_set($link) {
    $DB     = new Db(DBHost, DBPort, DBName, DBUser, DBPassword);
    $result = $DB->query("SELECT * FROM `semester_set`");
    //Foreach Loop um für jedes Semester Set den Stundenplan zu holen und ihn in die DB zu schreiben
    if (isset($result) AND is_array($result)) {
        foreach ($result as $num => $data) {
            //Check and Get Splus_key
            if (isset($data['splus_key'])) {
                $file = file_contents_exist($link . "/ical/studentset/{$data['splus_key']}.ics");
                // Wenn die .ics Datei geholt werden konnte
                if ($file) {
                    $iCal   = new iCal($file);
                    $events = $iCal->eventsByDate();
                }
                // Fuege den kompletten Stundenplan als vorformatierte Json in die Datenbank
                if ($file AND $events) {
                    $DB->query("UPDATE `semester_set` SET `change_date_json` = CURRENT_TIMESTAMP, `change_date` = CURRENT_TIMESTAMP, `ical_json` = ? WHERE `semester_set`.`id` = ?", array(
                        json_encode($events),
                        $data['id']
                    ));
                }
                // Keine Events vorhanden, vllt kein richtiges Set oder Fehler beim holen der ical Datei.
                else {
                    $DB->query("UPDATE `semester_set` SET `change_date_json` = CURRENT_TIMESTAMP, `change_date` = CURRENT_TIMESTAMP, `ical_json` = ? WHERE `semester_set`.`id` = ?", array(
                        "",
                        $data['id']
                    ));
                    $log = new PDOLog();
                   // $log->write("Keine Events für: {$data['id']}{$data['splus_key']}", DBName . md5(DBPassword));
                }
            }
        }
    }
}
