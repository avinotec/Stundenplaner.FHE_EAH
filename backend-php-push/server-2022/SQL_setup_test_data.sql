
-- NOTE: values are composed for WS 22/23

-- use database
USE `stundenplan`;


-- ------------------------------------------------- SET UP SCENARIO 1 -------------------------------------------------

-- Insert subscription with my test phone's firebase token
-- to test getting notifications
INSERT INTO `fcm_user`(`token`, `eventseries_name`, `os`, `language`)
VALUES ('cjvV3gR0S8CrHdGeiit35A:APA91bG2TVxTW-HTh8IL5_EKCpodm0PhlBQR98I-NaeYqgnB-LoJUj1SKsIjsumywPcEJXzsVUlus5Lx6wz4Bi8k0eQ7KoeP_Plsl8I_AmgdrVgaFuzPYTZYCz_fUINh-B96HSTqQRRE',
        'BT/MT/WT(BA)Biomat./V/01','0','DE');

-- Insert an event set belonging to the test subscription,
-- the entry must be outdated to force a timetable change being detected
INSERT INTO `event_sets`(`eventset_id`, `eventseries`, `module_id`, `eventset_data`)
VALUES ('B7D7E57FE89C0A1FEDE9F9BA762D67F8','BT/MT/WT(BA)Biomat./V/01','0B0EA277617D8B137013B76C0C187E99','{}')
ON DUPLICATE KEY UPDATE eventset_data = '{}';

-- Insert the test subscription with another token
-- to test sending notifications to multiple devices
INSERT INTO `fcm_user`(`token`, `eventseries_name`, `os`, `language`)
VALUES ('token0815','BT/MT/WT(BA)Biomat./V/01','0','DE');


-- ------------------------------------------------- SET UP SCENARIO 2 -------------------------------------------------
-- Test sending notifications for subscriptions made via app

-- First, subscribe an event series using the app on the test device
-- Then, insert/change an event set of the event series to force a timetable change being detected
INSERT INTO `event_sets`(`eventset_id`, `eventseries`, `module_id`, `eventset_data`)
VALUES ('B7D7E57FE89C0A1FEDE9F9BA762D67F8','BT/MT/WT(BA)Biomat./V/01','0B0EA277617D8B137013B76C0C187E99','{}')
ON DUPLICATE KEY UPDATE eventset_data = '{}';


-- ------------------------------------------------- SET UP SCENARIO 3 -------------------------------------------------
-- Test detecting and sending notifications for added exams

-- Insert subscription of another event set of the module with my test phone's firebase token
-- to simulate a freshly added exam when fetching module
DELETE FROM `event_sets`
WHERE eventset_id = '5611A5486E2170EF752657BAC054350E';

-- Insert/update exam (APL Personalmanagement) to force a "Exam added" being detected
INSERT INTO `fcm_user`(`token`, `eventseries_name`, `os`, `language`)
VALUES ('cjvV3gR0S8CrHdGeiit35A:APA91bG2TVxTW-HTh8IL5_EKCpodm0PhlBQR98I-NaeYqgnB-LoJUj1SKsIjsumywPcEJXzsVUlus5Lx6wz4Bi8k0eQ7KoeP_Plsl8I_AmgdrVgaFuzPYTZYCz_fUINh-B96HSTqQRRE',
        'WI(BA)Perso.-Mgm./S/01','0','DE');
