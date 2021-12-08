<?php

/*
  ~ Copyright (c) 2019 EAH Jena
  ~ This program is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU General Public License as published by
  ~ the Free Software Foundation, either version 3 of the License, or
  ~ (at your option) any later version.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with this program.  If not, see <http://www.gnu.org/licenses/>.
  */



/*
Von: https://www.segal-online.de/verschluesselte-daten-mysql/
Verschlüsslung mittels zweier selbst benannten Schluessel ( $_secret_key &  $_secret_iv)

Bitte beide Variablen  $_secret_key und $_secret_iv ein Zeichenkette eintragen. (Min. 16 Zeichen) --> in der settings.php Datei
Hierfür kann ein beliebiger Passwort Generator genutz werden

*/


class Encryption
{
  // Konstante für Verschlüsselungsmethode
  const AES_256_CBC = 'aes-256-cbc';

  private $_secret_key = SecretKey1; // hier einen sicheren Schlüssel einsetzen - min 16 Zeichen
  private $_secret_iv  = SecretKey2; // hier einen weiteren sicheren Schlüssel einsetzen - min 16 Zeichen
  private $_encryption_key;
  private $_iv;

  // im Konstruktor werden die Instanzvariablen initialisiert
  public function __construct()
  {
    $this->_encryption_key = hash('sha256', $this->_secret_key);
    $this->_iv             = substr(hash('sha256', $this->_secret_iv), 0, 16);
  }

  public function encryptString($data)
  {
    return base64_encode(openssl_encrypt($data, self::AES_256_CBC, $this->_encryption_key, 0, $this->_iv));
  }

  public function decryptString($data)
  {
    return openssl_decrypt(base64_decode($data), self::AES_256_CBC, $this->_encryption_key, 0, $this->_iv);
  }

  public function setEncryptionKey($key)
  {
    $this->_encryption_key = $key;
  }

  public function setInitVector($iv)
  {
    $this->_iv = $iv;
  }
}

