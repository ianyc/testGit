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
	 * 總共會寫入兩份檔案，其中一份以時間區分(e.g.每小時一份檔)，且濾掉不需要的欄位；另一份檔用來維護船隻基本資訊(e.g.MMSI、country、boat size...etc)
	 * @param inFile
	 * @return boolean: true if succeed
	 */
	public static boolean write2processedFile(File inFile) {
		StringBuffer sb = new StringBuffer(inFile.getName().substring(0,10).trim());	//擷取yyyyMMddHH
		sb.insert(8, "_").insert(6, "_").insert(4, "_").append(".csv");		//定義檔名格式: yyyy_MM_dd_HH.csv
		sb.insert(0, processedPath);	//加上路徑
		String newFilePath = sb.toString();		//完整檔案路徑
		File outFile = new File(newFilePath);	//output file
		File outFile2 = new File(processedPath+"MMSI_basic_info.csv");	//output file
		BufferedReader br = null;
		BufferedReader br2 = null;
		String line = null;
		String line2 = null;
		StringBuffer toNewLine = new StringBuffer();
		StringBuffer string2Write = new StringBuffer();
		StringBuffer string2Write2 = new StringBuffer();
		FileWriter fw = null;	//建立新檔案並將title列寫入檔案
		FileWriter fileWriter = null;	//寫入第一份檔 (named by date)
		FileWriter fileWriter2 = null;	//寫入第二份檔(MMSI_basic_info.csv)
		HashMap<String, String> mmsiContent = new HashMap<String, String>();
		ProfileCenter pc;
		ResolveField resolveField = new ResolveField();
		int dataType = 0;
		String packType = "boatRaw";
		
		try {
			//Create new file
			try {	//產生processed file named by time
				System.out.println("正在產生<" + outFile.getName() + ">...");
				logger.info("正在產生<" + outFile.getName() + ">...");
				if (outFile.createNewFile()) {
					System.out.println("檔案<" + outFile.getName() + ">建立成功!");
					logger.info("檔案<" + outFile.getName() + ">建立成功!");
					fw = new FileWriter(outFile);	//fw單純把檔案加上標題列，其實可有可無
					fw.write("Base station time stamp,MMSI,Latitude,Longitude,Speed over ground,Course over ground,Type of ship");
					fw.write("\n");
					fw.flush();
				}else {
					System.out.println("檔案<" + outFile.getName() + ">已存在!");
					logger.info("檔案<" + outFile.getName() + ">已存在!");
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e);
			}
			try {	//產生processed file named by MMSI_basic_info
				System.out.println("正在產生<" + outFile2.getName() + ">...");
				logger.info("正在產生<" + outFile2.getName() + ">...");
				if (outFile2.createNewFile()) {
					System.out.println("檔案<" + outFile2.getName() + ">建立成功!");
					logger.info("檔案<" + outFile2.getName() + ">建立成功!");
					fw = new FileWriter(outFile2);	//fw單純把檔案加上標題列，其實可有可無
					fw.write("MMSI,Callsign,Country,IMO,Ship Name,Size A,Size B,Size C,Size D,Type of ship (text)");
					fw.write("\n");
					fw.flush();
				}else {
					System.out.println("檔案<" + outFile2.getName() + ">已存在!");
					logger.info("檔案<" + outFile2.getName() + ">已存在!");
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
				if(line2.startsWith("MMSI"))	//略過第一列: title列
					continue;
				mmsiContent.put(line2.split(",")[0], line2.split(",", 2)[1]);
			}
			fileWriter2 = new FileWriter(outFile2, false);	//append=false，source file每讀完一份csv就刷新MMSI_basic_info.csv
			
			int countLine = 0;
			while( (line = br.readLine())!=null ) {
				if(line.startsWith("Base station time stamp") || line.startsWith("**********"))	//略過第一列: title列
					continue;
				countLine++;
				try {	//避免某一row resolveField失敗時後面的資料都不再resolve
					pc = ProfileCenter.getProfileCenter(Env.TABLE_FIELD_PATH);	//讀config file
					resolveField.getResolveField(line, dataType, pc, packType);
					Hashtable hashData = resolveField.toHashtable();	//將parse後的結果存成hash table，以便存取
					
					//第一份檔案:date.csv所需欄位
					toNewLine.setLength(0);	//每一row皆要clear toNewLine
					toNewLine.append(hashData.get("BaseStationTimeStamp").toString()).append(",")
							.append(hashData.get("MMSI").toString()).append(",")
							.append(Double.parseDouble(hashData.get("Latitude").toString())).append(",")
							.append(Double.parseDouble(hashData.get("Longitude").toString())).append(",")
							.append(hashData.get("SpeedOverGround").toString()).append(",")
							.append(hashData.get("CourseOverGround").toString()).append(",")
							.append(hashData.get("TypeOfShip").toString());
					fileWriter.write(toNewLine.toString());
					fileWriter.write("\n");
					
					//第二份檔案:MMSI_basic_info.csv(維護船隻基本資訊)所需欄位
					String chCountry = CountryMapping.mmsi2Country(hashData.get("MMSI").toString());	//利用MMSI對應出船隻國籍
					String chType = BoatTypeMapping.code2Text(hashData.get("TypeOfShip").toString());	//利用boat type code對應出船隻種類
					toNewLine.setLength(0);	//將基本資料欄位寫入MMSI_basic_info.csv，故先clear toNewLine
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
					
					String tempMMSI = hashData.get("MMSI").toString();	//source file中目前所在line對應的MMSI
					mmsiContent.put(tempMMSI, toNewLine.toString());

				} catch (Exception e) {
					logger.error(e);
					e.printStackTrace();
				}
		
			}//while loop END
			System.out.println("(write2processedFile總共處理了" + countLine + "筆資料)");
			
			fileWriter.flush();		//fileWriter的flush放在while loop外，效能會好一些
		
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
	 * 將最新的source file寫入current.csv，一登入網頁時從此檔讀取資料。current.csv欄位沒有進行瘦身，因為此檔會一直replaced by last data，故資料量不會很大。
	 * @param inFile
	 * @return boolean: true if succeed
	 */
	public static boolean write2currentFile(File inFile) {
		File outFile = new File(processedPath+"current.csv");	//output file
		BufferedReader br = null;
		String line = null;
		FileWriter fileWriter = null;
		
		try {
			System.out.println("正在產生<" + outFile.getName() + ">...");
			logger.info("正在產生<" + outFile.getName() + ">...");
			if (outFile.createNewFile()) {
				System.out.println("檔案<" + outFile.getName() + ">建立成功!");
				logger.info("檔案<" + outFile.getName() + ">建立成功!");
			}else {
				System.out.println("檔案<" + outFile.getName() + ">已存在!");
				logger.info("檔案<" + outFile.getName() + ">已存在!");
			}
			
			br = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
			fileWriter = new FileWriter(outFile);	//append=false
			fileWriter.write("Base station time stamp,Callsign,Country (AIS),Course over ground,Data source type,Data source type (text),Destination,Draught,ETA,Heading,IMO,Latitude,Longitude,MMSI,Navigational status,Navigational status (text),PGHP time stamp,Rotation,Ship name,Size A,Size B,Size C,Size D,Speed over ground,TAG time stamp,Target class,Target class (text),Type of cargo,Type of cargo (text),Type of position fixing device,Type of ship,Type of ship (text),Type of ship and cargo,UNIX time stamp");
			fileWriter.write("\n");
			int countLine = 0;
			while( (line = br.readLine())!=null ) {
				if(line.startsWith("Base station time stamp") || line.startsWith("**********"))	//略過第一列: title列
					continue;
				countLine++;
				fileWriter.write(line);
				fileWriter.write("\n");
			}
			System.out.println("(write2currentFile總共處理了" + countLine + "筆資料)");
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
	 * 將處理過的source file移到別的資料夾
	 * @param file
	 * @return boolean: true if succeed
	 */
	public static boolean moveRawFile(File file) {
		Path src = Paths.get(file.getAbsolutePath());
		Path dest = Paths.get(rawAfterPath+file.getName());
		
		try {
			if(Files.exists(dest)) {	//如果目的地已經有同檔名，則先刪除舊檔再移動新檔，否則會發生FileAlreadyExistsException
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
		
		while(true) {	//無窮迴圈
			files = rawBefore.listFiles();
			Arrays.sort(files);		//sort file by file name
			for(File file : files)		//一次loop folder裡全部檔案
			{
				//20秒處理一次檔案
				Thread.sleep(2000);
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
				if(write2currentFile(file)) 	//write2currentFile成功才執行write2processedFile
				{		
					System.out.println("檔案<" + file.getName() + ">寫入<current.csv>成功!!");
					logger.info("檔案<" + file.getName() + ">寫入<current.csv>成功!!");
					if (write2processedFile(file)) 
					{
						System.out.println("檔案<" + file.getName() + ">處理完畢，正在將原始檔案搬移至rawAfter資料夾...");
						logger.info("檔案<" + file.getName() + ">處理完畢，正在將原始檔案搬移至rawAfter資料夾...");
						if(moveRawFile(file)) 
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
				System.out.println("-*-*-*-*-*-*-*-*-*-END*-*-*-*-*-*-*-*-*-*-\n");
				logger.info("-*-*-*-*-*-*-*-*-*-END*-*-*-*-*-*-*-*-*-*-\n");
			}//for loop END
		}//while END
		
	}//main END
	
}
