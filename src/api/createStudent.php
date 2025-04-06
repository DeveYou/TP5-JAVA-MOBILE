<?php
ob_start();
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");

ini_set('display_errors', 1);
error_reporting(E_ALL);

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    include_once '../root.php';
    include_once ROOT . '/services/StudentService.php';
    create();
}

function create()
{
    try {
        $json = file_get_contents("php://input");
        $data = json_decode($json, true);

        if (!$data) {
            http_response_code(400);
            echo json_encode(["error" => "Invalid JSON data."]);
            return;
        }

        // Extract and sanitize fields
        $firstName = isset($data['firstName']) ? trim($data['firstName']) : '';
        $lastName = isset($data['lastName']) ? trim($data['lastName']) : '';
        $city = isset($data['city']) ? trim($data['city']) : '';
        $gender = isset($data['gender']) ? trim($data['gender']) : '';
        $dateOfBirth = isset($data['dateOfBirth']) ? trim($data['dateOfBirth']) : '';
        $imageBase64 = isset($data['image']) ? $data['image'] : null;

        // Validate required fields
        if (empty($firstName) || empty($lastName) || empty($city) || empty($gender) || empty($dateOfBirth)) {
            http_response_code(422);
            echo json_encode(["error" => "Missing required fields."]);
            return;
        }

        // Validate date format
        if (!preg_match('/^\d{4}-\d{2}-\d{2}$/', $dateOfBirth)) {
            http_response_code(400);
            echo json_encode(["error" => "Invalid date format. Use YYYY-MM-DD."]);
            return;
        }

        // Decode image
        $imageContent = null;
        if (!empty($imageBase64)) {
            if (strlen($imageBase64) > 2 * 1024 * 1024) {
                http_response_code(400);
                echo json_encode(["error" => "Image too large. Max 2MB allowed."]);
                return;
            }
            $imageContent = base64_decode($imageBase64, true);
            if ($imageContent === false) {
                http_response_code(400);
                echo json_encode(["error" => "Invalid Base64 image."]);
                return;
            }
        }

        // Save to database
        $student = new Student(null, $firstName, $lastName, $city, $gender, $dateOfBirth, $imageContent);
        $es = new StudentService();
        $es->create($student);

        ob_end_clean();
        http_response_code(200);
        echo json_encode(["success" => true, "message" => "Student added successfully"]);
    } catch (Exception $e) {
        ob_end_clean();
        http_response_code(500);
        echo json_encode(["error" => "Failed to save student", "details" => $e->getMessage()]);
    }
}

ob_end_flush();
/*
ini_set('display_errors', 1);
error_reporting(E_ALL);

if($_SERVER["REQUEST_METHOD"] == "POST"){
    include_once '../root.php';
    include_once ROOT . '/services/StudentService.php';
    create();
}
function create()
{
    // Read raw JSON input
    $json = file_get_contents("php://input");
    $data = json_decode($json, true);

    if (!$data) {
        http_response_code(400);
        echo json_encode(["error" => "Invalid JSON data."]);
        return;
    }

    // Extract and sanitize fields
    $firstName = isset($data['firstName']) ? trim($data['firstName']) : '';
    $lastName = isset($data['lastName']) ? trim($data['lastName']) : '';
    $city = isset($data['city']) ? trim($data['city']) : '';
    $gender = isset($data['gender']) ? trim($data['gender']) : '';
    $dateOfBirth = isset($data['dateOfBirth']) ? trim($data['dateOfBirth']) : '';
    $imageBase64 = isset($data['image']) ? $data['image'] : null;

    if (empty($firstName) || empty($lastName) || empty($city) || empty($gender) || empty($dateOfBirth)) {
        http_response_code(422);
        echo json_encode(["error" => "Missing required fields."]);
        return;
    }

    // Decode image
    $imageContent = null;
    if (!empty($imageBase64)) {
        $imageContent = base64_decode($imageBase64, true);
        if ($imageContent === false) {
            http_response_code(400);
            echo json_encode(["error" => "Invalid Base64 image."]);
            return;
        }
    }

    // Create and save student
    try {
        $student = new Student(null, $firstName, $lastName, $city, $gender, $dateOfBirth, $imageContent);
        $es = new StudentService();
        $es->create($student);

        // Return success response
        header('Content-Type: application/json');
        echo json_encode(["success" => true, "message" => "Student added successfully"]);
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode([
            "error" => "Failed to save student",
            "details" => $e->getMessage()
        ]);
    }

    /*extract($_POST);
    if (!empty($_FILES['image']['tmp_name']) && is_uploaded_file($_FILES['image']['tmp_name'])){
        $imageContent = file_get_contents($_FILES['image']['tmp_name']);
    } else {
        $imageContent = null;
    }

    $es = new StudentService();
    $es->create(new Student(null, $firstName, $lastName, $city, $gender, $dateOfBirth, $imageContent));

    header('Content-type:application/json');
    echo json_encode($es->findAllApi());*/
