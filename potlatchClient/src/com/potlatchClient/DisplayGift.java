package com.potlatchClient;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;



import com.potlatchClient.provider.GiftInClient;
import com.potlatchClient.provider.UserEmotionInClient;
import com.potlatchClient.server.emotionType;
import com.potlatchClient.server.queryDataType;

public class DisplayGift extends Activity {

	public final String tag = DisplayGift.class.getName();
	
	protected TextView giftTitle;
	protected TextView descriptionView;
	protected ImageView dataImageView;	

	protected ImageButton mBtn[] = new ImageButton[emotionType.values().length];
	protected int mBtnId[] = { -1, R.id.imageButton1, R.id.imageButton2, R.id.imageButton3};

	protected GiftInClient mGift;
	private PotlatchUtil mUtil;
	private long mUserId;
	private UserEmotionInClient mUser;
	
	private counterEnable [] mCounterEnable = new counterEnable[emotionType.values().length];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(tag, "onCreate");
		super.onCreate(savedInstanceState);
		
		mGift = getIntent().getParcelableExtra("gift");
		mUserId = getIntent().getLongExtra("userId", -1);
	
		setContentView(R.layout.activity_display_gift);		
	
		giftTitle = (TextView)findViewById(R.id.TitleView1);
		descriptionView = (TextView)findViewById(R.id.DescriptionView1);
		dataImageView = (ImageView)findViewById(R.id.imageView1);
	

		giftTitle.setText(mGift.getTitle());
		
		mUtil = MainActivity.mUtil;
		
		Handler handle = new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
			
				Log.i(tag, "Handler");
			//	PotlatchMsg potlatchMsg = null;
				Bundle b = msg.getData();
				
				if (msg.what == PotlatchConst.MESSAGE_GET_DATA)
				{					
					byte [] data = b.getByteArray(PotlatchConst.get_data);
					showImage(data);
				}
				else if (msg.what == PotlatchConst.MESSAGE_QUERY_USERDATA)
				{
					UserEmotionInClient userdata = b.getParcelable(PotlatchConst.query_user_data);
					updateEmotionButton(userdata);
				}
			}
		};
		
		mUtil.setHandler(handle);
		mUtil.getData(Long.toString(mGift.getId()), "data");

		descriptionView.setText(mGift.getDescription());
		
		for (int i=1; i<mBtn.length; i++)
		{
			mBtn[i] = (ImageButton) findViewById(mBtnId[i]);
			mBtn[i].setId(i);
		}
		
		mUtil.queryUserData(queryDataType.QUERY_USERDATA,
				new String[] {Long.toString(mGift.getId()), Long.toString(mUserId)});
		
		final emotionType etype[] = emotionType.values();

		for (int i=1; i<mBtn.length; i++)
		{
			mBtn[i].setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (mUser == null)
					{
						Log.i(tag, "updateEmotionState null mUser");
						return;
					}
					emotionType type = etype[arg0.getId()];
					float alpha = 0.2F; 
					alpha = (arg0.getAlpha() == 0.2F) ? 1.0F: 0.2F;
					if (type != emotionType.EMOTION_NONE) {
						mCounterEnable[type.getVal()] = (arg0.getAlpha() == 0.2F) ? 
								counterEnable.DECREMENT:
								counterEnable.INCREMENT;	
					}
					
					mUser.setEmotion(type, alpha == 0.2F);
					arg0.setAlpha(alpha);
				//	updateEmotionState(type);
				}				
			}); 
		}
		
		Button BtnDone = (Button) findViewById(R.id.button1);
		
		BtnDone.setOnClickListener(new OnClickListener()
		{			
			@Override
			public void onClick(View arg0) {
				Log.i(tag, "done");
				mUtil.setUserEmotion(mUser);
				mUtil.setEmotionCounter(mGift, mCounterEnable);
				Intent intent = new Intent();		
				setResult(RESULT_OK, intent);				
				finish();
			}
		});
		
		for (int i=0; i<mCounterEnable.length; i++)
		{
			mCounterEnable[i] = counterEnable.NONE;
		}
	
	}
	/*
	private void setEmotion(emotionType etype)
	{
		
		if (mUserId == -1 || etype == emotionType.EMOTION_UNKNOWN)
			return;
		
		emotionType emotion[] = mUtil.queryUserEmotion(mUserId);
//		boolean emotion[] = null;
		boolean bSet = false;
		
		bSet = (emotion[etype.getVal()] == true) ? false: true;

		mUtil.setUserEmotion(mUserId, etype, bSet);
		
	}
*/
	
	protected void showImage(byte data[])
	{
		Log.i(tag, "showImage");
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
	
		DisplayMetrics dm = getResources().getDisplayMetrics();
		int max_height = dm.heightPixels * 4 / 5;
		int max_width = dm.widthPixels * 4 / 5;
		int bHeight = bitmap.getHeight();
		int bWidth = bitmap.getWidth();
		float ratio = 1.0F;
		if (bHeight >=  max_height || bWidth >= max_width)
		{
			double wRatio = 1.0*bWidth/max_width;
			double hRatio = 1.0*bHeight/max_height;
			
			ratio = (float)((wRatio > hRatio) ? Math.ceil(wRatio + 0.5): Math.ceil(hRatio + 0.5));
			
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, (int)(bWidth/ratio), (int)(bHeight/ratio));
		}

		Log.i(tag, "metrics w= " + dm.widthPixels + " h= " + dm.heightPixels);
		Log.i(tag, "old bitmap (w, h) = (" + bWidth + ", " + bHeight + ")");
		Log.i(tag, "new bitmap (w, h) = (" + bitmap.getWidth()+ ", " + bitmap.getHeight() + ")");
		
		dataImageView.setImageBitmap(bitmap);
	}
	
	protected void updateEmotionButton(final UserEmotionInClient userdata)
	{
		if (userdata == null)
			return;
		
		if (userdata.getId() == -1)
		{
			mBtn[emotionType.EMOTION_TOUCHED.getVal()].setVisibility(View.INVISIBLE);
			mBtn[emotionType.EMOTION_INAPPROPRIATE.getVal()].setVisibility(View.INVISIBLE);
			mBtn[emotionType.EMOTION_OBSCENE.getVal()].setVisibility(View.INVISIBLE);
		}
		else
		{
			Log.i(tag, "val " + emotionType.EMOTION_TOUCHED.getVal()); 
			mBtn[emotionType.EMOTION_TOUCHED.getVal()].setAlpha(
					userdata.getEmotion(emotionType.EMOTION_TOUCHED)==1 ? 0.2F: 1.0F);
			mBtn[emotionType.EMOTION_INAPPROPRIATE.getVal()].setAlpha(
					userdata.getEmotion(emotionType.EMOTION_INAPPROPRIATE)== 1 ? 0.2F: 1.0F);
			mBtn[emotionType.EMOTION_OBSCENE.getVal()].setAlpha(		
					userdata.getEmotion(emotionType.EMOTION_OBSCENE) == 1 ? 0.2F: 1.0F);
		}
		mUser = userdata;

	}

	@Override
	public void onResume()
	{
		Log.i(tag, "onResume");
		super.onResume();

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
		}
		return super.onOptionsItemSelected(item);
	}
	
}
