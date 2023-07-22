package com.chtn.spaws;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONObject;

import com.chtn.util.Env;

public class BoatTypeMapping {
	
	
	/**
	 * 「船隻種類代碼」轉換「船隻種類名稱」
	 * @param typeCode
	 * @return String
	 */
	public static String code2Text(String typeCode) {
		File configFile = new File(Env.BOAT_TYPE_JSON_PATH);
		BufferedReader br = null;
		String line = null;
		StringBuffer sb = new StringBuffer();;
		String chBoatType = null;
		
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(configFile)));
			while( (line = br.readLine())!=null ) {
				sb.append(line);
			}
			
			JSONObject boatTypeJson = new JSONObject(sb.toString());
			if(boatTypeJson.has(typeCode)) {
				chBoatType = boatTypeJson.getString(typeCode);
			}else {
				chBoatType = "";
			}
			
			return chBoatType;
		} catch (IOException e) {
			System.out.println("Boat type code轉換 type text時發生exception: " + e);
			return "";
		} finally {
			try { if(br!=null) br.close(); } catch (IOException e) {e.printStackTrace();}
		}
		
	}//code2Text END
	
	
	
	
	public static void main(String[] args) {
		String[] testAry = {"0","0","253","0","0","253","253","70","200","200","0","0","0","0","0","0","0","0","0","0","39",
				"39","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0",
				"0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","39","39","39","39","39","39","39","39",
				"0","0","0","0","0","0","0","0","39","39","39","0","39","39","39","39","39","39","39","39","39","39","39","39",
				"39","39","39","39","39","39","39","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","255","0","0",
				"0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","39","39","39","0","0","0","0","0",
				"0","0","0","0","0","39","253","253","253","253","39","39","39","39","39","39","39","39","39","39","39","39","39",
				"39","39","39","39","39","39","39","39","39","30","39","39","39","39","39","39","30","39","39","39","39","39","39",
				"39","39","39","39","39","39","39","39","39","30","39","39","39","39","39","39","39","0","39","200","200","30","30",
				"30","137","0","30","0","0","0","30","0","0","70","70","0","0","0","0","30","70","30","0","30","30","30","30","253",
				"253","0","0","39","39","0","0","39","39","39","39","0","39","39","39","39","0","0","0","200","0","0","0","253","0"};

		for (int i = 0; i < testAry.length; i++) {
			System.out.println(testAry[i] + ">> " + code2Text(testAry[i]));
		}
	}
	
	

}
