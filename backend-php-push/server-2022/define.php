<?php
/****************************************************************************
 *  Copyright (c) 2014-2022 Ernst-Abbe-Hochschule Jena
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *****************************************************************************/

declare(strict_types=1);

const API_BASE_URL = "https://stundenplanung.eah-jena.de/api/mobileapp/";
const ENDPOINT_MODULE = "v1/module";
const ENDPOINT_MODULE_DETAIL = "v1/module/"; //+{id}

const FCM_URL = "https://fcm.googleapis.com/fcm/send";
const SERVER_KEY = "AAAAU9EyU7g:APA91bF0q-zUEC2f3A6IMSk0at797Wb6OXt3AD-v1X_mCP5_s2HQ4EwSJqwhzjj0AnVB3aGUNMKMkhKSrZuwZblG4SPRrExBVRjQLvQ6w9tWTZePbAElHElzLBKRjLuo_xFeTNYLWHUL";

const ANDROID = "android";
const IOS = "ios";

const LANG_DE = "DE";
const LANG_EN = "EN";
