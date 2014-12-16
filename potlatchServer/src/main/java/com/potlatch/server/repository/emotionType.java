package com.potlatch.server.repository;

public enum emotionType {
	EMOTION_NONE(0),
	EMOTION_TOUCHED(1),
	EMOTION_INAPPROPRIATE(2),
	EMOTION_OBSCENE(3);
	
	private int val;
	
	emotionType(int val)
	{		
		this.val = val;
	}
	
	public int getVal()
	{
		return this.val;
	}
	
	public static emotionType getType(int val)
	{
		emotionType elist[] = emotionType.values();
		emotionType etype = emotionType.EMOTION_NONE;
		for (int i=0; i<elist.length; i++)
		{
			if (val == elist[i].getVal())
			{
				etype = elist[i];
				break;
			}
		}
		return etype;
	}
}
