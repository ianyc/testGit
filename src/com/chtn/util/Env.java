package com.chtn.util;



/**
 * <p>Title:IT Infrastructure環境變數</p>
 * <p>Description:封裝it infrastructure環境變數</p>
 * <p>Copyright: Copyright (c) 2020/10/13</p>
 * @author IAN HSIAO
 */
public class Env {
	/**
	 * 系統的目錄分隔符號
	 */
	public static String FILE_SEPARATOR = System.getProperty("file.separator");

	/**
	 * 航港局透過FTP傳送之船隻檔案存放路徑
	 */
//	public static String BOAT_RAW_BEFORE_PATH = "/home/ntouftp/boatdata/before/";	//OL
//	public static String BOAT_RAW_AFTER_PATH = "/home/ntouftp/boatdata/after/";		//OL
	public static String BOAT_RAW_BEFORE_PATH = "D:\\Tomcat\\SPAWS\\webapps\\SPAWS\\datas\\boat\\raw\\before\\";	//OA
	public static String BOAT_RAW_AFTER_PATH = "D:\\Tomcat\\SPAWS\\webapps\\SPAWS\\datas\\boat\\raw\\after\\";		//OA
	
	
	/**
	 * 專案系統路徑
	 */
//	public static String HOME_PATH = "/tomcat/SPAWS/webapps/SPAWS/";		//OL
	public static String HOME_PATH = "D:\\Tomcat\\SPAWS\\webapps\\SPAWS\\";		//OA
//	public static String HOME_PATH = System.getProperty("user.dir") + FILE_SEPARATOR;	//直接執行和用tomcat執行home path會不同!!
	/**
	 * 定義table欄位的config file路徑
	 */
	public static String TABLE_FIELD_PATH = HOME_PATH + "system" + FILE_SEPARATOR + "config" + FILE_SEPARATOR + "TableField.conf";
	/**
	 * MMSI與船隻國籍對照表Country.json路徑
	 */
	public static String COUNTRY_JSON_PATH = HOME_PATH + "system" + FILE_SEPARATOR + "config" + FILE_SEPARATOR + "Country.json";
	/**
	 * 船隻種類對照表BoatType.json路徑
	 */
	public static String BOAT_TYPE_JSON_PATH = HOME_PATH + "system" + FILE_SEPARATOR + "config" + FILE_SEPARATOR + "BoatType.json";
	/**
	 * 定義Alert.xml路徑
	 */
	public static String ALERT_CFG_PATH = HOME_PATH + "system" + FILE_SEPARATOR + "config" + FILE_SEPARATOR + "Alert.xml";
	/**
	 * 定義Mail.xml路徑
	 */
	public static String MAIL_CFG_PATH = HOME_PATH + "system" + FILE_SEPARATOR + "config" + FILE_SEPARATOR + "Mail.xml";
	/**
	 * 處理過的船隻檔案存放路徑
	 */
	public static String BOAT_PROCESSED_DATA_PATH = HOME_PATH + "datas" + FILE_SEPARATOR + "boat" + FILE_SEPARATOR + "processed" + FILE_SEPARATOR;
	/**
	 * 海纜data路徑
	 */
	public static String SC_DATA_PATH = HOME_PATH + "datas" + FILE_SEPARATOR + "sc" + FILE_SEPARATOR;
	/**
	 * 海纜警戒區data路徑
	 */
	public static String SC_WARN_DATA_PATH = HOME_PATH + "datas" + FILE_SEPARATOR + "scWarn" + FILE_SEPARATOR;
	/**
	 * 告警記錄檔路徑
	 */
	public static String BOAT_ALERT_RECORD_PATH = HOME_PATH + "datas" + FILE_SEPARATOR + "alertRecord" + FILE_SEPARATOR;
	
	/**
	 * 存放images的路徑
	 */
	public static String IMAGE_PATH = HOME_PATH + "images" + FILE_SEPARATOR;
	
	
	public static void main(String[] args) {
		System.out.println("FILE_SEPARATOR> " + FILE_SEPARATOR);
		System.out.println("HOME_PATH> " + HOME_PATH);
		System.out.println("TABLE_FIELD_PATH> " + TABLE_FIELD_PATH);
		System.out.println("COUNTRY_JSON_PATH> " + COUNTRY_JSON_PATH);
		System.out.println("BOAT_TYPE_JSON_PATH> " + BOAT_TYPE_JSON_PATH);
		System.out.println("ALERT_CFG_PATH> " + ALERT_CFG_PATH);
		System.out.println("MAIL_CFG_PATH> " + MAIL_CFG_PATH);
		System.out.println("BOAT_RAW_BEFORE_PATH> " + BOAT_RAW_BEFORE_PATH);
		System.out.println("BOAT_RAW_AFTER_PATH> " + BOAT_RAW_AFTER_PATH);
		System.out.println("BOAT_PROCESSED_DATA_PATH> " + BOAT_PROCESSED_DATA_PATH);
		System.out.println("SC_DATA_PATH> " + SC_DATA_PATH);
		System.out.println("SC_WARN_DATA_PATH> " + SC_WARN_DATA_PATH);
		System.out.println("IMAGE_PATH> " + IMAGE_PATH);
	}
}
