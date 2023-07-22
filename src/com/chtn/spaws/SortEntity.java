package com.chtn.spaws;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.chtn.spaws.entity.BoatInfo;
import com.chtn.spaws.entity.SubCableInfo;
import com.chtn.spaws.parse.ParseData;
import com.chtn.util.Env;

public class SortEntity {
	
	/**
	 * �ǤJ�@�өΦh�Ӯ��l�ɪ�boatInfo array�A�N�ƧǦ�{ �iĵ��1:[[�iĵ��1��BoatInfo1],[�iĵ��1��BoatInfo2]..], �iĵ��2:[[�iĵ��2��BoatInfo1],[�iĵ��2��BoatInfo2]..] }
	 * @param boatInfos
	 * @return HashMap<String, ArrayList<BoatInfo>>
	 */
	public HashMap<String, ArrayList<BoatInfo>> sortBoatByAlertRecordFile(BoatInfo[] boatInfos) {
		HashMap<String, ArrayList<BoatInfo>> boatList = new HashMap<>();
		ArrayList<BoatInfo> boat;
		
		for (int i = 0; i < boatInfos.length; i++) {
			if ( boatList.get(boatInfos[i].getAlertRecordFileName()) == null ) {	//�p�GalertRecordFileName.get�줣��A�N��AlertRecordFileName�O�s���ɦW
				boat = new ArrayList<>();
				boat.add(boatInfos[i]);
				boatList.put(boatInfos[i].getAlertRecordFileName(), boat);
			}else {		//�p�GalertRecordFileName.get�s�b�A�h���boatInfo�[�즹alertRecordFileName������value(arrayList)��
				boat = boatList.get(boatInfos[i].getAlertRecordFileName());
				boat.add(boatInfos[i]);
				boatList.put(boatInfos[i].getAlertRecordFileName(), boat);
			}
		}
		return boatList;
	}//sortBoatByAlertRecordFile END
	

	
	/**
	 * �ǤJ�ѪR�᪺���TboatInfo array�A�åHkey-value���Φ��^�ǡC(key:MMSI�Bvalue:��MMSI���Ҧ����ơAtype��ArrayList<BoatInfo>)
	 * @param boatInfos
	 * @return HashMap<String, ArrayList<BoatInfo>>
	 */
	public HashMap<String, ArrayList<BoatInfo>> sortBoatByMMSI(BoatInfo[] boatInfos) {
		HashMap<String, ArrayList<BoatInfo>> boatList = new HashMap<>();	//�ΨӦs�񤣦P����s���A�P�ɥi�p�⦳�X����Ckey: mmsi�Bvalue: ArrayList<BoatInfo>�C
		ArrayList<BoatInfo> boat;
		
		for (int i = 0; i < boatInfos.length; i++) {
			if ( boatList.get(boatInfos[i].getMmsi()) == null ) {	//�p�GMMSIs.get�줣��A�N��MMSI�O�s���
				boat = new ArrayList<>();
				boat.add(boatInfos[i]);
				boatList.put(boatInfos[i].getMmsi(), boat);
			}else {		//�p�GMMSI.get�s�b�A�h���boatInfo�[�즹MMSI������value(arrayList)��
				boat = boatList.get(boatInfos[i].getMmsi());
				boat.add(boatInfos[i]);
				boatList.put(boatInfos[i].getMmsi(), boat);
			}
		}
		return boatList;
	}//sortByMMSI END
	
	
	/**
	 * �ǤJ�@�өΦh�Ӯ��l�ɪ�SubCableInfo array�A�N�ƧǦ�{ ���l1:[[���l1��SubCableInfo1],[���l1��SubCableInfo2]..], ���l2:[[���l2��SubCableInfo1],[���l2��SubCableInfo2]..] }
	 * @param subCableInfos
	 * @return HashMap<String, ArrayList<SubCableInfo>>
	 */
	public HashMap<String, ArrayList<SubCableInfo>> sortSubCableByName(SubCableInfo[] subCableInfos) {
		HashMap<String, ArrayList<SubCableInfo>> subCableList = new HashMap<>();	
		ArrayList<SubCableInfo> subCable;
		
		for(int i=0; i<subCableInfos.length; i++) {
			if(subCableList.get(subCableInfos[i].getSubCableName()) == null) {	//�p�GsubCableInfos[i].getSubCableName()�줣��A�N���O�s�����l��
				subCable = new ArrayList<>();
				subCable.add(subCableInfos[i]);
				subCableList.put(subCableInfos[i].getSubCableName(), subCable);
			}else {		//�p�GsubCableInfos[i].getSubCableName()�s�b�A�h��ӵ����l��T�[�즹���l�W�ٹ�����value(arrayList)��
				subCable = subCableList.get(subCableInfos[i].getSubCableName());
				subCable.add(subCableInfos[i]);
				subCableList.put(subCableInfos[i].getSubCableName(), subCable);
			}
		}
		return subCableList;
	}//sortSubCableByName END
	
	
	
	public static void main(String[] args) throws IOException {
		int dataType = 0;
		String filePath = Env.BOAT_PROCESSED_DATA_PATH;
		String fileName = "current.csv";
		String packType = "boatRaw";
		ParseData parseData = new ParseData();
		BoatInfo[] boatInfos = parseData.readFromMPBFile(filePath, fileName, dataType, packType);
		
		SortEntity sortEntity = new SortEntity();
		HashMap<String, ArrayList<BoatInfo>> MMSIsList = sortEntity.sortBoatByMMSI(boatInfos);
		//�L�X���T
		System.out.println("���X����: " + MMSIsList.size());
		for( Map.Entry<String, ArrayList<BoatInfo>> entry : MMSIsList.entrySet() ){		//iterate hashmap
			String key = entry.getKey();
			ArrayList<BoatInfo> boats = entry.getValue();
			System.out.println("��s" + key + "���X�����: " + boats.size());
		}
		
		
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
		int SCdataType = 0;
		File SCfilePath = new File(Env.SC_DATA_PATH);
		String SCpackType = "subCable";
		ParseData SCparseData = new ParseData();
	 	ArrayList<SubCableInfo> subCableInfos = new ArrayList<>();
	 	for(File file : SCfilePath.listFiles())		//�@��loop folder�̥������l��
	 	{
	 		SubCableInfo[] infos = SCparseData.readFromSCFile(SCfilePath.getAbsolutePath()+Env.FILE_SEPARATOR, file.getName(), dataType, SCpackType);
	 		subCableInfos.addAll(Arrays.asList(infos));
	 	}
		
	 	SubCableInfo[] TempSCAry = new SubCableInfo[subCableInfos.size()];	//�ѩ�sortSubCableByName()�ݥN�Jarray�A�]���o�̱NarrayList�নarray
	 	TempSCAry = subCableInfos.toArray(TempSCAry);
		SortEntity SCsortEntity = new SortEntity();
		HashMap<String, ArrayList<SubCableInfo>> subCableList = SCsortEntity.sortSubCableByName(TempSCAry);
		//�L�X���l��T
		System.out.println("\n���X�����l: " + subCableList.size());
		for (Map.Entry<String, ArrayList<SubCableInfo>> entry : subCableList.entrySet()) {
			String key = entry.getKey();
			ArrayList<SubCableInfo> subCables = entry.getValue();
			System.out.println("���l[" + key + "]���X���g�n�׸��: " + subCables.size());
		}
		
		
		
	}//main END

}
