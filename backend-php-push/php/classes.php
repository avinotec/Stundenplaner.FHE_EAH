<?php
/*
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
 */

declare(strict_types=1);


class Course {

    var $course;
    var $year;
    var $labels;
    var $semester;
    
    function __construct($course, $year ,$de, $en) {
        $this->course=$course;
        $this->year=$year;
        if(trim($en)==false){
            $en = $de;
        }
        $this->labels=array("de"=>$de, "en"=>$en);
        $this->semester=array();
    }
    
    function addSemester($semester){
        $this->semester=$semester;
    }
}

class MyScheduleEvent {
    var $eventset_id;
    var $title;
    var $studyGroups; 
    var $startDateTime; 
    var $endDateTime;  
    var $lecturer;  
    var $location;
    var $changes;
    
    function __construct($eventset_id, $title, $lecturer, $groups, $startDateTime, $endDateTime, $location) {        
        $this->activity_id = $eventset_id;
        $this->title = $title;
        $this->studyGroups = $groups;
        $this->startDateTime = $startDateTime;
        $this->endDateTime = $endDateTime;
        $this->lecturer = $lecturer;
        $this->location = $location;
    }
    
    function setChanges($changes) {
        $this->changes = $changes;
    }
}
