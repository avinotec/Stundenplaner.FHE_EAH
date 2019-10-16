-- phpMyAdmin SQL Dump
-- version 4.9.0.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Sep 24, 2019 at 05:04 PM
-- Server version: 10.4.7-MariaDB
-- PHP Version: 7.2.5

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


--
-- Database: `stundenplan`
--

-- --------------------------------------------------------

--
-- Table structure for table `changes`
--

CREATE TABLE `changes` (
  `id` int(11) NOT NULL,
  `module_splus_id` int(11) NOT NULL,
  `event_splus_key` varchar(254) NOT NULL,
  `changes_reason` int(9) NOT NULL,
  `changes_reason_text` varchar(254) NOT NULL,
  `new_event_json` varchar(5012) DEFAULT NULL,
  `change_date` datetime NOT NULL DEFAULT current_timestamp(),
  `set_splus_key` varchar(99) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `device`
--

CREATE TABLE `device` (
  `id` int(11) NOT NULL,
  `device_id` varchar(512) NOT NULL,
  `os_id` int(11) NOT NULL COMMENT '1 = ios, 2= android',
  `import` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `device_module`
--

CREATE TABLE `device_module` (
  `id` int(11) NOT NULL,
  `device_id` int(11) NOT NULL,
  `module_event_name` varchar(254) NOT NULL,
  `import` datetime NOT NULL DEFAULT current_timestamp(),
  `set_splus_key` varchar(99) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `module`
--

CREATE TABLE `module` (
  `id` int(11) NOT NULL,
  `module_splus_key` varchar(254) NOT NULL,
  `import` datetime NOT NULL DEFAULT current_timestamp(),
  `change_date` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `module_events`
--

CREATE TABLE `module_events` (
  `id` int(11) NOT NULL,
  `module_id` int(11) NOT NULL,
  `module_event_splus_key` varchar(254) NOT NULL,
  `import` datetime NOT NULL DEFAULT current_timestamp(),
  `change_date` datetime DEFAULT NULL,
  `startDate` varchar(254) NOT NULL,
  `title` varchar(254) NOT NULL,
  `shortTitle` varchar(254) NOT NULL,
  `date` varchar(254) NOT NULL,
  `dayOfWeek` varchar(254) NOT NULL,
  `startTime` varchar(254) NOT NULL,
  `endTime` varchar(254) NOT NULL,
  `lecturer` varchar(254) NOT NULL,
  `room` varchar(254) NOT NULL,
  `set_splus_key` varchar(99) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `os`
--

CREATE TABLE `os` (
  `id` int(11) NOT NULL,
  `os_number` int(11) NOT NULL COMMENT '1= ios, 2 = android',
  `os_name` varchar(254) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `os`
--

INSERT INTO `os` (`id`, `os_number`, `os_name`) VALUES
(1, 1, 'Apple-IOS'),
(2, 2, 'Google-Android');

-- --------------------------------------------------------

--
-- Table structure for table `semester`
--

CREATE TABLE `semester` (
  `id` int(11) NOT NULL,
  `studiengang_id` int(11) NOT NULL,
  `semester_name` varchar(254) NOT NULL,
  `import` datetime NOT NULL DEFAULT current_timestamp(),
  `change_date` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `semester_set`
--

CREATE TABLE `semester_set` (
  `id` int(11) NOT NULL,
  `semester_id` int(11) NOT NULL,
  `set_name` varchar(254) NOT NULL,
  `splus_key` varchar(254) NOT NULL,
  `import` datetime NOT NULL DEFAULT current_timestamp(),
  `change_date` datetime DEFAULT NULL,
  `ical_json` mediumtext DEFAULT NULL,
  `change_date_json` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `studiengang`
--

CREATE TABLE `studiengang` (
  `id` int(11) NOT NULL,
  `studiengang_name` varchar(254) NOT NULL,
  `import` datetime NOT NULL DEFAULT current_timestamp(),
  `change_date` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `changes`
--
ALTER TABLE `changes`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `device`
--
ALTER TABLE `device`
  ADD PRIMARY KEY (`id`),
  ADD KEY `os_id` (`os_id`);

--
-- Indexes for table `device_module`
--
ALTER TABLE `device_module`
  ADD PRIMARY KEY (`id`),
  ADD KEY `device_id` (`device_id`);

--
-- Indexes for table `module`
--
ALTER TABLE `module`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `module_events`
--
ALTER TABLE `module_events`
  ADD PRIMARY KEY (`id`),
  ADD KEY `module_id` (`module_id`);

--
-- Indexes for table `os`
--
ALTER TABLE `os`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `semester`
--
ALTER TABLE `semester`
  ADD PRIMARY KEY (`id`),
  ADD KEY `studiengang_id` (`studiengang_id`);

--
-- Indexes for table `semester_set`
--
ALTER TABLE `semester_set`
  ADD PRIMARY KEY (`id`),
  ADD KEY `semester_id` (`semester_id`);

--
-- Indexes for table `studiengang`
--
ALTER TABLE `studiengang`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `changes`
--
ALTER TABLE `changes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `device`
--
ALTER TABLE `device`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `device_module`
--
ALTER TABLE `device_module`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `module`
--
ALTER TABLE `module`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `module_events`
--
ALTER TABLE `module_events`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `os`
--
ALTER TABLE `os`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `semester`
--
ALTER TABLE `semester`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `semester_set`
--
ALTER TABLE `semester_set`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `studiengang`
--
ALTER TABLE `studiengang`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `device`
--
ALTER TABLE `device`
  ADD CONSTRAINT `device_ibfk_1` FOREIGN KEY (`os_id`) REFERENCES `os` (`id`);

--
-- Constraints for table `device_module`
--
ALTER TABLE `device_module`
  ADD CONSTRAINT `device_modulee_ibfk_1` FOREIGN KEY (`device_id`) REFERENCES `device` (`id`);

--
-- Constraints for table `module_events`
--
ALTER TABLE `module_events`
  ADD CONSTRAINT `module_events_ibfk_1` FOREIGN KEY (`module_id`) REFERENCES `module` (`id`);

--
-- Constraints for table `semester`
--
ALTER TABLE `semester`
  ADD CONSTRAINT `semester_ibfk_1` FOREIGN KEY (`studiengang_id`) REFERENCES `studiengang` (`id`);

--
-- Constraints for table `semester_set`
--
ALTER TABLE `semester_set`
  ADD CONSTRAINT `semester_set_ibfk_1` FOREIGN KEY (`semester_id`) REFERENCES `semester` (`id`);
COMMIT;

