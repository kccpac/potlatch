package com.potlatchClient.server;

public class UserEmotion {

	protected long id;
	protected int[] emotion;
	protected long giftId;
	
	public UserEmotion()
	{
	 
	}

	public UserEmotion(long id, long giftId)
	{
		super();
		this.id = id;
		this.giftId = giftId;
		emotion = new int[emotionType.values().length];
		for (int i=0; i<emotion.length; i++)
		{
			emotion[i] = 0;
		}
	}
	
	public long getId()
	{
		return id;
	}
	
	public long getGiftId()
	{
		return giftId;
	}
	
	public int getEmotion(emotionType etype) throws NegativeArraySizeException
	{		
		if (etype == emotionType.EMOTION_NONE)
		{
			throw new NegativeArraySizeException(); 
		}
		return emotion[etype.getVal()];
	}
	
	public int [] getEmotion()
	{
		return emotion;
	}
	
	public void setEmotion(emotionType etype, boolean state) throws NegativeArraySizeException
	{
		if (etype == emotionType.EMOTION_NONE)
			return;		
				
		emotion[etype.getVal()] = state ? 1: 0;
	}

}
