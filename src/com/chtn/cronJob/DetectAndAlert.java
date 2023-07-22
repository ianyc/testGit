package com.chtn.cronJob;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import com.chtn.spaws.BoatTypeMapping;
import com.chtn.spaws.CountryMapping;
import com.chtn.spaws.SortEntity;
import com.chtn.spaws.entity.BoatInfo;
import com.chtn.spaws.entity.Point;
import com.chtn.spaws.entity.SubCableInfo;
import com.chtn.spaws.parse.ParseData;
import com.chtn.spaws.parse.ParseXML;
import com.chtn.util.Env;

public class DetectAndAlert {
	private String configFile = Env.ALERT_CFG_PATH;
	//再次告警時間間隔
	private int warnAgainPeriod_d;
	private int warnAgainPeriod_h;
	private int warnAgainPeriod_m;
	private String[] mailList;
	
	//Constructor
	public DetectAndAlert() {
		Hashtable hashData = null;
		ParseXML parseXML = new ParseXML();
		hashData = parseXML.getAlertConfig(configFile);
		//若無法從xml取得設定值，則代入預設值
		if(hashData!=null) {
			warnAgainPeriod_d = hashData.get("AlertDuration_d") != null ? Integer.parseInt(hashData.get("AlertDuration_d").toString()) : 0;
			warnAgainPeriod_h = hashData.get("AlertDuration_h") != null ? Integer.parseInt(hashData.get("AlertDuration_h").toString()) : 1;
			warnAgainPeriod_m = hashData.get("AlertDuration_m") != null ? Integer.parseInt(hashData.get("AlertDuration_m").toString()) : 0;
			mailList = hashData.get("mailList") != null ? hashData.get("mailList").toString().split(",") : new String[] {"ianyc@cht.com.tw"};
		}else {	
			warnAgainPeriod_d = 0;
			warnAgainPeriod_h = 1;
			warnAgainPeriod_m = 0;
			mailList = new String[] {"ianyc@cht.com.tw"};
		}
//		System.out.println(warnAgainPeriod_d);
//		System.out.println(warnAgainPeriod_h);
//		System.out.println(warnAgainPeriod_m);
//		for (String item : mailList)
//			System.out.println(item);
	}//Constructor END

	
	/**
	 * 寄送告警mail
	 * @param warnName
	 * @param boatList
	 */
	public void sendAlertMail(String warnName, ArrayList<BoatInfo> boatList, ArrayList<Integer> alertTimes) {
		MailAlert mailAlert = new MailAlert();
		
		//收件者
		mailAlert.setTo(mailList);
		
		//信件內容
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < boatList.size(); i++) {
//		for (BoatInfo boat : boatList) {
			//處理船的長、寬
			Double boatLength_d;
			String boatLength;
			Double boatWidth_d;
			String boatWidth;
			try {
				boatLength_d = Double.parseDouble(boatList.get(i).getSizeA()) + Double.parseDouble(boatList.get(i).getSizeB());
				boatLength = boatLength_d.toString();
			} catch (Exception e) {
				boatLength = "";
			}
			try {
				boatWidth_d = Double.parseDouble(boatList.get(i).getSizeC()) + Double.parseDouble(boatList.get(i).getSizeD());
				boatWidth = boatWidth_d.toString();
			} catch (Exception e) {
				boatWidth = "";
			}
			
			String chCountry = CountryMapping.mmsi2Country(boatList.get(i).getMmsi());	//利用MMSI對應出船隻國籍
			String chType = BoatTypeMapping.code2Text(boatList.get(i).getTypeCode());	//利用boat type code對應出船隻種類
			//mail content
			sb.append("************第" + alertTimes.get(i) + "次告警***********").append("\n")
				.append("Warning Area: ").append(warnName).append("\n")
				.append("MMSI: ").append(boatList.get(i).getMmsi()).append("\n")
				.append("TimeStamp: ").append(boatList.get(i).getTimestamp()).append("\n")
				.append("Position: (").append(boatList.get(i).getLatitude()).append(", ").append(boatList.get(i).getLongitude()).append(")").append("\n")
				.append("Speed: ").append(boatList.get(i).getSpeed()).append(" km/hr").append("\n")
				.append("Direction: ").append(boatList.get(i).getDirection()).append(" 度").append("\n")
				.append("Callsign: ").append(boatList.get(i).getCallsign()).append("\n")
				.append("Country: ").append(chCountry).append("\n")
				.append("Ship Name: ").append(boatList.get(i).getShipName()).append("\n")
				.append("Heading: ").append(boatList.get(i).getHeading()).append(" 度").append("\n")
				.append("IMO: ").append(boatList.get(i).getImo()).append("\n")
				.append("Length Overall: ").append(boatLength).append(" m").append("\n")
				.append("Breadth Extreme: ").append(boatWidth).append(" m").append("\n")
				.append("Type: ").append(chType).append("\n")
				.append("********************************")
				.append("\n\n\n");
		}
		System.out.println(sb.toString());
		
		//信件title
		mailAlert.setContent("[SAWS OA] " + warnName + "警戒區告警!!!", sb);
		
//		//Send
		mailAlert.send();
		
	}//sendAlertMail END
	
	
	/**
	 * 檢查船隻是否符合告警條件
	 * @param subCableWarnList
	 * @param boatInfos
	 */
	public void checkLastWarn(HashMap<String, ArrayList<SubCableInfo>> subCableWarnList, BoatInfo[] boatInfos) {
		BufferedReader br = null;
		FileWriter fw = null;
		String line = null;
		HashMap<String, String> fileContent = new HashMap<String, String>();
		ArrayList<BoatInfo> alertMailList = new ArrayList<>();	//要放進告警mail的船隻資訊
		ArrayList<Integer> alertMailTimes = new ArrayList<>();	//要放進告警mail的船隻，被告警次數
		StringBuffer msg = new StringBuffer();
		Point[] warn = new Point[4];
		
		//loop所有告警區，每個告警區都要經過以下程序: create alert.csv、read alert.csv to fileContent、compare with fileContent、write to alert.csv
		//注意: read from alert.csv、write to alert.csv不能在"loop告警區"外執行。換句話說，每個告警區其實是獨立處理的，不能混在一起處理。
		for( Map.Entry<String, ArrayList<SubCableInfo>> entry : subCableWarnList.entrySet() ){	//iterate each key
			String subCableWarnName = entry.getKey();	//key: 海纜警戒區的名稱(抓檔名)
			ArrayList<SubCableInfo> subCableWarns = entry.getValue();	//value: [SubCableInfo, SubCableInfo, SubCableInfo...]
			int count = 0;
			//每個告警區皆產生一份告警記錄檔
			this.createWarnRecordFile(subCableWarnName);
		
			//清空fileContent，以免殘留上一個告警區的內容
			fileContent.clear();
			//清空alertMailList，以免殘留上一個告警區的alert mail內容
			alertMailList.clear();
			//清空alertMailTimes
			alertMailTimes.clear();
			
			//將XXX_alert.csv的內容讀到記憶體
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(Env.BOAT_ALERT_RECORD_PATH + subCableWarnName + "_alert.csv")));
				while( (line = br.readLine())!=null ) {
					if(line.startsWith("MMSI"))	//略過第一列: title列
						continue;
					fileContent.put(line.split(",")[0], line.split(",", 2)[1]);
				}
				try { if(br!=null) br.close(); } catch (IOException e) { e.printStackTrace(); }
			} catch (IOException e) {
				e.printStackTrace();
				continue;	//發生exception的話則直接跳到下一個告警區的處理
			} finally {
				try { if(br!=null) br.close(); } catch (IOException e) { e.printStackTrace(); }
			}
			System.out.println("上次記錄檔的內容: " + fileContent);
			
			
			for(int i=0; i<subCableWarns.size(); i++) {	//目前size一定要為4，且warn point順序為左上、右上、右下、左下
				warn[i] = new Point();
				warn[i].setX(subCableWarns.get(i).getLongitude());	//經度是X
				warn[i].setY(subCableWarns.get(i).getLatitude());	//緯度是Y
//				System.out.println(subCableWarnName+": ("+warn[i].getY()+","+warn[i].getX()+")");
			}
			
			Point testPoint = new Point();
			boolean isWarn = false;
			for (int i = 0; i < boatInfos.length; i++) {
				testPoint.setX(boatInfos[i].getLongitude());
				testPoint.setY(boatInfos[i].getLatitude());
				isWarn = this.isPointInArea(warn[0], warn[1], warn[2], warn[3], testPoint);
				
				//告警條件: 1.在警戒區、2.時速<5節(海浬/小時)
				if(isWarn==true && (Double.parseDouble(boatInfos[i].getSpeed())/1.852)<=5) {
//				if(isWarn==true) {
					if(fileContent.containsKey(boatInfos[i].getMmsi())) {	//代表alert.csv中原本已有此船隻紀錄
						///判斷time duration///
						String lastWarn = fileContent.get(boatInfos[i].getMmsi()).split(",")[0].trim();
						String thisWarn = boatInfos[i].getTimestamp().trim();
						Long duration = this.getTimeDiff(lastWarn, thisWarn);
						Long warnAgainPeriod = new Long(warnAgainPeriod_m*60*1000 + warnAgainPeriod_h*60*60*1000 + warnAgainPeriod_d*24*60*60*1000);	//millisecond
						Integer alertTimes = Integer.parseInt(fileContent.get(boatInfos[i].getMmsi()).split(",")[1].trim());	//告警次數
						
						if (duration!=null && duration >= warnAgainPeriod) {	//null代表發生getTimeDiff method exception；duration >= warnAgainPeriod則再次告警，反之則不做任何動作
//							System.out.println("記錄檔裡船隻上一次的時間: " + lastWarn);
//							System.out.println("後來船隻的時間: " + thisWarn);
							String chCountry = CountryMapping.mmsi2Country(boatInfos[i].getMmsi());	//利用MMSI對應出船隻國籍
							String chType = BoatTypeMapping.code2Text(boatInfos[i].getTypeCode());	//利用boat type code對應出船隻種類
							alertTimes ++;	//告警次數+1
        					msg.setLength(0);
        					msg.append(boatInfos[i].getTimestamp()).append(",")
        						.append(alertTimes.toString()).append(",")
        						.append(boatInfos[i].getLatitude()).append(",")
        						.append(boatInfos[i].getLongitude()).append(",")
        						.append(boatInfos[i].getSpeed()).append(",")
        						.append(boatInfos[i].getDirection()).append(",")
        						.append(boatInfos[i].getCallsign()).append(",")
        						.append(chCountry).append(",")
        						.append(boatInfos[i].getHeading()).append(",")
        						.append(boatInfos[i].getImo()).append(",")
        						.append(boatInfos[i].getSizeA()).append(",")
        						.append(boatInfos[i].getSizeB()).append(",")
        						.append(boatInfos[i].getSizeC()).append(",")
        						.append(boatInfos[i].getSizeD()).append(",")
        						.append(boatInfos[i].getShipName()).append(",")
        						.append(chType);
        					fileContent.put(boatInfos[i].getMmsi(), msg.toString());	//fileContent放的是要寫回alert.csv的內容
        					alertMailList.add(boatInfos[i]);	//要發告警的list
        					alertMailTimes.add(alertTimes);
        					count++;
						}
					}else {	//代表進入警戒區的是新船隻
						String chCountry = CountryMapping.mmsi2Country(boatInfos[i].getMmsi());	//利用MMSI對應出船隻國籍
						String chType = BoatTypeMapping.code2Text(boatInfos[i].getTypeCode());	//利用boat type code對應出船隻種類
						msg.setLength(0);
						msg.append(boatInfos[i].getTimestamp()).append(",")
						.append("1").append(",")	//第一次進告警區，Alert time設為1 (代表第一次告警)
						.append(boatInfos[i].getLatitude()).append(",")
						.append(boatInfos[i].getLongitude()).append(",")
						.append(boatInfos[i].getSpeed()).append(",")
						.append(boatInfos[i].getDirection()).append(",")
						.append(boatInfos[i].getCallsign()).append(",")
						.append(chCountry).append(",")
						.append(boatInfos[i].getHeading()).append(",")
						.append(boatInfos[i].getImo()).append(",")
						.append(boatInfos[i].getSizeA()).append(",")
						.append(boatInfos[i].getSizeB()).append(",")
						.append(boatInfos[i].getSizeC()).append(",")
						.append(boatInfos[i].getSizeD()).append(",")
						.append(boatInfos[i].getShipName()).append(",")
						.append(chType);
						fileContent.put(boatInfos[i].getMmsi(), msg.toString());	//fileContent放的是要寫回alert.csv的內容
						alertMailList.add(boatInfos[i]);	//要發告警的list
						alertMailTimes.add(1);
						count++;
					}

				}else if(isWarn==true){	//船隻在告警區內，但不滿足其他告警條件，因此不做任何動作
					System.out.println("!!!船隻位於警戒區，但不滿足告警條件!!!");
				}else {		//代表此船不在告警區，則從fileContent移除該筆資料(不管原本fileContent有沒有此筆紀錄，都執行remove())
					fileContent.remove(boatInfos[i].getMmsi());
				}
					
			}//inner for loop END			
			
			System.out.println("這次要寫進去的內容:" + fileContent);
			System.out.println("發送" + count + "筆告警資料");
			
			//發送告警mail
			if(alertMailList.size() != 0)
				this.sendAlertMail(subCableWarnName, alertMailList, alertMailTimes);
			
			//將資料寫回alert.csv
			try {
				fw = new FileWriter(Env.BOAT_ALERT_RECORD_PATH + subCableWarnName + "_alert.csv", false);	//append=false, means overwrite
				fw.write("MMSI,Timestamp,Alert times,Latitude,Longitude,Speed,Direction,Callsign,Country,Heading,IMO,Size A,Size B,Size C,Size D,Ship Name,Type of ship (text)");
				fw.write("\n");

				for( Map.Entry<String, String> entry2 : fileContent.entrySet() ){	
					String key = entry2.getKey();
					String value = entry2.getValue();
					fw.write(key + "," + value + "\n");
				}
				fw.flush();
				try { if(fw!=null) fw.close(); } catch (IOException e) { e.printStackTrace(); }
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try { if(fw!=null) fw.close(); } catch (IOException e) { e.printStackTrace(); }
			}
		}//outer for loop END
		
		
	}//checkLastWarn END
	
	
	/**
	 * 計算兩時間間隔
	 * @param time1
	 * @param time2
	 * @return Long
	 */
	public Long getTimeDiff(String time1, String time2) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date1;
		Date date2;
		
		try {
			date1 = sdf.parse(time1);
			date2 = sdf.parse(time2);
			long difference = date2.getTime() - date1.getTime();
			
			return new Long(difference);
		} catch (ParseException e) {
			System.out.println("計算time duration時發生錯誤: " + e);
			return null;
		}
	}//getTimeDiff END
	
	
	/**
	 * 判斷P點是否在矩形area中
	 * @param leftUp
	 * @param rightUp
	 * @param rightDwn
	 * @param leftDwn
	 * @param p
	 * @return boolean
	 */
	public boolean isPointInArea(Point leftUp, Point rightUp, Point rightDwn, Point leftDwn, Point p) {
		return getCross(leftUp, leftDwn, p) * getCross(rightDwn, rightUp, p) >= 0 && getCross(rightUp, leftUp, p) * getCross(leftDwn, rightDwn, p) >= 0;
	}//isPointInArea END
	
	
	/**
	 * 代入三點座標，計算外積 [ p1p2 X p1p = (p2.x-p1.x)(p.y-p1.y) - (p.x-p1.x)(p2.y-p1.y) ]
	 * @param p1
	 * @param p2
	 * @param p
	 * @return double
	 */
	public double getCross(Point p1, Point p2, Point p) {
		return (p2.getX()-p1.getX()) * (p.getY()-p1.getY()) - (p.getX()-p1.getX()) * (p2.getY()-p1.getY());   
	}//getCross END
	
	
	/**
	 * 產生告警記錄檔
	 * @param warnName
	 */
	public void createWarnRecordFile(String warnName) {
		String path = Env.BOAT_ALERT_RECORD_PATH;

		File alertFile = new File(path + warnName + "_alert.csv");

		FileWriter fw = null;
		
		try {
			System.out.println("正在產生告警記錄檔<" + alertFile.getName() + ">...");
			if (alertFile.createNewFile()) {
				System.out.println("檔案<" + alertFile.getName() + ">建立成功!");
				fw = new FileWriter(alertFile);	//fw單純把檔案加上標題列，其實可有可無

				fw.write("MMSI,Timestamp,Alert times,Latitude,Longitude,Speed,Direction,Callsign,Country,Heading,IMO,Size A,Size B,Size C,Size D,Ship Name,Type of ship (text)");

				fw.write("\n");
				fw.flush();
			}else {
				System.out.println("檔案<" + alertFile.getName() + ">已存在!");
			}
			try { if(fw!=null) fw.close(); } catch (IOException e) { e.printStackTrace(); }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { if(fw!=null) fw.close(); } catch (IOException e) { e.printStackTrace(); }
		}
	}//createWarnRecordFile END
	
	
	public static void main(String[] args) throws IOException {
		int dataType = 0;	//0: .csv
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
		
		//撈出current.csv的船隻資料
		String boatFilePath = Env.BOAT_PROCESSED_DATA_PATH;
		String boatFileName = "current.csv";
		String boatPackType = "boatRaw";
		ParseData boatParseData = new ParseData();
		BoatInfo[] boatInfos = boatParseData.readFromMPBFile(boatFilePath, boatFileName, dataType, boatPackType);
		
		DetectAndAlert detectAndAlert = new DetectAndAlert();
		detectAndAlert.checkLastWarn(subCableWarnList, boatInfos);
		
		
		
	}//main END

	
	
}