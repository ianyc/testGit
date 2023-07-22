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
	//�A���iĵ�ɶ����j
	private int warnAgainPeriod_d;
	private int warnAgainPeriod_h;
	private int warnAgainPeriod_m;
	private String[] mailList;
	
	//Constructor
	public DetectAndAlert() {
		Hashtable hashData = null;
		ParseXML parseXML = new ParseXML();
		hashData = parseXML.getAlertConfig(configFile);
		//�Y�L�k�qxml���o�]�w�ȡA�h�N�J�w�]��
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
	 * �H�e�iĵmail
	 * @param warnName
	 * @param boatList
	 */
	public void sendAlertMail(String warnName, ArrayList<BoatInfo> boatList, ArrayList<Integer> alertTimes) {
		MailAlert mailAlert = new MailAlert();
		
		//�����
		mailAlert.setTo(mailList);
		
		//�H�󤺮e
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < boatList.size(); i++) {
//		for (BoatInfo boat : boatList) {
			//�B�z����B�e
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
			
			String chCountry = CountryMapping.mmsi2Country(boatList.get(i).getMmsi());	//�Q��MMSI�����X����y
			String chType = BoatTypeMapping.code2Text(boatList.get(i).getTypeCode());	//�Q��boat type code�����X�����
			//mail content
			sb.append("************��" + alertTimes.get(i) + "���iĵ***********").append("\n")
				.append("Warning Area: ").append(warnName).append("\n")
				.append("MMSI: ").append(boatList.get(i).getMmsi()).append("\n")
				.append("TimeStamp: ").append(boatList.get(i).getTimestamp()).append("\n")
				.append("Position: (").append(boatList.get(i).getLatitude()).append(", ").append(boatList.get(i).getLongitude()).append(")").append("\n")
				.append("Speed: ").append(boatList.get(i).getSpeed()).append(" km/hr").append("\n")
				.append("Direction: ").append(boatList.get(i).getDirection()).append(" ��").append("\n")
				.append("Callsign: ").append(boatList.get(i).getCallsign()).append("\n")
				.append("Country: ").append(chCountry).append("\n")
				.append("Ship Name: ").append(boatList.get(i).getShipName()).append("\n")
				.append("Heading: ").append(boatList.get(i).getHeading()).append(" ��").append("\n")
				.append("IMO: ").append(boatList.get(i).getImo()).append("\n")
				.append("Length Overall: ").append(boatLength).append(" m").append("\n")
				.append("Breadth Extreme: ").append(boatWidth).append(" m").append("\n")
				.append("Type: ").append(chType).append("\n")
				.append("********************************")
				.append("\n\n\n");
		}
		System.out.println(sb.toString());
		
		//�H��title
		mailAlert.setContent("[SAWS OA] " + warnName + "ĵ�ٰϧiĵ!!!", sb);
		
//		//Send
		mailAlert.send();
		
	}//sendAlertMail END
	
	
	/**
	 * �ˬd��O�_�ŦX�iĵ����
	 * @param subCableWarnList
	 * @param boatInfos
	 */
	public void checkLastWarn(HashMap<String, ArrayList<SubCableInfo>> subCableWarnList, BoatInfo[] boatInfos) {
		BufferedReader br = null;
		FileWriter fw = null;
		String line = null;
		HashMap<String, String> fileContent = new HashMap<String, String>();
		ArrayList<BoatInfo> alertMailList = new ArrayList<>();	//�n��i�iĵmail�����T
		ArrayList<Integer> alertMailTimes = new ArrayList<>();	//�n��i�iĵmail����A�Q�iĵ����
		StringBuffer msg = new StringBuffer();
		Point[] warn = new Point[4];
		
		//loop�Ҧ��iĵ�ϡA�C�ӧiĵ�ϳ��n�g�L�H�U�{��: create alert.csv�Bread alert.csv to fileContent�Bcompare with fileContent�Bwrite to alert.csv
		//�`�N: read from alert.csv�Bwrite to alert.csv����b"loop�iĵ��"�~����C���y�ܻ��A�C�ӧiĵ�Ϩ��O�W�߳B�z���A����V�b�@�_�B�z�C
		for( Map.Entry<String, ArrayList<SubCableInfo>> entry : subCableWarnList.entrySet() ){	//iterate each key
			String subCableWarnName = entry.getKey();	//key: ���lĵ�ٰϪ��W��(���ɦW)
			ArrayList<SubCableInfo> subCableWarns = entry.getValue();	//value: [SubCableInfo, SubCableInfo, SubCableInfo...]
			int count = 0;
			//�C�ӧiĵ�ϬҲ��ͤ@���iĵ�O����
			this.createWarnRecordFile(subCableWarnName);
		
			//�M��fileContent�A�H�K�ݯd�W�@�ӧiĵ�Ϫ����e
			fileContent.clear();
			//�M��alertMailList�A�H�K�ݯd�W�@�ӧiĵ�Ϫ�alert mail���e
			alertMailList.clear();
			//�M��alertMailTimes
			alertMailTimes.clear();
			
			//�NXXX_alert.csv�����eŪ��O����
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(Env.BOAT_ALERT_RECORD_PATH + subCableWarnName + "_alert.csv")));
				while( (line = br.readLine())!=null ) {
					if(line.startsWith("MMSI"))	//���L�Ĥ@�C: title�C
						continue;
					fileContent.put(line.split(",")[0], line.split(",", 2)[1]);
				}
				try { if(br!=null) br.close(); } catch (IOException e) { e.printStackTrace(); }
			} catch (IOException e) {
				e.printStackTrace();
				continue;	//�o��exception���ܫh��������U�@�ӧiĵ�Ϫ��B�z
			} finally {
				try { if(br!=null) br.close(); } catch (IOException e) { e.printStackTrace(); }
			}
			System.out.println("�W���O���ɪ����e: " + fileContent);
			
			
			for(int i=0; i<subCableWarns.size(); i++) {	//�ثesize�@�w�n��4�A�Bwarn point���Ǭ����W�B�k�W�B�k�U�B���U
				warn[i] = new Point();
				warn[i].setX(subCableWarns.get(i).getLongitude());	//�g�׬OX
				warn[i].setY(subCableWarns.get(i).getLatitude());	//�n�׬OY
//				System.out.println(subCableWarnName+": ("+warn[i].getY()+","+warn[i].getX()+")");
			}
			
			Point testPoint = new Point();
			boolean isWarn = false;
			for (int i = 0; i < boatInfos.length; i++) {
				testPoint.setX(boatInfos[i].getLongitude());
				testPoint.setY(boatInfos[i].getLatitude());
				isWarn = this.isPointInArea(warn[0], warn[1], warn[2], warn[3], testPoint);
				
				//�iĵ����: 1.�bĵ�ٰϡB2.�ɳt<5�`(���@/�p��)
				if(isWarn==true && (Double.parseDouble(boatInfos[i].getSpeed())/1.852)<=5) {
//				if(isWarn==true) {
					if(fileContent.containsKey(boatInfos[i].getMmsi())) {	//�N��alert.csv���쥻�w���������
						///�P�_time duration///
						String lastWarn = fileContent.get(boatInfos[i].getMmsi()).split(",")[0].trim();
						String thisWarn = boatInfos[i].getTimestamp().trim();
						Long duration = this.getTimeDiff(lastWarn, thisWarn);
						Long warnAgainPeriod = new Long(warnAgainPeriod_m*60*1000 + warnAgainPeriod_h*60*60*1000 + warnAgainPeriod_d*24*60*60*1000);	//millisecond
						Integer alertTimes = Integer.parseInt(fileContent.get(boatInfos[i].getMmsi()).split(",")[1].trim());	//�iĵ����
						
						if (duration!=null && duration >= warnAgainPeriod) {	//null�N��o��getTimeDiff method exception�Fduration >= warnAgainPeriod�h�A���iĵ�A�Ϥ��h��������ʧ@
//							System.out.println("�O���ɸ̲�W�@�����ɶ�: " + lastWarn);
//							System.out.println("��Ӳ���ɶ�: " + thisWarn);
							String chCountry = CountryMapping.mmsi2Country(boatInfos[i].getMmsi());	//�Q��MMSI�����X����y
							String chType = BoatTypeMapping.code2Text(boatInfos[i].getTypeCode());	//�Q��boat type code�����X�����
							alertTimes ++;	//�iĵ����+1
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
        					fileContent.put(boatInfos[i].getMmsi(), msg.toString());	//fileContent�񪺬O�n�g�^alert.csv�����e
        					alertMailList.add(boatInfos[i]);	//�n�o�iĵ��list
        					alertMailTimes.add(alertTimes);
        					count++;
						}
					}else {	//�N��i�Jĵ�ٰϪ��O�s�
						String chCountry = CountryMapping.mmsi2Country(boatInfos[i].getMmsi());	//�Q��MMSI�����X����y
						String chType = BoatTypeMapping.code2Text(boatInfos[i].getTypeCode());	//�Q��boat type code�����X�����
						msg.setLength(0);
						msg.append(boatInfos[i].getTimestamp()).append(",")
						.append("1").append(",")	//�Ĥ@���i�iĵ�ϡAAlert time�]��1 (�N��Ĥ@���iĵ)
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
						fileContent.put(boatInfos[i].getMmsi(), msg.toString());	//fileContent�񪺬O�n�g�^alert.csv�����e
						alertMailList.add(boatInfos[i]);	//�n�o�iĵ��list
						alertMailTimes.add(1);
						count++;
					}

				}else if(isWarn==true){	//��b�iĵ�Ϥ��A����������L�iĵ����A�]����������ʧ@
					System.out.println("!!!����ĵ�ٰϡA���������iĵ����!!!");
				}else {		//�N����b�iĵ�ϡA�h�qfileContent�����ӵ����(���ޭ쥻fileContent���S�����������A������remove())
					fileContent.remove(boatInfos[i].getMmsi());
				}
					
			}//inner for loop END			
			
			System.out.println("�o���n�g�i�h�����e:" + fileContent);
			System.out.println("�o�e" + count + "���iĵ���");
			
			//�o�e�iĵmail
			if(alertMailList.size() != 0)
				this.sendAlertMail(subCableWarnName, alertMailList, alertMailTimes);
			
			//�N��Ƽg�^alert.csv
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
	 * �p���ɶ����j
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
			System.out.println("�p��time duration�ɵo�Ϳ��~: " + e);
			return null;
		}
	}//getTimeDiff END
	
	
	/**
	 * �P�_P�I�O�_�b�x��area��
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
	 * �N�J�T�I�y�СA�p��~�n [ p1p2 X p1p = (p2.x-p1.x)(p.y-p1.y) - (p.x-p1.x)(p2.y-p1.y) ]
	 * @param p1
	 * @param p2
	 * @param p
	 * @return double
	 */
	public double getCross(Point p1, Point p2, Point p) {
		return (p2.getX()-p1.getX()) * (p.getY()-p1.getY()) - (p.getX()-p1.getX()) * (p2.getY()-p1.getY());   
	}//getCross END
	
	
	/**
	 * ���ͧiĵ�O����
	 * @param warnName
	 */
	public void createWarnRecordFile(String warnName) {
		String path = Env.BOAT_ALERT_RECORD_PATH;

		File alertFile = new File(path + warnName + "_alert.csv");

		FileWriter fw = null;
		
		try {
			System.out.println("���b���ͧiĵ�O����<" + alertFile.getName() + ">...");
			if (alertFile.createNewFile()) {
				System.out.println("�ɮ�<" + alertFile.getName() + ">�إߦ��\!");
				fw = new FileWriter(alertFile);	//fw��§��ɮץ[�W���D�C�A���i���i�L

				fw.write("MMSI,Timestamp,Alert times,Latitude,Longitude,Speed,Direction,Callsign,Country,Heading,IMO,Size A,Size B,Size C,Size D,Ship Name,Type of ship (text)");

				fw.write("\n");
				fw.flush();
			}else {
				System.out.println("�ɮ�<" + alertFile.getName() + ">�w�s�b!");
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
		ArrayList<SubCableInfo> subCableWarnInfos = new ArrayList<>();	//�ѩ�@��loop���folder�̪�csv�ɨ�parseData�A�N�^�Ǫ�array�নarrayList�Φ�
		for(File file : SCWarnFilePath.listFiles())		//�@��loop folder�̥������l��
	 	{
	 		SubCableInfo[] infos = SCWarnParseData.readFromSCFile(SCWarnFilePath.getAbsolutePath()+Env.FILE_SEPARATOR, file.getName(), dataType, SCWarnPackType);
	 		subCableWarnInfos.addAll(Arrays.asList(infos));
	 	}
		SubCableInfo[] tempSCWarnAry = new SubCableInfo[subCableWarnInfos.size()];	//�ѩ�sortSubCableByName()�ݥN�Jarray�A�]���o�̱NarrayList�নarray
		tempSCWarnAry = subCableWarnInfos.toArray(tempSCWarnAry);
		SortEntity SCWarnSortEntity = new SortEntity();
		HashMap<String, ArrayList<SubCableInfo>> subCableWarnList = SCWarnSortEntity.sortSubCableByName(tempSCWarnAry);
		
		//���Xcurrent.csv������
		String boatFilePath = Env.BOAT_PROCESSED_DATA_PATH;
		String boatFileName = "current.csv";
		String boatPackType = "boatRaw";
		ParseData boatParseData = new ParseData();
		BoatInfo[] boatInfos = boatParseData.readFromMPBFile(boatFilePath, boatFileName, dataType, boatPackType);
		
		DetectAndAlert detectAndAlert = new DetectAndAlert();
		detectAndAlert.checkLastWarn(subCableWarnList, boatInfos);
		
		
		
	}//main END

	
	
}