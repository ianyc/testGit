package com.chtn.spaws.parse;

import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chtn.util.Env;
import com.chttl.iois.profile.DataProfile;
import com.chttl.iois.profile.PROFILEException;
import com.chttl.iois.profile.ProfileCenter;


public class ResolveField {
	Logger logger = LogManager.getLogger(this.getClass());
	
	private DataProfile tableDefine;	//�s��config table������row�Akey-value type
	String[] fieldNames = null;			//�s��config table���^��W
	String[] fieldCNames = null;		//�s��config table��줤��W
	String[] fieldValues = null;		//�s��input data value
	String[] fieldName = null;			//�Ȧsarray�A�s��config table�U���^��W
	String[] fieldCName = null;			//�Ȧsarray�A�s��config table�U��줤��W
	
	/***
	 * �ѪRone row of raw data.
	 * @param line
	 * @param dataType: 0->.csv�A���ӥi��W�[��Linput data����
	 * @param pc
	 * @param packType
	 * @throws PROFILEException
	 */
	public void getResolveField(String line,int dataType, ProfileCenter pc, String packType) throws PROFILEException {	
		DataProfile dataProfile = pc.getProfile(packType);
		if(dataProfile != null) {
			this.tableDefine = dataProfile;
			this.fieldNames = new String[this.tableDefine.size()];
			this.fieldCNames = new String[this.tableDefine.size()];
			this.fieldValues = new String[this.tableDefine.size()];
		} else {
			logger.error("***NULL definition in packType: " + packType + "***");
			throw new PROFILEException("***NULL definition in packType: " + packType + "***");
		}
		
		if(fieldName==null)		//Ū��config�ɯӮį�A�]���u��Ū�@���N�n
			fieldName = this.tableDefine.getColInArray("EngName");		
		if(fieldCName==null)
			fieldCName = this.tableDefine.getColInArray("ChName");		
		String[] fieldValue = null;
			
		
		if(dataType==0)		//input data is .csv	
			fieldValue = ParseCSV(line);
//		else if(dataType==1)	//dataType=1�N��parse processed data�A���F�[�t�j�M�ӫȻs���g�k
//			fieldValue = ParseProcessedCSV(line);
	
		//��csv�ɨC�@�C����ƼƥػPconfig table���w�q�����ƥؤ��P�ɵL�k���Tparse data�A�]���ߥXexception
		if(fieldName.length != fieldValue.length) {
			logger.error("***The row number of config table and the lin number of csv file is not match!!***");
			throw new PROFILEException("***The row number of config table and the lin number of csv file is not match!!***");
		}
	
		for(int i=0; i<fieldName.length; i++) {
			this.fieldNames[i] = fieldName[i];
			this.fieldCNames[i] = fieldCName[i];
			this.fieldValues[i] = fieldValue[i];
		}

	}//ResolveField END

	
	
//	/***
//	 * ���F�[�tprocessed data���B�z�ӫȻs��method�A�pprocessed data���s�W���A�Ҷ��ק惡method
//	 * @param line
//	 * @return String[]
//	 */
//	public String[] ParseProcessedCSV(String line) {
//		int index = line.indexOf(',');
//		String time = line.substring(0, index);
//		line = line.substring(index + 1);
//		index = line.indexOf(',');
//		String mmsi = line.substring(0, index);
//		line = line.substring(index + 1);
//		index = line.indexOf(',');
//		String lat = line.substring(0, index);
//		line = line.substring(index + 1);
//		index = line.indexOf(',');
//		String lng = line.substring(0, index);
//		line = line.substring(index + 1);
//		index = line.indexOf(',');
//		String spd = line.substring(0, index);
//		line = line.substring(index + 1);
//		index = line.indexOf(',');
//		String cog = line.substring(0, index);
//		String type = line.substring(index + 1);
//		
//		String[] valueArray = new String[7];
//		valueArray[0] = time;
//		valueArray[1] = mmsi;
//		valueArray[2] = lat;
//		valueArray[3] = lng;
//		valueArray[4] = spd;
//		valueArray[5] = cog;
//		valueArray[6] = type;
//		
//		return valueArray;
//	}//ParseProcessedCSV END
	
	
	
	
	/***
	 * �ѪRtype=.csv��input data (parsed by line)
	 * @param line
	 * @return String[]
	 */
	public String[] ParseCSV(String line) {
//		//regex�g�k�A�iwork���D�`�Ӯį�C���O�d�����ϥ�
//		String[] valueArray = null;
//		valueArray = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);\
		
		ArrayList<String> words = new ArrayList<String>();
	    boolean notInsideComma = true;
	    int start = 0;
	    for(int i=0; i<line.length(); i++) {
	    	if(line.charAt(i)==',' && notInsideComma) {
	            words.add(line.substring(start,i));
	            start = i+1;
	        }else if(line.charAt(i)=='"') {
	        	notInsideComma =! notInsideComma;
	        }
	    }
	    words.add(line.substring(start));	//�B�z�̫�@��
	    String[] valueArray = words.toArray( new String[words.size()] ); 	//arrayList -> array
		
		return valueArray;
	}//ParseCSV END
	
	
	/***
	 * �L�X�ѪR�᪺ [�������W : ����]
	 * @return String
	 */
	public String toChString() {
		if (this.fieldNames == null) 
			return null; 
		String LF = System.getProperty("line.separator", "\r\n");
		String strContents = "";
		try {
			for (int i = 0; i < this.fieldCNames.length; i++) {
				if (this.fieldValues[i] != null) {
					String valuestr = this.fieldValues[i].trim();
					if (valuestr.length() > 0) {
						strContents = String.valueOf(String.valueOf(strContents)).concat(String.valueOf(String.valueOf(String.valueOf(String.valueOf((new StringBuffer("        ")).append(this.fieldCNames[i]).append("        ").append(": [").append(valuestr).append("]").append(LF))))));
					}
				}
			} 
		} catch (Exception e) {
			return null;
		} 
		return strContents;
	}//toChString END
	
	
	/***
	 * �Nparse�᪺���G�s��hashtable�A�H�K����s���B�Q��
	 * @return hashtable
	 */
	public Hashtable toHashtable() {
		int recno = 0;
		if (this.fieldNames == null)
			return null;
		Hashtable hashpack = new Hashtable();
		for (int i = 0; i < this.fieldNames.length; i++) {
			if (this.fieldValues[i] != null) {
				String valuestr;
				try {
					valuestr = this.fieldValues[i].trim();
				} catch (Exception e) {
					System.out.println("***Error while converting to hashtable: " + e + "***");
					logger.error("***Error while converting to hashtable: " + e + "***");
					return null;
				}
				if (valuestr != null)
					hashpack.put(this.fieldNames[i], valuestr);
			}
		}
		return hashpack;
	}//toHashtable END

	
	public static void main(String[] args) throws PROFILEException {
		String str1 = "04 Jun 2020 21:05:20 CST,25.65638667,121.4321767";	//test data
		String str2 = "04 Jun 2020 21:05:20 CST,PMHQ,Indonesia,20,0,AIS,TAIPEI,5.8,27,6,0,5,2020,20,9151448,25.65638667,121.4321767,525009331,1,Anchor,\"$PGHP,1,2020,6,4,13,5,20,0,416,,994161822,1,*2D\",0,SC ETERNITY XLVII,80,20,7,11,12.3,\"\\s:r994161822,c:1591275920*73\\\",1,Class A,1,Category X,1,18,Tanker,81,1591275920";	//test data
//		String str3 = "2020-11-25 16:35:55,,,0.0,0,AIS,,0.0,,0,,22.466803333333335,120.44317,808,15,,,128.0,6P4J2) <3518D-4?*(,2,68,52,48,50,0.0,,,B,,,0,104,,,";
		String str3 = "2020-11-25 16:35:58,,,230.9,0,AIS,,0.0,,230,,22.335383333333333,120.410385,80866391,15,,,128.0,ONWA1504            ,68,52,48,50,1.8,,,B,,,0,104,,,";
		int dataType = 0;	//0 means the type of input data is csv
		ProfileCenter pc = ProfileCenter.getProfileCenter(Env.TABLE_FIELD_PATH);
//		String packType = "testSet";
		String packType = "boatRaw";
		try {
			ResolveField resolveField = new ResolveField();
			resolveField.getResolveField(str3, dataType, pc, packType);
//			System.out.println(resolveField.toChString());		//�L�Xparse�᪺���G
			Hashtable hashData = resolveField.toHashtable();	//�Nparse�᪺���G�s��hashtable�A�H�K�s��
			System.out.println("hashtable> " + hashData);
			System.out.println("�n��> " + hashData.get("Latitude"));
			System.out.println("�g��> " + hashData.get("Longitude"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}//main END
	
	
	

}
