<?php
if (PHP_SAPI == 'cli-server') {
    // To help the built-in PHP dev server, check if the request was actually for
    // something which should probably be served as a static file
    $url  = parse_url($_SERVER['REQUEST_URI']);
    $file = __DIR__ . $url['path'];
    if (is_file($file)) {
        return false;
    }
}

// Einbindung aller benötigter Klassen und Funktionen
require __DIR__ . '/../vendor/autoload.php';
require_once(dirname(__FILE__)."/../../settings/settings.php");
require_once(dirname(__FILE__)."/../../general_functions/PDO.class.php");
require_once(dirname(__FILE__)."/../../general_functions/encryption.php");

session_start();

// Instantiate the app
$settings = require __DIR__ . '/../src/settings.php';
$app = new \Slim\App($settings);

// Set up dependencies
require __DIR__ . '/../src/dependencies.php';

// Register routes
require __DIR__ . '/../src/routes.php';

// Run app
$app->run();
