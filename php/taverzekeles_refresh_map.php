<?php

mysql_connect('mysql2.000webhost.com','a9524642_djzolee','szakdoga12') or die (mysql_error());
mysql_select_db('a9524642_hive') or die (mysql_error());

mysql_query('SET NAMES UTF8');
mysql_query('SET CHARACTER SET UTF8');

$start 	= strlen($_GET['start_date']) > 0 ? $_GET['start_date'] : false;
$end 	= strlen($_GET['end_date']) > 0 ? $_GET['end_date'] : false;

$time = strtotime('10/16/2003');

$start_date 	= date('Y-m-d h:i:s', strtotime($start));
$end_date 		= date('Y-m-d h:i:s', strtotime($end))

$response 	= array();

$response["success"] 	= 0;
$response["message"] 	= "No data recieved";

// If we have a marker id
if ($marker_id)
{
	$result 	= mysql_query("SELECT id FROM markers WHERE (create_date BETWEEN '".$start_date."' and '".$end_date."') or (delete_date BETWEEN '".$start_date."' and '".$end_date."')" or die(mysql_error());

	if (count(mysql_fetch_array($result)) > 0)
	{
		$response['success'] 	= 1; 
		$temp 	= array();
		while ($row = mysql_fetch_array($result)) 
		{
	        array_push($temp, $row['id']);
	    }
	    $response['data'] 		= $temp;
	    $response['message'] 	= "Successfully retrieved data";
	}
	else
	{
		$response['success'] 	= 0;
		$response['data'] 		= array();
		$response['message'] 	= "No changes in the database";
	}
}

echo json_encode($response);
?>
