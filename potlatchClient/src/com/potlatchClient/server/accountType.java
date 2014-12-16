package com.potlatchClient.server;

public enum accountType {
	ACCOUNT_ANONYMOUS(0),
	ACCOUNT_ADMIN(1),
	ACCOUNT_USER(2);

	private int val;
	
	accountType(int val)
	{
		this.val = val;
	}
	
	public int getVal()
	{
		return val;
	}
	

	

}
