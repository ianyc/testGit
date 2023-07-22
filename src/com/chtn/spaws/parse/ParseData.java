package com.chtn.spaws.parse;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chtn.spaws.entity.BoatInfo;
import com.chtn.spaws.entity.SubCableInfo;
import com.chtn.util.Env;
import com.chttl.iois.profile.ProfileCenter;
import com.sun.org.apache.xpath.internal.operations.And;

public class ParseData {
	Logger logger = LogManager.getLogger(this.getClass());
	
	//船隻資訊
	private ArrayList<String> mmsi = new ArrayList<>();
	private ArrayList<String> timestamp = new ArrayList<>();
	private ArrayList<Double> latitude = new ArrayList<>();
	private ArrayList<Double> longitude = new ArrayList<>();
	private ArrayList<String> speed = new ArrayList<>();
	private ArrayList<String> direction = new ArrayList<>();
	private ArrayList<String> heading = new ArrayList<String>();
	private ArrayList<String> alertTimes = new ArrayList<String>();
	private int countFromMPBFile = 0;	//用來記錄檔案中處理了幾行
	private int countFromProcessedFile = 0;	//用來記錄檔案中處理了幾行
	//船隻基本資料
	private ArrayList<String> basicInfo_mmsi = new ArrayList<>();
	private ArrayList<String> basicInfo_callsign = new ArrayList<>();
	private ArrayList<String> basicInfo_country = new ArrayList<>();
	private ArrayList<String> basicInfo_imo = new ArrayList<>();
	private ArrayList<String> basicInfo_shipName = new ArrayList<>();
	private ArrayList<String> basicInfo_sizeA = new ArrayList<>();
	private ArrayList<String> basicInfo_sizeB = new ArrayList<>();
	private ArrayList<String> basicInfo_sizeC = new ArrayList<>();
	private ArrayList<String> basicInfo_sizeD = new ArrayList<>();
	private ArrayList<String> basicInfo_typeCode = new ArrayList<>();
	private ArrayList<String> basicInfo_typeText = new ArrayList<>();
	private int countFromMMSIFile = 0;
	//getter
	public ArrayList<String> getTimeStamp(){
		return timestamp;
	}
	public ArrayList<String> getMMSI(){
		return mmsi;
	}
	public ArrayList<Double> getLatitude(){
		return latitude;
	}
	public ArrayList<Double> getLongitude(){
		return longitude;
	}
	public ArrayList<String> getSpeed(){
		return speed;
	}
	public ArrayList<String> getDirection(){
		return direction;
	}
	public ArrayList<String> getHeading(){
		return heading;
	}
	public ArrayList<String> getAlertTimes(){
		return alertTimes;
	}
	public int getCountFromMPBFile() {
		return countFromMPBFile;
	}
	public ArrayList<String> getBasicInfo_mmsi() {
		return basicInfo_mmsi;
	}
	public ArrayList<String> getBasicInfo_callsign() {
		return basicInfo_callsign;
	}
	public ArrayList<String> getBasicInfo_country() {
		return basicInfo_country;
	}
	public ArrayList<String> getBasicInfo_imo() {
		return basicInfo_imo;
	}
	public ArrayList<String> getBasicInfo_shipName() {
		return basicInfo_shipName;
	}
	public ArrayList<String> getBasicInfo_sizeA() {
		return basicInfo_sizeA;
	}
	public ArrayList<String> getBasicInfo_sizeB() {
		return basicInfo_sizeB;
	}
	public ArrayList<String> getBasicInfo_sizeC() {
		return basicInfo_sizeC;
	}
	public ArrayList<String> getBasicInfo_sizeD() {
		return basicInfo_sizeD;
	}
	public ArrayList<String> getBasicInfo_typeCode() {
		return basicInfo_typeCode;
	}
	public ArrayList<String> getBasicInfo_typeText() {
		return basicInfo_typeText;
	}
	public int getCountFromMMSIFile() {
		return countFromMMSIFile;
	}
	
	//海纜資訊
	private ArrayList<Double> lat_sc = new ArrayList<>();
	private ArrayList<String> lat_dir = new ArrayList<>();
	private ArrayList<Double> lng_sc = new ArrayList<>();
	private ArrayList<String> lng_dir = new ArrayList<>();
	private int countFromSCFile = 0;	//用來記錄檔案中處理了幾行
	//getter
	public ArrayList<Double> getLatSC(){
		return lat_sc;
	}
	private ArrayList<String> getLatDir(){
		return lat_dir;
	}
	public ArrayList<Double> getLngSC(){
		return lng_sc;
	}
	private ArrayList<String> getLngDir(){
		return lng_dir;
	}
	public int getCountFromSCFile() {
		return countFromSCFile;
	}
	
	
	
	/***
	 * 讀取Alert Record檔案內容並逐行parsing (和readFromMPBFile類似但有些微差異，因此重寫一個method)
	 * @param filePath
	 * @param fileName
	 * @param dataType
	 * @param packType
	 * @return BoatInfo[]
	 */
	public BoatInfo[] readFromAlertRecordFile(String filePath, String fileName, int dataType, String packType) {
		ProfileCenter pc;
		BufferedReader br = null;
		String line = null;
		Double sog;		//speed over ground
		ResolveField resolveField = new ResolveField();
		
		try {
			logger.info("解析船隻檔案...");
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath+fileName)));		//未來思考是否檔案可能過大導致bufferReader裝不下，要切分
			while( (line = br.readLine())!=null ) {
				if(line.startsWith("MMSI"))	//略過第一列: title列
					continue;
//				System.out.println("Line in MPB File>> " + line);
				try {	//避免某一row resolveField失敗時後面的資料都不再resolve
					pc = ProfileCenter.getProfileCenter(Env.TABLE_FIELD_PATH);	//讀config file
					resolveField.getResolveField(line, dataType, pc, packType);
					Hashtable hashData = resolveField.toHashtable();	//將parse後的結果存成hash table，以便存取
					
//					System.out.println(resolveField.toChString());		//印出parse後的結果
//					System.out.println("hashtable> " + hashData);		//印出parse後存成hash table的結果
					this.mmsi.add(hashData.get("MMSI").toString());
					this.timestamp.add(hashData.get("BaseStationTimeStamp").toString().trim());
					this.alertTimes.add(hashData.get("AlertTimes").toString().trim());
					this.latitude.add(Double.parseDouble(hashData.get("Latitude").toString()));
					this.longitude.add(Double.parseDouble(hashData.get("Longitude").toString()));
					this.heading.add(hashData.get("Heading").toString());
					try {
						this.direction.add(hashData.get("CourseOverGround").toString());
					} catch (Exception e) {
						this.direction.add("N/A");
					}
					try {
						this.speed.add(hashData.get("SpeedOverGround").toString());	
					} catch (Exception e) {
						this.speed.add("N/A");
					}
					this.basicInfo_callsign.add(hashData.get("Callsign").toString());
					this.basicInfo_country.add(hashData.get("Country").toString());
					this.basicInfo_imo.add(hashData.get("IMO").toString());
					this.basicInfo_shipName.add(hashData.get("ShipName").toString());
					this.basicInfo_sizeA.add(hashData.get("SizeA").toString());
					this.basicInfo_sizeB.add(hashData.get("SizeB").toString());
					this.basicInfo_sizeC.add(hashData.get("SizeC").toString());
					this.basicInfo_sizeD.add(hashData.get("SizeD").toString());
					this.basicInfo_typeText.add(hashData.get("TypeOfShipText").toString());
					
					countFromMPBFile++;
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e);
				}
			}
			BoatInfo[] boatInfos = new BoatInfo[countFromMPBFile];		//將parse後的資料存到BoatInfo object
			int pos = fileName.lastIndexOf(".");
			String savedFileName = fileName.lastIndexOf(".") > 0 ? fileName.substring(0, pos) : fileName;
			for (int i = 0; i < boatInfos.length; i++) {
				boatInfos[i] = new BoatInfo();
				boatInfos[i].setMmsi(this.getMMSI().get(i));
				boatInfos[i].setTimestamp(this.getTimeStamp().get(i));
				boatInfos[i].setAlertTimes(this.getAlertTimes().get(i));
				boatInfos[i].setLatitude(this.getLatitude().get(i));
				boatInfos[i].setLongitude(this.getLongitude().get(i));
				boatInfos[i].setHeading(this.getHeading().get(i));
				boatInfos[i].setDirection(this.getDirection().get(i));
				boatInfos[i].setSpeed(this.getSpeed().get(i));
				boatInfos[i].setCallsign(this.getBasicInfo_callsign().get(i));
				boatInfos[i].setCountry(this.getBasicInfo_country().get(i));
				boatInfos[i].setImo(this.getBasicInfo_imo().get(i));
				boatInfos[i].setShipName(this.getBasicInfo_shipName().get(i));
				boatInfos[i].setSizeA(this.getBasicInfo_sizeA().get(i));
				boatInfos[i].setSizeB(this.getBasicInfo_sizeB().get(i));
				boatInfos[i].setSizeC(this.getBasicInfo_sizeC().get(i));
				boatInfos[i].setSizeD(this.getBasicInfo_sizeD().get(i));
				boatInfos[i].setTypeText(this.getBasicInfo_typeText().get(i));;
				boatInfos[i].setAlertRecordFileName(savedFileName);
			}
			logger.info("解析成功，總共" + countFromMPBFile + "筆資料...");
			return boatInfos;
//			System.out.println("***End Of File, Totally Read " + count + " Lines.***\n");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new BoatInfo[0];
		} finally {
			//每次讀完一份檔案就清空變數，避免一次讀多份檔案時資料延續在變數中
			this.mmsi.clear();
			this.timestamp.clear();
			this.alertTimes.clear();
			this.latitude.clear();
			this.longitude.clear();
			this.heading.clear();
			this.direction.clear();
			this.speed.clear();
			this.basicInfo_callsign.clear();
			this.basicInfo_country.clear();
			this.basicInfo_imo.clear();
			this.basicInfo_shipName.clear();
			this.basicInfo_sizeA.clear();
			this.basicInfo_sizeB.clear();
			this.basicInfo_sizeC.clear();
			this.basicInfo_sizeD.clear();
			this.basicInfo_typeText.clear();
			this.countFromMPBFile = 0;
			//close bufferedReader
			try {
				if(br != null)
					br.close();
			} catch (Exception e2) {
				 System.out.println("[Exception] readFromMPBFile() in ParseData.java exception when BufferedReader closed: " + e2 + "\n");
				 logger.error("[Exception] readFromMPBFile() in ParseData.java exception when BufferedReader closed: " + e2 + "\n");
			}
		}//try...catch END
	}//readFromAlertRecordFile END
	
	
	
	/***
	 * 讀取Processed檔案內容並逐行parsing，提供給搜尋歷史紀錄時使用。此method與readFromMPBFile method不同的地方在於，會根據搜尋條件(mmsi or latlng)過濾船隻資訊。
	 * @param filePath
	 * @param fileName
	 * @param dataType
	 * @param packType
	 * @return BoatInfo[]
	 */
	public BoatInfo[] readFromProcessedFile(String filePath, String fileName, int dataType, String packType, String qry_mmsi, String qry_lat, String qry_lng) {
		ProfileCenter pc;
		BufferedReader br = null;
		String line = null;
		Double sog;		//speed over ground
		ResolveField resolveField = new ResolveField();
		
		try {
			if(!qry_mmsi.equals("")) {	//搜尋條件以mmsi為主
				String[] mmsiAry = qry_mmsi.split(",");		//可能有多筆
				for(String item : mmsiAry){
					br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath+fileName)));
					while( (line = br.readLine())!=null ) {
						if(line.startsWith("Base station time stamp"))	//略過第一列: title列
							continue;
						try {	//避免某一row resolveField失敗時後面的資料都不再resolve
							pc = ProfileCenter.getProfileCenter(Env.TABLE_FIELD_PATH);	//讀config file
							resolveField.getResolveField(line, dataType, pc, packType);
							Hashtable hashData = resolveField.toHashtable();	//將parse後的結果存成hash table，以便存取
							
							if(item.equals(hashData.get("MMSI").toString())) {	//match到mmsi才存boatinfo
								this.timestamp.add(hashData.get("BaseStationTimeStamp").toString().trim());
								this.mmsi.add(hashData.get("MMSI").toString());
								this.latitude.add(  (double)Math.round( Double.parseDouble(hashData.get("Latitude").toString())*100000d ) / 100000d  );		//取到小數後第五位
								this.longitude.add(  (double)Math.round( Double.parseDouble(hashData.get("Longitude").toString())*100000d ) / 100000d  );	//取到小數後第五位
								try {
									this.direction.add(hashData.get("CourseOverGround").toString());
								} catch (Exception e) {
									this.direction.add("N/A");
								}
								try {
									sog = (double)Math.round( Double.parseDouble(hashData.get("SpeedOverGround").toString())*1.852*100000d ) / 100000d;	//轉換為km/hr，取到小數後第五位
									this.speed.add(sog.toString());	
								} catch (Exception e) {
									this.speed.add("N/A");
								}
								this.basicInfo_typeCode.add(hashData.get("TypeOfShip").toString());
								countFromProcessedFile++;
							}
						}catch (Exception e) {
							e.printStackTrace();
							logger.error(e);
						}
					}//while( (line = br.readLine())!=null ) END
				}//for(String item : mmsiAry) END
			}else {	//搜尋條件以latlng為主
				double qry_lat_up = Double.parseDouble(qry_lat)+0.1;
				double qry_lat_down = Double.parseDouble(qry_lat)-0.1;
				double qry_lng_up = Double.parseDouble(qry_lng)+0.1;
				double qry_lng_down = Double.parseDouble(qry_lng)-0.1;
				br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath+fileName)));
				while( (line = br.readLine())!=null ) {
					if(line.startsWith("Base station time stamp"))	//略過第一列: title列
						continue;
					try {	//避免某一row resolveField失敗時後面的資料都不再resolve
						pc = ProfileCenter.getProfileCenter(Env.TABLE_FIELD_PATH);	//讀config file
						resolveField.getResolveField(line, dataType, pc, packType);
						Hashtable hashData = resolveField.toHashtable();	//將parse後的結果存成hash table，以便存取
						
						double temp_lat = Double.parseDouble(hashData.get("Latitude").toString());
						double temp_lng = Double.parseDouble(hashData.get("Longitude").toString());
						if(temp_lat <= qry_lat_up && temp_lat >= qry_lat_down && temp_lng<= qry_lng_up && temp_lng >= qry_lng_down) {	//match到latlng才存boatinfo
							this.timestamp.add(hashData.get("BaseStationTimeStamp").toString().trim());
							this.mmsi.add(hashData.get("MMSI").toString());
							this.latitude.add(Double.parseDouble(hashData.get("Latitude").toString()));
							this.longitude.add(Double.parseDouble(hashData.get("Longitude").toString()));
							try {
								this.direction.add(hashData.get("CourseOverGround").toString());
							} catch (Exception e) {
								this.direction.add("N/A");
							}
							try {
								sog = Double.parseDouble(hashData.get("SpeedOverGround").toString())*1.852;	//轉換為km/hr
								this.speed.add(sog.toString());	
							} catch (Exception e) {
								this.speed.add("N/A");
							}
							this.basicInfo_typeCode.add(hashData.get("TypeOfShip").toString());
							countFromProcessedFile++;
						}
					}catch (Exception e) {
						e.printStackTrace();
						logger.error(e);
					}
				}//while( (line = br.readLine())!=null ) END
			}//if(!qry_mmsi.equals("")) END
			BoatInfo[] boatInfos = new BoatInfo[countFromProcessedFile];		//將parse後的資料存到BoatInfo object
			for (int i = 0; i < boatInfos.length; i++) {
				boatInfos[i] = new BoatInfo();
				boatInfos[i].setTimestamp(this.getTimeStamp().get(i));
				boatInfos[i].setMmsi(this.getMMSI().get(i));
				boatInfos[i].setLatitude(this.getLatitude().get(i));
				boatInfos[i].setLongitude(this.getLongitude().get(i));
				boatInfos[i].setSpeed(this.getSpeed().get(i));
				boatInfos[i].setDirection(this.getDirection().get(i));
				boatInfos[i].setTypeCode(this.getBasicInfo_typeCode().get(i));
			}
			
			return boatInfos;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new BoatInfo[0];
		} finally {
			//每次讀完一份檔案就清空變數，避免一次讀多份檔案時資料延續在變數中
			this.timestamp.clear();
			this.mmsi.clear();
			this.latitude.clear();
			this.longitude.clear();
			this.speed.clear();
			this.direction.clear();
			this.basicInfo_typeCode.clear();
			this.countFromProcessedFile = 0;
			//close bufferedReader
			try {
				if(br != null)
					br.close();
			} catch (Exception e2) {
				 System.out.println("[Exception] readFromProcessedFile() in ParseData.java exception when BufferedReader closed: " + e2 + "\n");
				 logger.error("[Exception] readFromProcessedFile() in ParseData.java exception when BufferedReader closed: " + e2 + "\n");
			}
		}//try...catch END
	}//readFromProcessedFile END
	
	
	
	/***
	 * 讀取航港局(MPB)檔案內容並逐行parsing
	 * @param filePath
	 * @param fileName
	 * @param dataType
	 * @param packType
	 * @return BoatInfo[]
	 */
	public BoatInfo[] readFromMPBFile(String filePath, String fileName, int dataType, String packType) {
		ProfileCenter pc;
		BufferedReader br = null;
		String line = null;
		Double sog;		//speed over ground
		ResolveField resolveField = new ResolveField();
		
		try {
			logger.info("解析船隻檔案...");
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath+fileName)));		//未來思考是否檔案可能過大導致bufferReader裝不下，要切分
			while( (line = br.readLine())!=null ) {
				if(line.startsWith("Base station time stamp"))	//略過第一列: title列
					continue;
//				System.out.println("Line in MPB File>> " + line);
				try {	//避免某一row resolveField失敗時後面的資料都不再resolve
					pc = ProfileCenter.getProfileCenter(Env.TABLE_FIELD_PATH);	//讀config file
					resolveField.getResolveField(line, dataType, pc, packType);
					Hashtable hashData = resolveField.toHashtable();	//將parse後的結果存成hash table，以便存取
					
//					System.out.println(resolveField.toChString());		//印出parse後的結果
//					System.out.println("hashtable> " + hashData);		//印出parse後存成hash table的結果
					this.timestamp.add(hashData.get("BaseStationTimeStamp").toString().trim());
					this.mmsi.add(hashData.get("MMSI").toString());
					this.latitude.add(  (double)Math.round( Double.parseDouble(hashData.get("Latitude").toString())*100000d ) / 100000d  );		//取到小數後第五位
					this.longitude.add(  (double)Math.round( Double.parseDouble(hashData.get("Longitude").toString())*100000d ) / 100000d  );	//取到小數後第五位
					this.heading.add(hashData.get("Heading").toString());
					try {
						this.direction.add(hashData.get("CourseOverGround").toString());
					} catch (Exception e) {
						this.direction.add("N/A");
					}
					try {
						sog = (double)Math.round( Double.parseDouble(hashData.get("SpeedOverGround").toString())*1.852*100000d ) / 100000d;	//轉換為km/hr，取到小數後第五位
						this.speed.add(sog.toString());	
					} catch (Exception e) {
						this.speed.add("N/A");
					}
					this.basicInfo_callsign.add(hashData.get("Callsign").toString());
					this.basicInfo_country.add(hashData.get("Country").toString());
					this.basicInfo_imo.add(hashData.get("IMO").toString());
					this.basicInfo_shipName.add(hashData.get("ShipName").toString());
					this.basicInfo_sizeA.add(hashData.get("SizeA").toString());
					this.basicInfo_sizeB.add(hashData.get("SizeB").toString());
					this.basicInfo_sizeC.add(hashData.get("SizeC").toString());
					this.basicInfo_sizeD.add(hashData.get("SizeD").toString());
					this.basicInfo_typeCode.add(hashData.get("TypeOfShip").toString());
					
					countFromMPBFile++;
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e);
				}
			}
			BoatInfo[] boatInfos = new BoatInfo[countFromMPBFile];		//將parse後的資料存到BoatInfo object
			for (int i = 0; i < boatInfos.length; i++) {
				boatInfos[i] = new BoatInfo();
				boatInfos[i].setTimestamp(this.getTimeStamp().get(i));
				boatInfos[i].setMmsi(this.getMMSI().get(i));
				boatInfos[i].setLatitude(this.getLatitude().get(i));
				boatInfos[i].setLongitude(this.getLongitude().get(i));
				boatInfos[i].setHeading(this.getHeading().get(i));
				boatInfos[i].setDirection(this.getDirection().get(i));
				boatInfos[i].setSpeed(this.getSpeed().get(i));
				boatInfos[i].setCallsign(this.getBasicInfo_callsign().get(i));
				boatInfos[i].setCountry(this.getBasicInfo_country().get(i));
				boatInfos[i].setImo(this.getBasicInfo_imo().get(i));
				boatInfos[i].setShipName(this.getBasicInfo_shipName().get(i));
				boatInfos[i].setSizeA(this.getBasicInfo_sizeA().get(i));
				boatInfos[i].setSizeB(this.getBasicInfo_sizeB().get(i));
				boatInfos[i].setSizeC(this.getBasicInfo_sizeC().get(i));
				boatInfos[i].setSizeD(this.getBasicInfo_sizeD().get(i));
				boatInfos[i].setTypeCode(this.getBasicInfo_typeCode().get(i));;
			}
			logger.info("解析成功，總共" + countFromMPBFile + "筆資料...");
			return boatInfos;
//			System.out.println("***End Of File, Totally Read " + count + " Lines.***\n");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new BoatInfo[0];
		} finally {
			//每次讀完一份檔案就清空變數，避免一次讀多份檔案時資料延續在變數中
			this.timestamp.clear();
			this.mmsi.clear();
			this.latitude.clear();
			this.longitude.clear();
			this.heading.clear();
			this.direction.clear();
			this.speed.clear();
			this.basicInfo_callsign.clear();
			this.basicInfo_country.clear();
			this.basicInfo_imo.clear();
			this.basicInfo_shipName.clear();
			this.basicInfo_sizeA.clear();
			this.basicInfo_sizeB.clear();
			this.basicInfo_sizeC.clear();
			this.basicInfo_sizeD.clear();
			this.basicInfo_typeCode.clear();
			this.countFromMPBFile = 0;
			//close bufferedReader
			try {
				if(br != null)
					br.close();
			} catch (Exception e2) {
				 System.out.println("[Exception] readFromMPBFile() in ParseData.java exception when BufferedReader closed: " + e2 + "\n");
				 logger.error("[Exception] readFromMPBFile() in ParseData.java exception when BufferedReader closed: " + e2 + "\n");
			}
		}//try...catch END
	}//readFromMPBFile END

	
	
	/**
	 * 讀取MMSI基本資料檔 (MMSI_basic_info.csv)
	 * @param filePath
	 * @param fileName
	 * @param dataType
	 * @param packType
	 * @return BoatInfo[]
	 */
	public BoatInfo[] readFromMMSIFile(String filePath, String fileName, int dataType, String packType) {
		ProfileCenter pc;
		BufferedReader br = null;
		String line = null;
		ResolveField resolveField = new ResolveField();
		
		try {
			logger.info("解析MMSI檔案...");
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath+fileName)));		//未來思考是否檔案可能過大導致bufferReader裝不下，要切分
			while( (line = br.readLine())!=null ) {
				if(line.startsWith("MMSI"))	//略過第一列: title列
					continue;
				try {	//避免某一row resolveField失敗時後面的資料都不再resolve
					pc = ProfileCenter.getProfileCenter(Env.TABLE_FIELD_PATH);	//讀config file
					resolveField.getResolveField(line, dataType, pc, packType);
					Hashtable hashData = resolveField.toHashtable();	//將parse後的結果存成hash table，以便存取
					
//					System.out.println(resolveField.toChString());		//印出parse後的結果
//					System.out.println("hashtable> " + hashData);		//印出parse後存成hash table的結果
					
					this.basicInfo_mmsi.add(hashData.get("MMSI").toString());
					this.basicInfo_callsign.add(hashData.get("Callsign").toString());
					this.basicInfo_country.add(hashData.get("Country").toString());
					this.basicInfo_imo.add(hashData.get("IMO").toString());
					this.basicInfo_shipName.add(hashData.get("ShipName").toString());
					this.basicInfo_sizeA.add(hashData.get("SizeA").toString());
					this.basicInfo_sizeB.add(hashData.get("SizeB").toString());
					this.basicInfo_sizeC.add(hashData.get("SizeC").toString());
					this.basicInfo_sizeD.add(hashData.get("SizeD").toString());
					this.basicInfo_typeText.add(hashData.get("TypeOfShipText").toString());
					countFromMMSIFile++;
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e);
				}
			}
			BoatInfo[] boatInfos = new BoatInfo[countFromMMSIFile];		//將parse後的資料存到BoatInfo object
			for (int i = 0; i < boatInfos.length; i++) {
				boatInfos[i] = new BoatInfo();
				boatInfos[i].setMmsi(this.getBasicInfo_mmsi().get(i));
				boatInfos[i].setCallsign(this.getBasicInfo_callsign().get(i));
				boatInfos[i].setCountry(this.getBasicInfo_country().get(i));
				boatInfos[i].setImo(this.getBasicInfo_imo().get(i));
				boatInfos[i].setShipName(this.getBasicInfo_shipName().get(i));
				boatInfos[i].setSizeA(this.getBasicInfo_sizeA().get(i));
				boatInfos[i].setSizeB(this.getBasicInfo_sizeB().get(i));
				boatInfos[i].setSizeC(this.getBasicInfo_sizeC().get(i));
				boatInfos[i].setSizeD(this.getBasicInfo_sizeD().get(i));
				boatInfos[i].setTypeText(this.getBasicInfo_typeText().get(i));
			}
			logger.info("解析成功，總共" + countFromMMSIFile + "筆資料...");
			return boatInfos;
//			System.out.println("***End Of File, Totally Read " + count + " Lines.***\n");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new BoatInfo[0];
		} finally {
			//每次讀完一份檔案就清空變數，避免一次讀多份檔案時資料延續在變數中
			this.basicInfo_mmsi.clear();
			this.basicInfo_callsign.clear();
			this.basicInfo_country.clear();
			this.basicInfo_imo.clear();
			this.basicInfo_shipName.clear();
			this.basicInfo_sizeA.clear();
			this.basicInfo_sizeB.clear();
			this.basicInfo_sizeC.clear();
			this.basicInfo_sizeD.clear();
			this.basicInfo_typeText.clear();
			this.countFromMPBFile = 0;
			//close bufferedReader
			try {
				if(br != null)
					br.close();
			} catch (Exception e2) {
				 System.out.println("[Exception] readFromMMSIFile() in ParseData.java exception when BufferedReader closed: " + e2 + "\n");
				 logger.error("[Exception] readFromMMSIFile() in ParseData.java exception when BufferedReader closed: " + e2 + "\n");
			}
		}//try...catch END
	}//readFromMMSIFile END
	
	
	
	/***
	 * 讀取海纜(SC)檔案內容並逐行parsing
	 * @param filePath
	 * @param fileName
	 * @param dataType
	 * @param packType
	 * @return SubCableInfo[] 
	 */
	public SubCableInfo[] readFromSCFile(String filePath, String fileName, int dataType, String packType) throws IOException {
		ProfileCenter pc;
		BufferedReader br = null;
		String line = null;
		double lat_d, lat_m, lng_d, lng_m;
		ResolveField resolveField = new ResolveField();
		
		try {
			logger.info("解析海纜檔案...");
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath+fileName)));		//未來思考是否檔案可能過大導致bufferReader裝不下，要切分
			while( (line = br.readLine())!=null ) {
				if(line.startsWith("Lat"))	//略過第一列: title列
					continue;
				
				try {	//避免某一row resolveField失敗時後面的資料都不再resolve
					pc = ProfileCenter.getProfileCenter(Env.TABLE_FIELD_PATH); // 讀config file
					resolveField.getResolveField(line, dataType, pc, packType);
					Hashtable hashData = resolveField.toHashtable(); // 將parse後的結果存成hash table，以便存取
					lat_d = Double.parseDouble(hashData.get("LatDeg").toString());
					lat_m = Double.parseDouble(hashData.get("LatMin").toString());
					lng_d = Double.parseDouble(hashData.get("LngDeg").toString());
					lng_m = Double.parseDouble(hashData.get("LngMin").toString());
					this.lat_sc.add(lat_d + lat_m / 60); // 緯度=度+分/60
					this.lat_dir.add(hashData.get("LatDir").toString());
					this.lng_sc.add(lng_d + lng_m / 60); // 經度=度+分/60
					this.lng_dir.add(hashData.get("LngDir").toString());
					countFromSCFile++;
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e);
				}
			}
			SubCableInfo[] subCableInfos = new SubCableInfo[countFromSCFile];
			int pos = fileName.lastIndexOf(".");
			String savedFileName = fileName.lastIndexOf(".") > 0 ? fileName.substring(0, pos) : fileName;
			for (int i = 0; i < subCableInfos.length; i++) {
				subCableInfos[i] = new SubCableInfo();
				subCableInfos[i].setSubCableName(savedFileName);
				subCableInfos[i].setLatitude(this.getLatSC().get(i));
				subCableInfos[i].setLongitude(this.getLngSC().get(i));
			}
			
			logger.info("解析成功，總共" + countFromSCFile + "筆資料...");
			return subCableInfos;
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new SubCableInfo[0];		
		} finally {
			//每次讀完一份檔案就清空變數，避免一次讀多份檔案時資料延續在變數中
			this.lat_sc.clear();
			this.lat_dir.clear();
			this.lng_sc.clear();
			this.lng_dir.clear();
			this.countFromSCFile = 0;
			//close bufferedReader
			try {
				if(br != null)
					br.close();
			} catch (Exception e2) {
				 System.out.println("[Exception] readFromSCFile() in ParseData.java exception when BufferedReader closed: " + e2 + "\n");
				 logger.error("[Exception] readFromSCFile() in ParseData.java exception when BufferedReader closed: " + e2 + "\n");
			}
		}//try...catch END
	}//readFromSCFile END
	
	
	
	public static void main(String[] args) throws IOException {
//*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*Test Boat Raw Data*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-
		String filePath = Env.BOAT_PROCESSED_DATA_PATH;
		String fileName = "test.csv";
//		String fileName = "testA.csv";
		int dataType = 0;	//定義0=.csv file，或許未來會有不同input data type
		String packType = "boatProcessed";	//要使用config file中的哪一種pack type
		ParseData parseData = new ParseData();
		BoatInfo[] boatInfos = parseData.readFromMPBFile(filePath, fileName, dataType, packType);
		for (int i = 0; i < boatInfos.length; i++) {		//以後若要過濾原始檔案，將特定欄位寫入新檔，在此for loop中操作即可
			System.out.println("時間> " + boatInfos[i].getTimestamp());
			System.out.println("緯度> " + boatInfos[i].getLatitude());
			System.out.println("經度> " + boatInfos[i].getLongitude());
			System.out.println("MMSI> " + boatInfos[i].getMmsi());
			System.out.println("航速> " + boatInfos[i].getSpeed());
			System.out.println("航向> " + boatInfos[i].getDirection());
			System.out.println("**********************************************************\n");
		}
		System.out.println("***End Of File, Totally Read " + boatInfos.length + " Lines.***\n");
//*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*Test Boat Raw Data*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-		
		
		
//*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*Test Submarine Cable Data*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-
//		String SCfilePath = Env.SC_DATA_PATH;
//		String SCfileName = "TP3.csv";
//		int SCdataType = 0;
//		String SCpackType = "subCable";
//		ParseData SCparseData = new ParseData();
//		SubCableInfo[] subCableInfos = SCparseData.readFromSCFile(SCfilePath, SCfileName, SCdataType, SCpackType);		////將parse後的資料存到SubCableInfo object
//		for (int i = 0; i < subCableInfos.length; i++) {
//			System.out.println("海纜(警戒區)名稱> " + subCableInfos[i].getSubCableName());
//			System.out.println("緯度> " + subCableInfos[i].getLatitude());
//			System.out.println("經度> " + subCableInfos[i].getLongitude());
//			System.out.println("**********************************************************\n");
//		}
//		System.out.println("***End Of File, Totally Read " + subCableInfos.length + " Lines.***\n");
//*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*Test Submarine Cable Data*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-		
		
		
		
		
		
		
//*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*Test Search History*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-		
//		String filePath = Env.BOAT_PROCESSED_DATA_PATH;
//		String fileName = "2020_11_29_09.csv";
//		int dataType = 0;	//定義0=.csv file，或許未來會有不同input data type
//		String packType = "boatProcessed";	//要使用config file中的哪一種pack type
////		String mmsi = "998500011,671873000";
////		String lat = "";
////		String lng = "";
//		String mmsi = "";
//		String lat = "25.1841983333333";
//		String lng = "121.4095";
//		ParseData parseData = new ParseData();
//		BoatInfo[] boatInfos = parseData.readFromProcessedFile(filePath, fileName, dataType, packType, mmsi, lat, lng);
//		for (int i = 0; i < boatInfos.length; i++) {		//以後若要過濾原始檔案，將特定欄位寫入新檔，在此for loop中操作即可
//			System.out.println("時間> " + boatInfos[i].getTimestamp());
//			System.out.println("緯度> " + boatInfos[i].getLatitude());
//			System.out.println("經度> " + boatInfos[i].getLongitude());
//			System.out.println("MMSI> " + boatInfos[i].getMmsi());
//			System.out.println("航速> " + boatInfos[i].getSpeed());
//			System.out.println("航向> " + boatInfos[i].getDirection());
//			System.out.println("**********************************************************\n");
//		}
//		System.out.println("***End Of File, Totally Read " + boatInfos.length + " Lines.***\n");
//*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*Test Search History*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-	
		
		
		
		
		
		
		
		
	}//main END

	

	
}
