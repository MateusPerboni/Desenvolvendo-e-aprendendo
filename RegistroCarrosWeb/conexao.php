<?php
$servidor = "localhost";
$usuario = "root";
$senha = "";
$dbname = "registro_carros";

// Criar a conexão
$conn = mysqli_connect($servidor, $usuario, $senha, $dbname);

// Checar a conexão
if (!$conn) {
    die("Falha na conexão: " . mysqli_connect_error());
}
?>