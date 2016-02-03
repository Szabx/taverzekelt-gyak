<?php

mysql_connect('s4.nethely.hu','taverzekelt','taverzekelt2016') or die (mysql_error());
mysql_select_db('taverzekelt') or die (mysql_error());

mysql_query('SET NAMES UTF8');
mysql_query('SET CHARACTER SET UTF8');
mysql_query("SET time_zone = '+2:00'");

$start 	= strlen($_GET['start_date']) > 0 ? $_GET['start_date'] : false;
$end 	= strlen($_GET['end_date']) > 0 ? $_GET['end_date'] : false;

$response 	= array();

if (!$start || !$end)
{
	$response["success"] 	= 0;
	$response["comment"] 	= "No data recieved";
}
else
{
	$start_date 	= date('Y-m-d h:i:s', strtotime($start));
	$end_date 		= date('Y-m-d h:i:s', strtotime($end));

	$result 		= mysql_query("SELECT * FROM markers WHERE (create_date BETWEEN '".$start_date."' and '".$end_date."') or (delete_date BETWEEN '".$start_date."' and '".$end_date."')" or die(mysql_error());

	if (count(mysql_fetch_array($result)) > 0)
	{
		$response['success'] 	= 1; 
		$temp 	= array();
		while ($row = mysql_fetch_array($result)) 
		{
	        	array_push($temp, $row);
	   	}
	    $response['data'] 		= json_encode($temp);
	   	$response['comment'] 	= "Successfully retrieved data";
	}
	else
	{
		$response['success'] 	= 2;
		$response['data'] 		= json_encode(array());
		$response['comment'] 	= "No changes in the database";
	}
}

echo json_encode($response);
exit();
?>
