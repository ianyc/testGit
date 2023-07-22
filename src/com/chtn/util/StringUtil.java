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
* �����O���Ѥ@������r��`�Ϊ��u��禡.
*/
public abstract class StringUtil{
	  
	  /**
		 * @author ���¸s
		 * Title: ���򳣤���
		 * @return str
		 * @param str ���o�r��
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
		 * @author ���¸s
		 * Title: EncodeforHtmlContent
		 * @return str
		 * @param str ���o�r��
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
	 * @author ���¸s
	 * Title: �L�o�S��r��
	 * Description: �L�o�S��r��
	 * @return boolean
	 * @ToTRISData.java �ϥΤ�
	 * @param str ���o�n�ഫ���r��
	 * @date 2015.02.24
	 */
	public static String StringFilter(String str) throws PatternSyntaxException {
		// �u���\�r���M�Ʀr
		// String   regEx  =  "[^a-zA-Z0-9]";
		// �M�����Ҧ��S��r��
		//String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~�I@#�D%�K�K&*�]�^�X�X+|{}�i�j���F�G�������C�A�B�H]";
		String regEx="[`~!@$%^&*()+=|{}':'\\[\\].<>/?~�I@�D%�K�K&*�]�^�X�X+|{}�i�j���F�G�������C�A�B�H]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

/*
 * �N�r��̷Ӥ��j�r����Ѧ��h�Ӥl�r��.�]�O�ϥ� StringTokenizer �ҥH����Ӥ��j�r���X�b�@�_,
 * �|�����u���@��
 * @param str �n��Ѫ��r��
 * @param delim ���j�r��
 * @return ���G�}�C
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
  * �N�r��̷Ӥ��j�r����Ѧ��h�Ӥl�r��.�]�O�ϥ� StringTokenizer �ҥH����Ӥ��j�r���X�b�@�_,
  * �|�����u���@��
  * @param str �n��Ѫ��r��
  * @return ���G�}�C
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
  * �N�r��̷Ӥ��j�r����Ѧ��h�Ӥl�r��. �Y��� delimiter ��F�����S������r��, �|�^�� null �r��
  * @param str �n��Ѫ��r��
  * @param delim ���j�r��
  * @return ��ѵ��G�}�C
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
  * �N�r��̷Ӥ��j�r����Ѧ��h�Ӥl�r��. �Y��� delimiter ��F�����S������r��, �|�^��""�r��
  * @param str �n��Ѫ��r��
  * @param delim ���j�r��
  * @return ��ѵ��G�}�C
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
  * �N�r��̷өT�w���Z��Ѧ��h�Ӥl�r��.
  * @param str �n��Ѫ��r��
  * @param interval  ���Z
  * @return ��ѵ��G�}�C
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
  * �N��ƨ̾ڿ�J�r���k��ɨ��T�w����.
  * @param str  �n�ɨ�����ƥ���
  * @param length  �ɨ�������
  * @param fillChar �n��J���r��
  * @return �ɨ��᪺���G�r��
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
  * �N��ƨ̾ڿ�J�r������ɨ��T�w����.
  * @param str  �n�ɨ�����ƥ���
  * @param length  �ɨ�������
  * @param fillChar �n��J���r��
  * @return �ɨ��᪺���G�r��
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
  * �N�ӷ��r��̪��l�r��H�s�r��Ӵ���
  * @param src �ӷ��r��
  * @param oldStr �n�Q�������l�r��
  * @param newStr �n���N���s�r��
  * @return ���G�r��
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
  * �I���D null �r��e�᪺�ť�
  * @param str �ӷ��r��
  * @return �B�z�᪺�r��
  */
 public static String trim(String str){
    if (str != null) {
       str = str.trim();
    }
    return str;
 }


  /**
    * �N����r�ѽX
    * @param inString      ����r��
    * @return              �ѽX��8859_1�᪺�r��
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
   * �N����r�ѽX
   * @param inString      ����r��
   * @return              �ѽX��8859_1�᪺�r��
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
    * �N����r�ѽX
    * @param inString      ����r��
    * @return              �ѽX��8859_1�᪺�r��
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
  * check ��J String[] ���� element ���ȬO�ߤ@
  * @param element      �r�궰�A�r��
  * @return             �C��element�O�ߤ@���r�궰
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
  * �N�r��k�ɪťզܦr����׬���
  * @param element      �r��A�r�����
  * @return             �r��
  * add by vikky
  */
 public static String paddingSpaceRight(String s, int len) {

   //�B�z�Ŧr����D, by ivan
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
  * �N�r�ꥪ�ɪťզܦr����׬���
  * @param element      �r��A�r�����
  * @return             �r��
  * add by vikky
  */
 public static String paddingSpaceLeft(String s, int len) {

   //�B�z�Ŧr����D, by ivan
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
  * ����G�r��j�p
  * @param element      �r��1�A�r��2
  * @return             (0)�G�۵�(1)�r��<1�r��2(2)�r��1>�r��2
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
  * �N�r�ꥪ�ɹs�ܦr����׬���
  * @param String �r��
  * @param int    �r�����
  * @return       �r��
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
  * �N�r�ꥪ�䪺�s�I��
  * @param String �r��
  * @return       �r��
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
 * �N�r��k�䪺�s�I��
 * @param String �r��
 * @return       �r��
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
   * �P�_��J�r�ŬO�_��Big5���s�X�榡
   * @param c ��J�r��
   * @return �p�G�OBig5��^�u�A�_�h��^��
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
  * �P�_�r�Ŧꪺ�s�X
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
 * �N�r���r��@��,�Ʀr��@��
 * @param String �r��
 * @return       �r��
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
  * @author ���¸s
  * Title: �N�a�}�r���ର���X
  * Description: �NClass��iso8859���r���ন���X�H�Q���㪺�bSystem.out�@show�Xdebug
  * @return String �^�Ǹg�L��X�᪺�r��
  * ary:
  * @param str ���o�n�ഫ���r��
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
  * @author ���¸s
  * Title: �N�ȦW�W�٦r���ର���X
  * Description: �NClass��iso8859���r���ন���X�H�Q���㪺�bSystem.out�@show�Xdebug
  * @return String �^�Ǹg�L��X�᪺�r��
  * ary:
  * @param str ���o�n�ഫ���r��
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
 * @author ���¸s
 * Title: �P�_�r�ӬO�_�����Ʀr
 * Description: �Q�ΰj��@�Ӧr���@�Ӧr�����R,true �����Ʀr
 * @return boolean
 * @NeighborhoodWSQrybyCable.java �ϥΤ�
 * @param str ���o�n�ഫ���r��
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
  * @author ���¸s
  * �L�o�D�Ʀr����
 * @param str �ǤJ���r�Ŧ�  
 * @return �O��ƪ�^true,�_�h��^false  
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
   * �P�_�O�_�����  
  * @param str �ǤJ���r�Ŧ�  
  * @return �O��ƪ�^true,�_�h��^false  
  * @date 2019.05.07
  */  
 public static boolean isInteger(String str) {    
	   Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");    
	   return pattern.matcher(str).matches();    
 }

 /**
  * (2008/10/3) �����G�ন��������r
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
     if(ca[i] > '\200'){    continue;   }      //�W�L�o�����ӳ��O����r�F�K
     if(ca[i] == 32){    ca[i] = (char)12288;        continue;                  }  //�b���ť��ন�����ť�
     if(Character.isLetterOrDigit(ca[i])){   ca[i] = (char)(ca[i] + 65248);  continue;  }  //�O���w�q���r�B�Ʀr�βŸ�

     ca[i] = (char)12288;  //�䥦���X�n�D���A�����ন�����ťաC
   }

   return String.valueOf(ca);
 }

 /**
 * @author Mark Chung
 * Title: �ഫ�D�k�r��
 * Description: �N�ǤJ�����ഫ�קKSQL Injection�άOXSS����
 * @return String
 * @param value ���o�n�ഫ���r��
 * @date 2013.12.25
  */
 public static String cleanXSS(String value) {
   if (value == null) return null;
   //You'll need to remove the spaces from the html entities below
   value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
   //2016.12.12 �h���P�_() for �����ݨD
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
  * Title: �^��null
  * Description: �ȱ����ǤJ�Ŧr��A�B�^��null��
  * @return String
  * @param value ���o�n�ഫ���r��
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
