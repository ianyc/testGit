package com.chtn.spaws;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

import com.chtn.spaws.parse.ResolveField;
import com.chtn.util.Env;
import com.chttl.iois.profile.PROFILEException;
import com.chttl.iois.profile.ProfileCenter;

public class ProcessHistory {
	private static String before = "C:\\Users\\user\\Desktop\\temp\\hisData.csv";
	private static String after = "C:\\Users\\user\\Desktop\\temp\\after";
	
	
	
	
	void createFile(String fileName) throws IOException {
		File outFile = new File(after+Env.FILE_SEPARATOR+fileName+".csv");
		FileWriter fw = null;
		
		if (outFile.createNewFile()) {
			System.out.println("�ɮ׫إߦ��\");
			fw = new FileWriter(outFile);	//fw��§��ɮץ[�W���D�C�A���i���i�L
			fw.write("Base station time stamp,MMSI,Latitude,Longitude,Speed over ground,Course over ground,Type of ship (text)");
			fw.write("\n");
			fw.flush();
		}else {
			System.out.println("�ɮפw�s�b");
		}
	}//createFile END
	
	
	
	
	
	void classifyData(String fileName, Hashtable hashData) throws IOException {
		FileWriter fileWriter = null;
		File outFile = new File(after+Env.FILE_SEPARATOR+fileName+".csv");
		fileWriter = new FileWriter(outFile, true);	//append=true
		
		StringBuffer toNewLine = new StringBuffer();
		toNewLine.setLength(0);	//�C�@row�ҭnclear toNewLine
		toNewLine.append(hashData.get("BaseStationTimeStamp").toString().replace("/", "-")).append(",")
				.append(hashData.get("MMSI").toString()).append(",")
				.append(Double.parseDouble(hashData.get("Latitude").toString())).append(",")
				.append(Double.parseDouble(hashData.get("Longitude").toString())).append(",")
				.append(hashData.get("SpeedOverGround").toString()).append(",")
				.append(hashData.get("CourseOverGround").toString()).append(",")
				.append(hashData.get("TypeOfShipText").toString());
		fileWriter.write(toNewLine.toString());
		fileWriter.write("\n");
		fileWriter.flush();
	}//classifyData END
	
	
	
	
	
	public static void main(String[] args) {
		ProcessHistory processHistory = new ProcessHistory();
		File inFile = new File(before);
		BufferedReader br = null;
		String line = null;
		ProfileCenter pc;
		int dataType = 0;
		String packType = "boatRaw";
		int count = 0;
		
		try {
			
			br = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
			while( (line = br.readLine())!=null ) {
				count++;
				if(line.startsWith("Base")) {
					continue;
				}
				pc = ProfileCenter.getProfileCenter(Env.TABLE_FIELD_PATH);	//Ūconfig file
				ResolveField resolveField = new ResolveField();
				resolveField.getResolveField(line, dataType, pc, packType);
				Hashtable hashData = resolveField.toHashtable();
				
				StringBuffer sb = new StringBuffer(hashData.get("BaseStationTimeStamp").toString().substring(0,13).replace("/", "_").replace(" ", "_"));
				
				//����
//				processHistory.createFile(sb.toString());
				
				//�B�z���
				processHistory.classifyData(sb.toString(), hashData);
				
			}
			
			
			
			System.out.println("TOT: " + count);
	
			
			
		} catch (IOException | PROFILEException e1) {
			e1.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		
		
		
		
		
		
		
		
		
	}//main END

	
}
