package com.chtn.cronJob;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chtn.spaws.BoatTypeMapping;
import com.chtn.spaws.CountryMapping;
import com.chtn.spaws.parse.ResolveField;
import com.chtn.util.Env;
import com.chttl.iois.profile.ProfileCenter;

public class ClassifyRawData {
	public static String rawBeforePath = Env.BOAT_RAW_BEFORE_PATH;
	public static String rawAfterPath = Env.BOAT_RAW_AFTER_PATH;
	public static String processedPath = Env.BOAT_PROCESSED_DATA_PATH;
	
	static Logger logger = LogManager.getLogger(ClassifyRawData.class);
	
	/**
	 * �`�@�|�g�J����ɮסA�䤤�@���H�ɶ��Ϥ�(e.g.�C�p�ɤ@����)�A�B�o�����ݭn�����F�t�@���ɥΨӺ��@��򥻸�T(e.g.MMSI�Bcountry�Bboat size...etc)
	 * @param inFile
	 * @return boolean: true if succeed
	 */
	public static boolean write2processedFile(File inFile) {
		StringBuffer sb = new StringBuffer(inFile.getName().substring(0,10).trim());	//�^��yyyyMMddHH
		sb.insert(8, "_").insert(6, "_").insert(4, "_").append(".csv");		//�w�q�ɦW�榡: yyyy_MM_dd_HH.csv
		sb.insert(0, processedPath);	//�[�W���|
		String newFilePath = sb.toString();		//�����ɮ׸��|
		File outFile = new File(newFilePath);	//output file
		File outFile2 = new File(processedPath+"MMSI_basic_info.csv");	//output file
		BufferedReader br = null;
		BufferedReader br2 = null;
		String line = null;
		String line2 = null;
		StringBuffer toNewLine = new StringBuffer();
		StringBuffer string2Write = new StringBuffer();
		StringBuffer string2Write2 = new StringBuffer();
		FileWriter fw = null;	//�إ߷s�ɮרñNtitle�C�g�J�ɮ�
		FileWriter fileWriter = null;	//�g�J�Ĥ@���� (named by date)
		FileWriter fileWriter2 = null;	//�g�J�ĤG����(MMSI_basic_info.csv)
		HashMap<String, String> mmsiContent = new HashMap<String, String>();
		ProfileCenter pc;
		ResolveField resolveField = new ResolveField();
		int dataType = 0;
		String packType = "boatRaw";
		
		try {
			//Create new file
			try {	//����processed file named by time
				System.out.println("���b����<" + outFile.getName() + ">...");
				logger.info("���b����<" + outFile.getName() + ">...");
				if (outFile.createNewFile()) {
					System.out.println("�ɮ�<" + outFile.getName() + ">�إߦ��\!");
					logger.info("�ɮ�<" + outFile.getName() + ">�إߦ��\!");
					fw = new FileWriter(outFile);	//fw��§��ɮץ[�W���D�C�A���i���i�L
					fw.write("Base station time stamp,MMSI,Latitude,Longitude,Speed over ground,Course over ground,Type of ship");
					fw.write("\n");
					fw.flush();
				}else {
					System.out.println("�ɮ�<" + outFile.getName() + ">�w�s�b!");
					logger.info("�ɮ�<" + outFile.getName() + ">�w�s�b!");
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e);
			}
			try {	//����processed file named by MMSI_basic_info
				System.out.println("���b����<" + outFile2.getName() + ">...");
				logger.info("���b����<" + outFile2.getName() + ">...");
				if (outFile2.createNewFile()) {
					System.out.println("�ɮ�<" + outFile2.getName() + ">�إߦ��\!");
					logger.info("�ɮ�<" + outFile2.getName() + ">�إߦ��\!");
					fw = new FileWriter(outFile2);	//fw��§��ɮץ[�W���D�C�A���i���i�L
					fw.write("MMSI,Callsign,Country,IMO,Ship Name,Size A,Size B,Size C,Size D,Type of ship (text)");
					fw.write("\n");
					fw.flush();
				}else {
					System.out.println("�ɮ�<" + outFile2.getName() + ">�w�s�b!");
					logger.info("�ɮ�<" + outFile2.getName() + ">�w�s�b!");
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e);
			}
			
			//Read file and Write file
			br = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
			fileWriter = new FileWriter(outFile, true);	//append=true
			
			//Read MMSI_basic_info file to memory
			br2 = new BufferedReader(new InputStreamReader(new FileInputStream(outFile2)));		//outFile2: MMSI_basic_info.csv
			while( (line2 = br2.readLine())!=null ) {
				if(line2.startsWith("MMSI"))	//���L�Ĥ@�C: title�C
					continue;
				mmsiContent.put(line2.split(",")[0], line2.split(",", 2)[1]);
			}
			fileWriter2 = new FileWriter(outFile2, false);	//append=false�Asource file�CŪ���@��csv�N��sMMSI_basic_info.csv
			
			int countLine = 0;
			while( (line = br.readLine())!=null ) {
				if(line.startsWith("Base station time stamp") || line.startsWith("**********"))	//���L�Ĥ@�C: title�C
					continue;
				countLine++;
				try {	//�קK�Y�@row resolveField���Ѯɫ᭱����Ƴ����Aresolve
					pc = ProfileCenter.getProfileCenter(Env.TABLE_FIELD_PATH);	//Ūconfig file
					resolveField.getResolveField(line, dataType, pc, packType);
					Hashtable hashData = resolveField.toHashtable();	//�Nparse�᪺���G�s��hash table�A�H�K�s��
					
					//�Ĥ@���ɮ�:date.csv�һ����
					toNewLine.setLength(0);	//�C�@row�ҭnclear toNewLine
					toNewLine.append(hashData.get("BaseStationTimeStamp").toString()).append(",")
							.append(hashData.get("MMSI").toString()).append(",")
							.append(Double.parseDouble(hashData.get("Latitude").toString())).append(",")
							.append(Double.parseDouble(hashData.get("Longitude").toString())).append(",")
							.append(hashData.get("SpeedOverGround").toString()).append(",")
							.append(hashData.get("CourseOverGround").toString()).append(",")
							.append(hashData.get("TypeOfShip").toString());
					fileWriter.write(toNewLine.toString());
					fileWriter.write("\n");
					
					//�ĤG���ɮ�:MMSI_basic_info.csv(���@��򥻸�T)�һ����
					String chCountry = CountryMapping.mmsi2Country(hashData.get("MMSI").toString());	//�Q��MMSI�����X����y
					String chType = BoatTypeMapping.code2Text(hashData.get("TypeOfShip").toString());	//�Q��boat type code�����X�����
					toNewLine.setLength(0);	//�N�򥻸�����g�JMMSI_basic_info.csv�A�G��clear toNewLine
					toNewLine
					.append(hashData.get("Callsign").toString()).append(",")
					.append(chCountry).append(",")
					.append(hashData.get("IMO").toString()).append(",")
					.append(hashData.get("ShipName").toString()).append(",")
					.append(hashData.get("SizeA").toString()).append(",")
					.append(hashData.get("SizeB").toString()).append(",")
					.append(hashData.get("SizeC").toString()).append(",")
					.append(hashData.get("SizeD").toString()).append(",")
					.append(chType);
					
					String tempMMSI = hashData.get("MMSI").toString();	//source file���ثe�Ҧbline������MMSI
					mmsiContent.put(tempMMSI, toNewLine.toString());

				} catch (Exception e) {
					logger.error(e);
					e.printStackTrace();
				}
		
			}//while loop END
			System.out.println("(write2processedFile�`�@�B�z�F" + countLine + "�����)");
			
			fileWriter.flush();		//fileWriter��flush��bwhile loop�~�A�į�|�n�@��
		
			fileWriter2.write("MMSI,Callsign,Country,IMO,Ship Name,Size A,Size B,Size C,Size D,Type of ship (text)");
			fileWriter2.write("\n");
			for( Map.Entry<String, String> entry : mmsiContent.entrySet() ){	
				String key = entry.getKey();
				String value = entry.getValue();
				fileWriter2.write(key + "," + value + "\n");
			}
			fileWriter2.flush();
			
			
			try { if(br!=null) br.close(); } catch (IOException e) { e.printStackTrace(); logger.error(e);}
			try { if(br2!=null) br2.close(); } catch (IOException e) { e.printStackTrace(); logger.error(e);}
			try { if(fw!=null) fw.close(); } catch (IOException e) { e.printStackTrace(); logger.error(e);}
			try { if(fileWriter!=null) fileWriter.close(); } catch (IOException e) { e.printStackTrace(); logger.error(e);}
			try { if(fileWriter2!=null) fileWriter2.close(); } catch (IOException e) { e.printStackTrace(); logger.error(e);}
			
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e);
			return false;
		} finally {
			try { if(br!=null) br.close(); } catch (IOException e) { e.printStackTrace(); logger.error(e);}
			try { if(br2!=null) br2.close(); } catch (IOException e) { e.printStackTrace(); logger.error(e);}
			try { if(fw!=null) fw.close(); } catch (IOException e) { e.printStackTrace(); logger.error(e);}
			try { if(fileWriter!=null) fileWriter.close(); } catch (IOException e) { e.printStackTrace(); logger.error(e);}
			try { if(fileWriter2!=null) fileWriter2.close(); } catch (IOException e) { e.printStackTrace(); logger.error(e);}
		}//try...catch END
	}//write2processedFile END

	 
	/**
	 * �N�̷s��source file�g�Jcurrent.csv�A�@�n�J�����ɱq����Ū����ơCcurrent.csv���S���i��G���A�]�����ɷ|�@��replaced by last data�A�G��ƶq���|�ܤj�C
	 * @param inFile
	 * @return boolean: true if succeed
	 */
	public static boolean write2currentFile(File inFile) {
		File outFile = new File(processedPath+"current.csv");	//output file
		BufferedReader br = null;
		String line = null;
		FileWriter fileWriter = null;
		
		try {
			System.out.println("���b����<" + outFile.getName() + ">...");
			logger.info("���b����<" + outFile.getName() + ">...");
			if (outFile.createNewFile()) {
				System.out.println("�ɮ�<" + outFile.getName() + ">�إߦ��\!");
				logger.info("�ɮ�<" + outFile.getName() + ">�إߦ��\!");
			}else {
				System.out.println("�ɮ�<" + outFile.getName() + ">�w�s�b!");
				logger.info("�ɮ�<" + outFile.getName() + ">�w�s�b!");
			}
			
			br = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
			fileWriter = new FileWriter(outFile);	//append=false
			fileWriter.write("Base station time stamp,Callsign,Country (AIS),Course over ground,Data source type,Data source type (text),Destination,Draught,ETA,Heading,IMO,Latitude,Longitude,MMSI,Navigational status,Navigational status (text),PGHP time stamp,Rotation,Ship name,Size A,Size B,Size C,Size D,Speed over ground,TAG time stamp,Target class,Target class (text),Type of cargo,Type of cargo (text),Type of position fixing device,Type of ship,Type of ship (text),Type of ship and cargo,UNIX time stamp");
			fileWriter.write("\n");
			int countLine = 0;
			while( (line = br.readLine())!=null ) {
				if(line.startsWith("Base station time stamp") || line.startsWith("**********"))	//���L�Ĥ@�C: title�C
					continue;
				countLine++;
				fileWriter.write(line);
				fileWriter.write("\n");
			}
			System.out.println("(write2currentFile�`�@�B�z�F" + countLine + "�����)");
			fileWriter.flush();
			
			
			try { if(br!=null) br.close(); } catch (IOException e) { e.printStackTrace(); logger.error(e);}
			try { if(fileWriter!=null) fileWriter.close(); } catch (IOException e) { e.printStackTrace(); logger.error(e);}
			
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e);
			return false;
		} finally {
			try { if(br!=null) br.close(); } catch (IOException e) { e.printStackTrace(); logger.error(e);}
			try { if(fileWriter!=null) fileWriter.close(); } catch (IOException e) { e.printStackTrace(); logger.error(e);}
		}//try...catch END
	}//write2currentFile END
	
	
	/**
	 * �N�B�z�L��source file����O����Ƨ�
	 * @param file
	 * @return boolean: true if succeed
	 */
	public static boolean moveRawFile(File file) {
		Path src = Paths.get(file.getAbsolutePath());
		Path dest = Paths.get(rawAfterPath+file.getName());
		
		try {
			if(Files.exists(dest)) {	//�p�G�ت��a�w�g���P�ɦW�A�h���R�����ɦA���ʷs�ɡA�_�h�|�o��FileAlreadyExistsException
				Files.delete(dest);
			}
			Files.move(src, dest);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e);
			return false;
		}
	}//moveRawFile END
	
	
	public static void main(String[] args) throws InterruptedException {
		File rawBefore = new File(rawBeforePath);
		File rawAfter = new File(rawAfterPath);
		File[] files;
		BufferedReader br = null;
		String line = null;
		boolean unlock = false;
		int lockCount = 0;
		
		while(true) {	//�L�a�j��
			files = rawBefore.listFiles();
			Arrays.sort(files);		//sort file by file name
			for(File file : files)		//�@��loop folder�̥����ɮ�
			{
				//20��B�z�@���ɮ�
				Thread.sleep(2000);
				long startTime = System.currentTimeMillis();
				
				//FTP�����ɮ׮ɡA���i���٨S���㱵���N�Q�B�z�A�]����br�h�������ɮ׬O�_�w����ƻs��local�ݡC
				try {
					br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
					while( (line = br.readLine())!=null ) {
						if(line.startsWith("**********")) {
							unlock = true;
						}
					}
					try { if(br!=null) br.close(); } catch (IOException e) { e.printStackTrace(); logger.error(e);}
				} catch (Exception e) {
				} finally {
					try { if(br!=null) br.close(); } catch (IOException e) { e.printStackTrace(); logger.error(e);}
				}
				if(unlock == false && lockCount<3) {	//���F�קK�ɮץ����N������Abreak 3����j��B�z�ɮסC
					System.out.println("�ɮץ�����A20��᭫��...");
					logger.info("�ɮץ�����A20��᭫��...");
					lockCount++;
					break;
				}
				unlock = false;
				lockCount = 0;
				
				//�}�l�B�z�ɮ�
				System.out.println("******************BEGIN*******************");
				logger.info("******************BEGIN*******************");
				System.out.println("�}�l�B�z�ɮ�<" + file.getName() + ">...");
				logger.info("�}�l�B�z�ɮ�<" + file.getName() + ">...");
				if(write2currentFile(file)) 	//write2currentFile���\�~����write2processedFile
				{		
					System.out.println("�ɮ�<" + file.getName() + ">�g�J<current.csv>���\!!");
					logger.info("�ɮ�<" + file.getName() + ">�g�J<current.csv>���\!!");
					if (write2processedFile(file)) 
					{
						System.out.println("�ɮ�<" + file.getName() + ">�B�z�����A���b�N��l�ɮ׷h����rawAfter��Ƨ�...");
						logger.info("�ɮ�<" + file.getName() + ">�B�z�����A���b�N��l�ɮ׷h����rawAfter��Ƨ�...");
						if(moveRawFile(file)) 
						{
							System.out.println("�ɮ�<" + file.getName() + ">�w�h����rawAfter��Ƨ�!!");
							logger.info("�ɮ�<" + file.getName() + ">�w�h����rawAfter��Ƨ�!!");
						} else 
						{
							System.out.println("[ERROR] �ɮ�<" + file.getName() + ">�L�k�h����rawAfter��Ƨ�!!");
							logger.info("[ERROR] �ɮ�<" + file.getName() + ">�L�k�h����rawAfter��Ƨ�!!");
							break;	//�B�z���ѴNbreak�A�������s���A�_�h���Ƿ|�ñ� (write2processedFile�|�@�����ư���A�ɭP�ɮפ@���g�J)
						}
					} else 
					{
						System.out.println("[ERROR] �ɮ�<" + file.getName() + ">�B�z�ɵo�Ϳ��~!!");
						logger.info("[ERROR] �ɮ�<" + file.getName() + ">�B�z�ɵo�Ϳ��~!!");
						break;	//�B�z���ѴNbreak�A�������s���A�_�h���Ƿ|�ñ�
					}
				} else 
				{
					System.out.println("[ERROR] �ɮ�<" + file.getName() + ">�g�Jcurrent.csv�ɵo�Ϳ��~!!");
					logger.info("[ERROR] �ɮ�<" + file.getName() + ">�g�Jcurrent.csv�ɵo�Ϳ��~!!");
					break;	//�B�z���ѴNbreak�A�������s���A�_�h���Ƿ|�ñ�
				}
				long endTime = System.currentTimeMillis();
				System.out.println("�Ӯɦ@" + (endTime - startTime) + " ms");
				logger.info("�Ӯɦ@" + (endTime - startTime) + " ms");
				System.out.println("-*-*-*-*-*-*-*-*-*-END*-*-*-*-*-*-*-*-*-*-\n");
				logger.info("-*-*-*-*-*-*-*-*-*-END*-*-*-*-*-*-*-*-*-*-\n");
			}//for loop END
		}//while END
		
	}//main END
	
}
