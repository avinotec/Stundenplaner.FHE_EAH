# Stundenplan Service

Dieser PHP Service wurde erstellt, um Push Benachrichtigungen für die EAH Jena App zu ermöglichen.

## Introduction

Dieses Servies Tool besteht aus folgenden zwei Komponenten:
 - Builder (builder)
    - Dieses Skript sammelt alle verfügbaren Studiengänge, Semester der Studiengänge und die jeweiligen Sets der Semester
    - Dieses erfolgt durch das parsen einer OPML Datei : http://stundenplanung.eah-jena.de/eahapp.opml
    - Darüber hinaus werden alle verfügbaren Informationen jedes Moduls und jedes Events eines Moduls gesammelt
        - Dies erfolgt mit durch auslesen der jeweiligen .ical Kalenderdatein (jedes Set eine Kalenderdatei). Die Links zu den jeweiligen Datein stammen aus der OPML Datei .
    - Alle Studiengänge, Semster, Sets, Module und Events werden einmal je Kombination aus SPlusKey und Set SPlus Key in der Datenbank gespeichert.
    - Mit jedem Durchlauf des Skriptes wird der vorhandene Datensatz mit dem neuen Datensatz abgeglichen, um Änderung an Studiengängen, Semestern, Sets, Modulen oder Events (Änderung, Hinzufügung, Entfernung) zu ermitteln. Alle Änderungen werden dann in die Datenbank eingepflegt.
    - Wenn Änderungen an Modulen oder Events von Modulen erkannt werden, werden alle Nutzer, welche dieses Modul abonniert haben, via Push Benachrichtigung über diese Änderung informiert. 
 - Rest Service (rest_api)
    - Dies ist ein PHP Rest Service, welcher das Framework 'Slim' nutz.
    - Hiermit wird ermöglicht, dass Nutzer Module (Events von Module) abonnieren können, um über Änderungen via Push Benachrichtigung informiert zu werden.
	- Weitere sinnvolle Abfragen werden bereitgestellt.
    - In der Datei "routes.php" werden alle Routen der Schnittstelle bereitgestellt.

## Code Samples

Code-Beschreibungen finden Sie in den weiteren READMEs oder Dokumentation

## Installation
1. Projekt auf einen Webserver bringen, welcher die Requirement einhält 
2. Alle Variablen aus /settings/settings.php bestimmten
3. build/start.php ausführen
    - Bei der Ausführung von 'start.php' wird überprüft ob eine DB Verbindung möglich ist, und ob die benötigte Datenbank schon vorhanden ist, sonst wird sie automatisch via SQL Dump File angelegt.
4. Der Service kann dann mittels allen Verfügbaren Routen (rest_api/routes.php) abgefragt werden.
    - Darüber hinaus ist es möglich das Nutzer Module abonnieren können
5. Cronjob anlegen, welche regelmäßig die builder/start.php Datei ausführt  -> Hierfür kann die crontab.txt genutzt werden.

## Requirement

Benötigte Systeme:

- aktueller Apache Server
- PHP min. Version 7.3
- aktuelle Mysql/MariaDB Datenbank min 10.4.7 
- Cronjob für start.php
