package com.chtn.spaws;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONObject;

import com.chtn.util.Env;

public class CountryMapping {
	

	/**
	 * mmsi轉換船隻國籍
	 * @param mmsi
	 * @return String
	 */
	public static String mmsi2Country(String mmsi) {
		File configFile = new File(Env.COUNTRY_JSON_PATH);
		BufferedReader br = null;
		String line = null;
		StringBuffer sb = new StringBuffer();;
		String chCountry = null;
		
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(configFile)));
			while( (line = br.readLine())!=null ) {
				sb.append(line);
			}
			
			mmsi = mmsi.length()>=3 ? mmsi.substring(0,3) : mmsi;	//取mmsi前三碼來比較國籍
			
			JSONObject countryJson = new JSONObject(sb.toString());
			if(countryJson.has(mmsi)) {
				chCountry = countryJson.getString(mmsi);
			}else {
				chCountry = "";
			}
			
			return chCountry;
		} catch (IOException e) {
			System.out.println("MMSI轉換國籍時發生exception: " + e);
			return "";
		} finally {
			try { if(br!=null) br.close(); } catch (IOException e) {e.printStackTrace();}
		}
	}//mmsi2Country
	
	
	
	public static void main(String[] args) throws IOException {
		String[] testAry = {"667001347", "416002045", "370064000", "450123123" , "1", "2", "416547"};

		for (int i = 0; i < testAry.length; i++) {
			System.out.println(">> " + mmsi2Country(testAry[i]));
		}
		
	}
	
	
	
}
