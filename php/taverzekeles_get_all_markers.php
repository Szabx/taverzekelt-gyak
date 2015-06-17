<?php

mysql_connect('mysql2.000webhost.com','a9524642_djzolee','szakdoga12') or die (mysql_error());
mysql_select_db('a9524642_hive') or die (mysql_error());

mysql_query('SET NAMES UTF8');
mysql_query('SET CHARACTER SET UTF8');

$result = mysql_query("SELECT *FROM markers WHERE deleted = 0") or die(mysql_error());

$response = array();

if (mysql_num_rows($result) > 0) {

    $response["markers"] = array();
    
    while ($row = mysql_fetch_array($result)) {
	
        $product = array();
		$product["id"] = $row["id"];
        $product["lat"] = $row["lat"];
        $product["lon"] = $row["lon"];
		$product["date"] = $row["date"];
		$product["comment"] = $row["comment"];


        array_push($response["markers"], $product);
    }
    $response["success"] = 1;

    echo json_encode($response);
} else {
    $response["success"] = 0;
    $response["message"] = "No markers found";

    echo json_encode($response);
}
?>
