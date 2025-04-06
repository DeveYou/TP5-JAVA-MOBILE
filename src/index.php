<!DOCTYPE html>
<?php
include_once './root.php';
?>

<html>
<head>
    <meta charset="UTF-8">
    <title>School</title>
    <link rel="stylesheet" href="../res/style/style.css" >
</head>
<body>
<div class="container">
    <form class="student-form" method="POST" action="controllers/addStudent.php" enctype="multipart/form-data">
        <fieldset>
            <legend>Ajouter un nouveau étudiant</legend>
            <div class="form-group">
                <label>Nom :</label>
                <input type="text" name="lastName" />
            </div>
            <div class="form-group">
                <label>Prénom :</label>
                <input type="text" name="firstName" />
            </div>
            <div class="form-group">
                <label>Ville :</label>
                <select name="city">
                    <option value="Marrakech">Marrakech</option>
                    <option value="Rabat">Rabat</option>
                    <option value="Agadir">Agadir</option>
                </select>
            </div>
            <div class="form-group">
                <label>Sexe :</label>
                <label><input type="radio" name="gender" value="homme" /> M</label>
                <label><input type="radio" name="gender" value="femme" /> F</label>
            </div>
            <div class="form-group">
                <label>Date de naissance :</label>
                <input type="date" name="dateOfBirth" />
            </div>
            <div class="form-group">
                <label>Photo :</label>
                <input type="file" name="image" />
            </div>
            <div class="form-buttons">
                <input type="submit" value="Envoyer" />
                <input type="reset" value="Effacer" />
            </div>
        </fieldset>
    </form>

    <table class="student-table">
        <thead>
        <tr>
            <th>ID</th>
            <th>Nom</th>
            <th>Prénom</th>
            <th>Ville</th>
            <th>Sexe</th>
            <th>Date de naissance</th>
            <th>Image</th>
            <th>Supprimer</th>
            <th>Modifier</th>
        </tr>
        </thead>
        <tbody>
            <?php
            include_once ROOT.'/services/StudentService.php';
            $es = new StudentService();
            foreach ($es->findAll() as $e) { ?>
                <tr>
                    <td><?php echo $e->getId(); ?></td>
                    <td><?php echo $e->getLastName(); ?></td>
                    <td><?php echo $e->getFirstName(); ?></td>
                    <td><?php echo $e->getCity(); ?></td>
                    <td><?php echo $e->getGender(); ?></td>
                    <td><?php echo $e->getDateOfBirth(); ?></td>
                    <td>
                        <?php if ($e->getImage()) { ?>
                            <img class="image-table" src="data:image/jpeg;base64,<?= base64_encode($e->getImage()) ?>" width="80" height="80" alt="Student image"/>
                        <?php } else { ?>
                            <span>No image</span>
                        <?php } ?>
                    </td>
                    <td>
                        <a href="controllers/deleteStudent.php?id= <?php echo $e->getId(); ?>" >Supprimer</a>
                    </td>
                    <td><a href="updateStudent.php">Modifier</a></td>
                </tr>
            <?php } ?>
        </tbody>
    </table>
</div>

</body>
</html>