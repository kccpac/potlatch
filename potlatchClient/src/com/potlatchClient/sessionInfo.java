package com.potlatchClient;

import com.potlatchClient.oauth.SecuredRestBuilder;
import com.potlatchClient.server.PotlatchSvcApi;

import android.os.Parcel;
import android.os.Parcelable;

public class sessionInfo implements Parcelable{

	private SecuredRestBuilder builder;
	private int userId;

	sessionInfo(SecuredRestBuilder builder, int userId)
	{
		this.builder = builder;
		this.userId = userId;
		
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		// TODO Auto-generated method stub

		parcel.writeValue(builder);
		parcel.writeInt(userId);
	}
	
	public SecuredRestBuilder getBuilder()
	{
		return builder;
	}
	
	public int getUserId()
	{
		return userId;
	}

}
