package com.chtn.spaws;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import com.chtn.cronJob.MailAlert;
import com.chtn.spaws.parse.ParseXML;
import com.chtn.util.Env;
import com.sun.swing.internal.plaf.basic.resources.basic;

public class TestJava {


		public static void main(String[] args) throws HttpException, IOException {
			HttpClient client = new HttpClient();
	    	PostMethod post = null;
			
			
	    	String userId = "edesk";
  		    String password = "edesk@12";
			
  		    post = new PostMethod("https://message.cht.com.tw/message/apitl1/login.jsp");
	    	post.addParameter(new NameValuePair("userid", userId));
	    	post.addParameter(new NameValuePair("password", password));
	    	client.setTimeout(20000); 
	    	client.executeMethod(post);
	    	byte[] responseBody = post.getResponseBody();
	    	String res = new String(responseBody);
	    	System.out.println(res);
	    	if (!res.startsWith("0: success!")) {
		    	//如果登入不成功?
	    		System.out.println("[UCSendMobile Error]https://message.cht.com.tw/message/apitl1/login.jsp fail!");
	    		System.out.println("[UCSendMobile Error]"+res);
	    	}
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
		}
		
	
		
}
