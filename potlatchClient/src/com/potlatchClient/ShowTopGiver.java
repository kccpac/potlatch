package com.potlatchClient;


import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

//import com.potlatchClient.provider.TouchCountInClient;
import com.potlatchClient.server.touchCount;


public class ShowTopGiver extends Activity  {

	private final String tag = ShowTopGiver.class.getName();
//	private loaderServiceImpl loaderService = null;
//	private IAlarmService alarmService = null;
	private ListAdapter mAdapter;
	private PotlatchUtil mUtil;
	private TableLayout mtlayout;
	
	//static final String[] TOUCHCOUNT_ROWS = new String[] { dataContract.Col.TOUCHCOUNT_TITLE, 
	//	dataContract.TOUCHCOUNT_COUNTER};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(tag, "onCreate  pid=" + android.os.Process.myPid() + 
				" tid=" + (int) Thread.currentThread().getId());	
		setContentView(R.layout.activity_show_topgiver);
		
		mUtil = MainActivity.mUtil;
		
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg)
			{
				Log.i(tag, "handleMessage");
				Bundle b = msg.getData();				
				if (msg.what == PotlatchConst.MESSAGE_QUERY_TOPGIVER && b != null)
				{					
					ArrayList<touchCount> data = (ArrayList<touchCount>)
							b.getSerializable(PotlatchConst.query_top_giver);
					
					showtable(data);
				}
			}
		};
		
		mUtil.setHandler(handler);
		mtlayout = (TableLayout) findViewById(R.id.tableLayout);

		mUtil.local_queryTopGiver();
	}
	
	private void showtable(ArrayList<touchCount> tlist)
	{
		int i=0;
		mtlayout.addView(genTableRow(new String[] { "Gift Title", "Touched #" }));
		for (i=0; i<tlist.size(); i++)
		{
			touchCount tc = tlist.get(i);
			final String elem[] = new String[] {					
				tc.getGiftTitle(),
				Integer.toString(tc.getCount()) 
			};
			mtlayout.addView(genTableRow(elem));
		}	
	}
	private TableRow genTableRow(String elems[])
	{
		TableRow row = new TableRow(this);
		int i=0;
		
		for (i=0; i<elems.length; i++)
		{
			TextView col = new TextView(this);
			col.setText(elems[i]);
			col.setTextColor(Color.BLUE);
			col.setPadding(5, 2, 5, 2);
	        row.addView(col);
		}
		
		return row;
	}
	
	@Override 
    public void onResume()
    {
    	Log.i(tag, "onResume pid=" + android.os.Process.myPid() + 
				" tid=" + (int) Thread.currentThread().getId());	
		super.onResume();
    }
    
    @Override
    public void onPause()
    {
    	Log.i(tag, "onPause");
    	super.onPause();
    }
    
    @Override 
    public void onDestroy()
    {
    	Log.i(tag, "onDestroy");

    	super.onDestroy();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} else if (id == R.id.action_preference) {
			Intent i = new Intent(this, SettingsActivity.class);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
