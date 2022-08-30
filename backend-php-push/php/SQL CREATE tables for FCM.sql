-- CREATE USER 'stundenplanuser'@'%' IDENTIFIED VIA mysql_native_password USING '***';GRANT SELECT, INSERT, UPDATE, DELETE, FILE ON *.* TO 'stundenplanuser'@'%' REQUIRE NONE WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 MAX_USER_CONNECTIONS 0;GRANT ALL PRIVILEGES ON `stundenplan`.* TO 'stundenplanuser'@'%';

-- use database
USE stundenplan;

--
-- Tabellenstruktur für Tabelle `fcm_user`
--
DROP TABLE IF EXISTS `fcm_user`;

CREATE TABLE `fcm_user` (
  `id` int AUTO_INCREMENT PRIMARY KEY NOT NULL ,
  `token` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT 'Firebase Messaging Token',
  `eventseries_name` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT 'Event series the event set belongs to',
  `eventset_id` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL

) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indizes für die Tabelle `fcm_user`
--
ALTER TABLE `fcm_user`
  ADD INDEX `token` (`token`),
  ADD INDEX `eventset_id` (`eventset_id`);


--
-- Tabellenstruktur für Tabelle `timetable_data`
--
DROP TABLE IF EXISTS `timetable_data`;

CREATE TABLE `timetable_data` (
  `eventset_id` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci PRIMARY KEY NOT NULL,
  `eventseries_name` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT 'Event series the event set belongs to',
  `module_id` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT 'Module the event set belongs to',
  `md5_checksum` VARCHAR(32) CHARACTER SET utf8 COLLATE utf8_unicode_ci COMMENT 'Checksum of the event set\'s data',
  `last_changed` TIMESTAMP
  
) ENGINE=InnoDB DEFAULT CHARSET=latin1;