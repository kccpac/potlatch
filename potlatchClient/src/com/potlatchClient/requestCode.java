package com.potlatchClient;

public enum requestCode {
	INTENT_ACTIVITY_UNKNOWN(-1),
	INTENT_ACTIVITY_ONE(2),
	INTENT_ACTIVITY_TWO(3),
	INTENT_ACTIVITY_THREE(4),
	LOAD_IMAGE_REQUEST_CODE(42),
	CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE(1);
	
	private int val;
	
	requestCode(int val)
	{
		this.val = val;
	}
	
	public int getVal()
	{
		return this.val;
	}
	
}
