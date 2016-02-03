<?php

mysql_connect('s4.nethely.hu','taverzekelt','taverzekelt2016') or die (mysql_error());
mysql_select_db('taverzekelt') or die (mysql_error());

mysql_query('SET NAMES UTF8');
mysql_query('SET CHARACTER SET UTF8');
mysql_query("SET time_zone = '+2:00'");

$result = mysql_query("SELECT *FROM markers WHERE deleted = 0") or die(mysql_error());

$response = array();

if (mysql_num_rows($result) > 0) {

    $response["markers"] = array();
    
    while ($row = mysql_fetch_array($result)) {
	
        	$product = array();
		$product["id"] 			= $row["id"];
        	$product["lat"] 			= $row["lat"];
        	$product["lon"] 			= $row["lon"];
		$product["create_date"] 	= $row["create_date"];
		$product["comment"] 	= $row["comment"];


        array_push($response["markers"], $product);
    }
    $response["success"] = 1;

    echo json_encode($response);
} else {
    $response["success"] = 0;
    $response["message"] = "No markers found";

    echo json_encode($response);
}
exit();
?>
