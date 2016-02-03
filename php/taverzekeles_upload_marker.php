<?php

mysql_connect('s4.nethely.hu','taverzekelt','taverzekelt2016') or die (mysql_error());
mysql_select_db('taverzekelt') or die (mysql_error());

mysql_query('SET NAMES UTF8');
mysql_query('SET CHARACTER SET UTF8');
mysql_query("SET time_zone = '+0:00'");

$response = array();

$response['success'] = 2;

if (isset($_GET["lat"]) && isset($_GET["lon"]) && isset($_GET["comment"])) {
    $lat = mysql_real_escape_string($_GET['lat']);
	$lon = mysql_real_escape_string($_GET['lon']);
	$comment = mysql_real_escape_string($_GET['comment']);

    $result = mysql_query("INSERT INTO markers (lat,lon,comment) VALUES ('$lat','$lon','$comment')");

	if($result){
		$response["success"] 	= 1;
		$response["id"] 		= mysql_insert_id();
		$response["comment"] 	= $comment;
		$response["lat"] 		= $lat;
		$response["lon"] 		= $lon;
		}
	else{
		$response["success"] = 0;
		}
}
echo json_encode($response);
exit();
?>