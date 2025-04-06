<?php

include_once '../root.php';
include_once ROOT . '/services/StudentService.php';

extract($_POST);
if (!empty($_FILES['image']['tmp_name']) && is_uploaded_file($_FILES['image']['tmp_name'])){
    $imageContent = file_get_contents($_FILES['image']['tmp_name']);
} else {
    $imageContent = null;
}

$es = new StudentService();
$es->create(new Student(1, $firstName, $lastName, $city, $gender, $dateOfBirth, $imageContent));
header("location:../index.php");