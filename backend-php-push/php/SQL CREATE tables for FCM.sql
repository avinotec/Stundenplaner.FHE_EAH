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


-- CREATE USER 'stundenplanuser'@'%' IDENTIFIED VIA mysql_native_password USING '***';GRANT SELECT, INSERT, UPDATE, DELETE, FILE ON--.* TO 'stundenplanuser'@'%' REQUIRE NONE WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 MAX_USER_CONNECTIONS 0;GRANT ALL PRIVILEGES ON `stundenplan`.* TO 'stundenplanuser'@'%';

-- use database
USE stundenplan;

--
-- Tabellenstruktur für Tabelle `fcm_user`
--
DROP TABLE IF EXISTS `fcm_user`;

CREATE TABLE `fcm_user`
(
    `id`               int AUTO_INCREMENT PRIMARY KEY                          NOT NULL,
    `token`            VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT 'Firebase Messaging Token',
    `eventseries_name` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT 'Subscribed event series',

    `os`               VARCHAR(7) COMMENT 'android or ios',
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
-- Tabellenstruktur für Tabelle `timetable_data`
--
DROP TABLE IF EXISTS `timetable_data`;

CREATE TABLE `timetable_data`
(
    `eventset_id`      VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci PRIMARY KEY NOT NULL,
    `eventseries_name` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci             NOT NULL COMMENT 'Event series the event set belongs to',
    `module_id`        VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci             NOT NULL COMMENT 'Module the event set belongs to',
    `md5_checksum`     VARCHAR(32) CHARACTER SET utf8 COLLATE utf8_unicode_ci COMMENT 'Checksum of the event set\'s data',
    `last_changed`     TIMESTAMP

) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;
