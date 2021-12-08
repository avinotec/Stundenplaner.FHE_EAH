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


// Dies ist eine Abwandlung des einfachen  "file_get_contents".
// Hierbei wird vorher geprüft ob der Server ein Status Code 200 zurück gibt, für die angefragte URL.
// Wenn ja: Rückgabe des file_get_contents
// Wenn nein: Rückgabe = FALSE
function file_contents_exist(String $url, ?int $response_code = 200) : String {
    $headers = get_headers($url);
    if (substr($headers[0], 9, 3) == $response_code) {
        return file_get_contents($url);
    }
    throw ( FileNotFoundException );
}
