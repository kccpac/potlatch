package com.potlatchClient.service;

import java.util.ArrayList;

import com.potlatchClient.MainActivity;
import com.potlatchClient.PotlatchConst;
import com.potlatchClient.PotlatchPref;
import com.potlatchClient.PotlatchUtil;

import com.potlatchClient.ServerPotlatchUtil;
import com.potlatchClient.server.touchCount;


import android.app.Service;

import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;

import android.os.IBinder;
import android.os.Message;
import android.util.Log;



public class DownloadService extends Service {

	private final static String tag = DownloadService.class.getCanonicalName();
	private static PotlatchUtil mServerUtil;

	private boolean mStopped;
	private long mUserId;
	public boolean mbSuspend;
	private boolean mDone;
	
	public DownloadService()
	{
		mServerUtil = MainActivity.mUtil;
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {

		Log.i(tag , "onStart");
		super.onStart(intent, startId);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.i(tag, "onStartCommand");
		
		Handler serverhandle = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				Log.i(tag, "Handle server message...");
				final Bundle b = msg.getData();		
				if (msg.what != PotlatchConst.MESSAGE_QUERY_TOPGIVER && b == null)
				{
					Log.i(tag, "done...");
					return;
				}
				new Thread(new Runnable() {
				
					@Override
					public void run()
					{
						Log.i(tag, "Load the touchcount data into content provider");
						ArrayList<touchCount> data = (ArrayList<touchCount>) b.getSerializable(PotlatchConst.query_top_giver);
						
						if (data == null) return;
						
						Log.i(tag, "data size " + data.size());
						
						for (int i=0; i<data.size(); i++)
						{
							if (mbSuspend == true) break;
							Log.i(tag, "data is " + data.get(i));							
							mServerUtil.updateTouchCountTable(data.get(i));
						}
					}
				}).start();
			}
		};

		mServerUtil = new ServerPotlatchUtil(this);
		mServerUtil.setHandler(serverhandle);

		mUserId = intent.getLongExtra("userId", -1);
		mStopped = false;
		mbSuspend = false;
		mDone = false;
		new Thread(new Runnable()
		{
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.i(tag, "onStartCommand run");
				while (mStopped == false)
				{
					
					Log.i(tag, "onStartCommand: begin execute queryTopGiver");
					
					mServerUtil.queryTopGiver();
					
					Log.i(tag, "onStartCommand: after execute queryTopGiver");
					
					PotlatchPref pref = mServerUtil.getPreference();
					Log.i(tag, "onStartCommand: refresh rate= " + pref.getRefreshRate());
					
					long endTime = System.currentTimeMillis() + pref.getRefreshRate()*60000;
					while (System.currentTimeMillis() < endTime)
					{
						synchronized (this)
						{
							try
							{
								wait(endTime - System.currentTimeMillis());
							} 
							catch (Exception e) {
							}
						}
					}
					
					Log.i(tag, "onStartCommand: wake up from sleep");
			
				}
				mDone = true;
				Log.i(tag, "onStartCommand: done");
			}
			
		}).start();
	

		// We want this service to continue running until it is explicitly
	    // stopped, so return sticky.
		Log.i(tag , "onStart");
	    return START_STICKY;
	}
	
	
	@Override
	public boolean onUnbind (Intent intent)
	{
		Log.i(tag , "onUnbind");
		return true;
	}
	
	@Override
    public void onDestroy() {
        // Cancel the persistent notification.
	 	Log.i(tag , "onDestroy");
	 	mStopped = true;
	 	while (mDone == false);
	 	
 		super.onDestroy();
        // Tell the user we stopped.
    }


}
