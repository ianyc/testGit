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



<%
	
	String mmsi = request.getParameter("mmsi");
	String[] mmsiAry = mmsi.split(",");	//可能有多筆
	
	int dataType = 0;
	String filePath = Env.BOAT_PROCESSED_DATA_PATH;
	String fileName = "MMSI_basic_info.csv";
	String packType = "boatBasicInfo";
	ParseData parseData = new ParseData();
	BoatInfo[] boatInfos = parseData.readFromMMSIFile(filePath, fileName, dataType, packType);
	
	//原jsp收到response時利用第一個pipeline前的文字識別是哪一種request。
	out.print("searchBoatBasicInfo|");

	for(int i=0; i<boatInfos.length; i++){
		for(String item : mmsiAry){
			if(boatInfos[i].getMmsi().equals(item)){
				out.print(boatInfos[i].getMmsi() + "|" 
							+ boatInfos[i].getCallsign() + "|"
							+ boatInfos[i].getCountry() + "|"
							+ boatInfos[i].getImo() + "|"
							+ boatInfos[i].getShipName() + "|" 
							+ boatInfos[i].getSizeA() + "|" 
							+ boatInfos[i].getSizeB() + "|" 
							+ boatInfos[i].getSizeC() + "|" 
							+ boatInfos[i].getSizeD() + "|" 
							+ boatInfos[i].getTypeText() + "|" 
							);
				
// 				System.out.print(boatInfos[i].getMmsi() + "|" 
// 						+ boatInfos[i].getCallsign() + "|"
// 						+ boatInfos[i].getCountry() + "|"
// 						+ boatInfos[i].getImo() + "|"
// 						+ boatInfos[i].getShipName() + "|" 
// 						+ boatInfos[i].getSizeA() + "|" 
// 						+ boatInfos[i].getSizeB() + "|" 
// 						+ boatInfos[i].getSizeC() + "|" 
// 						+ boatInfos[i].getSizeD() + "|" 
// 						+ boatInfos[i].getTypeText() + "|" 
// 						);
				
			}
		}
	}


%>








