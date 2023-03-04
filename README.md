# EAH Jena Stundenplan-App
Stundenplan-App der Ernst-Abbe-Hochschule Jena für Android. Entwickelt vom Fachbereich WI (Wirtschaftsingenieurwesen).


## Features
in der aktuellen Release-Version implementiert:
- Stundenplan
- Individueller Stundenplaner
- Mensa
- Campus Lagepläne
- Campus Navigation
- News
- Veranstaltungskalender
- Semestertermine
- Jobangebote
- Impressum

Sprachen in der App: Deutsch, Englisch

## Beschreibung der Features, aktueller Stand, Hinweise
#### Stundenplan
  - Abruf des allgemeinen Stundenplans für ein spezifisches Semester in einem Studiengang

- https://stundenplanung.eah-jena.de/api/mobileapp/v1/activity/{id}
- https://stundenplanung.eah-jena.de/api/mobileapp/v1/module
- https://stundenplanung.eah-jena.de/api/mobileapp/v1/module/{id}
- https://stundenplanung.eah-jena.de/api/mobileapp/v1/pos/{id}
- https://stundenplanung.eah-jena.de/api/mobileapp/v1/studentset[/list]			TimeTable
- https://stundenplanung.eah-jena.de/api/mobileapp/v1/studentset/{id}[/detail]	TimeTableEvents


#### Individueller Stundenplaner
  - Zusammenstellung eines Stundenplans durch individuelle Auswahl von Kursen
  - *Status:*
    - *Updates veröffentlicht am 23.09.2022*
    - *Update Januar 2023: Push-Benachrichtigungen und Abruf von Stundenplanänderungen*
#### Mensa
  - Menüs der Jenaer Mensen und zugehörige Studentenpreise
  - *Status: Updates veröffentlicht am 23.09.2022*
#### Campus Lagepläne
  - Etagenpläne der Campusgebäude sowie Campusübersicht
  - *Status: Pläne Ende 2021 überarbeitet*
#### Campus Navigation
  - Routenberechnung von einem Startraum zu einem Zielraum oder Büro einer Person
  - *Status: Feature veröffentlicht am 25.05.2022*
#### News
  - aktuelle Pressemitteilungen der Hochschule
  - *Status: vorübergehende Lösung als WebView, da vorherige Implementierung zur Zeit nicht anwendbar*
#### Veranstaltungskalender
  - Veranstaltungen der Hochschule
  - *Status: initiale Lösung als WebView*
#### Semestertermine
  - Vorlesungszeiten, Prüfungszeitraum, Bewerbungsfristen, ...
  - Hinweis: manuelle Einspeisung der Daten
#### Jobangebote
  - [Stellenticket der EAH](https://stellenticket.eah-jena.de/de/offers/fulltextsearch/EAH-Jena/#Inhalt): Stellenausschreibungen für AkademikerInnen und Studierende
  - *Status: Feature veröffentlicht im Frühling 2022* 
#### Impressum
  - Datenschutz-Verlinkung und Impressum der Hochschul-Homepage

## Lizenz
GPL-3.0
