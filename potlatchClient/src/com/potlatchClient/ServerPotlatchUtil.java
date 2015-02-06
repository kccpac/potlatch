package com.potlatchClient;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;

import retrofit.client.Response;
import retrofit.mime.TypedFile;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.potlatchClient.provider.GiftInClient;
import com.potlatchClient.server.touchCount;
import com.potlatchClient.server.UserEmotion;
import com.potlatchClient.provider.dataContract;
import com.potlatchClient.server.Gift;
import com.potlatchClient.server.PotlatchStatus;
import com.potlatchClient.server.PotlatchSvcApi;
import com.potlatchClient.server.UserEmotion;
import com.potlatchClient.server.emotionType;
import com.potlatchClient.server.queryDataType;
import com.potlatchClient.server.touchCount;

public class ServerPotlatchUtil extends PotlatchUtil {

	private PotlatchSvcApi svc;
	
	public ServerPotlatchUtil(Context ctx)
	{
		super(ctx);
		svc =  PotlatchSvc.getOrShowLogin(ctx);
	}
	
	private File getPhysicalFile(Uri uri)
	{
	    String dir = dataContract.BASE_PATH;
	    if (uri != null && uri.getPathSegments().size() > 0)
	    {
	    	dir += "/" + uri.getPathSegments().get(0);
	    }
	    File root = new File(Environment.getExternalStorageDirectory(), 
	    		dir);
	    long id = ContentUris.parseId(uri);
	    File path = new File(root, String.valueOf(id));
	    return path;
		
	}

	@Override
	public void addGift(final Gift gift) {
		// TODO Auto-generated method stub
		
		if (svc == null )
		{
			Log.i(tag, "No internet connection....");
			return;
		}
		
		if (resolver == null)
		{
			Log.i(tag, "Null content resolver....");
			return;
		}
		new Thread(new Runnable() {
			
/*			public Gift convertForServer(GiftInClient gift)
			{
				Gift g = new Gift(
						gift.getOwnerId(),
						gift.getTitle(),
						gift.getDescription(),
						gift.getGiftType());		
				return g;
			}*/
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Gift g= null;//convertForServer(gift);
				g = svc.addGift(g);

				
				Log.i(tag, "Gift id " + g.getId());
				ContentValues values = new ContentValues();
				values.put(dataContract.Col._ID, Long.toString(g.getId()));				
				values.put(dataContract.Col._SRC_URL, gift.getSUrl());
				
				Uri srcUri = resolver.insert(dataContract.SOURCE_CONTENT_URI, values);	
				Uri thumbUri = resolver.insert(dataContract.THUMBNAIL_CONTENT_URI, values);

				File src =  getPhysicalFile(srcUri);
				File thumdnail = getPhysicalFile(thumbUri);
				
				
				PotlatchStatus status = svc.setGiftData(
						Long.toString(g.getOwnerId()),
						Long.toString(g.getId()),
						new TypedFile ("image/jpeg", thumdnail),
						new TypedFile ("image/jpeg", src));
				Message msg = Message.obtain(handler, PotlatchMsg.ADD_GIFT.getVal());
				Bundle b = new Bundle();
				b.putString(PotlatchConst.add_gift, status.toString());
				msg.setData(b);
				handler.sendMessage(msg);
			}			
		}).start();		
	}

	@Override
	public void findGiftByTitle(String title) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getData(final String giftId, final String catergory) {
		// TODO Auto-generated method stub

		if (svc == null)
		{
			Log.i(tag, "No internet connection....");
			return;
		}
		
		new Thread(new Runnable() {
		
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Response response = svc.getData(giftId, catergory);
				byte[] giftImage = null;
				try {
					InputStream giftStream = response.getBody().in();
					giftImage = IOUtils.toByteArray(giftStream);
				} 
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Message msg = Message.obtain(handler, PotlatchMsg.GET_DATA.getVal());
				Bundle b = new Bundle();
				b.putByteArray(PotlatchConst.get_data, giftImage);
				msg.setData(b);
				handler.sendMessage(msg);
	
			}
		}).start();
		

	//	return null;
	}

	@Override
	public void queryGiftDataList(
			final queryDataType type,
			final emotionType etype,
			final String selectionArgs[]) throws RemoteException {
		// TODO Auto-generated method stub
		
		Log.i(tag, "queryGiftDataList thread id: " + Thread.currentThread().getId());
		
		if (svc == null)
		{
			Log.i(tag, "No internet connection....");
			return;
		}
		
		if (selectionArgs.length > 1)
		{
			Log.i(tag, "Invalid selection..");
			return;
		}
		
		new Thread(new Runnable()
		{

		/*	private void convertForClient(Collection<Gift> gifts, ArrayList<GiftInClient> gs)
			{
			//	ArrayList<GiftInClient> gs = new ArrayList<GiftInClient>();
			//	int i;
				Iterator<Gift> it = gifts.iterator();
				while (it.hasNext() == true)
				{					
					Gift gift = it.next();
					GiftInClient g = new GiftInClient(
						gift.getId(),
						gift.getOwnerId(),
						gift.getTitle(),
						gift.getDescription(),
						gift.getGiftType());
					gs.add(g);
				}
			//	return gs;
			}
*/

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Collection<Gift> gifts = null;

				Log.i(tag, "queryGiftDataList new thread id: " + Thread.currentThread().getId());
				switch(type)
				{
					case QUERY_GIFTBYTITLE:
						gifts = svc.getGiftList(etype);
						break;
					case QUERY_GIFTBYOWNER:
						gifts = svc.getGiftListByOwnerId(selectionArgs[0]);
						break;
					default:
						break;
				}
				
			//	ArrayList<GiftInClient> gClientlist = new ArrayList<GiftInClient>();
						
			//	convertForClient(gifts, gClientlist);
				Message msg = Message.obtain(handler, PotlatchMsg.QUERY_GIFTDATA.getVal());				
				Bundle b = new Bundle();
				b.putSerializable(PotlatchConst.query_gift_data, gifts.toArray());
		//		b.putParcelableArrayList(PotlatchConst.query_gift_data, gClientlist);
				msg.setData(b);
				boolean result = handler.sendMessage(msg);
				Log.i(tag, "result is " + result);
			}			
		}
		).start();

	}

	public void queryUserData(queryDataType type,
			String selectionArgs[])
	{
		Log.i(tag, "queryUserData");
		if (type != queryDataType.QUERY_USERDATA)
		{
			Log.i(tag, "Invalid queryDataType");
			return;
		}
		
		
		final long giftId = Long.parseLong(selectionArgs[0]);
		final long userId = Long.parseLong(selectionArgs[1]);
		
		new Thread(new Runnable()
		{
	/*		private UserEmotionInClient convertToUserEmotionInClient(UserEmotion user)
			{				
				UserEmotionInClient uInClient = new UserEmotionInClient(
					user.getId(),
					user.getGiftId());
				int emotion[] = user.getEmotion();
				for (int i=0; i<emotion.length; i++)
				{
					uInClient.setEmotion(emotionType.getType(i), emotion[i]==1);
				}

				return uInClient;
			}
			*/
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				UserEmotion userfromServer = svc.queryUserData(userId, giftId);
				
			//	UserEmotionInClient uInClient = convertToUserEmotionInClient(userfromServer);
				
				Message msg = Message.obtain(handler,
						PotlatchMsg.QUERY_USERDATA.getVal());
				Bundle b = new Bundle();
				b.putSerializable(PotlatchConst.query_user_data, userfromServer);
			//	b.putParcelable(PotlatchConst.query_user_data, uInClient);
				msg.setData(b);
				handler.sendMessage(msg);				
			}			
		}).start();
	}

	@Override
	public void setUserEmotion(final UserEmotion user)
	{
		Log.i(tag, "setUserEmotion");
	
		new Thread(new Runnable()
		{
	/*		private UserEmotion convertToClient(UserEmotionInClient user)
			{				
				UserEmotion uInClient = new UserEmotion(
					user.getId(),
					user.getGiftId());
				int emotion[] = user.getEmotion();
				for (int i=0; i<emotion.length; i++)
				{
					uInClient.setEmotion(emotionType.getType(i), emotion[i]==1);
				}

				return uInClient;
			}
			*/
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
			//	UserEmotion user = convertToClient(userInClient);
				boolean ret = svc.setUserEmotion(user);
				
				Message msg = Message.obtain(handler,
						PotlatchMsg.SET_USEREMOTION.getVal());
				Bundle b = new Bundle();
				b.putBoolean(PotlatchConst.set_user_emotion, ret);
				msg.setData(b);
				handler.sendMessage(msg);
			}
		}).start();
		
	}
	
	public void local_queryTopGiver() {

		new Thread(new Runnable()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Cursor cursor = null;
				cursor = mDB.query(dataContract.TABLE_TOUCHCOUNT,
						dataContract.TOUCHCOUNT_COLUMNS, null, null, null, null, null);

				ArrayList<touchCount> rValue = new ArrayList<touchCount>();
				
				if (cursor != null) {
					if (cursor.moveToFirst()) {
						do {

							long id = cursor.getLong(cursor
									.getColumnIndex(dataContract.Col._ID));
							String title = cursor.getString(cursor
									.getColumnIndex(dataContract.Col._TITLE));
							int count = cursor.getInt(cursor
									.getColumnIndex(dataContract.Col._COUNTER_TOUCHED));
							Log.i(tag, "gift " + id + ": " + count);
							rValue.add(new touchCount(id, title, count));
						} while (cursor.moveToNext() == true);
					}
					cursor.close();
				}
		
				Message msg = Message.obtain(handler,
						PotlatchMsg.QUERY_TOPGIVER.getVal());
				Bundle b = new Bundle();
			//	b.putParcelableArrayList(PotlatchConst.query_top_giver, rValue);
				b.putSerializable(PotlatchConst.query_top_giver, rValue);
				msg.setData(b);
				handler.sendMessage(msg);
			}
			
		}).start();
	}
	
	@Override
	public void queryTopGiver()
	{
		if (svc == null)
		{
			Log.i(tag, "No internet connection....");
			return;
		}
		
		new Thread(new Runnable()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Collection <touchCount> tcs = svc.getEmotionCountList(emotionType.EMOTION_TOUCHED);
				
		/*		Iterator <touchCount> it = tcs.iterator();
				touchCount tc = null;
				ArrayList<TouchCountInClient> ctc = new ArrayList<TouchCountInClient>();
				while (it.hasNext())
				{
					tc = it.next();
					ctc.add(new TouchCountInClient(
							tc.getGiftId(),
							tc.getGiftTitle(),
							tc.getCount()));
				}	
			*/	
				Message msg = Message.obtain(handler, PotlatchMsg.QUERY_TOPGIVER.getVal());				
				Bundle b = new Bundle();
			//	b.putParcelableArrayList(PotlatchConst.query_top_giver, ctc);
				b.putSerializable(PotlatchConst.query_top_giver, tcs.toArray());
				msg.setData(b);
				boolean result = handler.sendMessage(msg);
				Log.i(tag, "result is " + result);
			}			
		}).start();
				
	}
	

	@Override
	public void setEmotionCounter(final Gift gift, final counterEnable[] cEnabled) {
		// TODO Auto-generated method stub
		new Thread(new Runnable()
		{
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.i(tag, "setEmotionCounter start");
				emotionType etype = emotionType.EMOTION_NONE;
				for (int i = 0; i < cEnabled.length; i++) {
					etype = emotionType.getType(i);
					if (cEnabled[i] == counterEnable.INCREMENT) {
						svc.eMotionByGift(gift.getId(), etype);
					} else if (cEnabled[i] == counterEnable.DECREMENT) {
						svc.UneMotionByGift(gift.getId(), etype);
					}
				}
				Log.i(tag, "setEmotionCounter end");				
			}			
		});
		
	}

	public long updateTouchCountTable(touchCount tc) {

		Log.i(tag, "updateTouchCountTable");	

		if (mDB == null) {
			return 0;
		}

		String title = tc.getGiftTitle();
		String giftId = Long.toString(tc.getGiftId());
		int giftCount = tc.getCount();

		String selection = dataContract.Col._ID + " =?" + " and " +
				   dataContract.Col._TITLE + " LIKE ?";
		String args[] = new String[] {
				Long.toString(tc.getGiftId()), 
				tc.getGiftTitle() };
		
		Cursor c = mDB.query( dataContract.TABLE_TOUCHCOUNT, dataContract.TOUCHCOUNT_COLUMNS,
							selection, args, null, null, null);
		
		Long row  = 0L;
		
		ContentValues values = new ContentValues();
		values.put(dataContract.Col._TITLE,  title);
		values.put(dataContract.Col._ID, giftId);
		values.put(dataContract.Col._COUNTER_TOUCHED, giftCount);
		
		if (c.getCount() > 0)
		{
			row = (long) mDB.update(dataContract.TABLE_TOUCHCOUNT, values, selection, args);
			Log.i(tag, "update result:" + row);
		}
		else
		{	 		
			row = mDB.insert(dataContract.TABLE_TOUCHCOUNT, null, values);
			
			Log.i(tag, "insert result:" + row);
		}
	
		return row;
	}

	@Override
	public void setGiftData(Gift gift) {
		// TODO Auto-generated method stub
		
	}

}
