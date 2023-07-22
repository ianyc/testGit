<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@page import = "java.lang.*"%>
<%@page import = "java.io.File"%>
<%@page import = "java.util.Map"%>
<%@page import = "java.util.HashMap"%>
<%@page import = "java.util.Arrays"%>
<%@page import = "java.util.ArrayList"%>
<%@page import = "com.chtn.spaws.SortEntity"%>
<%@page import = "com.chtn.spaws.entity.*"%>
<%@page import = "com.chtn.spaws.parse.ParseData"%>
<jsp:useBean id="Env" scope="page" class="com.chtn.util.Env"/>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="shortcut icon" href="../../images/favicon.ico" type="image/x-icon"/>
<link rel="stylesheet" type="text/css" href="../../wutil/css/homePage.css" />
<!-- <meta http-equiv="X-UA-Compatible" content="IE=edge;chrome=1" /> -->
<title>船隻與海纜分布圖</title>


<script src="GetAPI.html" type=text/javascript></script>
<script language="JavaScript" type="text/javascript" src="../../wutil/js/cmapTextLabelOverlay.js" charset="utf-8"></script>
<script language="JavaScript" type="text/javascript" src="../../wutil/js/mapFunction.js" charset="utf-8"></script>
<script language="JavaScript" type="text/javascript">
	var map;
	var OC_boat, OC_lastBoat, OC_boatTrack;		//OC_boat: 所有的船座標、OC_lastBoat: 每一艘船的最後(最新)一個座標、OC_boatTrack: 所有船座標畫出的"線"
	var colormkr, textLayer, innerText;
	var warnPolygonAry = [];	//將所有警戒區polygon存起來，偵測船隻是否落在警戒區內時會用到
	
	var boatPointImg = new CIcon();		//boat track
	boatPointImg.image = "../../images/boatPoint_gray.png";
	boatPointImg.iconSize = new CSize(15, 15);
	boatPointImg.iconAnchor = new CPoint(7, 7);
	var boatImg = new CIcon();		//last boat marker
    boatImg.image = "../../images/boat.png";
    boatImg.iconSize = new CSize(50, 50);
    boatImg.iconAnchor = new CPoint(25, 35);
	var colorBoatImg = new CIcon();		//mouse passing
	colorBoatImg.image = "../../images/boatPoint_red.png";
	colorBoatImg.iconSize = new CSize(15, 15);
	colorBoatImg.iconAnchor = new CPoint(7, 7);
	
	
	
	var boats = [], currBoat_ll = [], currBoats_cluster;
	var LLCollection_boat=[], LLCollection_boat_mkr=[], LLCollection_boat_rect=[],textobj={};
	
	function drawToMap(){
		currBoat_ll = [];
		for(var i=0, sizei=boats.length; i<sizei; i++){
			currBoat_ll.push(boats[i].ll);
		}
		
// 		console.log(boats);
// 		console.log(currBoat_ll);
		
		
		if(currBoat_ll.length>0){
			currBoats_cluster = new CMarkerCluster(currBoat_ll, map,{icon: boatPointImg, title:"123"});
			map.removeMultiOverlay(LLCollection_boat_mkr);
			LLCollection_boat=[];
			LLCollection_boat_mkr=[];
			LLCollection_boat_rect=[];
			LLCollection_boat = currBoats_cluster.ClusterByGrid();
			
			var outputgrid=[], gridobj={};
			for(var i = 0; i < LLCollection_boat.length; i++)
            {
				gridobj={};
				lltemp = LLCollection_boat[i].ll;	//此聚集的座標
                numbersin = LLCollection_boat[i].membersize;	//此聚集有幾筆資料
//                 console.log(lltemp);
//                 console.log(numbersin);
                
                textobj={text:numbersin.toString(), textleft:5, texttop:5 };
                LLCollection_boat_mkr.push(new CMarker(lltemp,{icon: boatPointImg, clickable: false, draggable: false}))
                LLCollection_boat_mkr[i].setIcon(boatPointImg);	
                
                if(numbersin > 99)
                {
                	boatPointImg.iconSize = new CSize(50, 50);
                	boatPointImg.iconAnchor = new CPoint(25, 25);
                    textobj.textleft=12;
                    textobj.texttop=13;
                }
                else if(numbersin > 50)
                {
                	boatPointImg.iconSize = new CSize(40, 40);
                	boatPointImg.iconAnchor = new CPoint(20, 20);
                    textobj.textleft=10;
                    textobj.texttop=10;
                }
                else if(numbersin > 9)
                {
                	boatPointImg.iconSize = new CSize(30, 30);
                	boatPointImg.iconAnchor = new CPoint(15, 15);
                    textobj.textleft=6;
                    textobj.texttop=5;
                }
                else 
                {
                	boatPointImg.iconSize = new CSize(20, 20);
                	boatPointImg.iconAnchor = new CPoint(10, 10);
                    textobj.textleft=5;
                    textobj.texttop=0;
                }
                LLCollection_boat_mkr[i].setIcon(boatPointImg);
                map.addOverlay(LLCollection_boat_mkr[i]);
                if(numbersin > 1)
                	LLCollection_boat_mkr[i].setText(textobj);
                
                
            }
			
			
			
			
		}
		
		
	}
	
	
	
	
	
	//Initial
	function loadMap() {
	
		map = new CMap(document.getElementById("map"));		//宣告地圖
		var centerLoc = new CLatLng(25.427225090910216, 121.3940515117644);	//中心點經緯度
// 		var centerLoc = new CLatLng(25.362272805839027, 121.45485858154301);	//中心點經緯度
		map.setCenter(centerLoc, 10);		//設定地圖中心點及比例尺(必要！)
		map.addControl(C_SCALE_CTRL);		//設定地圖控制項-比例尺圖示
		map.addControl(C_LARGE_ZOOM_V);		//設定地圖控制項-縮放按鈕及滑桿
		map.enableScrollWheelZoom(true);		//開啟滾輪縮放地圖設定
		
		//畫船隻及軌跡
		OC_boat = new COverlayCollection(map);	//利用COverlayCollection繪製大量點，增加速度
		OC_lastBoat = new COverlayCollection(map);
		OC_boatTrack = new COverlayCollection(map);
		colormkr = new CMarker(new CLatLng(24.02821916862763, 120.978669921875), {icon: colorBoatImg, title: "", clickable: true, draggable: false});		//Initial滑鼠經過變色
		textLayer = new CTextLabel(new CLatLng(25.7476, 121.5170), "<font size='2'>這是文字標籤</font>", "#000", "#ffd", "1px solid #000");		//Initial滑鼠經過的文字標籤
		
		//test cluster
		for(var i=0; i<100000; i++)
		{
			var lat = 25.427225090910216+Math.random();
			var lng = 121.3940515117644+Math.random();
			var boatObj = {};
			boatObj.ll = new CLatLng(lat, lng);
			boats.push(boatObj);
			
		}
		
		
		
		textLayer = new CTextLabel(new CLatLng(25.7476, 121.5170), "<font size='2'>這是文字標籤</font>", "#000", "#ffd", "1px solid #000");
		
		CEvent.addListener(map, "mousemove", OCfocus);
		
		
		drawToMap();
		CEvent.addListener(map, "click", function () {drawToMap();});
		CEvent.addListener(map, "zoomend", function () {drawToMap();});
		
	}//loadMap END
	
	
	
	
	
	
	
</script>

</head>







<body style="background-color: #e0f0f5;" onload="loadMap()" onunload="CUnload(1)">
<form id="form" name="form" method="post" action="">
<table>
	<tr>
		<td>
			<div class='map' id="map" style="width:1200px; height:900px; border:1px solid #000;"></div>
  		</td>
  		
	</tr>
</table>

<input type="hidden" name="hiddenTest" id="hiddenTest" value="333">
</form>
</body>





</html>