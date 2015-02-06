package com.potlatchClient;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;

//import com.potlatchClient.provider.GiftInClient;
import com.potlatchClient.server.Gift;
import com.potlatchClient.server.touchCount;
import com.potlatchClient.server.UserEmotion;//InClient;
import com.potlatchClient.provider.storageDBHelper;
import com.potlatchClient.server.emotionType;
import com.potlatchClient.server.queryDataType;

public abstract class PotlatchUtil {

	public static final String tag = PotlatchUtil.class.getCanonicalName();
	private Context ctx;
	public Handler handler;
	protected ContentResolver resolver;
	protected SQLiteDatabase mDB;

	PotlatchUtil(Context ctx)
	{
		this.ctx = ctx;
		resolver = ctx.getContentResolver();
		storageDBHelper dbHelper = new storageDBHelper(ctx);
		mDB = dbHelper.getWritableDatabase();
	}	
	
	public abstract void addGift(Gift gift);
	
	public abstract void setGiftData(Gift gift);
	
	public abstract void findGiftByTitle(String title);
	
	public abstract void getData(String giftId, String catergory);

	public abstract void queryGiftDataList(
			queryDataType type,
			emotionType etype,
			String selectionArgs[]) throws RemoteException;
	
	public abstract void queryUserData(
			queryDataType type,			
			String selectionArgs[]);

	public abstract void setUserEmotion(UserEmotion user);

	public abstract void queryTopGiver();
	
	public abstract void local_queryTopGiver(); 
	
	public abstract void setEmotionCounter(Gift gift, counterEnable cEnabled[]);

	public abstract long updateTouchCountTable(touchCount tc);
	
	public Message setMessage(Handler handler, int what, String key, Object data)
	{
		Log.i(tag , "setMessage");
		Message msg = Message.obtain(handler, what);
		Bundle b = new Bundle();		

		switch(what)
		{
			case 0:
				b.putParcelable(key, (Parcelable) data);
				break;
			case 1:
			case 2:		
				b.putInt(key, (Integer)data);
				break;
			case 3:
				b.putByteArray(key, (byte[])data);
				break;
			default:
				break;
		}	
		msg.setData(b);
		
		return msg;		
		
	}
	
	public PotlatchPref getPreference()
	{
		Log.d(tag, "getPreference....");
		SharedPreferences sPref = ctx.getSharedPreferences("potlatch", Context.MODE_PRIVATE);
		String refreshRateArray[] = ctx.getResources().getStringArray(R.array.pref_refresh_rate_list_values);    
		String prohabitTypeArray[] = ctx.getResources().getStringArray(R.array.prohabit_gift_list_values);
	
		int rIdx = sPref.getInt("refresh rate index", 0);
		int pIdx = sPref.getInt("prohabit type index", -1);
		
		PotlatchPref pref = new PotlatchPref(); 
		
		int rate = Integer.parseInt(refreshRateArray[0]);
		if (rIdx >= 0 && rIdx < refreshRateArray.length)
		{						
			rate = Integer.parseInt(refreshRateArray[rIdx]);
		}
		pref.setRefreshRate(rate);
		int etypeIdx = emotionType.EMOTION_NONE.getVal();
		if (pIdx >= 0 && pIdx < prohabitTypeArray.length) 
		{
			etypeIdx = Integer.parseInt(prohabitTypeArray[pIdx]);
		}
		pref.setEType(etypeIdx);		

		return pref;
	}
	
	public void setHandler(Handler handle)
	{
		this.handler = handle;
	}
	

}
