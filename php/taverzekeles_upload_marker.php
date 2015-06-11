<?php

mysql_connect('mysql2.000webhost.com','a9524642_djzolee','szakdoga12') or die (mysql_error());
mysql_select_db('a9524642_hive') or die (mysql_error());

mysql_query('SET NAMES UTF8');
mysql_query('SET CHARACTER SET UTF8');

$response = array();

if (isset($_GET["lat"]) && isset($_GET["lon"]) && isset($_GET["megjegyzes"])) {
    $lat = mysql_real_escape_string($_GET['lat']);
	$lon = mysql_real_escape_string($_GET['lon']);
	$megjegyzes = mysql_real_escape_string($_GET['megjegyzes']);

    $result = mysql_query("INSERT INTO markers (lat,lon,megjegyzes) VALUES ('$lat','$lon','$megjegyzes')");
	
	if($result){
		$response["success"] = 1;
		}
	else{
		$response["success"] = 0;
		}

	echo $result;
    //echo json_encode($response);
}
?>