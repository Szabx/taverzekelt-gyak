<?php

mysql_connect('mysql2.000webhost.com','a9524642_djzolee','szakdoga12') or die (mysql_error());
mysql_select_db('a9524642_hive') or die (mysql_error());

mysql_query('SET NAMES UTF8');
mysql_query('SET CHARACTER SET UTF8');

$marker_id 	= strlen($_GET['marker_id']) > 0 ? intval($_GET['marker_id']) : false;

$response 	= array();
$response["success"] 	= 0;
$response["megjegyzes"] 	= 'No data recieved';

// If we have a marker id
if ($marker_id)
{
	mysql_query("UPDATE markers SET deleted = 1, deleted_date = '".date('Y-m-d h:i:s')."'  WHERE id=".mysql_real_escape_string($marker_id)) or die(mysql_error());

	if (mysql_affected_rows() > 0) 
	{

	    $response["success"] 	= 1;
	    $response["megjegyzes"] 	= "Marker removed from database";
	}
	else
	{
		$response["success"] 	= 0;
		$response["megjegyzes"]	= "Invalid marker ID";
	} 
}

echo json_encode($response);
?>
