<?php

include_once '../root.php';
include_once ROOT . '/services/StudentService.php';

extract($_GET);
$es = new StudentService();
$es->delete($es->findById($id));
header("location:../index.php");