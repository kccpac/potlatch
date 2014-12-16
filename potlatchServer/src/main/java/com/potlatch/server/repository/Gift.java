package com.potlatch.server.repository;

//import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.common.base.Objects;


@Entity
public class Gift {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	protected long id;
	
	protected String title;	
	protected String description;	
	protected URL url;	
	protected long ownerId;	
	protected String giftType;		
	protected String sUrl; // source url	
	protected String tUrl; // thumbnail url
	protected int emotionCounter[];
	
	@ElementCollection
	@CollectionTable(name="touchByUser")
	protected Set<String> touchBy = new HashSet<String>();

	public Set<String> getTouchBy()
	{
		return touchBy;
	}
	
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
	
	public void setEmotionCounter(emotionType etype, String username)
	{		
		switch(etype)
		{
		case EMOTION_TOUCHED:
			touchBy.add(username);
		case EMOTION_INAPPROPRIATE:
		case EMOTION_OBSCENE:
			emotionCounter[etype.getVal()]++;
			break;
		default:
			break;
		}		
	}
	
	public void setUnEmotionCounter(emotionType etype, String username)
	{		
		switch(etype)
		{
		case EMOTION_TOUCHED:
			touchBy.remove(username);
		case EMOTION_INAPPROPRIATE:
		case EMOTION_OBSCENE:
			emotionCounter[etype.getVal()]--;
			break;
		default:
			break;
		}		
	}
	
	@Override
	public int hashCode() {
		// Google Guava provides great utilities for hashing
		return Objects.hashCode(id, ownerId, description, giftType);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Gift) {
			Gift other = (Gift) obj;
			// Google Guava provides great utilities for equals too!
			return Objects.equal(id, other.id)
					&& Objects.equal(ownerId, other.ownerId)
					&& Objects.equal(description, other.description)
					&& Objects.equal(giftType, other.giftType);
		} else {
			return false;
		}
	}

}
