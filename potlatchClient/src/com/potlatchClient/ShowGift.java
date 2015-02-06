package com.potlatchClient;

//import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

public class ShowGift extends Activity {

	private final static String tag = ShowGift.class.getCanonicalName();
	private long mUserId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(tag, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showgift);
	
		mUserId = getIntent().getLongExtra("userId", -1);
		
		if (savedInstanceState == null) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			getFragmentManager().beginTransaction()
					.add(R.id.container, fragment, "Test_tag").commit();
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
	
	@Override 
    public void onResume()
    {
		Log.i(tag, "onResume");	
		super.onResume();
    }
	
	public long getUserId()
	{
		Log.i(tag, "getUserId");
		return mUserId;
	}
	
	@Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		Log.i(tag, "onKeyDown");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            event.startTracking();
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(final int keyCode, final KeyEvent event) {
    	Log.i(tag, "onKeyLongPress");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }
}
