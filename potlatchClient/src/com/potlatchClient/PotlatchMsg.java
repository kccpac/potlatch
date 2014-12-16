package com.potlatchClient;

public enum PotlatchMsg {
	
	UNKNOWN(-1),
	ADD_GIFT(0),
	SET_GIFT_DATA(1),
	FIND_GIFT_BY_TITLE(2),
	GET_DATA(3),
	QUERY_GIFTDATA(4),
	QUERY_USERDATA(5),
	QUERY_TOPGIVER(6),
	SET_USEREMOTION(7),
	SET_EMOTIONCOUNTER(8);

	private int val;
	
	PotlatchMsg(int val)
	{
		this.val = val;
	}
	
	public int getVal()
	{
		return this.val;
	}
}
