package com.potlatchClient.provider;

import android.os.Parcel;
import android.os.Parcelable;

import com.potlatchClient.server.touchCount;


public class TouchCountInClient extends touchCount implements Parcelable {

	public TouchCountInClient(long giftId, String title, int count)
	{
		super(giftId, title, count);
	}
	
	/**
	 * Used for writing a copy of this object to a Parcel, do not manually call.
	 */
	public static final Parcelable.Creator<TouchCountInClient> CREATOR = new Parcelable.Creator<TouchCountInClient>() {
		public TouchCountInClient createFromParcel(Parcel in) {
			return new TouchCountInClient(in);
		}
	
		public TouchCountInClient[] newArray(int size) {
			return new TouchCountInClient[size];
		}
	};
	
	private TouchCountInClient(Parcel in) {
		giftId = in.readLong();
		giftTitle = in.readString();
		count = in.readInt();
	}
	
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(giftId);
		dest.writeString(giftTitle);
		dest.writeLong(count);
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	


}
