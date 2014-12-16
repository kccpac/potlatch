package com.potlatch.server.repository;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class UserEmotion {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	protected long id;
	
	protected long userId;
	protected int[] emotion;
	protected long giftId;
	
	public UserEmotion()
	{
		
	}
	
	public UserEmotion(long userId, long giftId)
	{
		this.userId = userId;
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
	
	public long getUserId()
	{
		return userId;
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
