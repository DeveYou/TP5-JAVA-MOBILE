<?php
class Connection
{
    private $connection;

    public function __construct() {
        $host = 'localhost';
        $dbname = 'school';
        $username = 'root';
        $password = '';
        try {
            $this->connection = new PDO("mysql:host=$host;dbname=$dbname", $username, $password);
            $this->connection->query("SET NAMES UTF8");
        } catch (Exception $e) {
            die('Erreur : ' . $e->getMessage());
        }
    }
    function getConnection() {
        return $this->connection;
    }
}