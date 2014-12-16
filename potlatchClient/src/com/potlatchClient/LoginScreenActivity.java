package com.potlatchClient;


import java.util.concurrent.Callable;

import com.potlatchClient.server.PotlatchSvcApi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 
 * This application uses ButterKnife. AndroidStudio has better support for
 * ButterKnife than Eclipse, but Eclipse was used for consistency with the other
 * courses in the series. If you have trouble getting the login button to work,
 * please follow these directions to enable annotation processing for this
 * Eclipse project:
 * 
 * http://jakewharton.github.io/butterknife/ide-eclipse.html
 * 
 */
public class LoginScreenActivity extends Activity {

	protected static final String tag = LoginScreenActivity.class.getName();

	protected EditText userName_;
	protected EditText password_;
	protected EditText server_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_screen);

		userName_ = (EditText)findViewById(R.id.userName);
		password_ = (EditText)findViewById(R.id.password);
		server_ = (EditText)findViewById(R.id.server);

		Button btn = (Button) findViewById(R.id.loginButton);
		
		btn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final String user = userName_.getText().toString();
				String pass = password_.getText().toString();
				String server = server_.getText().toString();
				
				final PotlatchSvcApi svc = PotlatchSvc.init(server, user, pass);

				CallableTask.invoke(new Callable<Long>() {

					@Override
					public Long call() throws Exception {
						// TODO Auto-generated method stub
						long userId = svc.getUserId(user);
						return userId;
					}
				}, new TaskCallback<Long>() {

					@Override
					public void success(Long result) {
						// OAuth 2.0 grant was successful and we
						// can talk to the server, open up the video listing
						Intent intent = new Intent(
								LoginScreenActivity.this,
								MainActivity.class);
						intent.putExtra("userId", result);
						startActivity(intent);
					}

					@Override
					public void error(Exception e) {
						Log.e(tag, "Error logging in via OAuth.", e);
						
						Toast.makeText(
								LoginScreenActivity.this,
								"Login failed, check your Internet connection and credentials.",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
			
		}
		);
	}
	
	public boolean isOnline() {
	    
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
		return false;
	}
}
