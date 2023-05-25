# Anmerkung Bilderlizenz:
- Toiletten-Icon und Technisches Icon bearbeitet, Original ist lizenzfrei von Pixabay

# Dateien:
- Campus Kartographie Handpläne - Skizzen von Tim Münzinger (ursprüngliche Bachelorarbeit zur Campusnavigation)
- floorplans_x.svg Vektorgrafiken aller Etagen im Gebäude x
- all_cells_grid.xlsx beinhaltet die Pläne und die Position aller Zellen für die Navigation
- grid_overlay.png - das zugrundeliegende Raster
- parse_cells.py liest die Positionen für die Navigation aus dem Ordner csv ein und generiert die entsprechenden JSON-Dateien in den Ordner json
- Ordner Icons: Icons für die Lagepläne + Icons für die Navigation (Icons.svg)

# Programme:
.svg -> erstellt mit Inkspace

# Anmerkungen für späteres Editieren:
- Export der einzelnen PNGs der Pläne aus floorplans_x.svg in die App unter app\fhemobile\src\eah\res\drawable-nodpi
	! Für die Dateinamen der Untergeschosse müssen "ug" bzw. "ug1" und "ug2" genutzt werden da "-" nach Android Konventionen nicht in Dateinamen der Ressourcen vorkommen darf. 
	! Die exportierte CSV kann mit Excel geöffnet leer aussehen -> Texteditor verwenden
- Excel-Pläne: 
	* Bilder einfügen mit Transparenz 30% (Bildformat>Transparenz in Excel)
	* Da das Bild die Zellen versperrt, sind diese nicht mehr per Klick sondern nur noch per Pfeiltasten erreichbar
	* Treppen und Aufzüge sind walkable
- Generierte JSON-Dateien müssen nach app\fhemobile\src\eah\assets kopiert werden