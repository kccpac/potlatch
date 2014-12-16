package com.potlatchClient.provider;

import android.os.Parcel;
import android.os.Parcelable;

import com.potlatchClient.server.Gift;
import com.potlatchClient.server.emotionType;



public class GiftInClient extends Gift implements Parcelable {

	public GiftInClient() {
		super();
	}

	public GiftInClient(long ownerId, String title, String description, String giftType)
	{
		super(ownerId, title, description, giftType);		
		
	}
	
	public GiftInClient(long id, long ownerId, String title, String description, String giftType)
	{
		super(id, ownerId, title, description, giftType);
	}
	
/*	
	public void setTUrl(String url)
	{
		super.setTUrl(url);
	}
	
	public void setSUrl(String url)
	{
		super.setSUrl(url);
	}
	*/

	
	


	/**
	 * Used for writing a copy of this object to a Parcel, do not manually call.
	 */
	public static final Parcelable.Creator<GiftInClient> CREATOR = new Parcelable.Creator<GiftInClient>() {
		public GiftInClient createFromParcel(Parcel in) {
			return new GiftInClient(in);
		}

		public GiftInClient[] newArray(int size) {
			return new GiftInClient[size];
		}
	};

	private GiftInClient(Parcel in) {
		id= in.readLong();
		ownerId= in.readLong();
		title = in.readString();
		description = in.readString();
		giftType = in.readString();
		sUrl = in.readString();
		tUrl = in.readString();
		emotionCounter = new int[emotionType.values().length];
		in.readIntArray(emotionCounter);
	}
	
	@Override
	/**
	 * Used for writing a copy of this object to a Parcel, do not manually call.
	 */
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeLong(ownerId);
		dest.writeString(title);
		dest.writeString(description);
		dest.writeString(giftType);
		dest.writeString(sUrl);
		dest.writeString(tUrl);
		dest.writeIntArray(emotionCounter);		
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

}
