package com.chtn.spaws.entity;

public class BoatInfo {
	private String timestamp;
	private String mmsi;
	private double latitude;
	private double longitude;
	private String speed;
	private String direction;
	private String heading;
	private String callsign;
	private String country;
	private String imo;
	private String shipName;
	private String sizeA;
	private String sizeB;
	private String sizeC;
	private String sizeD;
	private String typeCode;	
	private String typeText;	
	private String alertRecordFileName;	//用來存放告警紀錄檔的檔名，web呈現告警資訊時會使用到
	private String alertTimes;	//用來記錄該船被告警了幾次
	
	
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getMmsi() {
		return mmsi;
	}
	public void setMmsi(String mmsi) {
		this.mmsi = mmsi;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getSpeed() {
		return speed;
	}
	public void setSpeed(String speed) {
		this.speed = speed;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public String getHeading() {
		return heading;
	}
	public void setHeading(String heading) {
		this.heading = heading;
	}
	public String getCallsign() {
		return callsign;
	}
	public void setCallsign(String callsign) {
		this.callsign = callsign;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getImo() {
		return imo;
	}
	public void setImo(String imo) {
		this.imo = imo;
	}
	public String getShipName() {
		return shipName;
	}
	public void setShipName(String shipName) {
		this.shipName = shipName;
	}
	public String getSizeA() {
		return sizeA;
	}
	public void setSizeA(String sizeA) {
		this.sizeA = sizeA;
	}
	public String getSizeB() {
		return sizeB;
	}
	public void setSizeB(String sizeB) {
		this.sizeB = sizeB;
	}
	public String getSizeC() {
		return sizeC;
	}
	public void setSizeC(String sizeC) {
		this.sizeC = sizeC;
	}
	public String getSizeD() {
		return sizeD;
	}
	public void setSizeD(String sizeD) {
		this.sizeD = sizeD;
	}
	public String getTypeCode() {
		return typeCode;
	}
	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}
	public String getTypeText() {
		return typeText;
	}
	public void setTypeText(String typeText) {
		this.typeText = typeText;
	}
	public String getAlertRecordFileName() {
		return alertRecordFileName;
	}
	public void setAlertRecordFileName(String alertRecordFileName) {
		this.alertRecordFileName = alertRecordFileName;
	}
	public String getAlertTimes() {
		return alertTimes;
	}
	public void setAlertTimes(String alertTimes) {
		this.alertTimes = alertTimes;
	}
	
	
	
	
	
	
	
}
