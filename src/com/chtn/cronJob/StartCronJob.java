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
		
		while(true) {	//無窮迴圈
			files = rawBefore.listFiles();
			Arrays.sort(files);		//sort file by file name
			for(File file : files)		//一次loop folder裡全部檔案
			{
				//20秒處理一次檔案
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					System.out.println("sleep 20sec時發生exception: " + e1);
					logger.info("sleep 20sec時發生exception: " + e1);
					break;
				}
				long startTime = System.currentTimeMillis();
				
				//FTP收到檔案時，有可能還沒完整接收就被處理，因此用br去偵測該檔案是否已完整複製於local端。
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
				if(unlock == false && lockCount<3) {	//為了避免檔案本身就不完整，break 3次後強制處理檔案。
					System.out.println("檔案未完整，20秒後重試...");
					logger.info("檔案未完整，20秒後重試...");
					lockCount++;
					break;
				}
				unlock = false;
				lockCount = 0;
				
				//開始處理檔案
				System.out.println("******************BEGIN*******************");
				logger.info("******************BEGIN*******************");
				System.out.println("開始處理檔案<" + file.getName() + ">...");
				logger.info("開始處理檔案<" + file.getName() + ">...");
				if(ClassifyRawData.write2currentFile(file)) 	//write2currentFile成功才執行write2processedFile
				{		
					System.out.println("檔案<" + file.getName() + ">寫入<current.csv>成功!!");
					logger.info("檔案<" + file.getName() + ">寫入<current.csv>成功!!");
					if (ClassifyRawData.write2processedFile(file)) 
					{
						System.out.println("檔案<" + file.getName() + ">處理完畢，正在將原始檔案搬移至rawAfter資料夾...");
						logger.info("檔案<" + file.getName() + ">處理完畢，正在將原始檔案搬移至rawAfter資料夾...");
						if(ClassifyRawData.moveRawFile(file)) 
						{
							System.out.println("檔案<" + file.getName() + ">已搬移至rawAfter資料夾!!");
							logger.info("檔案<" + file.getName() + ">已搬移至rawAfter資料夾!!");
						} else 
						{
							System.out.println("[ERROR] 檔案<" + file.getName() + ">無法搬移至rawAfter資料夾!!");
							logger.info("[ERROR] 檔案<" + file.getName() + ">無法搬移至rawAfter資料夾!!");
							break;	//處理失敗就break，全部重新做，否則順序會亂掉 (write2processedFile會一直重複執行，導致檔案一直寫入)
						}
					} else 
					{
						System.out.println("[ERROR] 檔案<" + file.getName() + ">處理時發生錯誤!!");
						logger.info("[ERROR] 檔案<" + file.getName() + ">處理時發生錯誤!!");
						break;	//處理失敗就break，全部重新做，否則順序會亂掉
					}
				} else 
				{
					System.out.println("[ERROR] 檔案<" + file.getName() + ">寫入current.csv時發生錯誤!!");
					logger.info("[ERROR] 檔案<" + file.getName() + ">寫入current.csv時發生錯誤!!");
					break;	//處理失敗就break，全部重新做，否則順序會亂掉
				}
				long endTime = System.currentTimeMillis();
				System.out.println("耗時共" + (endTime - startTime) + " ms");
				logger.info("耗時共" + (endTime - startTime) + " ms");
				System.out.println("-*-*-*-*-*-*-*-*-*-END*-*-*-*-*-*-*-*-*-*-");
				logger.info("-*-*-*-*-*-*-*-*-*-END*-*-*-*-*-*-*-*-*-*-");
				
				
				//啟動告警偵測
				System.out.println("============Start Alert Detect============");
				logger.info("============Start Alert Detect============");
				long startTime2 = System.currentTimeMillis();
				try {
					int dataType = 0;	//0: .csv
					File SCWarnFilePath = new File(scWarnPath);
					String SCWarnPackType = "subCable";
					ParseData SCWarnParseData = new ParseData();
					ArrayList<SubCableInfo> subCableWarnInfos = new ArrayList<>();	//由於一次loop整個folder裡的csv檔到parseData，將回傳的array轉成arrayList形式
					for(File file2 : SCWarnFilePath.listFiles())		//一次loop folder裡全部海纜檔
				 	{
				 		SubCableInfo[] infos = SCWarnParseData.readFromSCFile(SCWarnFilePath.getAbsolutePath()+Env.FILE_SEPARATOR, file2.getName(), dataType, SCWarnPackType);
				 		subCableWarnInfos.addAll(Arrays.asList(infos));
				 	}
					SubCableInfo[] tempSCWarnAry = new SubCableInfo[subCableWarnInfos.size()];	//由於sortSubCableByName()需代入array，因此這裡將arrayList轉成array
					tempSCWarnAry = subCableWarnInfos.toArray(tempSCWarnAry);
					SortEntity SCWarnSortEntity = new SortEntity();
					HashMap<String, ArrayList<SubCableInfo>> subCableWarnList = SCWarnSortEntity.sortSubCableByName(tempSCWarnAry);
					
					//撈出current.csv的船隻資料
					String boatFilePath = processedPath;
					String boatFileName = "current.csv";
					String boatPackType = "boatRaw";
					ParseData boatParseData = new ParseData();
					BoatInfo[] boatInfos = boatParseData.readFromMPBFile(boatFilePath, boatFileName, dataType, boatPackType);
					
					DetectAndAlert detectAndAlert = new DetectAndAlert();
					detectAndAlert.checkLastWarn(subCableWarnList, boatInfos);
				} catch (Exception e) {
					System.out.println("執行告警程序時發生exception: " + e);
					logger.info("執行告警程序時發生exception: " + e);
				}
				long endTime2 = System.currentTimeMillis();
				System.out.println("耗時共" + (endTime2 - startTime2) + " ms");
				logger.info("耗時共" + (endTime2 - startTime2) + " ms");
				System.out.println("===================END====================\n");
				logger.info("===================END====================\n");
				
				
			}//for loop END
		}//while END
		
		
	}//main END

}
