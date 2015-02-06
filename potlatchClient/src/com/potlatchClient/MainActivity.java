package com.potlatchClient;


//import java.util.ArrayList;

//import com.potlatchClient.server.Gift;//InClient;
import com.potlatchClient.server.touchCount;//InClient;
import com.potlatchClient.server.Gift;
import com.potlatchClient.server.accountType;
import com.potlatchClient.service.DownloadService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainActivity extends Activity {

	public class ActivityObject {
		private String name;
		private String class_name;
		private int requestCode;
		ActivityObject(String name, String class_name, int requestCode)
		{
			this.name = name;
			this.class_name = class_name;
			this.requestCode = requestCode;
		}
		public String getClassname() { return class_name; }
		public String getName() { return name; }
		public int getRequestCode() { return requestCode; }

	}

	private final String tag = MainActivity.class.getName();
 
	public final ActivityObject subActMap[] = { 
			new ActivityObject("Create a gift", CreateGift.class.getCanonicalName(), requestCode.INTENT_ACTIVITY_ONE.getVal() ),
			new ActivityObject("Show a gift", ShowGift.class.getCanonicalName(), requestCode.INTENT_ACTIVITY_TWO.getVal()),
			new ActivityObject("Top giver list", ShowTopGiver.class.getCanonicalName(), requestCode.INTENT_ACTIVITY_THREE.getVal())
			};
	
	private long mUserId;

	private accountType mAccountType;

	private Handler mHandler;

	public static PotlatchUtil mUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mUserId = getIntent().getLongExtra("userId", -1);
		mAccountType = (mUserId != -1)? accountType.ACCOUNT_USER: accountType.ACCOUNT_ANONYMOUS;

		mHandler = new Handler () {
			
			@Override 
			public void handleMessage(Message msg)
			{
				Log.i(tag, "handleMessage");
				Bundle b = msg.getData();				
				if (msg.what == PotlatchConst.MESSAGE_ADD_GIFT && b != null)
				{					
					String status = b.getString(PotlatchConst.add_gift);
					Log.i(tag, "The result of AddGift is " + status);
				}
				
			}
		};
		
		
		mUtil = (PotlatchUtil) new LocalPotlatchUtil(getApplicationContext());
		//mUtil = (PotlatchUtil) new ServerPotlatchUtil(getApplicationContext());
		Log.i(tag, "accountType " + mAccountType + " " + mAccountType.getVal());
	
		setContentView(R.layout.activity_main);

		ListView view = (ListView) findViewById(R.id.listView1);
		String actName[] = new String[subActMap.length];
		 
		for (int i=0; i<subActMap.length; i++)
		{
			actName[i] = subActMap[i].getName();
		}		

		ArrayAdapter <String> adapter = new ArrayAdapter <String> (this, android.R.layout.simple_list_item_1, actName);

		view.setAdapter(adapter);		
		
		PotlatchPref pref = mUtil.getPreference();
				
		Log.i(tag, "refresh rate: " + pref.getRefreshRate());
		
		view.setOnItemClickListener (new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				Log.i(tag, "click " + parent.getItemAtPosition(position).toString());

				Intent intent = new Intent();
				intent.setClassName(getApplicationContext(), subActMap[position].getClassname());
				intent.putExtra("userId", mUserId);
				startActivityForResult(intent, getRequestCode(subActMap[position].getName()));				
			}			
		});
	}
	
	private int getRequestCode(String activityName)
	{
		for (int i=0; i<subActMap.length; i++)
		{
			if (activityName.equals(subActMap[i].getName()))
				return subActMap[i].getRequestCode();
		}
		return requestCode.INTENT_ACTIVITY_UNKNOWN.getVal();
	}

    @Override
    public void onPause()
    {
    	Log.i(tag, "onPause");

    	super.onPause();
    }
    
	@Override 
    public void onResume()
    {
    	super.onResume();
     }
	
	@Override
	public void onDestroy()
	{
		Log.i(tag, "onDestroy");

		super.onDestroy();
		
		Intent intent = new Intent();		
		intent.setClass(this, DownloadService.class);
		stopService(intent);
	}
	@Override
	public void onActivityResult(int request_code, int result_code, Intent data)
	{
		Log.i(tag, "onActivityResult");
		super.onActivityResult(request_code, result_code, data);
		
		if (result_code == RESULT_OK)
		{
			if (request_code == requestCode.INTENT_ACTIVITY_ONE.getVal())
			{
				Bundle bundle = data.getExtras();
				
				if (bundle != null)
				{
					String title = bundle.getString("Title");//, imageTitle.getText());
					String description = bundle.getString("Description");
					String url = bundle.getString("ImageUrl");					 
											
					Gift v = new Gift(mUserId, title, description, "jpg");
					v.setSUrl(url);
					mUtil.setHandler(mHandler);
					mUtil.addGift(v);

				}

				Log.i(tag, "INTENT_ACTIVITY_ONE succeed");
			} 
			else if (request_code == requestCode.INTENT_ACTIVITY_TWO.getVal())
			{
				Log.i(tag, "INTENT_ACTIVITY_TWO succeed");
			}
			else if (request_code == requestCode.INTENT_ACTIVITY_THREE.getVal())
			{
				Log.i(tag, "INTENT_ACTIVITY_THREE succeed");
			}
		}
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
