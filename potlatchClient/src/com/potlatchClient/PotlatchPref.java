package com.potlatchClient;

import com.potlatchClient.server.emotionType;

public class PotlatchPref {
	
	private int refreshRate;
	private emotionType eType; 
	
	PotlatchPref()
	{
		refreshRate = 1;
		eType = emotionType.EMOTION_NONE;
	}
	
	public emotionType getEType()
	{
		return eType;
	}
	
	public void setEType(int etypeIdx)
	{
		eType = emotionType.getType(etypeIdx);
	}
	
	public int getRefreshRate()
	{
		return refreshRate;
	}
	
	public void setRefreshRate(int rate)
	{
		refreshRate = rate;
	}

}
