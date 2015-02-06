package com.potlatchClient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

//import com.potlatchClient.provider.GiftInClient;
//import com.potlatchClient.provider.TouchCountInClient;
//import com.potlatchClient.provider.UserEmotionInClient;
import com.potlatchClient.provider.dataContract;
import com.potlatchClient.server.*;
import com.potlatchClient.server.PotlatchStatus.PotlatchState;

public class LocalPotlatchUtil extends PotlatchUtil {

	public static final String tag = LocalPotlatchUtil.class.getCanonicalName();

	public LocalPotlatchUtil(Context ctx) {
		super(ctx);
	}	


	public void addGift(Gift gift) {
		final Gift v = gift;
		if (mDB == null)
			return;

		new Thread(new Runnable() {

			@Override
			public void run() {
				ContentValues values = new ContentValues();

				values.put("ownerId", Long.toString(v.getOwnerId()));
				values.put("title", v.getTitle());
				values.put("description", v.getDescription());
				values.put("type", v.getGiftType());
				Long row = mDB.insert(dataContract.TABLE_GIFT, null, values);

				values.clear();
				values.put(dataContract.Col._ID, row);
				values.put(dataContract.Col._SRC_URL, v.getSUrl());

				resolver.insert(dataContract.SOURCE_CONTENT_URI, values);
				resolver.insert(dataContract.THUMBNAIL_CONTENT_URI, values);
			}
		}).start();

	}

	@Override
	public void setGiftData(Gift gift) {

		PotlatchStatus status = new PotlatchStatus(null);
		Message msg = super.setMessage(handler, 1, "setGiftData",
				PotlatchState.READY);
		handler.sendMessage(msg);
	}

	@Override
	public void findGiftByTitle(String title) {
		// TODO Auto-generated method stub
	}

	@Override
	public void getData(final String giftId, final String catergory) {
		
		Log.i(tag, "getData");
		
		new Thread(new Runnable()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				Uri uri = dataContract.SOURCE_CONTENT_URI;
				if (catergory.equals("thumbnail"))
				{
					uri = dataContract.THUMBNAIL_CONTENT_URI;
				}
				Uri dataUri = uri.buildUpon()
							.appendPath(giftId).build();
				
				byte data[] = null;
				int dataRead = 0;
				int tdatalen = 0;
				int buffer_size = 2048;
				try {
					InputStream in = resolver.openInputStream(dataUri);
					data = new byte[buffer_size];
					do {
						dataRead = in.read(data, tdatalen, buffer_size);
						if (dataRead != -1) {
							byte tmp[] = new byte[data.length + buffer_size];
							System.arraycopy(data, 0, tmp, 0, data.length);
							tdatalen += dataRead;
							data = tmp;
						}
					} while (dataRead != -1);
					in.close();
				} 
				
				catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Message msg = Message.obtain(handler, PotlatchMsg.GET_DATA.getVal());
				Bundle b = new Bundle();
				b.putByteArray(PotlatchConst.get_data, data);
				msg.setData(b);
				handler.sendMessage(msg);
	
			}
			
		}).start();
		
	}

	public static Gift getGiftDataFromCursor(Cursor cursor) {

		long id = cursor.getLong(cursor.getColumnIndex(dataContract.Col._ID));
		String ownerId = cursor.getString(cursor
				.getColumnIndex(dataContract.Col._OWNERID));
		String title = cursor.getString(cursor
				.getColumnIndex(dataContract.Col._TITLE));
		String description = cursor.getString(cursor
				.getColumnIndex(dataContract.Col._DESCRIPTION));
		String type = cursor.getString(cursor
				.getColumnIndex(dataContract.Col._TYPE));

		// construct the returned object
		Gift gift = new Gift(id, Long.parseLong(ownerId),
				title, description, type);

		gift.setEmotionCounter(emotionType.EMOTION_TOUCHED, cursor
				.getInt(cursor
						.getColumnIndex(dataContract.Col._COUNTER_TOUCHED)));
		gift.setEmotionCounter(emotionType.EMOTION_INAPPROPRIATE,
				cursor.getInt(cursor
						.getColumnIndex(dataContract.Col._COUNTER_INPROP)));
		gift.setEmotionCounter(emotionType.EMOTION_OBSCENE, cursor
				.getInt(cursor
						.getColumnIndex(dataContract.Col._COUNTER_OBSCENE)));
		return gift;
	}
	

	private ArrayList<Gift> getGiftDataArrayListFromCursor(Cursor cursor, emotionType eFilter) {
		ArrayList<Gift> rValue = new ArrayList<Gift>();
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					Gift g = getGiftDataFromCursor(cursor);
					if (g.getEmotionCounter(eFilter) == 0)
						rValue.add(g);
				} while (cursor.moveToNext() == true);
			}
		}
		return rValue;
	}
	private ArrayList<Gift> getGiftDataArrayListFromCursor(Cursor cursor) {
		ArrayList<Gift> rValue = new ArrayList<Gift>();
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					rValue.add(getGiftDataFromCursor(cursor));
				} while (cursor.moveToNext() == true);
			}
		}
		return rValue;
	}

	public void queryGiftDataList(final queryDataType type,
			final emotionType eFilter,
			final String selectionArgs[]) throws RemoteException {

		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Cursor result = null;
				ArrayList<Gift> rValue = null;

				String projection[] = dataContract.GIFT_COLUMNS;
				String selection = null;

				switch (type) {
				case QUERY_GIFTBYTITLE:
					selection = dataContract.Col._TITLE + " LIKE ? ";
					break;
				case QUERY_GIFTBYOWNER:
					selection = dataContract.Col._OWNERID + " LIKE ? ";
					break;
				default:
					break;
				}

				try {
					result = mDB.query(dataContract.TABLE_GIFT, projection,
							selection, selectionArgs, null, null, null);

					rValue = new ArrayList<Gift>();

					ArrayList<Gift> glist = null; 
							
					if (eFilter == emotionType.EMOTION_NONE)
						glist = getGiftDataArrayListFromCursor(result);
					else
						glist = getGiftDataArrayListFromCursor(result, eFilter);
					
						rValue.addAll(glist);
						
					Message msg = Message.obtain(handler,
							PotlatchMsg.QUERY_GIFTDATA.getVal());
					Bundle b = new Bundle();
					b.putSerializable(PotlatchConst.query_gift_data,
							rValue);
					msg.setData(b);
					handler.sendMessage(msg);
					result.close();
				}
				catch (Exception e) {
					Log.d(tag, e.getMessage());
				}
			}

		}).start();
	}

	public void queryUserData(final queryDataType type,
			String selectionArgs[]) {
		
		Log.i(tag, "queryUserData");
		if (type != queryDataType.QUERY_USERDATA)
		{
			Log.i(tag, "Invalid queryDataType");
			return;
		}
		
		
		final String giftId = selectionArgs[0];
		final String userId = selectionArgs[1];
		
		final String args[] = new String[] {
				"%"+selectionArgs[0]+"%",
				"%"+selectionArgs[1]+"%"
				};
			
		new Thread(new Runnable()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Cursor result = null;
				UserEmotion rValue = null;

				String projection[] = dataContract.USER_COLUMNS;
				String selection = dataContract.Col._ID + " LIKE ? AND "
						+ dataContract.Col._OWNERID + " LIKE ? ";
							
				try {
					result = mDB.query(dataContract.TABLE_USER, projection,
							selection, args, null, null, null);

					Log.i(tag, "# of row: " + result.getCount());
					if (result != null && result.moveToFirst()) {
						rValue = getUserDataFromCursor(result);
						result.close();
					}
					
					if (rValue == null) {
						ContentValues values = new ContentValues();

						values.put(dataContract.Col._ID, giftId);
						values.put(dataContract.Col._OWNERID, userId);
						values.put(dataContract.Col._TOUCHED, 0);
						values.put(dataContract.Col._INPROP, 0);
						values.put(dataContract.Col._OBSCENE, 0);
						long row = mDB.insert(dataContract.TABLE_USER, null, values);

						result = mDB.query(dataContract.TABLE_USER, projection,
								selection, args, null, null, null);
						
						Log.i(tag, "# of row: " + row + " query result: " + result.getCount());
						
						if (result != null && result.moveToFirst()) {
							rValue = getUserDataFromCursor(result);
						}
					}

					Message msg = Message.obtain(handler,
							PotlatchMsg.QUERY_USERDATA.getVal());
					Bundle b = new Bundle();
					b.putSerializable(PotlatchConst.query_user_data,
							rValue);
					msg.setData(b);
					handler.sendMessage(msg);
					result.close();
				}

				catch (Exception e) {
					Log.d(tag, e.getMessage());
				}
			}
			
		}).start();
		
		
	}

	public void queryTopGiver() {

		Log.i(tag, "queryTopGiver");
		new Thread(new Runnable()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Cursor cursor = null;
				cursor = mDB.query(dataContract.TABLE_GIFT,
						dataContract.TOUCHCOUNT_COLUMNS, null, null, null, null, null);

			//	ArrayList<TouchCountInClient> rValue = new ArrayList<TouchCountInClient>();
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
				b.putSerializable(PotlatchConst.query_top_giver, rValue);
				msg.setData(b);
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	public UserEmotion getUserDataFromCursor(Cursor cursor) {

		UserEmotion user = null;

		if (cursor.getCount() <= 0)
			return user;

		long giftId = cursor.getLong(cursor
				.getColumnIndex(dataContract.Col._ID));
		long ownerId = cursor.getLong(cursor
				.getColumnIndex(dataContract.Col._OWNERID));
		boolean bTouched = cursor.getInt(cursor
				.getColumnIndex(dataContract.Col._TOUCHED)) > 0;
		Log.i(tag,
				"Touched = "
						+ cursor.getInt(cursor
								.getColumnIndex(dataContract.Col._TOUCHED)));
		boolean bInappropriate = cursor.getInt(cursor
				.getColumnIndex(dataContract.Col._INPROP)) > 0;
		Log.i(tag,
				"Inappropriate = "
						+ cursor.getInt(cursor
								.getColumnIndex(dataContract.Col._INPROP)));
		boolean bObscene = cursor.getInt(cursor
				.getColumnIndex(dataContract.Col._OBSCENE)) > 0;
		Log.i(tag,
				"Obscene = "
						+ cursor.getInt(cursor
								.getColumnIndex(dataContract.Col._OBSCENE)));

		// construct the returned object
		user = new UserEmotion(ownerId, giftId);

		user.setEmotion(emotionType.EMOTION_TOUCHED, bTouched);
		user.setEmotion(emotionType.EMOTION_INAPPROPRIATE, bInappropriate);
		user.setEmotion(emotionType.EMOTION_OBSCENE, bObscene);

		return user;
	}

	private List<UserEmotion> getUserDataArrayListFromCursor(Cursor cursor) {
		List<UserEmotion> rValue = new ArrayList<UserEmotion>();
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					rValue.add(getUserDataFromCursor(cursor));
				} while (cursor.moveToNext() == true);
			}
		}
		return rValue;
	}

	public List<UserEmotion> queryUserDataArray() {

		Cursor result = null;
		List<UserEmotion> rValue = null;
		result = mDB.query(dataContract.TABLE_USER, dataContract.USER_COLUMNS,
				null, null, null, null, null);

		rValue = getUserDataArrayListFromCursor(result);
		return rValue;

	}

	public void setUserEmotion(UserEmotion user) {

		Log.i(tag, "setUserEmotion");
		final String userId = Long.toString(user.getId());
		final String giftId = Long.toString(user.getGiftId());
		final int etypeEnabled[] = user.getEmotion();

		if (mDB == null
				|| etypeEnabled == null
				|| (etypeEnabled != null && etypeEnabled.length != emotionType
						.values().length)) {
			return;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.i(tag, "setUserEmotion start");

				ContentValues values = new ContentValues();
				values.put(dataContract.Col._ID, giftId);
				values.put(dataContract.Col._OWNERID, userId);
				values.put(dataContract.Col._TOUCHED,
						etypeEnabled[emotionType.EMOTION_TOUCHED.getVal()]);
				values.put(
						dataContract.Col._INPROP,
						etypeEnabled[emotionType.EMOTION_INAPPROPRIATE.getVal()]);
				values.put(dataContract.Col._OBSCENE,
						etypeEnabled[emotionType.EMOTION_OBSCENE.getVal()]);

				Log.i(tag, "Touched = "
						+ etypeEnabled[emotionType.EMOTION_TOUCHED.getVal()]);
				Log.i(tag,
						"Inappropriate = "
								+ etypeEnabled[emotionType.EMOTION_INAPPROPRIATE
										.getVal()]);
				Log.i(tag, "Obscene = "
						+ etypeEnabled[emotionType.EMOTION_OBSCENE.getVal()]);

				Log.i(tag, "setUserEmotion update");
				String strFilter = dataContract.Col._ID + "=?" + " AND "
						+ dataContract.Col._OWNERID + "=?";

				String sUserId = "%" + userId + "%";
				String sGiftId = "%" + giftId + "%";

				Log.i(tag, "userid: " + sUserId + "giftId: " + sGiftId);
				int row = mDB.update(dataContract.TABLE_USER, values,
						//strFilter, new String [] {sGiftId, sUserId});
						dataContract.Col._ID + "=" + giftId + " AND "
								+ dataContract.Col._OWNERID + "=" + userId,
						null);
				Log.i(tag, "row " + row + " is updated.");
				Log.i(tag, "setUserEmotion end");
			}
		}).start();
	}

	public void setEmotionCounter(final Gift gift,
			final counterEnable cEnabled[]) {

		Log.i(tag, "setEmotionCounter");

		if (mDB == null
				|| cEnabled == null
				|| (cEnabled != null && cEnabled.length != emotionType.values().length)) {
			return;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.i(tag, "setEmotionCounter start");

				String userId = Long.toString(gift.getOwnerId());
				String giftId = Long.toString(gift.getId());

				ContentValues values = new ContentValues();

				for (int i = 0; i < cEnabled.length; i++) {
					if (cEnabled[i] == counterEnable.INCREMENT) {
						gift.incrEmotionCount(i, null);
					} else if (cEnabled[i] == counterEnable.DECREMENT) {
						gift.decrEmotionCount(i, null);
					}
				}
				values.put(dataContract.Col._COUNTER_TOUCHED,
						gift.getEmotionCounter(emotionType.EMOTION_TOUCHED));

				values.put(dataContract.Col._COUNTER_INPROP, 
						gift.getEmotionCounter(emotionType.EMOTION_INAPPROPRIATE));
				values.put(dataContract.Col._COUNTER_OBSCENE,
						gift.getEmotionCounter(emotionType.EMOTION_OBSCENE));

				Log.i(tag, "Touched = " +
					gift.getEmotionCounter(emotionType.EMOTION_TOUCHED));
				Log.i(tag, "Inappropriate = " +
					 gift.getEmotionCounter(emotionType.EMOTION_INAPPROPRIATE));
				Log.i(tag, "Obscene = " +
					 gift.getEmotionCounter(emotionType.EMOTION_OBSCENE));

				Log.i(tag, "setEmotionCounter update");

				String stmt = dataContract.Col._ID + "=" + giftId + " AND "
						+ dataContract.Col._OWNERID + "=" + userId;

				int row = mDB.update(dataContract.TABLE_GIFT, values,
						stmt, null);
				Log.i(tag, "row " + row + " is updated.");
				Log.i(tag, "setEmotionCounter end");
			}
		}).start();

	}

	public long updateTouchCountTable(touchCount tc) {

		Log.i(tag, "updateTouchCountTable");	

		if (mDB == null) {
			return 0;
		}

		String title = tc.getGiftTitle();
		String giftId = Long.toString(tc.getGiftId());
		int giftCount = tc.getCount();
		
		ContentValues values = new ContentValues();
		values.put(dataContract.Col._TITLE,  title);
		values.put(dataContract.Col._ID, giftId);
		values.put(dataContract.Col._COUNTER_TOUCHED, giftCount);
 
		Long row = mDB.insert(dataContract.TABLE_TOUCHCOUNT, null, values);
		
		Log.i(tag, "insert result:" + row);
		if (row != -1)
			return 1L;
		
		String selection = dataContract.Col._ID + " =?" + "and" +
				   dataContract.Col._TITLE + "LIKE ?";
		String args[] = new String[] {
				Long.toString(tc.getGiftId()), 
				tc.getGiftTitle() };
		//
		row = (long) mDB.update(dataContract.TABLE_TOUCHCOUNT, values, selection, args);
		Log.i(tag, "update result:" + row);
	
		return 1L;
	}



	@Override
	public void local_queryTopGiver() {
		// TODO Auto-generated method stub
		queryTopGiver();
		
	}






}
