package com.chtn.util;



/**
 * <p>Title:IT Infrastructure�����ܼ�</p>
 * <p>Description:�ʸ�it infrastructure�����ܼ�</p>
 * <p>Copyright: Copyright (c) 2020/10/13</p>
 * @author IAN HSIAO
 */
public class Env {
	/**
	 * �t�Ϊ��ؿ����j�Ÿ�
	 */
	public static String FILE_SEPARATOR = System.getProperty("file.separator");

	/**
	 * ��䧽�z�LFTP�ǰe����ɮצs����|
	 */
//	public static String BOAT_RAW_BEFORE_PATH = "/home/ntouftp/boatdata/before/";	//OL
//	public static String BOAT_RAW_AFTER_PATH = "/home/ntouftp/boatdata/after/";		//OL
	public static String BOAT_RAW_BEFORE_PATH = "D:\\Tomcat\\SPAWS\\webapps\\SPAWS\\datas\\boat\\raw\\before\\";	//OA
	public static String BOAT_RAW_AFTER_PATH = "D:\\Tomcat\\SPAWS\\webapps\\SPAWS\\datas\\boat\\raw\\after\\";		//OA
	
	
	/**
	 * �M�רt�θ��|
	 */
//	public static String HOME_PATH = "/tomcat/SPAWS/webapps/SPAWS/";		//OL
	public static String HOME_PATH = "D:\\Tomcat\\SPAWS\\webapps\\SPAWS\\";		//OA
//	public static String HOME_PATH = System.getProperty("user.dir") + FILE_SEPARATOR;	//��������M��tomcat����home path�|���P!!
	/**
	 * �w�qtable��쪺config file���|
	 */
	public static String TABLE_FIELD_PATH = HOME_PATH + "system" + FILE_SEPARATOR + "config" + FILE_SEPARATOR + "TableField.conf";
	/**
	 * MMSI�P����y��Ӫ�Country.json���|
	 */
	public static String COUNTRY_JSON_PATH = HOME_PATH + "system" + FILE_SEPARATOR + "config" + FILE_SEPARATOR + "Country.json";
	/**
	 * �������Ӫ�BoatType.json���|
	 */
	public static String BOAT_TYPE_JSON_PATH = HOME_PATH + "system" + FILE_SEPARATOR + "config" + FILE_SEPARATOR + "BoatType.json";
	/**
	 * �w�qAlert.xml���|
	 */
	public static String ALERT_CFG_PATH = HOME_PATH + "system" + FILE_SEPARATOR + "config" + FILE_SEPARATOR + "Alert.xml";
	/**
	 * �w�qMail.xml���|
	 */
	public static String MAIL_CFG_PATH = HOME_PATH + "system" + FILE_SEPARATOR + "config" + FILE_SEPARATOR + "Mail.xml";
	/**
	 * �B�z�L����ɮצs����|
	 */
	public static String BOAT_PROCESSED_DATA_PATH = HOME_PATH + "datas" + FILE_SEPARATOR + "boat" + FILE_SEPARATOR + "processed" + FILE_SEPARATOR;
	/**
	 * ���ldata���|
	 */
	public static String SC_DATA_PATH = HOME_PATH + "datas" + FILE_SEPARATOR + "sc" + FILE_SEPARATOR;
	/**
	 * ���lĵ�ٰ�data���|
	 */
	public static String SC_WARN_DATA_PATH = HOME_PATH + "datas" + FILE_SEPARATOR + "scWarn" + FILE_SEPARATOR;
	/**
	 * �iĵ�O���ɸ��|
	 */
	public static String BOAT_ALERT_RECORD_PATH = HOME_PATH + "datas" + FILE_SEPARATOR + "alertRecord" + FILE_SEPARATOR;
	
	/**
	 * �s��images�����|
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
