package com.chtn.cronJob;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chtn.spaws.SortEntity;
import com.chtn.spaws.entity.BoatInfo;
import com.chtn.spaws.entity.SubCableInfo;
import com.chtn.spaws.parse.ParseData;
import com.chtn.util.Env;

public class StartCronJob {
	public static String rawBeforePath = Env.BOAT_RAW_BEFORE_PATH;
	public static String rawAfterPath = Env.BOAT_RAW_AFTER_PATH;
	public static String processedPath = Env.BOAT_PROCESSED_DATA_PATH;
	public static String scWarnPath = Env.SC_WARN_DATA_PATH;
	
	static Logger logger = LogManager.getLogger(ClassifyRawData.class);
	
	
	
	public static void main(String[] args) {
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
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					System.out.println("sleep 20sec�ɵo��exception: " + e1);
					logger.info("sleep 20sec�ɵo��exception: " + e1);
					break;
				}
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
				if(ClassifyRawData.write2currentFile(file)) 	//write2currentFile���\�~����write2processedFile
				{		
					System.out.println("�ɮ�<" + file.getName() + ">�g�J<current.csv>���\!!");
					logger.info("�ɮ�<" + file.getName() + ">�g�J<current.csv>���\!!");
					if (ClassifyRawData.write2processedFile(file)) 
					{
						System.out.println("�ɮ�<" + file.getName() + ">�B�z�����A���b�N��l�ɮ׷h����rawAfter��Ƨ�...");
						logger.info("�ɮ�<" + file.getName() + ">�B�z�����A���b�N��l�ɮ׷h����rawAfter��Ƨ�...");
						if(ClassifyRawData.moveRawFile(file)) 
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
				System.out.println("-*-*-*-*-*-*-*-*-*-END*-*-*-*-*-*-*-*-*-*-");
				logger.info("-*-*-*-*-*-*-*-*-*-END*-*-*-*-*-*-*-*-*-*-");
				
				
				//�Ұʧiĵ����
				System.out.println("============Start Alert Detect============");
				logger.info("============Start Alert Detect============");
				long startTime2 = System.currentTimeMillis();
				try {
					int dataType = 0;	//0: .csv
					File SCWarnFilePath = new File(scWarnPath);
					String SCWarnPackType = "subCable";
					ParseData SCWarnParseData = new ParseData();
					ArrayList<SubCableInfo> subCableWarnInfos = new ArrayList<>();	//�ѩ�@��loop���folder�̪�csv�ɨ�parseData�A�N�^�Ǫ�array�নarrayList�Φ�
					for(File file2 : SCWarnFilePath.listFiles())		//�@��loop folder�̥������l��
				 	{
				 		SubCableInfo[] infos = SCWarnParseData.readFromSCFile(SCWarnFilePath.getAbsolutePath()+Env.FILE_SEPARATOR, file2.getName(), dataType, SCWarnPackType);
				 		subCableWarnInfos.addAll(Arrays.asList(infos));
				 	}
					SubCableInfo[] tempSCWarnAry = new SubCableInfo[subCableWarnInfos.size()];	//�ѩ�sortSubCableByName()�ݥN�Jarray�A�]���o�̱NarrayList�নarray
					tempSCWarnAry = subCableWarnInfos.toArray(tempSCWarnAry);
					SortEntity SCWarnSortEntity = new SortEntity();
					HashMap<String, ArrayList<SubCableInfo>> subCableWarnList = SCWarnSortEntity.sortSubCableByName(tempSCWarnAry);
					
					//���Xcurrent.csv������
					String boatFilePath = processedPath;
					String boatFileName = "current.csv";
					String boatPackType = "boatRaw";
					ParseData boatParseData = new ParseData();
					BoatInfo[] boatInfos = boatParseData.readFromMPBFile(boatFilePath, boatFileName, dataType, boatPackType);
					
					DetectAndAlert detectAndAlert = new DetectAndAlert();
					detectAndAlert.checkLastWarn(subCableWarnList, boatInfos);
				} catch (Exception e) {
					System.out.println("����iĵ�{�Ǯɵo��exception: " + e);
					logger.info("����iĵ�{�Ǯɵo��exception: " + e);
				}
				long endTime2 = System.currentTimeMillis();
				System.out.println("�Ӯɦ@" + (endTime2 - startTime2) + " ms");
				logger.info("�Ӯɦ@" + (endTime2 - startTime2) + " ms");
				System.out.println("===================END====================\n");
				logger.info("===================END====================\n");
				
				
			}//for loop END
		}//while END
		
		
	}//main END

}
