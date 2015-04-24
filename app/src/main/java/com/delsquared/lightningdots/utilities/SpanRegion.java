package com.delsquared.lightningdots.utilities;

public class SpanRegion {

	//---------- Class Members ----------//
	
	// The flag
	private String _flag;
	
	// The start index
	private int _startIndex;
	
	// The end index
	private int _endIndex;
	
	//---------- Constructors ------------//
	
	public SpanRegion() {
		this.setFlag("");
	}
	
	public SpanRegion(
            String flag
            , int startIndex
            , int endIndex) {
		
		this.setFlag(flag);
		this.setStartIndex(startIndex);
		this.setEndIndex(endIndex);
		
	}
	
	//---------- Data Accessors ----------//
	
	public void setFlag(String flag) {
		this._flag = flag;
	}
	
	public String getFlag() {
		return this._flag;
	}
	
	public void setStartIndex(int startIndex) {
		this._startIndex = startIndex;
	}
	
	public int getStartIndex() {
		return this._startIndex;
	}
	
	public void setEndIndex(int endIndex) {
		this._endIndex = endIndex;
	}
	
	public int getEndIndex() {
		return this._endIndex;
	}
		
}
