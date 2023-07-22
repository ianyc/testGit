package com.chtn.spaws.parse;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.chtn.util.Env;
import com.chtn.util.XMLUtil;
import com.sun.xml.parser.Resolver;
import com.sun.xml.tree.XmlDocument;

public class ParseXML {

	private XmlDocument doc = null;

	
	/**
	 * 解析Alert.cfg並返回內容
	 * @param inputXml
	 * @return Hashtable
	 */
	public Hashtable getAlertConfig(String inputXml) {
		Hashtable hashTable = new Hashtable();

		if(this.parseXmlData(inputXml)) {	////代表parseXml成功
			try {
				Node root = (Node) doc.getDocumentElement();
				NodeList nodeList = root.getChildNodes();
				Node childNode = null;
				
				for (int i = 0; i < nodeList.getLength(); i++) {
					childNode = nodeList.item(i);
					if (childNode.getNodeName().equals("mailServer")) {
						if (childNode.getChildNodes().getLength() != 0)
							hashTable.put("mailServer", childNode.getChildNodes().item(0).toString());
					}else if (childNode.getNodeName().equals("mailList")) {
						if (childNode.getChildNodes().getLength() != 0)
							hashTable.put("mailList", childNode.getChildNodes().item(0).toString());
					}else if (childNode.getNodeName().equals("AlertDuration_d")) {
						if (childNode.getChildNodes().getLength() != 0)
							hashTable.put("AlertDuration_d", childNode.getChildNodes().item(0).toString());
					}else if (childNode.getNodeName().equals("AlertDuration_h")) {
						if (childNode.getChildNodes().getLength() != 0)
							hashTable.put("AlertDuration_h", childNode.getChildNodes().item(0).toString());
					}else if (childNode.getNodeName().equals("AlertDuration_m")) {
						if (childNode.getChildNodes().getLength() != 0)
							hashTable.put("AlertDuration_m", childNode.getChildNodes().item(0).toString());
					}
				}
				return hashTable;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}else {	//代表parseXml失敗
			System.out.println("解析xml失敗!!");
			return null;
		}
		
}//getAlertConfig END
	
	
	
	/**
	 * 解析xml file
	 * @param inputXml
	 * @return boolean
	 */
	public boolean parseXmlData(String inputXml) {
		try {
			int SOCK_BUF_SIZE = 4096;
			// 讀取 XML file 的 raw data
			InputStream ins = new FileInputStream(inputXml);
			BufferedInputStream bins = new BufferedInputStream(ins, SOCK_BUF_SIZE);
			byte[] rcvdata = new byte[SOCK_BUF_SIZE];
			bins.read(rcvdata, 0, SOCK_BUF_SIZE);
			int rcvdataLen;
			for (rcvdataLen = 0; rcvdataLen < SOCK_BUF_SIZE; rcvdataLen++)
				if (rcvdata[rcvdataLen] == 0)
					break;

			byte[] rawdata = new byte[rcvdataLen];
			System.arraycopy(rcvdata, 0, rawdata, 0, rcvdataLen);
			rcvdata = null;
			bins.close();
			ins.close();
			doc = XMLUtil.parseXml(rawdata);	// 讀取 xmlSession 區段資料...

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}// parseXmlData END


	
	public static void main(String[] args) {
		
		String configFile = Env.ALERT_CFG_PATH;
		Hashtable hashData = null;
		ParseXML parseXML = new ParseXML();
		hashData = parseXML.getAlertConfig(configFile);
		
		if(hashData!=null)
		System.out.println("mailServer: " + hashData.get("mailServer"));
		System.out.println("mailList: " + hashData.get("mailList"));
		System.out.println("AlertDuration_d: " + hashData.get("AlertDuration_d"));
		System.out.println("AlertDuration_h: " + hashData.get("AlertDuration_h"));
		System.out.println("AlertDuration_m: " + hashData.get("AlertDuration_m"));
		
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
