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
 * Returns the event title without the number of the timetable entry.
 * (note: event titles have no such number if entered for the first time,
 * but later added events belonging to the same event series get one)
 *
 * @param  string $event_title The title of an event
 * @return string The title of the Event Series th event belongs to
 */
function getEventSeriesName(string $event_title): string
{

	//cut away all ending numbers after the dot
	//example: WI/WIEC(BA)Mathe/Ü/01.1 -> WI/WIEC(BA)Mathe/Ü/01
	$eventseries_name = preg_replace("/\.\d+$/", "", $event_title);

	return $eventseries_name;
}

/* **** regular expressions
    https://www.php.net/manual/en/function.preg-replace

    Special Character Definitions
    \ Quote the next metacharacter
    ^ Match the beginning of the line
    . Match any character (except newline)
    $ Match the end of the line (or before newline at the end)
    | Alternation
    () Grouping
    [] Character class
    * Match 0 or more times
    + Match 1 or more times
    ? Match 1 or 0 times
    {n} Match exactly n times
    {n,} Match at least n times
    {n,m} Match at least n but not more than m times
    More Special Character Stuff
    \t tab (HT, TAB)
    \n newline (LF, NL)
    \r return (CR)
    \f form feed (FF)
    \a alarm (bell) (BEL)
    \e escape (think troff) (ESC)
    \033 octal char (think of a PDP-11)
    \x1B hex char
    \c[ control char
    \l lowercase next char (think vi)
    \u uppercase next char (think vi)
    \L lowercase till \E (think vi)
    \U uppercase till \E (think vi)
    \E end case modification (think vi)
    \Q quote (disable) pattern metacharacters till \E
    Even More Special Characters
    \w Match a "word" character (alphanumeric plus "_")
    \W Match a non-word character
    \s Match a whitespace character
    \S Match a non-whitespace character
    \d Match a digit character
    \D Match a non-digit character
    \b Match a word boundary
    \B Match a non-(word boundary)
    \A Match only at beginning of string
    \Z Match only at end of string, or before newline at the end
    \z Match only at end of string
    \G Match only where previous m//g left off (works only with /g)
*/
