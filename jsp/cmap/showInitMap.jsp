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
<%@page import = "org.apache.logging.log4j.LogManager"%>
<%@page import = "org.apache.logging.log4j.Logger"%>
<jsp:useBean id="Env" scope="page" class="com.chtn.util.Env"/>

<%

	int dataType = 0;	//0: .csv
	//船隻資訊
	String filePath = Env.BOAT_PROCESSED_DATA_PATH;
	String fileName = "current.csv";
	String packType = "boatRaw";
	ParseData parseData = new ParseData();
	BoatInfo[] boatInfos = parseData.readFromMPBFile(filePath, fileName, dataType, packType);	//理論上讀current的csv檔只要讀一份，不用像海纜一樣loop整個folder
	SortEntity sortEntity = new SortEntity();
	HashMap<String, ArrayList<BoatInfo>> MMSIsList = sortEntity.sortBoatByMMSI(boatInfos);	//MMSIsList存放的是key=mmsi、value=此mmsi所有資料(arrayList)
	
			
	//船隻告警紀錄檔資訊
	File alertBoatPath = new File(Env.BOAT_ALERT_RECORD_PATH);
	String alertBoatPackType = "boatAlert";
	ParseData alertBoatParseData = new ParseData();
	ArrayList<BoatInfo> alertBoatInfos = new ArrayList<>();	//由於一次loop整個folder裡的csv檔到parseData，將回傳的array轉成arrayList形式
	for(File file : alertBoatPath.listFiles())		//一次loop folder裡全部海纜檔
 	{
		BoatInfo[] infos = alertBoatParseData.readFromAlertRecordFile(alertBoatPath.getAbsolutePath()+Env.FILE_SEPARATOR, file.getName(), dataType, alertBoatPackType);
		alertBoatInfos.addAll(Arrays.asList(infos));
 	}
	BoatInfo[] tempBoatAlertAry = new BoatInfo[alertBoatInfos.size()];	//sortBoatByAlertRecordFile()需代入array，因此這裡將arrayList轉成array
	tempBoatAlertAry = alertBoatInfos.toArray(tempBoatAlertAry);
	SortEntity BoatAlertSortEntity = new SortEntity();
	HashMap<String, ArrayList<BoatInfo>> boatAlertList = BoatAlertSortEntity.sortBoatByAlertRecordFile(tempBoatAlertAry);
	
	
	//海纜資訊
	File SCfilePath = new File(Env.SC_DATA_PATH);
	String SCpackType = "subCable";
	ParseData SCparseData = new ParseData();
	ArrayList<SubCableInfo> subCableInfos = new ArrayList<>();	//由於一次loop整個folder裡的csv檔到parseData，將回傳的array轉成arrayList形式
	for(File file : SCfilePath.listFiles())		//一次loop folder裡全部海纜檔
 	{
 		SubCableInfo[] infos = SCparseData.readFromSCFile(SCfilePath.getAbsolutePath()+Env.FILE_SEPARATOR, file.getName(), dataType, SCpackType);
 		subCableInfos.addAll(Arrays.asList(infos));
 	}
	SubCableInfo[] tempSCAry = new SubCableInfo[subCableInfos.size()];	//由於sortSubCableByName()需代入array，因此這裡將arrayList轉成array
 	tempSCAry = subCableInfos.toArray(tempSCAry);
	SortEntity SCsortEntity = new SortEntity();
	HashMap<String, ArrayList<SubCableInfo>> subCableList = SCsortEntity.sortSubCableByName(tempSCAry);
	
	
	//海纜警戒區資訊
	File SCWarnFilePath = new File(Env.SC_WARN_DATA_PATH);
	String SCWarnPackType = "subCable";
	ParseData SCWarnParseData = new ParseData();
	ArrayList<SubCableInfo> subCableWarnInfos = new ArrayList<>();	//由於一次loop整個folder裡的csv檔到parseData，將回傳的array轉成arrayList形式
	for(File file : SCWarnFilePath.listFiles())		//一次loop folder裡全部海纜檔
 	{
 		SubCableInfo[] infos = SCWarnParseData.readFromSCFile(SCWarnFilePath.getAbsolutePath()+Env.FILE_SEPARATOR, file.getName(), dataType, SCWarnPackType);
 		subCableWarnInfos.addAll(Arrays.asList(infos));
 	}
	SubCableInfo[] tempSCWarnAry = new SubCableInfo[subCableWarnInfos.size()];	//由於sortSubCableByName()需代入array，因此這裡將arrayList轉成array
	tempSCWarnAry = subCableWarnInfos.toArray(tempSCWarnAry);
	SortEntity SCWarnSortEntity = new SortEntity();
	HashMap<String, ArrayList<SubCableInfo>> subCableWarnList = SCWarnSortEntity.sortSubCableByName(tempSCWarnAry);
	
	
%>



<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
<link rel="shortcut icon" href="../../images/favicon.ico" type="image/x-icon"/>
<link rel="stylesheet" type="text/css" href="../../wutil/css/homePage.css" />
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css" integrity="sha384-GJzZqFGwb1QTTN6wy59ffF1BuGJpLSa9DkKMp0DgiMDm4iYMj70gZWKYbI706tWS" crossorigin="anonymous">
<!-- <meta http-equiv="X-UA-Compatible" content="IE=edge;chrome=1" /> -->
<title>船隻與海纜分布圖</title>


<script src="GetAPI.html" type=text/javascript></script>
<script language="JavaScript" type="text/javascript" src="../../wutil/js/jszip.min.js"></script>
<script language="JavaScript" type="text/javascript" src="../../wutil/js/FileSaver.min.js"></script>
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
    boatImg.iconSize = new CSize(30, 30);
    boatImg.iconAnchor = new CPoint(15, 15);
//     boatImg.iconSize = new CSize(50, 50);
//     boatImg.iconAnchor = new CPoint(25, 35);
	var colorBoatImg = new CIcon();		//mouse passing
	colorBoatImg.image = "../../images/boatPoint_red.png";
	colorBoatImg.iconSize = new CSize(15, 15);
	colorBoatImg.iconAnchor = new CPoint(7, 7);
	var errorImg = new CIcon();		//user defined marker
	errorImg.image = "../../images/error.png";
	errorImg.iconSize = new CSize(35, 35);
	errorImg.iconAnchor = new CPoint(15, 15);
	
	var downloadInfo;	//下載軌跡檔前，將搜尋結果存到此變數
	
	
	function startTime(){
		var dt = new Date();
		document.getElementById("datetime").innerHTML = dt.toLocaleString();
	}
	
	
///////////////////////////////////////////////////////////////////////////////////////////////	
	//Initial
	function loadMap() {
	
		//show clock
		setInterval(function() {
		    startTime();
		}, 500);
	
	
		map = new CMap(document.getElementById("map"));		//宣告地圖
		var centerLoc = new CLatLng(25.427225090910216, 121.3940515117644);	//中心點經緯度
// 		var centerLoc = new CLatLng(25.362272805839027, 121.45485858154301);	//中心點經緯度
		map.setCenter(centerLoc, 8);		//設定地圖中心點及比例尺(必要！)
		map.addControl(C_SCALE_CTRL);		//設定地圖控制項-比例尺圖示
		map.addControl(C_LARGE_ZOOM_V);		//設定地圖控制項-縮放按鈕及滑桿
		map.enableScrollWheelZoom(true);		//開啟滾輪縮放地圖設定
		//畫海纜
		drawSubCable();
		
		//畫海纜警戒區
		drawSubCableWarn();
		
		//畫船隻及軌跡
		OC_boat = new COverlayCollection(map);	//利用COverlayCollection繪製大量點，增加速度
		OC_lastBoat = new COverlayCollection(map);
		OC_boatTrack = new COverlayCollection(map);
		CEvent.addListener(map, "mousemove", OCfocus);	//加入 mousemove 事件，透過 OCfocus 判斷是否滑鼠經過宣告的物件。OCfocus()定義在mapFunction.js
		CEvent.addListener(map, "click", pastMMSI2Search);
// 		CEvent.addListener(map, "dbltouch", OCfocus);
		
		colormkr = new CMarker(new CLatLng(24.02821916862763, 120.978669921875), {icon: colorBoatImg, title: "", clickable: true, draggable: false});		//Initial滑鼠經過變色
		textLayer = new CTextLabel(new CLatLng(25.7476, 121.5170), "<font size='2'>這是文字標籤</font>", "#000", "#ffd", "1px solid #000");		//Initial滑鼠經過的文字標籤
		drawBoatAndTrack();		//在地圖上畫出船隻軌跡
		
		
		//在網頁中列出目前在告警清單中的船隻資訊
		showAlertBoat();
		//偵測船隻(最後位置)是否在警戒區內
// 		detectWarn();
		
		
	}//loadMap END
	
	
	
///////////////////////////////////////////////////////////////////////////////////////////////	
	function showAlertBoat(){
		<%
		StringBuffer alertMsgSB = new StringBuffer();
		
		for( Map.Entry<String, ArrayList<BoatInfo>> entry : boatAlertList.entrySet() ){
			String alertFileName = entry.getKey();	//key: 告警紀錄檔名稱(抓檔名)
			ArrayList<BoatInfo> alertBoats = entry.getValue();	//value: [BoatInfo, BoatInfo, BoatInfo...]
			alertMsgSB.append("************").append(alertFileName).append("************");
			for(int i=0; i<alertBoats.size(); i++)
			{
				//處理船的長、寬
				Double boatLength_d;
				String boatLength;
				Double boatWidth_d;
				String boatWidth;
				try {
					boatLength_d = Double.parseDouble(alertBoats.get(i).getSizeA()) + Double.parseDouble(alertBoats.get(i).getSizeB());
					boatLength = boatLength_d.toString();
				} catch (Exception e) {
					boatLength = "";
				}
				try {
					boatWidth_d = Double.parseDouble(alertBoats.get(i).getSizeC()) + Double.parseDouble(alertBoats.get(i).getSizeD());
					boatWidth = boatWidth_d.toString();
				} catch (Exception e) {
					boatWidth = "";
				}
				alertMsgSB.append("\\n").append("MMSI: ").append(alertBoats.get(i).getMmsi()).append(" (第").append(alertBoats.get(i).getAlertTimes()).append("次告警)\\n")
					.append("TimeStamp: ").append(alertBoats.get(i).getTimestamp()).append("\\n")
					.append("Position: (").append(alertBoats.get(i).getLatitude()).append(", ").append(alertBoats.get(i).getLongitude()).append(")").append("\\n")
					.append("Speed: ").append(alertBoats.get(i).getSpeed()).append(" km/hr").append("\\n")
					.append("Direction: ").append(alertBoats.get(i).getDirection()).append(" 度").append("\\n")
					.append("Callsign: ").append(alertBoats.get(i).getCallsign()).append("\\n")
					.append("Country: ").append(alertBoats.get(i).getCountry()).append("\\n")
					.append("Ship Name: ").append(alertBoats.get(i).getShipName()).append("\\n")
					.append("Heading: ").append(alertBoats.get(i).getHeading()).append(" 度").append("\\n")
					.append("IMO: ").append(alertBoats.get(i).getImo()).append("\\n")
					.append("Length Overall: ").append(boatLength).append(" m").append("\\n")
					.append("Breadth Extreme: ").append(boatWidth).append(" m").append("\\n")
					.append("Type: ").append(alertBoats.get(i).getTypeText()).append("\\n");
			}
			alertMsgSB.append("****************************************").append("\\n\\n");
		}
		%>
		
		var alertMsg = "<%=alertMsgSB.toString()%>";
		document.getElementById("alertTxtArea").innerHTML = alertMsg;
	}//showAlertBoat END
	
	
	
	
	
	
	
	
	
	
///////////////////////////////////////////////////////////////////////////////////////////////		
	//偵測船隻是否在警戒區內
	function detectWarn(){
		for(var i=0; i<warnPolygonAry.length; i++){
			<%
			for( Map.Entry<String, ArrayList<BoatInfo>> entry : MMSIsList.entrySet() ){		//iterate hashmap
				String key = entry.getKey();	//key: MMSI
				ArrayList<BoatInfo> boats = entry.getValue();	//value: [BoatInfo, BoatInfo, BoatInfo...]
			%>
				if( warnPolygonAry[i].containsPoint(new CLatLng("<%=boats.get(boats.size()-1).getLatitude()%>", "<%=boats.get(boats.size()-1).getLongitude()%>"))==true )
				{
					console.log("%c ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓", 'color: red');
					console.log("%c CAUTION!!! BOAT <" + "<%=boats.get(boats.size()-1).getMmsi()%>" + "> is in warning area.", 'color: red');
					console.log("%c (Lat: " + "<%=boats.get(boats.size()-1).getLatitude()%>" + 
							", Lng: " + "<%=boats.get(boats.size()-1).getLongitude()%>" + "), " + "Speed: " + "<%=boats.get(boats.size()-1).getSpeed()%>" + ".", 'color: red');
					console.log("%c ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑", 'color: red');
				}
			<%}%>
		}
	}//detectWarn END
	
	
	
///////////////////////////////////////////////////////////////////////////////////////////////		
	//按下按鈕後顯示/隱藏船隻軌跡
	function showHideBoatTrack(){
		if(document.getElementById("boatTrackBtn").value=="印出船隻軌跡")
		{
			document.getElementById("boatTrackBtn").value="隱藏船隻軌跡";
			setTimeout("showBoatCollection()", 100);
		}
		else
		{
			document.getElementById("boatTrackBtn").value="印出船隻軌跡";
			OC_boat.hide();
			OC_boatTrack.hide();
		}
	}//showHideBoatTrack END
	
	
///////////////////////////////////////////////////////////////////////////////////////////////	
	function drawBoatAndTrack(){
		OC_boat.clear();		//先清空內容，避免重複資料
		OC_lastBoat.clear();
		OC_boatTrack.clear();
		
		<%
		for( Map.Entry<String, ArrayList<BoatInfo>> entry : MMSIsList.entrySet() ){		//iterate hashmap
		%>
			var trackAry = [];	//存放boat track point
			<%
			String key = entry.getKey();	//key: MMSI
			ArrayList<BoatInfo> boats = entry.getValue();	//value: [BoatInfo, BoatInfo, BoatInfo...]
			for(int i=0; i<boats.size(); i++)
			{
				if(i != boats.size()-1)		//最後一個點之前船都畫成圓點
				{%>
					//addBoatMarker()定義在mapFunction.js
					OC_boat.push(addBoatMarker(boatPointImg, "<%=boats.get(i).getTimestamp()%>", "<%=boats.get(i).getMmsi()%>", "<%=boats.get(i).getLatitude()%>", "<%=boats.get(i).getLongitude()%>", "<%=boats.get(i).getSpeed()%>", "<%=boats.get(i).getDirection()%>"));		
				<%}
				else	//最後一個點才畫出船的圖案
				{%>
<%-- 					OC_boat.push(addBoatMarker(boatImg, "<%=boats.get(i).getTimestamp()%>", "<%=boats.get(i).getMmsi()%>", "<%=boats.get(i).getLatitude()%>", "<%=boats.get(i).getLongitude()%>", "<%=boats.get(i).getSpeed()%>", "<%=boats.get(i).getDirection()%>")); --%>
					OC_lastBoat.push(addBoatMarker(boatImg, "<%=boats.get(i).getTimestamp()%>", "<%=boats.get(i).getMmsi()%>", "<%=boats.get(i).getLatitude()%>", "<%=boats.get(i).getLongitude()%>", "<%=boats.get(i).getSpeed()%>", "<%=boats.get(i).getDirection()%>"));
				<%}%>
				
				trackAry.push(new CLatLng("<%=boats.get(i).getLatitude()%>", "<%=boats.get(i).getLongitude()%>"));
			<%}%>
			
			OC_boatTrack.push(addBoatTrack(trackAry, 0));		//addBoatTrack()定義在mapFunction.js，0代表track color=black (First load map時不應該有track，所以這裡addBoatTrack其實沒用。找時間拿掉!)
		<%}%>
		
		setTimeout(function(){ OC_lastBoat.show("canvas");}, 100);
		
	}//drawBoat END
	
	
	
	
	
	
	
	//以 show("canvas") 的方式疊加，表示是以繪製方式，若不加入 "canvas" 參數，則會直接以 CMarker 物件疊加，效能較低。
	function showBoatCollection(option){
		OC_boat.show("canvas");	
		OC_boatTrack.show("canvas");
	}//showBoatCollection END
	
	
///////////////////////////////////////////////////////////////////////////////////////////////	
	//畫出海纜警戒區
	function drawSubCableWarn(){
		<%
		for( Map.Entry<String, ArrayList<SubCableInfo>> entry : subCableWarnList.entrySet() ){
		%>
			var subCableWarnPosAry = [];	//存放海纜警戒區的座標(submarine cable warning position array)
			<%
			String subCableWarnName = entry.getKey();	//key: 海纜警戒區的名稱(抓檔名)
			ArrayList<SubCableInfo> subCableWarns = entry.getValue();	//value: [SubCableInfo, SubCableInfo, SubCableInfo...]
			for(int i=0; i<subCableWarns.size(); i++)
			{%>
				subCableWarnPosAry.push(new CLatLng("<%=subCableWarns.get(i).getLatitude()%>", "<%=subCableWarns.get(i).getLongitude()%>"));
			<%}%>
			
			warnPolygonAry.push(addSubCableWarnArea(map, "<%=subCableWarnName%>", subCableWarnPosAry));		//將回傳polygon存成array，以利告警function存取
		<%}%>
	}//drawSubCableWarn END

	
///////////////////////////////////////////////////////////////////////////////////////////////	
	//畫出海纜
	function drawSubCable(){
		<%
		for( Map.Entry<String, ArrayList<SubCableInfo>> entry : subCableList.entrySet() ){
		%>
			var subCablePosAry = [];	//存放海纜座標(submarine cable position array)
			<%
			String subCableName = entry.getKey();	//key: 海纜名稱(抓檔名)
			ArrayList<SubCableInfo> subCables = entry.getValue();	//value: [SubCableInfo, SubCableInfo, SubCableInfo...]
			for(int i=0; i<subCables.size(); i++)
			{%>
				subCablePosAry.push(new CLatLng("<%=subCables.get(i).getLatitude()%>", "<%=subCables.get(i).getLongitude()%>"));
			<%}%>
			
			addSubCableLine(map, "<%=subCableName%>", subCablePosAry);
		<%}%>
	}//drawSubCable END
	
	
	
///////////////////////////////////////////////////////////////////////////////////////////////		
	function downloadFile(zip, fileName, data, flag){
		
		if(flag == 0)	//1~N-1艘船
		{
			zip.file(fileName+".csv", data);
		}
		else if(flag == 1)	//最後一艘船
		{
			zip.file(fileName+".csv", data);
			zip.generateAsync({type:"blob"})
			.then(function(content) {
			    // see FileSaver.js
			    saveAs(content, "track.zip");
			});
		}
		
// 		一次下載一個檔的寫法 (未壓縮)，chrome一次最多只能下載10個檔，code先保留。
// 		let blob = new Blob( [data], {type: "application/octet-stream"} );
// 		var href = URL.createObjectURL(blob);
// 		// 從 Blob 取出資料
// 		var link = document.createElement("a");
// 		document.body.appendChild(link);
// 		link.href = href;
// 		link.download = fileName + ".csv";
// 		link.click();
	}//downloadFile END


	function doDownloadTrack(){
		var zip = new JSZip();	//每次點選下載皆重新產生一個JSZip object
		var downloadMMSI;
		var downloadTrack = "Timestamp,MMSI,Latitude,Longitude,Speed Over Ground (km/hr),Course Over Ground\n";
		var tempMMSI = "";
		var count = 0;
		
		if(downloadInfo == null)
		{
			alert("請先進行搜尋，再下載軌跡檔");
		}else
		{
			for(var i=0; i<downloadInfo.length; i+=6)
			{
				if(downloadInfo[i+1] != tempMMSI && tempMMSI != "")
				{
						downloadFile(zip, tempMMSI, downloadTrack, 0);
						downloadTrack = "Timestamp,MMSI,Latitude,Longitude,Speed Over Ground (km/hr),Course Over Ground\n";		//clear downloadTrack for the next boat
				}
				
				downloadTrack += downloadInfo[i] + "," + downloadInfo[i+1] + "," + downloadInfo[i+2] + "," + downloadInfo[i+3] + "," + downloadInfo[i+4] + "," + downloadInfo[i+5] + "\n";
				tempMMSI = downloadInfo[i+1];
			}
			
			//最後一艘船的最後一筆資料不會進到inner if condotion，因此在這裡做download的處理
			downloadFile(zip, tempMMSI, downloadTrack, 1);
		}
		
	}//downloadTrack END	
	
	

///////////////////////////////////////////////////////////////////////////////////////////////	
	function pastMMSI2Search(elem, ll){
		var focusList = [];
		focusList = OC_boat.getFocus(ll).concat(OC_lastBoat.getFocus(ll)); //透過 getFocus 函式，取得滑鼠有碰到的物件，因為可以多個，所以是陣列
		var mmsiList = "";
		var tempmmsi = "";
		
		for (var i = 0; i < focusList.length; i++) 
		{
			tempmmsi = focusList[i].title.split("<br>")[1].split(":")[1].trim();
			if(!mmsiList.includes(tempmmsi))	//mmsiList沒有才append tempmmsi，怕append到重複的mmsi
			{
				mmsiList += tempmmsi;
				if(i != focusList.length-1)	//1 ~ N-1項後面加逗點
				{
					mmsiList += ",";
				}
			}else	//進到else代表mmsiList已有此mmsi，要把最後一個逗號拿掉
			{	
				mmsiList = mmsiList.slice(0, -1); 
			}
		}
		
		if(focusList.length != 0)	//若滑鼠點擊在船隻上才清空搜尋欄，否則會造成只是拖曳地圖但搜尋條件被清空
		{
			//paste mmsiList to search field 
			document.getElementById('mmsi').value = mmsiList;
			//clear latLng from search field
			document.getElementById('userMkr_lat').value = "";
			document.getElementById('userMkr_lng').value = "";
		}
		
	}//pastMMSI2Search END








	//搜尋歷史紀錄後畫出船及軌跡，實作方法與drawBoatAndTrack()相似但不盡相同，這裡無法用java code，故重寫
	function drawHistory(infos){
// 		map.clearOverlays();
		OC_boat.clear();		//先清空內容，避免重複資料
		OC_lastBoat.clear();
		OC_boatTrack.clear();
		var trackAry = [];	//存放boat track point
		
		infos.shift();	//取出infos第一項，1代表搜尋條件有MMSI，0代表沒有。(目前用不到此識別，但還是先保留著)
		downloadInfo = infos;	//將info存到downloadInfo變數，以供下載軌跡檔使用
		var tempMMSI = "";
		var count = 0;
		for(var i=0; i<infos.length; i+=7)
		{
			if(infos[i+1] != tempMMSI)
			{
				//抓每艘船的最後一筆資料，畫出船的圖案。tempMMSI=""代表整個array的第一筆資料，不能OC_boat.push，會找不到index
				if(tempMMSI != "")	
				{
					OC_boat.removeAt(count-1);	//OC_boat移除每艘船的最後一筆資料，因這筆資料要換船的圖案
					count--;	//OC_boat取出最後一項後count要減1，才有辦法對應OC_boat array裡的資料數量
					OC_lastBoat.push(addBoatMarker(boatImg, infos[i-7], infos[i-6], infos[i-5], infos[i-4], infos[i-3], infos[i-2]));	
					OC_boatTrack.push(addBoatTrack(trackAry, infos[i-1]));	////畫1 ~ N-1艘船的軌跡
				}
				trackAry = [];
				tempMMSI = infos[i+1];
			}
			
			OC_boat.push(addBoatMarker(boatPointImg, infos[i], infos[i+1], infos[i+2], infos[i+3], infos[i+4], infos[i+5]));
			trackAry.push(new CLatLng(infos[i+2], infos[i+3]));
			count++;
		}
		OC_boat.removeAt(count-1);	//移除最後一艘船的最後一筆資料，因要改畫船的圖案
		OC_lastBoat.push(addBoatMarker(boatImg, infos[i-7], infos[i-6], infos[i-5], infos[i-4], infos[i-3], infos[i-2]));	//最後一艘船的最後一筆資料，畫出船的圖案
		OC_boatTrack.push(addBoatTrack(trackAry, infos[i-1]));	//畫最後一艘船的軌跡
		setTimeout(function(){ OC_lastBoat.show("canvas");}, 100);

		document.getElementById('searchBtn').disabled = false;	//結束搜尋時，將search btn解除disabled
		document.getElementById('downloadBtn').disabled = false;	//結束搜尋時，將download btn解除disabled
		
	}//drawHistory END
	
	
	//搜尋歷史船隻紀錄
	function search(){
		document.getElementById('searchBtn').disabled = true;	//按下搜尋時，將search btn設為disabled，避免重複點擊
		document.getElementById('downloadBtn').disabled = true;	//按下搜尋時，將download btn設為disabled，避免重複點擊
		
		var sTime_date = document.getElementById('startTime').value;
		var sTime_hr = document.getElementById('startTime_hr').value;
		var eTime_date = document.getElementById('endTime').value;
		var eTime_hr = document.getElementById('endTime_hr').value;
		var mmsi = document.getElementById('mmsi').value;
		var lat = document.getElementById('userMkr_lat').value;
		var lng = document.getElementById('userMkr_lng').value;
		var title = document.getElementById('userMkr_title').value;
		
		var sendStr = "";
		var sTime = sTime_date +  "-" + sTime_hr;
		var eTime = eTime_date + "-" + eTime_hr;

		
		document.getElementById("boatTrackBtn").value="印出船隻軌跡";	//reset boat track botton
		
		if( sTime<=eTime && sTime_date!='' && eTime_date!='')
		{
			if(mmsi!='' && lat=='' && lng=='')	//時間 + mmsi搜尋
			{
				console.log("---START QUERY---");
				console.log("mmsi: " + mmsi + "\nsTime: " + sTime + "\neTime: " + eTime);
				sendStr = "sTime=" + sTime + "&eTime="+ eTime + "&mmsi=" + mmsi + "&lat=" + lat + "&lng=" + lng;
				makeRequest("searchHistory.jsp" , sendStr);	
			}else if(mmsi=='' && lat!='' && lng!='')	//時間 + lat、lng搜尋
			{
				console.log("---START QUERY---");
				console.log("location: (" + lat + "," + lng + ")\nsTime: " + sTime + "\neTime: " + eTime);
				addUserMkr(lat, lng, title);
				sendStr = "sTime=" + sTime + "&eTime="+ eTime + "&mmsi=" + mmsi + "&lat=" + lat + "&lng=" + lng;
				makeRequest("searchHistory.jsp" , sendStr);	
			}else
			{
				alert("查詢條件不完整!");	
				document.getElementById('searchBtn').disabled = false;	//結束搜尋時，將search btn解除disabled
				document.getElementById('downloadBtn').disabled = false;	//結束搜尋時，將download btn解除disabled
			}
		}else	//查詢條件一定要有時間，且起始時間<終止時間，其餘組合皆視為時間不正確。
		{
			alert("查詢時間不正確，請重新操作!");
			clearCalendar("all");
			document.getElementById('searchBtn').disabled = false;	//結束搜尋時，將search btn解除disabled
			document.getElementById('downloadBtn').disabled = false;	//結束搜尋時，將download btn解除disabled
		}
		
	}//searchHistory END

	
	//跳轉到其他jsp
	function makeRequest(url, sendStr) {
		var httpRequest;
		if (window.XMLHttpRequest)	// Mozilla, Safari, ... 
		{ 	
			httpRequest = new XMLHttpRequest();
			if (httpRequest.overrideMimeType) 
			{
				httpRequest.overrideMimeType('text/xml');
			}
		} else if (window.ActiveXObject) 	// IE
		{ 
			try {
				httpRequest = new ActiveXObject("Msxml2.XMLHTTP");
			} catch (e) {
				try {
					httpRequest = new ActiveXObject("Microsoft.XMLHTTP");
				} catch (e) {
				}
			}
		}
		if (!httpRequest) 
		{
			alert('Giving up :( Cannot create an XMLHTTP instance');
			return false;
		}
		httpRequest.onreadystatechange = function() {
			alertContents(httpRequest);
		};
		
		httpRequest.open('POST', url, true);
		httpRequest.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded;charset=big5');

		httpRequest.send(sendStr);
	}//makeRequest END
	
	
	//處理從別的jsp回來的response
	function alertContents(httpRequest) {
		if (httpRequest.readyState == 4) 
		{
			if (httpRequest.status == 200) 
			{
				var textdoc = httpRequest.responseText;
				var textdocAry = trim(textdoc).split("|");
				var recognize = textdocAry.shift();
				if(recognize == "searchHistory")
				{
					var infos = textdocAry;
					infos.pop();	//拿掉最後一項: 最後一項是空的，是"|"造成的empty item
// 	 				console.log(infos);
					drawHistory(infos);
					
					
					
				}else if(recognize == "searchBoatBasicInfo")
				{
					var basicInfos = textdocAry;
					basicInfos.pop();	//拿掉最後一項: 最後一項是空的，是"|"造成的empty item
// 					console.log(basicInfos);
					ListBasicInfo(basicInfos);
				}
				
			}else 
			{
				alert('There was a problem with the request.' + httpRequest.status);
			}
		}
	}//alertContents END

	
	//去除字串左邊的空白虛格
	function ltrim(instr) {
		return instr.replace(/^[\s]*/gi, "");
	}

	//去除字串右邊的空白虛格
	function rtrim(instr) {
		return instr.replace(/[\s]*$/gi, "");
	}

	//去除字串前後的空白虛格
	function trim(instr) {
		instr = ltrim(instr);
		instr = rtrim(instr);
		return instr;
	}
	
	
	
///////////////////////////////////////////////////////////////////////////////////////////////		
	//清除搜尋日期
	function clearCalendar(option){
		if(option=="begin")
		{
			document.getElementById('startTime').value="";
			document.getElementById('startTime_hr').selectedIndex = 0;
		}
		else if(option=="end")
		{
			document.getElementById('endTime').value="";
			document.getElementById('endTime_hr').selectedIndex = 23;
		}
		else
		{
			document.getElementById('startTime').value="";
			document.getElementById('endTime').value="";
			document.getElementById('startTime_hr').selectedIndex = 0;
			document.getElementById('endTime_hr').selectedIndex = 23;
			
		}
	}//clearCalendar END
	
	
	
///////////////////////////////////////////////////////////////////////////////////////////////		
	//讓使用者在地圖上新增座標點
	function addUserMkr(lat, lng, title){
		var userMkr = new CMarker(new CLatLng(lat, lng), {icon: errorImg, title: "", clickable: true, draggable: true});
// 		userMkr.enableDragging();
		CEvent.addListener(userMkr, "click", function (eventmkr) { 
			this.openInfoWindow("<div style=\"background-color:#fce4b3\">Message: " + title + "</div>" + "<div style=\"background-color:#d4fcb6\">Lat: " + lat + "</div>" + "<div style=\"background-color:#d4fcb6\">Lng: " + lng + "</div>"); });
	    map.addOverlay(userMkr);
	}//addUserMkr END
	
	

	
///////////////////////////////////////////////////////////////////////////////////////////////	
	//
	function ListBasicInfo(basicInfos){
		var text2Show = "";
		
		for(var i=0; i<basicInfos.length; i+=10)
		{
			
			text2Show += "**MMSI** " + basicInfos[i] 
						+ "\n -Callsign: " + basicInfos[i+1]
						+ "\n -Country: " + basicInfos[i+2]
						+ "\n -IMO: " + basicInfos[i+3]
						+ "\n -Ship Name: " + basicInfos[i+4]
						+ "\n -Length Overall: " + (parseFloat(basicInfos[i+5])+parseFloat(basicInfos[i+6])) + " m"
						+ "\n -Breadth Extreme: " + (parseFloat(basicInfos[i+7])+parseFloat(basicInfos[i+8])) + " m"
						+ "\n -Type: " + basicInfos[i+9] 
						+ "\n\n";
		}
		document.getElementById("mmsiBasicInfoTxtArea").innerHTML=text2Show;
	}//ListBasicInfo END




	//查詢船隻基本資訊
	function searchBasicInfo(){
		var mmsi = document.getElementById('mmsiBasicInfo').value;
		
		if(mmsi != '')
		{
			sendStr = "mmsi=" + mmsi;
			makeRequest("searchBoatBasicInfo.jsp" , sendStr);	
		}else
		{
			alert("查詢條件不正確，請重新操作!");
		}
			
		
	}//searchBasicInfo END



///////////////////////////////////////////////////////////////////////////////////////////////		
	function latLngTransfer(){
		var lat_d = document.getElementById('lat_d').value;
		var lat_m = document.getElementById('lat_m').value;
		var lat_s = document.getElementById('lat_s').value;
		var lng_d = document.getElementById('lng_d').value;
		var lng_m = document.getElementById('lng_m').value;
		var lng_s = document.getElementById('lng_s').value;
		var lat_degree;
		var lng_degree;
		
		if(lat_d=="" || lng_d=="")
		{
			alert("請輸入正確經緯度");
		} else
		{
			lat_degree = parseFloat(lat_d) + parseFloat(lat_m/60) + parseFloat(lat_s/3600);
			lng_degree = parseFloat(lng_d) + parseFloat(lng_m/60) + parseFloat(lng_s/3600);
			document.getElementById('lat_Degree').value = lat_degree;
			document.getElementById('lng_Degree').value = lng_degree;
		}
	}//latLngTransfer END
	
	function pasteLatLng2Search(){
		var lat_degree = document.getElementById('lat_Degree').value;
		var lng_degree = document.getElementById('lng_Degree').value;
		document.getElementById('userMkr_lat').value = lat_degree;
		document.getElementById('userMkr_lng').value = lng_degree;
		
		//clear input field of latLngTransfer
		document.getElementById('lat_d').value = "";
		document.getElementById('lat_m').value = "";
		document.getElementById('lat_s').value = "";
		document.getElementById('lng_d').value = "";
		document.getElementById('lng_m').value = "";
		document.getElementById('lng_s').value = "";
		document.getElementById('lat_Degree').value = "";
		document.getElementById('lng_Degree').value = "";
	}//pasteLatLng2Search END
	
	
	
	
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

	
	
	
///////////////////////////////////////////////////////////////////////////////////////////////		
	function setAutoRefresh(){
		var srcVal = document.getElementById("refreshBtn").src;
// 		console.log(srcVal);
		if(srcVal.indexOf('black') >= 0){	//關閉auto refresh
			document.getElementById("refreshBtn").src = '../../images/refresh.png';
			
		}
		else{	//打開auto refresh
			document.getElementById("refreshBtn").src = '../../images/refresh_black.png';
// 			setInterval(function() {loadMap();}, 2000);
		}
// 			setTimeout(function() {
// 				  location.reload();
// 				}, 3000);
	}//setAutoRefresh END
	
	
</script>

</head>






<body style="background-color: #F0F0F0;" onload="loadMap()" onunload="CUnload(1)">
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
					<li class="nav-item active" style="font-size: 20px; width: 200px;"><a class="nav-link" href="#">Map</a>
					</li>
					<li class="nav-item" style="font-size: 20px; width: 200px;"><a class="nav-link" href="../settingPage.jsp">Setting</a>
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





<form id="form" name="form" method="post" action="">
<table>
	<tr>
		<td>
			<div class='map' id="map" style=" width:1200px; height:850px; border:1px solid #000;"></div>
  		</td>
  		<td valign="top">
  			<div class="divv">
  				<span>現在時間: </span>
  				<span id="datetime"></span>
  				
<!--   				<img id="refreshBtn" src='../../images/refresh.png' width="25" height="25" onClick="setAutoRefresh()" /> -->
  			</div>
  			<div class="divv" style="margin: 20px 0px 0px 30px">
  				<span>==船隻軌跡==</span>
  				<span style="position:absolute; top:0px; left:230px;">==經緯度轉換==</span>
  				<br><br>
  				<span>
    			<input type="button" id="boatTrackBtn" name="boatTrackBtn" value="印出船隻軌跡" onClick="showHideBoatTrack()">
    			</span>
    			<span style="position:absolute; left:230px;">
    			<input type="text" name="lat_d" id="lat_d" style="width:80px ;height:25px;" placeholder="緯度">
    			<input type="text" name="lat_m" id="lat_m" style="width:80px ;height:25px;" placeholder="緯分">
    			<input type="text" name="lat_s" id="lat_s" style="width:80px ;height:25px;" placeholder="緯秒">
    			</span>
    			<br>
    			<span style="position:absolute; left:230px;">
    			<input type="text" name="lng_d" id="lng_d" style="width:80px ;height:25px;" placeholder="經度">
    			<input type="text" name="lng_m" id="lng_m" style="width:80px ;height:25px;" placeholder="經分">
    			<input type="text" name="lng_s" id="lng_s" style="width:80px ;height:25px;" placeholder="經秒">
    			</span>
    			<br><br>
    			<span style="position:absolute; left:230px;">
    			<input type="text" name="lat_Degree" id="lat_Degree" style="width:105px ;height:25px;" placeholder="緯度" readonly>
    			<input type="text" name="lng_Degree" id="lng_Degree" style="width:105px ;height:25px;" placeholder="經度" readonly>
    			<input type="button" name="latLngTransfer_btn" id="latLngTransfer_btn" value="轉換" onClick="latLngTransfer()">
    			<input type="button" name="paste2Search_btn" id="paste2Search_btn" value="貼至搜尋框" onClick="pasteLatLng2Search()">
    			</span>
  			</div>
  			
  			<div class="divv" style="margin: 30px 0px 0px 30px">
  				<p>==查詢船隻歷史紀錄==</p>
  				起: <input type="date" id="startTime" name="startTime" value="2020-12-15">
  					<select name="startTime_hr" id="startTime_hr">
  						<script language="javascript" type="text/javascript"> 
							for(var hr=0;hr<24;hr++)
							{
								hr = hr<10 ? '0'+hr : hr;
								document.write("<option value='" + hr +"'>"+hr+":00</option>");
							}
						</script>
					</select>
				<input type="button" name="clearST" value=" X " title="清除起始時間" onClick="clearCalendar('begin')">
  			</div>
  			<div class="divv">
  				迄: <input type="date" id="endTime" name="endTime" value="2020-12-15">
  					<select name="endTime_hr" id="endTime_hr">
  						<script language="javascript" type="text/javascript"> 
							for(var hr=0;hr<24;hr++)
							{
								hr = hr<10 ? '0'+hr : hr;
    							document.write("<option value='" + hr +"'>"+hr+":59</option>");
							}
							document.getElementById('endTime_hr').selectedIndex = 23;
						</script>
					</select>
				<input type="button" name="clearET" value=" X " title="清除終止時間" onClick="clearCalendar('end')">
  			</div>
  			<div class="divv">
  				<input type="text" name="mmsi" id="mmsi" placeholder="MMSI，多筆請以逗號隔開" value="994160451">
  			</div>
  			<div class="divv">
  				<input type="text" name="userMkr_lat" id="userMkr_lat" placeholder="座標點緯度" >
  				<input type="text" name="userMkr_lng" id="userMkr_lng" placeholder="座標點經度" >
  				<input type="text" name="userMkr_lng" id="userMkr_title" placeholder="座標點文字資訊">
  			</div>
  			<div class="divv">
  			    <input type="button" id="searchBtn" name="searchBtn" value="搜尋" onClick="search()">
    			<input type="button" id="downloadBtn" name="downloadBtn" value="下載軌跡檔" onClick="doDownloadTrack()">
  			</div>
  			
  			<div class="divv" style="margin: 30px 0px 0px 30px">
  				<span>==查詢船隻基本資訊==</sapn>
    			<span style="position:absolute; top:0px; left:310px;">==告警清單==</span>
    			<br><br>
  				<input type="text" name="mmsiBasicInfo" id="mmsiBasicInfo" placeholder="MMSI，多筆請以逗號隔開" value="">
    			<input type="button" id="mmsiBasicInfoBtn" name="mmsiBasicInfoBtn" value="搜尋" onClick="searchBasicInfo()">
  			</div>
  			<div class="divv">
  			<textarea id="mmsiBasicInfoTxtArea" name="mmsiBasicInfoTxtArea" style="width:300px;height:250px;" readonly>
			</textarea>	
			<textarea id="alertTxtArea" name="alertTxtArea" style="color:red;width:300px;height:250px;" readonly>
			</textarea>	
  			</div>
  			
  		</td>
	</tr>
</table>

</form>


	<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
	<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>
</body>


<!-- <script language="JavaScript"> -->
<!-- //   	document.getElementById('endTime').value = new Date().toISOString().substr(0, 10); -->
<!-- //   	document.getElementById('startTime').value = new Date().toISOString().substr(0, 10); -->
<!-- </script> -->




</html>
