--
--  Copyright (c) 2014-2022 Ernst-Abbe-Hochschule Jena
--  This program is free software: you can redistribute it and/or modify
--  it under the terms of the GNU General Public License as published by
--  the Free Software Foundation, either version 3 of the License, or
--  (at your option) any later version.
--
--  This program is distributed in the hope that it will be useful,
--  but WITHOUT ANY WARRANTY; without even the implied warranty of
--  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
--  GNU General Public License for more details.
--
--  You should have received a copy of the GNU General Public License
--  along with this program.  If not, see <http://www.gnu.org/licenses/>.
--
--

-- Host: localhost
-- Erstellungszeit: 25. Okt 2022 um 21:40
-- Server-Version: 10.5.15-MariaDB-0+deb11u1
-- PHP-Version: 8.1.11


--
-- Datenbank: `stundenplan`
--
CREATE DATABASE IF NOT EXISTS `stundenplan` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
-- use database
USE `stundenplan`;

-- --------------------------------------------------------

SET FOREIGN_KEY_CHECKS = 0;
SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS = @@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION = @@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

-- CREATE USER 'stundenplanuser'@'%' IDENTIFIED VIA mysql_native_password USING '***';GRANT SELECT, INSERT, UPDATE, DELETE, FILE ON--.* TO 'stundenplanuser'@'%' REQUIRE NONE WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 MAX_USER_CONNECTIONS 0;GRANT ALL PRIVILEGES ON `stundenplan`.* TO 'stundenplanuser'@'%';
CREATE USER 'stundenplanuser'@'%' IDENTIFIED BY 'stundenplan2022';
GRANT SELECT, INSERT, UPDATE, DELETE, FILE ON *.* TO 'stundenplanuser'@'%' REQUIRE NONE WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 MAX_USER_CONNECTIONS 0;
GRANT ALL PRIVILEGES ON `stundenplan`.* TO 'stundenplanuser'@'%';

--
-- Tabellenstruktur für Tabelle `fcm_user`
--
DROP TABLE IF EXISTS `fcm_user`;

CREATE TABLE IF NOT EXISTS `fcm_user`
(
    `id`               int(11)                                                 NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `token`            VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT 'Firebase Messaging Token',
    `eventseries_name` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT 'Subscribed event series',
    `os`               VARCHAR(7) DEFAULT NULL COMMENT 'android or ios',
    `language`         VARCHAR(2) COMMENT 'DE or EN'

) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

--
-- Indizes für die Tabelle `fcm_user`
--
ALTER TABLE `fcm_user`
    ADD INDEX `token` (`token`),
    ADD INDEX `eventseries_name` (`eventseries_name`);

--
-- TRUNCATE Tabelle vor dem Einfügen `fcm_user`
--

TRUNCATE TABLE `fcm_user`;
SET FOREIGN_KEY_CHECKS = 1;
-- ----------------------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `event_sets`
-- (beinhaltet die Event Sets aller Stundenpläne)
--
DROP TABLE IF EXISTS `event_sets`;

CREATE TABLE IF NOT EXISTS `event_sets`
(

    `eventset_id`   VARCHAR(40) CHARACTER SET utf8 COLLATE utf8_unicode_ci  NOT NULL PRIMARY KEY COMMENT 'in EAH API called activity_id',
    `eventseries`   VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL
        COMMENT 'Name of the event series the event set belongs to, e.g. BT(BA)Mathe I/V/01',
    `module_id`     VARCHAR(40) CHARACTER SET utf8 COLLATE utf8_unicode_ci  NOT NULL COMMENT 'Module the event set belongs to',
    `eventset_data` TEXT CHARACTER SET utf8 COLLATE utf8_unicode_ci                  DEFAULT NULL COMMENT 'Data of the event series as json string',
    `last_changed`  TIMESTAMP                                               NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()

) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;


ALTER TABLE `event_sets`
    ADD INDEX `eventset_id` (`eventset_id`),
    ADD INDEX `eventseries` (`eventseries`),
    ADD INDEX `module_id` (`module_id`);

--
-- TRUNCATE Tabelle vor dem Einfügen `event_sets`
--

TRUNCATE TABLE `event_sets`;
-- ----------------------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `notifications`
-- (sammelt die Benachrichtigungen, die versendet werden sollen)
--
DROP TABLE IF EXISTS `notifications`;

CREATE TABLE IF NOT EXISTS `notifications`
(
    `token`     VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT 'Firebase Messaging Token',
    `subject`   VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT 'Name of module or event series',
    `type`      VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '"Event added" or "Timetable changed"',
    `status`    VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT 'open, sent or failed',
    `timestamp` TIMESTAMP DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    PRIMARY KEY (`token`, `subject`, `type`)

) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

ALTER TABLE `notifications`
    ADD INDEX `timestamp` (`timestamp`);

--
-- TRUNCATE Tabelle vor dem Einfügen `notifications`
--

TRUNCATE TABLE `notifications`;
-- --------------------------------------------------------

COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS = @OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION = @OLD_COLLATION_CONNECTION */;
