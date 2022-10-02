<?php
/****************************************************************************
 *  Copyright (c) 2014-2022 Ernst-Abbe-Hochschule Jena
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *****************************************************************************/

declare(strict_types=1);

/**
 * Returns the event title without the number of the time table entry.
 * (note: event titles have no such number if entered for the first time,
 * but later added events belonging to the same event series get one)
 *
 * @param  string $event_title The title of an event
 * @return string The title of the Event Series th event belongs to
 */
function getEventSeriesName(string $event_title): string {

    //cut away all ending numbers after the dot
    //example: WI/WIEC(BA)Mathe/Ü/01.1 -> WI/WIEC(BA)Mathe/Ü/01
    $eventseries_name = preg_replace("/\.\d+$/", "", $event_title);

	return $eventseries_name;
}
