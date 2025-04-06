<?php

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    include_once '../root.php';
    include_once ROOT . '/services/StudentService.php';
    loadAll();
}
function loadAll() {
    header("Access-Control-Allow-Origin: *");
    header("Content-Type: application/json; charset=UTF-8");
    // get the data
    $es = new StudentService();
    $data = $es->findAllApi();

    foreach ($data as &$student) {
        if ($student['image']) {
            // convert the binary image to Base64
            $student['image'] = base64_encode($student['image']);
        } else {
            $student['image'] = null;
        }
    }

    // encode data to json
    $json_data = json_encode($data);

    //check if the json_encode work properly
    if ($json_data === false) {
        echo 'JSON Encoding Error: ' . json_last_error_msg();
        exit;
    }

    echo $json_data;
}

