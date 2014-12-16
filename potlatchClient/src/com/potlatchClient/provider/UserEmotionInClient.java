package com.potlatchClient.provider;

import com.potlatchClient.server.UserEmotion;
import com.potlatchClient.server.emotionType;

import android.os.Parcel;
import android.os.Parcelable;

public class UserEmotionInClient extends UserEmotion implements Parcelable {
	
	public UserEmotionInClient(long id, long giftId)
	{
		super(id, giftId);
	}	


	/**
	 * Used for writing a copy of this object to a Parcel, do not manually call.
	 */
	public static final Parcelable.Creator<UserEmotionInClient> CREATOR = new Parcelable.Creator<UserEmotionInClient>() {
		public UserEmotionInClient createFromParcel(Parcel in) {
			return new UserEmotionInClient(in);
		}

		public UserEmotionInClient[] newArray(int size) {
			return new UserEmotionInClient[size];
		}
	};

	private UserEmotionInClient(Parcel in) {
		id = in.readLong();
		giftId = in.readLong();
		emotion = new int[emotionType.values().length];
		in.readIntArray(emotion);
	}
	
	@Override
	/**
	 * Used for writing a copy of this object to a Parcel, do not manually call.
	 */
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeLong(giftId);		
		dest.writeIntArray(emotion);		
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
}
