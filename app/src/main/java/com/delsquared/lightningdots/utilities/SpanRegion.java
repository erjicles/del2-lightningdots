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

	@SuppressWarnings("unused")
	public SpanRegion() {
		this.setFlag("");
	}

	@SuppressWarnings("unused")
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

	@SuppressWarnings("unused")
	public String getFlag() {
		return this._flag;
	}
	
	public void setStartIndex(int startIndex) {
		this._startIndex = startIndex;
	}

	@SuppressWarnings("unused")
	public int getStartIndex() {
		return this._startIndex;
	}
	
	public void setEndIndex(int endIndex) {
		this._endIndex = endIndex;
	}

	@SuppressWarnings("unused")
	public int getEndIndex() {
		return this._endIndex;
	}
		
}
