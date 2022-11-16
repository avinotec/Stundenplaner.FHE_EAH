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



// Diese kleine Funktion gibt die DB Zeit zurück.
// Wird benötigt, um zu überprüfen, welche Studiengänge, Semester, Sets, Module und EVents nicht mehr verfügbar sind.
function get_db_time() : String {
    // Aufbau DB Con
    $DB     = new Db(DBHost, DBPort, DBName, DBUser, DBPassword);
    // SQL zur Db Time
    $result = $DB->single("SELECT NOW()");
    $DB->closeConnection();
    // Wenn true dann return DB Time
    if ($result) {
        return $result;
    }
    // Log + Exit wenn kein Timestamp aus der DB kam.
    else {
        $log = new PDOLog();
        $log->write("Keine Result für die Function: get_db_time ", DBName . md5(DBPassword));
        exit;
    }
}
