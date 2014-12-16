package com.potlatchClient;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CreateGift extends Activity {

	public static final String tag = CreateGift.class.getCanonicalName();

	public static final int MEDIA_TYPE_IMAGE = 1;

	private ImageView loadImage;
	private ImageView captureImage;
	private Button btnSubmit;
	private TextView imageURL;

	private TextView imageTitle;

	private TextView imageDescription;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creategift);

		imageURL = (TextView) findViewById(R.id.TextLine4);
		imageTitle = (TextView) findViewById(R.id.editText1);
		imageDescription = (TextView) findViewById(R.id.editText2);
		loadImage = (ImageView) findViewById(R.id.imageView1);		
		loadImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.i(tag, "onClick loadImage from device");

				Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);


				intent.addCategory(Intent.CATEGORY_OPENABLE);

				intent.setType("image/*");

				startActivityForResult(intent, requestCode.LOAD_IMAGE_REQUEST_CODE.getVal());
			}
		});

		captureImage = (ImageView) findViewById(R.id.imageView2);
		captureImage.setOnClickListener(new OnClickListener() {
			Uri getOutputMediaFileUri(int type) {
				Uri uri = null;
				try {
					uri = Uri.fromFile(getOutputMediaFile(type));
				}
				catch(NullPointerException e)
				{
					Log.i(tag, "getOutputMediaFileUri: fail to open the file");
				}
				return uri;
			}

			/** Create a File for saving an image or video */
			File getOutputMediaFile(int type) {
				// To be safe, you should check that the SDCard is mounted
				// using Environment.getExternalStorageState() before doing
				// this.

				File mediaStorageDir = new File(
						Environment
								.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
						"adapter");
				// This location works best if you want the created images to be
				// shared
				// between applications and persist after your app has been
				// uninstalled.

				// Create the storage directory if it does not exist
				if (mediaStorageDir.exists() == false && mediaStorageDir.isDirectory() == false) {
					if (mediaStorageDir.mkdirs() == false) {
						Log.d(tag, "failed to create directory");
						return null;
					}
				}

				// Create a media file name
				String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
						.format(new Date());
				File mediaFile;
				if (type == MEDIA_TYPE_IMAGE) {
					mediaFile = new File(mediaStorageDir.getPath()
							+ File.separator + "IMG_" + timeStamp + ".jpg");
				} else {
					return null;
				}

				return mediaFile;
			}

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Uri fileUri;

				Log.i(tag, "captureImage onClick");

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				

				fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
				if (fileUri != null)
				{
					intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image	file name
					imageURL.setText(fileUri.toString());
				}
				
				// start the image capture Intent
				startActivityForResult(intent, requestCode.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE.getVal());

			}

		});

		btnSubmit = (Button) findViewById(R.id.button1);

		btnSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.i(tag, "Submit onClick");
			
				Intent intent = new Intent();

				Bundle bundle = new Bundle();
				bundle.putString("Title", imageTitle.getText().toString());
				bundle.putString("Description", imageDescription.getText().toString());
				bundle.putString("ImageUrl", imageURL.getText().toString());
				intent.putExtras(bundle);
				
				setResult(RESULT_OK, intent);	
				finish();

			}
		});

	}

	@Override
	public void onResume() {
		Log.i(tag, "onResume Enter");
		super.onResume();
	}

	@Override
	public void onActivityResult(int rc, int resultCode, Intent result) {
		Log.i(tag, "onActivityResult Enter");

		super.onActivityResult(rc, resultCode, result);
		
		if (resultCode != RESULT_OK)
			return;

		if (rc == requestCode.LOAD_IMAGE_REQUEST_CODE.getVal()) {
			Log.i(tag, "loadImage");
			Uri uri = null;
			if (result != null) {
				uri = result.getData();
				Log.i(tag, "Uri: " + uri.toString());
				imageURL.setText(uri.toString());
				imageURL.setVisibility(View.VISIBLE);

			}
		} else if (rc == requestCode.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE.getVal()) {
			Log.i(tag, "captureImage");
			{

				Log.i(tag, "Uri: " + imageURL.getText().toString());
				imageURL.setVisibility(View.VISIBLE);
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
		}
		return super.onOptionsItemSelected(item);
	}

}
