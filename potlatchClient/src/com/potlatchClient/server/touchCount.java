package com.potlatchClient.server;

public class touchCount {
	protected long giftId;
	protected String giftTitle;
	protected int count;

	public touchCount()
	{
		
	}
	
	public touchCount(long giftId, String title, int count)
	{
		this.giftId = giftId;
		giftTitle = title;
		this.count = count;
	}
	
	public int getCount()
	{
		return count;
	}
	
	public String getGiftTitle()
	{
		return giftTitle;
	}
	
	public long getGiftId()
	{
		return giftId;
	}
}
