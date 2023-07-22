package com.chtn.util;




//Title:        StringUtil.java
//Version: 12.3
//Copyright:    Copyright (c) 1999
//Author:       863 team
//Description:

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.net.InetAddress;
//import javax.servlet.*;

/*
* 本類別提供一些關於字串常用的工具函式.
*/
public abstract class StringUtil{
	  
	  /**
		 * @author 蔡威群
		 * Title: 什麼都不做
		 * @return str
		 * @param str 取得字串
		 * @date 2018.05.30
		 */
	  public static String Nothingtodo(String nothingtodo) {	
		  if(nothingtodo == null ) return null;
		  return nothingtodo;
	  }
	  
	  public static InetAddress Nothingtodo(InetAddress addr) {	
		  if(addr == null ) return null;
		  return addr;
	  }
	  
	  /**
		 * @author 蔡威群
		 * Title: EncodeforHtmlContent
		 * @return str
		 * @param str 取得字串
		 * @date 2018.06.07
		 */
	  public static String EncodeforHtmlContent(String forHtmlContent) {
		  if(forHtmlContent == null ) return null;
		  return org.owasp.encoder.Encode.forHtmlContent(forHtmlContent);
	  }
	  public static String[] EncodeforHtmlContent(String[] forHtmlContent) {
		  if(forHtmlContent == null ) return null;
		  for(int i=0;i<forHtmlContent.length;i++) {
			  if(forHtmlContent[i] == null ) 
				  forHtmlContent[i] =  null;
			  else
				  forHtmlContent[i] =  org.owasp.encoder.Encode.forHtmlContent(forHtmlContent[i]);
		  }
		  return forHtmlContent;
	  }
	  
	/**
	 * @author 蔡威群
	 * Title: 過濾特殊字符
	 * Description: 過濾特殊字符
	 * @return boolean
	 * @ToTRISData.java 使用中
	 * @param str 取得要轉換的字串
	 * @date 2015.02.24
	 */
	public static String StringFilter(String str) throws PatternSyntaxException {
		// 只允許字母和數字
		// String   regEx  =  "[^a-zA-Z0-9]";
		// 清除掉所有特殊字符
		//String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		String regEx="[`~!@$%^&*()+=|{}':'\\[\\].<>/?~！@￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

/*
 * 將字串依照分隔字元拆解成多個子字串.因是使用 StringTokenizer 所以有兩個分隔字元合在一起,
 * 會視為只有一個
 * @param str 要拆解的字串
 * @param delim 分隔字元
 * @return 結果陣列
 */
 public static String[] split(String str, String delim) {
    // Use a Vector to hold the splittee strings
    Vector v = new Vector();

    // Use a StringTokenizer to do the splitting
    StringTokenizer tokenizer = new StringTokenizer(str, delim);
    while (tokenizer.hasMoreTokens()) {
       v.addElement(tokenizer.nextToken());
    }

    String[] ret = new String[v.size()];
    v.toArray(ret);
    return ret;
 }

 /*
  * 將字串依照分隔字元拆解成多個子字串.因是使用 StringTokenizer 所以有兩個分隔字元合在一起,
  * 會視為只有一個
  * @param str 要拆解的字串
  * @return 結果陣列
  */
 public static String[] split(String str) {
    // Use a Vector to hold the splittee strings
    Vector v = new Vector();

    // Use a StringTokenizer to do the splitting
    StringTokenizer tokenizer = new StringTokenizer(str);
    while (tokenizer.hasMoreTokens()) {
       v.addElement(tokenizer.nextToken());
    }

    String[] ret = new String[v.size()];
    v.toArray(ret);
    return ret;
 }

 /*
  * 將字串依照分隔字元拆解成多個子字串. 若兩個 delimiter 緊鄰中間沒有任何字串, 會回傳 null 字串
  * @param str 要拆解的字串
  * @param delim 分隔字元
  * @return 拆解結果陣列
  */
 public static String[] split1(String str, String delimiter){
    int start=0;
    int end=0;
    Vector v = new Vector();

    while(start <= str.length()) {
       String item = null;
       int idx = str.indexOf(delimiter,start);
       if (idx!=-1) {
          end = idx;
       }
       else {
          end = str.length();
       }
       if (start<end) {
          item = str.substring(start,end);
       }
       v.addElement(item);
       start = end + delimiter.length();
    }
    String[] ret = new String[v.size()];
    v.toArray(ret);
    return ret;
 }

 /*
  * 將字串依照分隔字元拆解成多個子字串. 若兩個 delimiter 緊鄰中間沒有任何字串, 會回傳""字串
  * @param str 要拆解的字串
  * @param delim 分隔字元
  * @return 拆解結果陣列
  */
 public static String[] split2(String str, String delimiter){
    int start=0;
    int end=0;
    Vector v = new Vector();

    while(start <= str.length()) {
       String item = "";
       int idx = str.indexOf(delimiter,start);
       if (idx!=-1) {
          end = idx;
       }
       else {
          end = str.length();
       }
       if (start<end) {
          item = str.substring(start,end);
       }
       v.addElement(item);
       start = end + delimiter.length();
    }
    String[] ret = new String[v.size()];
    v.toArray(ret);
    return ret;
 }

 /*
  * 將字串依照固定間距拆解成多個子字串.
  * @param str 要拆解的字串
  * @param interval  間距
  * @return 拆解結果陣列
  */
 public static String[] split(String str, int interval){
    int start=0;
    int end=0;
    Vector v = new Vector();

    while(start<str.length()) {
       String item = null;
       end = start + interval;
       if (end > str.length()) {
          end = str.length();
       }
       item = str.substring(start,end);
       v.addElement(item);
       start = end;
    }
    String[] ret = new String[v.size()];
    v.toArray(ret);
    return ret;
 }

 /*
  * 將資料依據輸入字元右邊補足固定長度.
  * @param str  要補足的資料本身
  * @param length  補足的長度
  * @param fillChar 要填入的字元
  * @return 補足後的結果字串
  */
 public static String fillDataRight(String str, int length, String fillChar){
   if(str ==null)
     return str;
   StringBuffer sb = new StringBuffer(str);

   for (int i=str.length();i<length;i++) {
     sb.append(fillChar);
   }
   return sb.toString();
 }

 /*
  * 將資料依據輸入字元左邊補足固定長度.
  * @param str  要補足的資料本身
  * @param length  補足的長度
  * @param fillChar 要填入的字元
  * @return 補足後的結果字串
  */
 public static String fillDataLeft(String str, int length, String fillChar){
   if(str ==null)
     return str;
   StringBuffer sb = new StringBuffer(str);

   for (int i=str.length();i<length;i++) {
     sb.insert(0,fillChar);
   }
   return sb.toString();
 }

 /*
  * 將來源字串裡的子字串以新字串來替換
  * @param src 來源字串
  * @param oldStr 要被替換的子字串
  * @param newStr 要取代的新字串
  * @return 結果字串
  */
 public static String replaceString(String src, String oldStr, String newStr){
    String[] tmp = split2(src, oldStr);
    StringBuffer dest = new StringBuffer();

    for(int i=0 ; i<tmp.length ; i++) {
       dest.append(tmp[i]);
       if (i != (tmp.length - 1)) {
          dest.append(newStr);
       }
    }
    return dest.toString();
 }

 /*
  * 截掉非 null 字串前後的空白
  * @param str 來源字串
  * @return 處理後的字串
  */
 public static String trim(String str){
    if (str != null) {
       str = str.trim();
    }
    return str;
 }


  /**
    * 將中文字解碼
    * @param inString      中文字串
    * @return              解碼為8859_1後的字串
    */
 public static String big52iso(String inString) {
   if (inString == null) return null;
   if (System.getProperties().get("file.encoding").equals("MS950")){
     return inString;
   }
   String outString = null;

   try {
     outString = new String(inString.getBytes("Big5"), "8859_1");
   }
   catch (java.io.UnsupportedEncodingException ex) {
     ex.printStackTrace();
     outString = inString;
   }

   return outString;
 }

 /**
   * 將中文字解碼
   * @param inString      中文字串
   * @return              解碼為8859_1後的字串
   */
public static String big52isoAlways(String inString) {
  if (inString == null) return null;
  if (System.getProperties().get("file.encoding").equals("MS950")){
    //return inString;
  }
  String outString = null;

  try {
    outString = new String(inString.getBytes(), "8859_1");
  }
  catch (java.io.UnsupportedEncodingException ex) {
    ex.printStackTrace();
    outString = inString;
  }

  return outString;
 }


  /**
    * 將中文字解碼
    * @param inString      中文字串
    * @return              解碼為8859_1後的字串
    */
 public static String iso2big5(String inString) {
    String outString = null;
    if (inString == null) return null;
    try {
       outString = new String(inString.getBytes("8859_1"), "Big5");
    }
    catch (java.io.UnsupportedEncodingException ex) {
       ex.printStackTrace();
       outString = inString;
    }
    return outString;
 }

 /*
  * check 放入 String[] 中之 element 之值是唯一
  * @param element      字串集，字串
  * @return             每個element是唯一之字串集
  * add by vikky
  */
 public static String[] addElement(String[] arrary, String element) {
    boolean find = false;

    for (int i=0 ; i<arrary.length; i++) {
       if (arrary[i].equals(element)) {
          find = true;
          break;
       }
    }
    if (find == false) arrary[arrary.length] = element;
    return arrary;
 }

 /*
  * 將字串右補空白至字串長度為止
  * @param element      字串，字串長度
  * @return             字串
  * add by vikky
  */
 public static String paddingSpaceRight(String s, int len) {

   //處理空字串問題, by ivan
   if (s == null) {
     StringBuffer tmpStr = new StringBuffer();
     for (int i=0; i<len; i++) tmpStr.append(" ");
     return tmpStr.toString();
   }

    if ((s != null) && (s.length() > len)) return s.substring(0, len);

    int i;
    StringBuffer sb = new StringBuffer();
    sb.append(s);
    for (i=len; i>s.length(); i--) sb.append(" ");
    String str = sb.toString();
    return(str);
 }

 /*
  * 將字串左補空白至字串長度為止
  * @param element      字串，字串長度
  * @return             字串
  * add by vikky
  */
 public static String paddingSpaceLeft(String s, int len) {

   //處理空字串問題, by ivan
   if (s == null) {
     StringBuffer tmpStr = new StringBuffer();
     for (int i=0; i<len; i++) tmpStr.append(" ");
     return tmpStr.toString();
   }


    StringBuffer sb = new StringBuffer();
    for (int i=len; i>s.length(); i--) sb.append(" ");
    sb.append(s);
    String str = sb.toString();
    return(str);
 }

 /*
  * 比較二字串大小
  * @param element      字串1，字串2
  * @return             (0)：相等(1)字串<1字串2(2)字串1>字串2
  * add by vikky
  */
 public static int compare(String str1, String str2) {
    if ((str1 == null) || (str2 == null)) return -1;
    if (Integer.parseInt(str1.trim()) == Integer.parseInt(str2.trim()))
       return 0;
    else if (Integer.parseInt(str1.trim()) < Integer.parseInt(str2.trim()))
       return 1;
    else
       return 2;
 }

 /*
  * 將字串左補零至字串長度為止
  * @param String 字串
  * @param int    字串長度
  * @return       字串
  * add by ivan
  */
 public static String paddingZeroLeft(String s, int len) {
    if (s == null) s = "";
    int i;
    StringBuffer sb = new StringBuffer();
    for (i=len; i>s.length(); i--) sb.append("0");
    sb.append(s);
    String str = sb.toString();
    return(str);
 }

 /*
  * 將字串左邊的零截掉
  * @param String 字串
  * @return       字串
  * add by ivan
  */
 public static String cutZeroLeft(String s) throws Exception {
    if (s == null) s = "";
    boolean isPrefix = true;
    StringBuffer sb = new StringBuffer();
    for(int i=0; i<s.length(); i++) {
       if ((s.charAt(i) != '0')) {
          isPrefix = false;
          sb.append(s.charAt(i));
       }
       else if (!isPrefix)
          sb.append(s.charAt(i));
    }
    String str = sb.toString();
    return(str);
 }

/*
 * 將字串右邊的零截掉
 * @param String 字串
 * @return       字串
 * add by ivan
  */
 public static String cutZeroRight(String s) throws Exception {
   if (s == null) s = "";
   boolean isPrefix = true;
   StringBuffer sb = new StringBuffer();
   for(int i=(s.length()-1);i>=0; i--) {
     if ((s.charAt(i) != '0')) {
       isPrefix = false;
       sb.append(s.charAt(i));
     }
     else if (!isPrefix)
       sb.append(s.charAt(i));
   }
   String str = sb.toString();
   sb = null;
   sb =  new StringBuffer();
   for(int i=(str.length()-1);i>=0; i--) {
     sb.append(str.charAt(i));
   }
   str = sb.toString();
   return(str);
 }

 /**
   * 判斷輸入字符是否為Big5的編碼格式
   * @param c 輸入字符
   * @return 如果是Big5返回真，否則返回假
   * @date 2012/05/21
   * add by Willy
   */
  public static boolean isBig5(char c) {
      Character ch = new Character(c);
      String sCh = ch.toString();
      try {
          byte[]   bb = sCh.getBytes("Big5");
          if (bb.length > 1) {
              return true;
          }
      } catch (java.io.UnsupportedEncodingException ex) {
          return false;
      }
      return false;
  }

 /**
  * 判斷字符串的編碼
  * @param str
  * @return str
  * @date 2012/05/21
  * add by Willy
  */
 public static String getEncoding(String str) {
   String encode = "Big5";
   try {
     if (str.equals(new String(str.getBytes(encode), encode))) {
       String s = encode;
       return s;
     }
   } catch (Exception exception) {
   }
   encode = "GB2312";
   try {
     if (str.equals(new String(str.getBytes(encode), encode))) {
       String s = encode;
       return s;
     }
   } catch (Exception exception) {
   }
   encode = "ISO8859-1";
   try {
     if (str.equals(new String(str.getBytes(encode), encode))) {
       String s1 = encode;
       return s1;
     }
   } catch (Exception exception1) {
   }
   encode = "UTF-8";
   try {
     if (str.equals(new String(str.getBytes(encode), encode))) {
       String s2 = encode;
       return s2;
     }
   } catch (Exception exception2) {
   }
   encode = "GBK";
   try {
     if (str.equals(new String(str.getBytes(encode), encode))) {
       String s3 = encode;
       return s3;
     }
   } catch (Exception exception3) {
   }
   return "";
 }


 /*
 * 將字串文字拆一邊,數字拆一邊
 * @param String 字串
 * @return       字串
 * @date 2007/06/13
 * add by Willy
  */
 public static String[] allDigits(String str) {
   String[] sd = new String[2];
   sd[0]="";
   sd[1]="";
   if(str!=null) {
     for(int   i=0;i<str.length();i++){
       if(!Character.isDigit(str.charAt(i))) {
         sd[0]+=str.charAt(i);
       }
       else
         sd[1]+=str.charAt(i);
     }
   }
   return   sd;
 }

 /**
  * @author 蔡威群
  * Title: 將地址字串轉為隱碼
  * Description: 將Class中iso8859的字串轉成隱碼以利完整的在System.out　show出debug
  * @return String 回傳經過轉碼後的字串
  * ary:
  * @param str 取得要轉換的字串
  * @date 2007.12.31
  */
 public static String hidingadr(String str){
   if(str!=null && !str.equals("") ) {
     if(str.length()>=12) {
       str = str.substring(0,12)+"************";
     }
   }
   return str;
 }

 /**
  * @author 蔡威群
  * Title: 將客名名稱字串轉為隱碼
  * Description: 將Class中iso8859的字串轉成隱碼以利完整的在System.out　show出debug
  * @return String 回傳經過轉碼後的字串
  * ary:
  * @param str 取得要轉換的字串
  * @date 2007.12.31
  */
 public static String hidingname(String str){
	     if(str!=null && !str.equals("") ) {
	       if(str.length()>10) {
	         str = str.substring(0,6)+"**********";
	       }
	       else if (str.length() >= 2)
	         str = str.substring(0,2)+"********";
	     }
	     return str;
	   }

 /**
 * @author 蔡威群
 * Title: 判斷字申是否全為數字
 * Description: 利用迴圈一個字元一個字元分析,true 全為數字
 * @return boolean
 * @NeighborhoodWSQrybyCable.java 使用中
 * @param str 取得要轉換的字串
 * @date 2009.04.15
  */
 public static boolean isNumber(String str){
   int Flag = 0;
   char[] strArray = str.toCharArray();
   for (int i = 0; i < strArray.length; i++){
     if (Character.isDigit(strArray[i])){
       Flag++;
     }
     else {
       Flag = -1;
       break;
     }
   }
   if ( Flag > 0 ){
     return true;
   }
   else{
     return false;
   }
 }
 
 /* 
  * @author 蔡威群
  * 過濾非數字部份
 * @param str 傳入的字符串  
 * @return 是整數返回true,否則返回false  
 * @date 2019.09.27
 */  
 public static String filterInteger(String s){
	   if(s != null) {
		   String regEx = "[^0-9]";
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(s);
			return m.replaceAll("").trim();
	   }
	   else
		   return null;		
}

 
 /* 
   * 判斷是否為整數  
  * @param str 傳入的字符串  
  * @return 是整數返回true,否則返回false  
  * @date 2019.05.07
  */  
 public static boolean isInteger(String str) {    
	   Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");    
	   return pattern.matcher(str).matches();    
 }

 /**
  * (2008/10/3) 說明：轉成中文全型字
  * @author willy
  * @param s
  * @return
  */
 public static String toChanisesFullChar(String s){
   if(s==null || s.equals("")){
     return "";
   }

   char[] ca = s.toCharArray();
   for(int i=0; i<ca.length; i++){
     if(ca[i] > '\200'){    continue;   }      //超過這個應該都是中文字了…
     if(ca[i] == 32){    ca[i] = (char)12288;        continue;                  }  //半型空白轉成全型空白
     if(Character.isLetterOrDigit(ca[i])){   ca[i] = (char)(ca[i] + 65248);  continue;  }  //是有定義的字、數字及符號

     ca[i] = (char)12288;  //其它不合要求的，全部轉成全型空白。
   }

   return String.valueOf(ca);
 }

 /**
 * @author Mark Chung
 * Title: 轉換非法字元
 * Description: 將傳入的值轉換避免SQL Injection或是XSS攻擊
 * @return String
 * @param value 取得要轉換的字串
 * @date 2013.12.25
  */
 public static String cleanXSS(String value) {
   if (value == null) return null;
   //You'll need to remove the spaces from the html entities below
   value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
   //2016.12.12 去除判斷() for 介面需求
   //value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
   value = value.replaceAll("'", "&#39;");
   value = value.replaceAll("\"", "&#34;");
   value = value.replaceAll("eval\\((.*)\\)", "");
   value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
   value = value.replaceAll("script", "");
   return value;
  }

  /**
  * @author Mark Chung
  * Title: 回傳null
  * Description: 僅接受傳入空字串，且回傳null值
  * @return String
  * @param value 取得要轉換的字串
  * @date 2019.05.21
  */
  public static String getStringNull(String in) {
    if (in != null && in.length() == 0)return null;
    else return new String(in);
  }


 public static void main(String[] argv) throws Exception {
    String str = "009";
    System.out.println("="+StringUtil.cutZeroLeft(str)+"=");
 }

}
