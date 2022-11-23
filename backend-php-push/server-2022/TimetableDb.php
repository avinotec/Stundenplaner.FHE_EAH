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

const HTML_BR = '<br />';

require_once 'config.php';
require_once 'define.php';

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//global variables

/** @var TimetableDb $db_timetable database connection */
global $db_timetable;
$db_timetable = null;


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// debug functionality

global $debug;
if (!isset($GLOBALS['debug'])) {
    $GLOBALS['debug'] = false;
}
if (!isset($debug)) {
    $debug = false;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * DB-Connection
 */
function initDbConnection(): void
{
    global $debug;
    /** @var object $db_timetable Datenbankinstanz */
    global $db_timetable;
    global $_config;

    /** @var TimetableDb|null $db_timetable gets initialized or, if already initialized, this calls the destructor implicitly. */
    $db_timetable = null;

    // Wenn die Datenbank noch nicht weggeflogen ist, dann nicht erneut instanziieren
    try {
        $db_timetable = new TimetableDb($_config['dbhost'], $_config['dbuser'], $_config['dbpassword'], $_config['dbname']);
    } catch (Exception $e) {
        echo '<h1>Wartungsarbeiten</h1>
				<p>
					Sehr geehrte Anwender.
					Wir fuehren momentan wichtige Wartungsarbeiten durch.
				</p>
				<p>
					Ihre<br />
					Administratoren
				</p> <!-- Database in maintenance mode. -->' . PHP_EOL;
        if ($debug) {
            echo 'DEBUG: DBInitConnection: Exception abgefangen: ' . $e->getMessage() . HTML_BR . PHP_EOL;
            echo 'DEBUG: DBInitConnection: Stack trace:' . $e->getTraceAsString() . HTML_BR . PHP_EOL;
        }
        die();
    }
}

/**
 * @return void
 */
function closeDbConnection(): void
{
    global $db_timetable;

    $db_timetable = null;
}


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
final class TimetableDb
{
    /** @var mysqli|null mysqli */
    private ?mysqli $mysqli = null;

    /**
     * @param string $dbhost
     * @param string $dbuser
     * @param string $dbpassword
     * @param string $dbname
     * @throws Exception
     */
    public function __construct(string $dbhost, string $dbuser, string $dbpassword, string $dbname)
    {
        global $debug;

        /*
            MySQLi Now supports persistent connections, by prepending the hostname with 'p:'

            The hostname "localhost" has a special meaning. It is bound to the use of Unix domain sockets.
            It is not possible to open a TCP/IP connection using the hostname localhost you must use 127.0.0.1 instead.
        */
        if ($debug) {
            echo 'DEBUG: MySQLi: constructor called: dbHost=' . $dbhost . '; dbUser=' . $dbuser . '; dbPass=' . /* $dbpass */ '***' . '; dbName=' . $dbname . HTML_BR . PHP_EOL;
        }

        try {
            $this->mysqli = new mysqli($dbhost, $dbuser, $dbpassword, $dbname);
            if (mysqli_connect_errno()) {
                throw new Exception();
            }
        } catch (Exception $e) {
            if ($debug) {
                echo 'DEBUG: MySQLi: error database "' . $dbhost . '": ' . mysqli_connect_error() . HTML_BR . PHP_EOL;
                echo 'DEBUG: MySQLi: Exception abgefangen: ' . $e->getMessage() . HTML_BR . PHP_EOL;
                echo 'DEBUG: MySQLi: Stack trace:' . $e->getTraceAsString() . HTML_BR . PHP_EOL;
            }

            throw $e;
            // die();
        }

        //Am Ende der Verarbeitung muss die Klasse geloescht werden.
        //$this->mysqli->close();
        // -> Siehe __destruct()
        //$this->mysqli->dump_debug_info();

        /* ****************************************************************************************************** */
        /* Tracing SQL */
        /*
            "No index used in query/prepared statement"

            Description:
            ------------
            Mysqli extension throws too many warnings. For example, "SELECT * FROM table" results in a warning:
            "Warning: mysqli::query(): No index used in query/prepared statement SELECT * FROM table ..." There's no way to
            have MySQL use an index when selecting all records, so this warning appears every time one wants to select all records
            from any table. Sometimes Mysqli gives this warning even when MySQL is apparently using an index (i.e. the same query,
            executed with EXPLAIN, says it's using an index).

            Mysqli should give warnings only on real problems. Not using an index is at most worthy of a notice, not a warning,
            especially when there's no way to use an index with a particular query!

            https://bugs.php.net/bug.php?id=35450&edit=1
        */

        /* alternativ anstelle mysqli_report():
            activate reporting
            $driver = new mysqli_driver();
            $driver->report_mode = MYSQLI_REPORT_ALL;
        */

        // turn on tracing for SQL
        if ($debug) {
            ini_set('mysql.trace_mode', 'true');
            ini_set('mysqli.trace_mode', 'true');
            /* activate reporting */
            /* throws an exception */
            mysqli_report(MYSQLI_REPORT_ALL);

            // mysqli_report(MYSQLI_REPORT_ALL ^ MYSQLI_REPORT_STRICT ); /* prints a warning, funktioniert hier aber nicht */
        } else {
            mysqli_report(MYSQLI_REPORT_OFF);
        }
    }

    /**
     *
     */
    public function __destruct()
    {
        /** @var bool $debug */
        global $debug;
        if ($debug) {
            echo 'DEBUG: MySQLi: destructor called.<br />' . PHP_EOL;
        }

        if ($this->mysqli) {
            $this->mysqli->close();
        }
        $this->mysqli = null;
    }

    /* ******************************************************************************************** */


    /* ******************************************************************************************** */

    /**
     * Insert a user into fcm_user with the given values
     * @param string $fcm_token
     * @param string $subscribed_eventseries
     * @param string $os
     * @param string $language
     * @return bool true success, false failure
     */
    final public function insertUser(string &$fcm_token,
                                     string &$subscribed_eventseries,
                                     string &$os,
                                     string &$language): bool
    {
        $subscribed_eventseries = $this->mysqli->real_escape_string($subscribed_eventseries);

        /** @var String $sql */
        $sql =
            /** @lang MySQL */
            'INSERT INTO fcm_user (token, eventseries_name, os, language) ' .
            "VALUES ('$fcm_token', '$subscribed_eventseries', '$os', '$language')";

        /** @var bool $result */
        $result = $this->runQuery($sql, "insertUser");

        return $result;
    }

    /**
     * Delete user with the given fcmToken and os
     * @param string $fcm_token
     * @return bool true success, false failure
     */
    final public function deleteUser(string &$fcm_token): bool
    {
        $sql = /** @lang MySQL */
            "DELETE FROM fcm_user WHERE token = '$fcm_token'";

        /** @var bool $result */
        $result = $this->runQuery($sql, "deleteUser");
        return $result;
    }

    /**
     * Get all users subscribing the given event series as sql result
     * @param string $eventseries_name The event series
     * @return mysqli_result|null The sql result of the query
     */
    final public function getSubscribingUsers(string &$eventseries_name): ?mysqli_result
    {
        $sql =
            /** @lang MySQL */
            "SELECT token, language, os FROM fcm_user WHERE eventseries_name = '$eventseries_name'";
        /** @mysqli_result | null $result */
        $result = $this->runQueryAndGetResult($sql, "getSubscribingUsers");
        return $result;
    }

    /**
     * Get all users subscribing an event series of the given module as sql result
     * @param string $module_id The module ID
     * @return mysqli_result|null The sql result of the query, array von token, language, os
     */
    final public function getUserSubscribingAnythingInModule(string $module_id): ?mysqli_result
    {
        $sql =
            /** @lang MySQL */
            'SELECT token, language, os '
            . 'FROM fcm_user join event_sets on eventseries_name = event_sets.eventseries '
            . "WHERE event_sets.module_id = '$module_id'";
        /** @var mysqli_result|null $result */
        $result = $this->runQueryAndGetResult($sql, "getUserSubscribingAnythingInModule");
        return $result;
    }

    /**
     * Update the stored json of the corresponding event set
     * @param string $eventset_id
     * @param string $new_eventset_data The json string containing the updated event set data
     * @return bool true success, false failure
     */
    final public function updateEventSet(string &$eventset_id, string &$new_eventset_data): bool
    {
        $sql = /** @lang MySQL */
            "UPDATE event_sets SET eventset_data = '$new_eventset_data'" .
            "WHERE eventset_id = '$eventset_id'";
        /** @var bool $result */
        $result = $this->runQuery($sql, "updateEventSet");
        return $result;
    }

    /**
     * Add event set to database
     * @param string $eventset_id The id of the event set
     * @param string $eventseries_name The name of the event series the event set belongs to
     * @param string $module_id The id of the module the event set belongs to
     * @param string $eventset_json Data of the event set as json string
     * @return bool true success, false failure
     */
    final public function insertEventSet(string &$eventset_id,
                                         string &$eventseries_name,
                                         string &$module_id,
                                         string &$eventset_json): bool
    {
        $sql = /** @lang MySQL */
            'INSERT INTO event_sets (eventset_id, eventseries, module_id, eventset_data, last_changed)' .
            "VALUES ('$eventset_id', '$eventseries_name', '$module_id', '$eventset_json', SYSDATE())";
        /** @var bool $result */
        $result = $this->runQuery($sql, "insertEventSet");
        return $result;
    }


    /**
     * Delete event set with the corresponding event set id from the database
     * @param string $eventset_id
     * @return bool true success, false failure
     */
    final public function deleteEventSet(string &$eventset_id): bool
    {
        $sql = /** @lang MySQL */
            "DELETE FROM event_sets WHERE eventset_id = '$eventset_id'";
        /** @var bool $result */
        $result = $this->runQuery($sql, "deleteEventSet");
        return $result;
    }


    /**
     * Get event sets belonging to the given module id
     * @param string $module_id
     * @return array The list of queried event set ids
     */
    final public function getEventSetIds(string &$module_id): array
    {
        $sql = /** @lang MySQL */
            "SELECT eventset_id FROM event_sets WHERE module_id = '$module_id'";
        $result = $this->runQueryAndGetResult($sql, "getEventSetIds");

        /** @var array eventset_ids */
        $eventset_ids = array();
        if (!is_null($result) && $result->num_rows > 0) {
            $eventset_ids[] = $result->fetch_assoc()["eventset_id"];
        }
        return $eventset_ids;
    }

    /**
     * Get the event set of the corresponding event set id
     * @param string $eventset_id
     * @return array The array containing the queried event set
     */
    final public function getEventSet(string &$eventset_id): array
    {
        $sql = /** @lang MySQL */
            "SELECT * FROM event_sets WHERE eventset_id = '$eventset_id'";
        $result = $this->runQueryAndGetResult($sql, "getEventSet");
        if (!is_null($result)) {
            return $result->fetch_all(MYSQLI_ASSOC);
        } else {
            return array();
        }
    }

    /**
     * Add an entry to table `notifications`
     * @param string $fcm_token
     * @param string $subject
     * @param string $type
     * @return bool
     */
    final public function insertNotification(string &$fcm_token,
                                             string &$subject,
                                             string $type): bool
    {
        $status = STATUS_OPEN;
        $sql = /** @lang MySQL */
            'INSERT INTO notifications (token, subject, type, status, timestamp)' .
            "VALUES ('$fcm_token', '$subject', '$type', '$status', SYSDATE())" .
            'ON DUPLICATE KEY UPDATE timestamp = SYSDATE()';
        /** @var bool $result */
        $result = $this->runQuery($sql, "addNotification");
        return $result;

    }

    /**
     * Run a sql query and get the result
     * @param string $sql The query
     * @param string $function_name caller of this funktion (debugging), constant string, so no reference
     * @return mysqli_result|null The sql result when succeeded, null if an error occurred
     */
    private function runQueryAndGetResult(string &$sql, string /* & */ $function_name): ?mysqli_result
    {
        global $debug;

        $result = null;
        try {
            $result = $this->mysqli->query($sql);

            // in case the query was not successful
            if ($result === false) {
                return null; //otherwise false will be returned as result
            }

        } catch (Exception $e) {
            if ($debug) {
                echo 'DEBUG: ' . $function_name . ': Exception abgefangen: ' . $e->getMessage() . HTML_BR . PHP_EOL;
                echo 'DEBUG: ' . $function_name . ': Stack trace:' . $e->getTraceAsString() . HTML_BR . PHP_EOL;
            }
            //return null; //redundant
        }

        return $result;
    }

    /**
     * Run the given sql statement
     * @param string $sql The sql statement
     * @param string $functionNameForDebuggingCallstack The name of the function runQuery gets called within (for debugging output)
     *                    nicht als Referenz, da feste Strings übergeben werden
     * @return bool true success, false failure
     */
    private function runQuery(string &$sql, string /* & */ $functionNameForDebuggingCallstack): bool
    {
        global $debug;

        try {
            $this->mysqli->query($sql);

        } catch (Exception $e) {
            if ($debug) {
                echo 'DEBUG: ' . $functionNameForDebuggingCallstack . ': Exception abgefangen: ' . $e->getMessage() . HTML_BR . PHP_EOL;
                echo 'DEBUG: ' . $functionNameForDebuggingCallstack . ': Stack trace:' . $e->getTraceAsString() . HTML_BR . PHP_EOL;
                return false; //error
            }
        }

        return true; //success
    }


    /** nur fuer DEBUGSQL
     * @return array | void
     */
    public function debugDisplayAllEventSets(): array
    {
        if ($GLOBALS['DEBUGDATABASE']) {

            $sql = 'SELECT * FROM event_sets WHERE 1 = 1';
            $result = $this->runQueryAndGetResult($sql, 'debugDisplayAllEventSets');
            if (!is_null($result)) {
                return $result->fetch_all(MYSQLI_ASSOC);
            }
        }
        return array();
    }


    /** nur fuer DEBUGSQL
     * @return array | void
     */
    public function debugDisplayAllFcmUser(): array
    {
        if ($GLOBALS['DEBUGDATABASE']) {

            $sql = 'SELECT * FROM fcm_user WHERE 1 = 1';
            $result = $this->runQueryAndGetResult($sql, 'debugDisplayAllFcmUser');
            if (!is_null($result)) {
                return $result->fetch_all(MYSQLI_ASSOC);
            }
        }
        return array();
    }


}


// end class

// do it, finally connect the database
initDbConnection();
