<?php
include_once ROOT . "/classes/Student.php";
include_once ROOT . "/connection/Connection.php";
include_once ROOT . "/dao/Idao.php";

class StudentService implements Idao
{
    private $connection;

    public function __construct()
    {
        $this->connection = new Connection();
    }

    public function create($student) {
        $query = "INSERT INTO Student (`id`, `firstName`, `lastName`, `city`, `gender`, `dateOfBirth`, `image`) 
                  VALUES (NULL, :firstName, :lastName, :city, :gender, :dateOfBirth, :image)";
        $req = $this->connection->getConnection()->prepare($query);
        $req->execute([
            ':firstName' => $student->getFirstName(),
            ':lastName'  => $student->getLastName(),
            ':city'      => $student->getCity(),
            ':gender'    => $student->getGender(),
            ':dateOfBirth' => $student->getDateOfBirth(),
            ':image'     => $student->getImage()
        ]) or die('SQL Error');
    }

    public function delete($student) {
        $query = "DELETE FROM Student WHERE id = :id";
        $req = $this->connection->getConnection()->prepare($query);
        $req->execute([':id' => $student->getId()]) or die('SQL Error');
    }

    public function findAll() {
        $students = array();
        $query = "SELECT * FROM Student";
        $req = $this->connection->getConnection()->prepare($query);
        $req->execute();
        while ($student = $req->fetch(PDO::FETCH_OBJ)) {
            $students[] = new Student($student->id, $student->firstName, $student->lastName, $student->city, $student->gender, $student->dateOfBirth, $student->image);
        }
        return $students;
    }

    public function findById($id) {
        $query = "SELECT * FROM Student WHERE id = :id";
        $req = $this->connection->getConnection()->prepare($query);
        $req->execute([':id' => $id]);
        if ($student = $req->fetch(PDO::FETCH_OBJ)) {
            return new Student($student->id, $student->firstName, $student->lastName, $student->city, $student->gender, $student->dateOfBirth, $student->image);
        }
        return null;
    }

    public function update($student) {
        $query = "UPDATE Student SET 
                    firstName = :firstName, 
                    lastName = :lastName, 
                    city = :city, 
                    gender = :gender,
                    dateOfBirth = :dateOfBirth,
                    image = :image
                  WHERE id = :id";
        $req = $this->connection->getConnection()->prepare($query);
        $req->execute([
            ':firstName' => $student->getFirstName(),
            ':lastName'  => $student->getLastName(),
            ':city'      => $student->getCity(),
            ':gender'    => $student->getGender(),
            ':dateOfBirth' => $student->getDateOfBirth(),
            ':image'     => $student->getImage(),
            ':id'        => $student->getId(),
        ]) or die('SQL Error');
    }

    public function findAllApi()
    {
        $query = "SELECT * FROM Student";
        $req = $this->connection->getConnection()->prepare($query);
        $req->execute();
        return $req->fetchAll(PDO::FETCH_ASSOC);
    }
}
