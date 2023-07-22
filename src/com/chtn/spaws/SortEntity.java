package com.chtn.spaws;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.chtn.spaws.entity.BoatInfo;
import com.chtn.spaws.entity.SubCableInfo;
import com.chtn.spaws.parse.ParseData;
import com.chtn.util.Env;

public class SortEntity {
	
	/**
	 * 傳入一個或多個海纜檔的boatInfo array，將排序成{ 告警檔1:[[告警檔1的BoatInfo1],[告警檔1的BoatInfo2]..], 告警檔2:[[告警檔2的BoatInfo1],[告警檔2的BoatInfo2]..] }
	 * @param boatInfos
	 * @return HashMap<String, ArrayList<BoatInfo>>
	 */
	public HashMap<String, ArrayList<BoatInfo>> sortBoatByAlertRecordFile(BoatInfo[] boatInfos) {
		HashMap<String, ArrayList<BoatInfo>> boatList = new HashMap<>();
		ArrayList<BoatInfo> boat;
		
		for (int i = 0; i < boatInfos.length; i++) {
			if ( boatList.get(boatInfos[i].getAlertRecordFileName()) == null ) {	//如果alertRecordFileName.get抓不到，代表此AlertRecordFileName是新的檔名
				boat = new ArrayList<>();
				boat.add(boatInfos[i]);
				boatList.put(boatInfos[i].getAlertRecordFileName(), boat);
			}else {		//如果alertRecordFileName.get存在，則把該boatInfo加到此alertRecordFileName對應的value(arrayList)中
				boat = boatList.get(boatInfos[i].getAlertRecordFileName());
				boat.add(boatInfos[i]);
				boatList.put(boatInfos[i].getAlertRecordFileName(), boat);
			}
		}
		return boatList;
	}//sortBoatByAlertRecordFile END
	

	
	/**
	 * 傳入解析後的船隻資訊boatInfo array，並以key-value的形式回傳。(key:MMSI、value:此MMSI的所有船隻資料，type為ArrayList<BoatInfo>)
	 * @param boatInfos
	 * @return HashMap<String, ArrayList<BoatInfo>>
	 */
	public HashMap<String, ArrayList<BoatInfo>> sortBoatByMMSI(BoatInfo[] boatInfos) {
		HashMap<String, ArrayList<BoatInfo>> boatList = new HashMap<>();	//用來存放不同的船隻編號，同時可計算有幾艘船。key: mmsi、value: ArrayList<BoatInfo>。
		ArrayList<BoatInfo> boat;
		
		for (int i = 0; i < boatInfos.length; i++) {
			if ( boatList.get(boatInfos[i].getMmsi()) == null ) {	//如果MMSIs.get抓不到，代表此MMSI是新的船隻
				boat = new ArrayList<>();
				boat.add(boatInfos[i]);
				boatList.put(boatInfos[i].getMmsi(), boat);
			}else {		//如果MMSI.get存在，則把該boatInfo加到此MMSI對應的value(arrayList)中
				boat = boatList.get(boatInfos[i].getMmsi());
				boat.add(boatInfos[i]);
				boatList.put(boatInfos[i].getMmsi(), boat);
			}
		}
		return boatList;
	}//sortByMMSI END
	
	
	/**
	 * 傳入一個或多個海纜檔的SubCableInfo array，將排序成{ 海纜1:[[海纜1的SubCableInfo1],[海纜1的SubCableInfo2]..], 海纜2:[[海纜2的SubCableInfo1],[海纜2的SubCableInfo2]..] }
	 * @param subCableInfos
	 * @return HashMap<String, ArrayList<SubCableInfo>>
	 */
	public HashMap<String, ArrayList<SubCableInfo>> sortSubCableByName(SubCableInfo[] subCableInfos) {
		HashMap<String, ArrayList<SubCableInfo>> subCableList = new HashMap<>();	
		ArrayList<SubCableInfo> subCable;
		
		for(int i=0; i<subCableInfos.length; i++) {
			if(subCableList.get(subCableInfos[i].getSubCableName()) == null) {	//如果subCableInfos[i].getSubCableName()抓不到，代表此是新的海纜檔
				subCable = new ArrayList<>();
				subCable.add(subCableInfos[i]);
				subCableList.put(subCableInfos[i].getSubCableName(), subCable);
			}else {		//如果subCableInfos[i].getSubCableName()存在，則把該筆海纜資訊加到此海纜名稱對應的value(arrayList)中
				subCable = subCableList.get(subCableInfos[i].getSubCableName());
				subCable.add(subCableInfos[i]);
				subCableList.put(subCableInfos[i].getSubCableName(), subCable);
			}
		}
		return subCableList;
	}//sortSubCableByName END
	
	
	
	public static void main(String[] args) throws IOException {
		int dataType = 0;
		String filePath = Env.BOAT_PROCESSED_DATA_PATH;
		String fileName = "current.csv";
		String packType = "boatRaw";
		ParseData parseData = new ParseData();
		BoatInfo[] boatInfos = parseData.readFromMPBFile(filePath, fileName, dataType, packType);
		
		SortEntity sortEntity = new SortEntity();
		HashMap<String, ArrayList<BoatInfo>> MMSIsList = sortEntity.sortBoatByMMSI(boatInfos);
		//印出船隻資訊
		System.out.println("有幾艘船: " + MMSIsList.size());
		for( Map.Entry<String, ArrayList<BoatInfo>> entry : MMSIsList.entrySet() ){		//iterate hashmap
			String key = entry.getKey();
			ArrayList<BoatInfo> boats = entry.getValue();
			System.out.println("船編" + key + "有幾筆資料: " + boats.size());
		}
		
		
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
		int SCdataType = 0;
		File SCfilePath = new File(Env.SC_DATA_PATH);
		String SCpackType = "subCable";
		ParseData SCparseData = new ParseData();
	 	ArrayList<SubCableInfo> subCableInfos = new ArrayList<>();
	 	for(File file : SCfilePath.listFiles())		//一次loop folder裡全部海纜檔
	 	{
	 		SubCableInfo[] infos = SCparseData.readFromSCFile(SCfilePath.getAbsolutePath()+Env.FILE_SEPARATOR, file.getName(), dataType, SCpackType);
	 		subCableInfos.addAll(Arrays.asList(infos));
	 	}
		
	 	SubCableInfo[] TempSCAry = new SubCableInfo[subCableInfos.size()];	//由於sortSubCableByName()需代入array，因此這裡將arrayList轉成array
	 	TempSCAry = subCableInfos.toArray(TempSCAry);
		SortEntity SCsortEntity = new SortEntity();
		HashMap<String, ArrayList<SubCableInfo>> subCableList = SCsortEntity.sortSubCableByName(TempSCAry);
		//印出海纜資訊
		System.out.println("\n有幾條海纜: " + subCableList.size());
		for (Map.Entry<String, ArrayList<SubCableInfo>> entry : subCableList.entrySet()) {
			String key = entry.getKey();
			ArrayList<SubCableInfo> subCables = entry.getValue();
			System.out.println("海纜[" + key + "]有幾筆經緯度資料: " + subCables.size());
		}
		
		
		
	}//main END

}
