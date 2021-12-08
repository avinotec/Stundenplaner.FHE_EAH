<?php

global $app;

// DIC configuration
$container = $app->getContainer();


// monolog
$container['logger'] = function ($c) {
    $settings = $c->get('settings')['logger'];
    $logger = new Monolog\Logger($settings['name']);
    $logger->pushProcessor(new Monolog\Processor\UidProcessor());
    $logger->pushHandler(new Monolog\Handler\StreamHandler($settings['path'], $settings['level']));
    return $logger;
};

// Database connection
$container['db'] = function ($c) {
    return new Db(DBHost, DBPort, DBName, DBUser, DBPassword);
};


// Verschlüsslung
$container['encryption_class'] = function ($c) {
    return new Encryption();
};



