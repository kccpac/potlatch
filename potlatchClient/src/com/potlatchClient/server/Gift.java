package com.potlatchClient.server;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import com.potlatchClient.server.emotionType;

public class Gift { 

	protected Long id;
	
	protected String title;
	protected String description;
	protected URL url;
	protected long ownerId;
	protected String giftType;
	protected String sUrl; // source url
	protected String tUrl; // thumbnail url
	protected int emotionCounter[];

	protected Set<String> touchBy = new HashSet<String>();
	
	public Gift() {
	}

	public Gift(long ownerId, String title, String description, String giftType)
	{
		super();
		init(ownerId, title, description, giftType);
		
	}
	
	public Gift(long id, long ownerId, String title, String description, String giftType)
	{
		super();
		this.id = id;
		init(ownerId, title, description, giftType);
	}

	private void init(long ownerId, String title, String description, String giftType)
	{
		this.title = title;
		this.description = description;
		this.giftType = giftType;
		this.ownerId = ownerId;
		this.emotionCounter = new int[emotionType.values().length];
		for (int i=0; i<this.emotionCounter.length; i++)
		{
			this.emotionCounter[i] = 0;
		}				
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public long getOwnerId()
	{
		return ownerId;
	}
	
	public long getId()
	{
		return id;
	}
	
	public String getGiftType()
	{
		return giftType;
	}	
	
	public String getSUrl()
	{
		return sUrl;
	}	

	public void setSUrl(String url)
	{
		this.sUrl = url; 
	}
	
	public String getTUrl()
	{
		return tUrl;
	}	

	public void setTUrl(String url)
	{
		this.tUrl = url; 
	}
	
	public int[] getEmotionCounter()
	{		
		return emotionCounter;
	}
	
	public int getEmotionCounter(emotionType etype)
	{	
		int count = 0;
		switch(etype)
		{
		case EMOTION_TOUCHED:			
		case EMOTION_INAPPROPRIATE:
		case EMOTION_OBSCENE:
			count = emotionCounter[etype.getVal()];
			break;
		default:
			break;
		}		
		return count;
	}
	
	public void incrEmotionCount(int etypeIdx, String username) throws NegativeArraySizeException
	{		
		if (etypeIdx == emotionType.EMOTION_NONE.getVal())
		{
			throw new NegativeArraySizeException(); 
		}
		emotionCounter[etypeIdx]++;
		touchBy.add(username);
	}

	public void decrEmotionCount(int etypeIdx, String username) throws NegativeArraySizeException
	{		
		if (etypeIdx == emotionType.EMOTION_NONE.getVal())
		{
			throw new NegativeArraySizeException(); 
		}
		emotionCounter[etypeIdx]--;
		touchBy.remove(username);
	}
	
	public void setEmotionCounter(emotionType etype, int countVal )
	{		
		switch(etype)
		{
		case EMOTION_TOUCHED:			
		case EMOTION_INAPPROPRIATE:
		case EMOTION_OBSCENE:
			emotionCounter[etype.getVal()] = countVal;
			break;
		default:
			break;
		}		
	}
}
