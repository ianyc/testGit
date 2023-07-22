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
<%@page import = "java.text.SimpleDateFormat"%>
<%@page import = "java.util.Date"%>
<jsp:useBean id="Env" scope="page" class="com.chtn.util.Env"/>



<%
long time1 = System.currentTimeMillis();	
	Logger logger = LogManager.getLogger(this.getClass());

	String mmsi = request.getParameter("mmsi");
	String lat = request.getParameter("lat");
	String lng = request.getParameter("lng");
	String sTime = request.getParameter("sTime").replace("-", "_");
	String eTime = request.getParameter("eTime").replace("-", "_");

// 	System.out.println("\n*/*/*/*/*/*/*/*/*/*/*/*/*/*/");
// 	System.out.println("Start to Search History...");
// 	System.out.println("MMSI: " + mmsi);
// 	System.out.println("lat: " + lat);
// 	System.out.println("lng: " + lng);
// 	System.out.println("From: " + sTime);
// 	System.out.println("To: " + eTime);
// 	System.out.println("*/*/*/*/*/*/*/*/*/*/*/*/*/*/");
	logger.info("======查詢船隻歷史紀錄======");
	logger.info("From: " + sTime);
	logger.info("To: " + eTime);
	logger.info("MMSI: " + mmsi);
	logger.info("lat: " + lat);
	logger.info("lng: " + lng);
	
	
	int dataType = 0;
	File filePath = new File(Env.BOAT_PROCESSED_DATA_PATH);
	String packType = "boatProcessed";
	ParseData parseData = new ParseData();
	ArrayList<BoatInfo> boatInfos = new ArrayList();	//BoatInfo[] infos每換一份檔內容就會被覆蓋，因此用ArrayList boatInfos來append
	File[] files = filePath.listFiles();
	Arrays.sort(files);
	
	
 	for(File file : files){		//loop folder裡全部檔案
		if( sTime.compareTo(file.getName().split("\\.")[0].trim())<=0 && eTime.compareTo(file.getName().split("\\.")[0].trim())>=0 ){	//搜尋區間有找到.csv檔案
			BoatInfo[] infos = parseData.readFromProcessedFile(filePath.getAbsolutePath()+Env.FILE_SEPARATOR, file.getName(), dataType, packType, mmsi, lat, lng);
			boatInfos.addAll(Arrays.asList(infos));		
		}
	}
 	
 	
	//原jsp收到response時利用第一個pipeline前的文字識別是哪一種request。
 	out.print("searchHistory|");
	
	
 	if(boatInfos.size() != 0){	//size=0代表搜尋區間找不到.csv檔案
 		BoatInfo[] tempBoatAry = new BoatInfo[boatInfos.size()];	///sortBoatByMMSI()需代入array，因此這裡將arrayList轉成array
 		tempBoatAry = boatInfos.toArray(tempBoatAry);
 		SortEntity sortEntity = new SortEntity();
 		HashMap<String, ArrayList<BoatInfo>> MMSIsList = sortEntity.sortBoatByMMSI(tempBoatAry);	//MMSIsList存放的是key=mmsi、value=此mmsi所有資料(arrayList)
 				
 		if(mmsi != "")	//1: mmsi為搜尋條件、0: latlng為搜尋條件
 			out.print("1|");
 		else
 			out.print("0|");
 				
 		
 		for( Map.Entry<String, ArrayList<BoatInfo>> entry : MMSIsList.entrySet() ){		//iterate hashmap
 			String key = entry.getKey();	//key: MMSI
			ArrayList<BoatInfo> boats = entry.getValue();	//value: [BoatInfo, BoatInfo, BoatInfo...]
			for(int i=0; i<boats.size(); i++)
			{
				
				
				
// 				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
// 				Date date_i;
// 				Date date_before_i;
// 				Date date_after_i;
// 				long diff_front;
// 				long diff_back;
				
// 				if(i!=0 && i!=boats.size()-1){	//頭、尾不處理
// 					//找出(i~i-1)、(i~i+1)時間差(sec)
// 					date_before_i = sdf.parse(boats.get(i-1).getTimestamp());
//  					date_i = sdf.parse(boats.get(i).getTimestamp());
//  					date_after_i = sdf.parse(boats.get(i+1).getTimestamp());
//  					diff_back = date_after_i.getTime() - date_i.getTime();
//  					diff_front = date_i.getTime() - date_before_i.getTime();
 					
//  					System.out.println(boats.get(i).getSpeed());
 					
 					
 					
// 				}else{	
// 					diff_back = 0;
// 					diff_front = 0;
// 				}
				
				
// 				System.out.println("front: " + diff_front/1000 + " sec");
// 				System.out.println("back: " + diff_back/1000 + " sec");
				
				
				
				
				
				
				
				out.print(boats.get(i).getTimestamp() + "|" + boats.get(i).getMmsi() + "|" + boats.get(i).getLatitude() + "|" + boats.get(i).getLongitude() + "|" + boats.get(i).getSpeed() + "|" + boats.get(i).getDirection() + "|" + boats.get(i).getTypeCode() + "|");
// 				System.out.println(boats.get(i).getTimestamp() + "|" + boats.get(i).getMmsi() + "|" + boats.get(i).getLatitude() + "|" + boats.get(i).getLongitude() + "|" + boats.get(i).getSpeed() + "|" + boats.get(i).getDirection() + "|" + boats.get(i).getTypeCode() + "|");
			}
 		}//iterate hashmap END
 	}else{
 		out.println("EMPTY");
//  		System.out.println("區間內沒有找到檔案!");
 		logger.info("區間內沒有找到檔案!");
 	}//if(boatInfos.size() != 0) END
 	
 	logger.info("==查詢完畢，總共" + boatInfos.size() + "筆資料==");
 	
long time2 = System.currentTimeMillis();
System.out.println("耗時共: " + (time2-time1) + " ms");
%>








