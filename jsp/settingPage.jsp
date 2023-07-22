<%@ page language="java" contentType="text/html; charset=utf-8"%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
<link rel="shortcut icon" href="../../images/favicon.ico" type="image/x-icon"/>
<link rel="stylesheet" type="text/css" href="../wutil/css/homePage.css" />
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css" integrity="sha384-GJzZqFGwb1QTTN6wy59ffF1BuGJpLSa9DkKMp0DgiMDm4iYMj70gZWKYbI706tWS" crossorigin="anonymous">
<!-- <meta http-equiv="X-UA-Compatible" content="IE=edge;chrome=1" /> -->
<title>船隻與海纜分布圖</title>

<script language="JavaScript" type="text/javascript">
///////////////////////////////////////////////////////////////////////////////////////////////		
	function setHrefVariable(choose){
		var mmsi = prompt("Please enter MMSI");
		var url = "";
		if(choose == 0)		//MARINE TRAFFIC_pic
		{	
			url = "https://photos.marinetraffic.com/ais/showphoto.aspx?mmsi=" + mmsi;
		}else		//MARINE TRAFFIC_info
		{	
			url = "https://www.marinetraffic.com/en/ais/details/ships/mmsi:" + mmsi;
		}
		if(mmsi)
		{
			window.open(url);
		}
			
		
	}//setHrefVariable
</script>

</head>

<body style="background-color: #F0F0F0;" >
	<!-- Navigation menu -->
	<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
		<div class="container">
			<!-- 品牌 Brand -->
			<a class="navbar-brand" href="#" style="font-size: 25px; width: 200px;">SAWS</a>
			<!-- 漢堡選單按鈕 -->
			<button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNavDropdown" aria-controls="navbarNavDropdown" aria-expanded="false" aria-label="Toggle navigation">
				<span class="navbar-toggler-icon"></span>
			</button>
			<!-- 選單內容 -->
			<div class="collapse navbar-collapse" id="navbarNavDropdown">
				<ul class="navbar-nav">
					<li class="nav-item" style="font-size: 20px; width: 200px;"><a class="nav-link" href="cmap/showInitMap.jsp">Map</a>
					</li>
					<li class="nav-item active" style="font-size: 20px; width: 200px;"><a class="nav-link" href="#">Setting</a>
					</li>
					<li class="nav-item dropdown" style="font-size: 20px; width: 200px;">
					  <a class="nav-link dropdown-toggle" href="#" id="navbarDropdownMenuLink" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"> Links </a>
						<div class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink">
							<a class="dropdown-item" href="https://gatehouse.motcmpb.gov.tw/aisweb/" target="_blank">航港局 AIS 電子助航及監控系統</a>
							<a class="dropdown-item" href="https://mpbais.motcmpb.gov.tw/" target="_blank">航港局臺灣海域船舶即時資訊系統</a> 
							<a class="dropdown-item" href="https://www.motcmpb.gov.tw/Information/Notice?SiteId=1&NodeId=483" target="_blank">航港局航船布告</a>
							<a class="dropdown-item" href="https://ocean.moi.gov.tw/Map/" target="_blank">內政部海域資訊整合平臺</a>
							<a class="dropdown-item" href="https://www.marinetraffic.com/" target="_blank">MARINE TRAFFIC</a>
							<a class="dropdown-item" href="#" onclick="setHrefVariable(0);return false;">MARINE TRAFFIC (Picture)</a>
							<a class="dropdown-item" href="#" onclick="setHrefVariable(1);return false;">MARINE TRAFFIC (Information)</a>
						</div>
					</li>
				</ul>
			</div>
		</div>
	</nav>

<h1>Still Working...</h1>

	<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
	<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>

</body>
</html>