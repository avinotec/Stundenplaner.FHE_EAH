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


// Diese Part muss zwingend zum Start des gesamten Skriptes (also beim Start von start.php) ausgeführt werden.
// Hierbei wird gepürft, ob eine DB Verbindung möglich ist.
// Es wird überprüft, ob die benötigte Datenbank vorhanden ist.
// Wenn sie nicht vorhanden ist (z.b. beim erstmaligen nutzen dieses Tools), wird die Datenbank mittels DB_Dump (src/stundenplan.sql) generiert
function check_db_con() : void {
    // Aufbau DB Con
    // Wenn die Verbindung nicht aufgebaut werden kann, wird das Skript beendet und der Fehler in den Log geschrieben
    $conn = new mysqli(DBHost, DBUser, DBPassword);
    // Check connection
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }
}

function check_db_available() : void {
    global $first_start;
    $DB     = new Db(DBHost, DBPort, "", DBUser, DBPassword);
    $log    = new PDOLog();
    // Hole alle verfuegbaren DBs
    $result = $DB->column("SHOW DATABASES");
    if (in_array(DBName, $result)) {
        // DB available 
        $first_start = 0;
    } else {
        // Benötigte DB ist nicht verfügbar
        $first_start = 1;
        // DB erstellen
        $sql         = "CREATE DATABASE IF NOT EXISTS " . DBName . " DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;";
        $DB->query($sql);
        $DB->closeConnection();
        // Nutzung von Mysqli, da PDO Probleme verursacht.
        $con      = new mysqli(DBHost, DBUser, DBPassword, DBName);
        // Temp. Var 
        $templine = '';
        // SQL Dump File laden
        $lines    = file(__DIR__ . '/stundenplan.sql');
        // Loop durch jede Zeile
        foreach ($lines as $line) {
            // Erkennung und Skip, wenn es eine Kommentier Zeile ist.
            if (substr($line, 0, 2) == '--' || $line == '')
                continue;
            // Zeile zum SQL Befehl hinzufügen
            $templine .= $line;
            // Wenn die Zeile ein Semikolon am Ende hat, ist jenes das Ende des aktuellen SQL Befehls. 
            if (substr(trim($line), -1, 1) == ';') {
                // SQL Query ausführen
                $con->query($templine) or print('Error performing query \'<strong>' . $templine . '\': ' . $con->error() . '<br /><br />');
                // Temp Veriable leeren
                $templine = '';
            }
        }
        $log->write("Tables imported successfully", DBName . md5(DBPassword));
        $con->close();
    }

}
