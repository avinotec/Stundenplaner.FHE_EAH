#!/bin/bash
#
# Contab Datei
# start.php muss hierbei regelmäßig ausgeführt werden
# Hierbei empfehle ich zum start erstmal jede Stunde -> jede Stunde zur Minute 0, zwischen 5 Uhr frühs und 22 Uhr Abends
#0 5-22 * * *	php {Pfad zur PHP Datei} > {Pfad zum Log} 2>&1
#
# Ersten * durch 5-0 ersetzen damit nachts keine Benachrichtigungen gesendet werden.
#
php -f /var/www/html/api/fcm_update_and_send.php 



#ausfuehrbar machen
# chmod 755 cronfcm.sh
#
#Eigentuemer www-data
# chown www-data:www-data cronfcm.sh
#
#
#crontab eintragen
# crontab -e 
#
#
# alle 5 Minuten	
# */5 * * * *
# */5 * * * * /var/www/html/api/cronfcm.sh >> /var/www/html/api/cronfcm.log 2>&1
#
#
# cronfcm.php muss hierbei regelmäßig ausgeführt werden
# Hierbei empfehle ich zum start erstmal jede Stunde -> jede Stunde zur Minute 0, zwischen 5 Uhr frühs und 22 Uhr Abends
#  0 5-22 * * *   php {Pfad zur PHP Datei} > {Pfad zum Log} 2>&1
# Ersten * durch 5-0 ersetzen damit nachts keine Benachrichtigungen gesendet werden.
#  0 5-22 * * * /var/www/html/api/cronfcm.sh &	>> /var/www/html/api/cronfcm.log 2>&1
#
#crontab -e
#
# 02 23 * * 5     nohup /home/vmadmin/backup.sh >>/home/vmadmin/backup.log 2>&1 &
#  1.	Minute		0-59
#  2.	Stunde		0-23
#  3.	Tag				1-31
#  4.	Monat			1-12 oder Jan, Feb, Mar, ..., Dec
#  5.	Wochentag	0-7 oder Mon-Sun, wobei 0=Sun, 1=Mon, ..., 6=Sat, 7=Sun
#
# Jeden Freitag (5) um 23:02 soll der Job ausgeführt werden.
#


