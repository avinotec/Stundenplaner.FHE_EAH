-- CREATE USER 'stundenplanuser'@'%' IDENTIFIED VIA mysql_native_password USING '***';GRANT SELECT, INSERT, UPDATE, DELETE, FILE ON *.* TO 'stundenplanuser'@'%' REQUIRE NONE WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 MAX_USER_CONNECTIONS 0;GRANT ALL PRIVILEGES ON `stundenplan`.* TO 'stundenplanuser'@'%';

-- use database
USE stundenplan;

--
-- Tabellenstruktur für Tabelle `fcm_user`
--
DROP TABLE IF EXISTS `fcm_user`;

CREATE TABLE `fcm_user` (
  `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `token` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `eventset_id` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT 'zusammengehörige Menge von Veranstaltungen'

) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indizes für die Tabelle `fcm_user`
--
ALTER TABLE `fcm_user`
  ADD INDEX `token` (`token`),
  ADD INDEX `eventset_id` (`eventset_id`);


--
-- Tabellenstruktur für Tabelle `timetable_events`
--
DROP TABLE IF EXISTS `timetable_events`;

CREATE TABLE `timetable_events` (
  `title` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `eventset_id` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `startdatetime` INT UNSIGNED NOT NULL,
  `enddatetime` INT UNSIGNED NOT NULL,
  `lecturer_id` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci,
  `location_id` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci,
  `changed` BOOLEAN
  
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indizes für die Tabelle `timetable_events`
--
ALTER TABLE `timetable_events`
  ADD INDEX `title` (`title`),
  ADD INDEX `eventset_id` (`eventset_id`),
  ADD INDEX `startdatetime` (`startdatetime`),
  ADD INDEX `enddatetime` (`enddatetime`);



--
-- Tabellenstruktur für Tabelle `modules`
--
DROP TABLE IF EXISTS `modules`;

CREATE TABLE `modules` (
  `module_id` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `eventset_id` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci
  
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indizes für die Tabelle `modules`
--
ALTER TABLE `modules`
  ADD INDEX `module_id` (`module_id`);