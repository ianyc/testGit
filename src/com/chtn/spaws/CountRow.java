package com.chtn.spaws;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

import com.chtn.spaws.parse.ResolveField;
import com.chtn.util.Env;
import com.chttl.iois.profile.ProfileCenter;

public class CountRow {
	
	
	
	public static void main(String[] args) {
		String path1 = "C:\\Users\\user\\Desktop\\temp\\after";
		String path2 = "C:\\Users\\user\\Desktop\\temp\\before";
		File folder1 = new File(path1);
		File folder2 = new File(path2);
		File[] files1;
		File[] files2;
		files1 = folder1.listFiles();
		files2 = folder2.listFiles();
		BufferedReader br1 = null;
		BufferedReader br2 = null;
		String line1;
		String line2;
		int count1 = 0;
		int count2 = 0;
		ProfileCenter pc;
		
		
		for(File file : files1)		
		{
			try {
				br1 = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				while( (line1 = br1.readLine())!=null ) {
					if(line1.startsWith("Base station time stamp"))	//略過第一列: title列
						continue;
					try {
						pc = ProfileCenter.getProfileCenter(Env.TABLE_FIELD_PATH);	
						ResolveField resolveField = new ResolveField();
						resolveField.getResolveField(line1, 0, pc, "boatProcessed");
						count1++;
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("count1: " + count1);
		
		
		for(File file : files2)		
		{
			try {
				br2 = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				while( (line2 = br2.readLine())!=null ) {
					if(line2.startsWith("Base station time stamp"))	//略過第一列: title列
						continue;
					count2++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("count2: " + count2);
		
		
	}

}