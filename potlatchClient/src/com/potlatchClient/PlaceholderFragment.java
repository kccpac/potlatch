package com.potlatchClient;

import java.io.Serializable;
import java.util.ArrayList;

//import com.potlatchClient.provider.GiftInClient;
import com.potlatchClient.server.Gift;
import com.potlatchClient.server.queryDataType;
import android.app.ListFragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
//import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;


/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends ListFragment {

	protected EditText giftlistFilter;
	
	public static final String tag = PlaceholderFragment.class.getCanonicalName();
	
	private PotlatchUtil mUtil;
	
	private ArrayList<Gift> giftData;
	private GiftDataArrayAdapter aa;

	public PlaceholderFragment() {
		mUtil = MainActivity.mUtil;
	}
	
	public void onCreate(Bundle savedInstanceState) {
		Log.i(tag, "onCreate thread id: " + Thread.currentThread().getId());
		super.onCreate(savedInstanceState);	

		giftData = new ArrayList<Gift>();
		
		setRetainInstance(true);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		Log.i(tag, "onTextChanged thread id: " + Thread.currentThread().getId());
		
		View rootView = inflater.inflate(R.layout.activity_showgift, container,
				false);

		giftlistFilter = (EditText) rootView.findViewById(R.id.gift_listview_tags_filter);
		
		
		giftlistFilter.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				Log.i(tag, "onTextChanged thread id: " + Thread.currentThread().getId());
				queryGiftData();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				Log.i(tag, "beforeTextChanged");
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				Log.i(tag, "afterTextChanged");
			}
		});
		


		return rootView;
	}
	
	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	public void onActivityCreated(Bundle savedInstanceState) {
		// create the custom array adapter that will make the custom row
		// layouts
		super.onActivityCreated(savedInstanceState);
		Log.i(tag, "onActivityCreated thread id: " + Thread.currentThread().getId());
		aa = new GiftDataArrayAdapter(getActivity(),
				R.layout.gift_listview_custom_row, giftData);

		Handler handle = new Handler() {
			
			@Override
			public void handleMessage(Message msg) {

				Log.i(tag, "handleMessage thread id: " + Thread.currentThread().getId());
				final Bundle b = msg.getData();
				if (msg.what == PotlatchConst.MESSAGE_QUERY_GIFTDATA)
				{					
					
					new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Log.i(tag, "handleMessage new thread id: " + Thread.currentThread().getId());
							ArrayList<Gift> gs = (ArrayList<Gift>) b.getSerializable(PotlatchConst.query_gift_data);
							updateGiftdata(gs);
						}						
			
					}).start();			
				}
				
			}
		};

		mUtil.setHandler(handle);
		
		// update the back end data.
		queryGiftData();

		setListAdapter(aa);		

	}
	
	@Override 
    public void onResume()
    {
		Log.i(tag, "onResume");	
		super.onResume();

    }
	
	public void updateGiftdata(final ArrayList<Gift> gs)
	{
		Log.i(tag, "updateGiftdata thread id: " + Thread.currentThread().getId());
		getActivity().runOnUiThread(new Runnable()
		{

			@Override
			public void run() {
				Log.i(tag, "runOnUiThread thread id: " + Thread.currentThread().getId());
				// TODO Auto-generated method stub
				giftData.addAll(gs);				
				aa.notifyDataSetChanged();
				
			}
			
		});
		
	}
	
	public void queryGiftData() {

		Log.i(tag, "queryGiftData thread id: " + Thread.currentThread().getId());
		try {
			giftData.clear();

			String filterWord = giftlistFilter.getText().toString();
		
			PotlatchPref pref = mUtil.getPreference();
			Log.i(tag, "Flagged gift to block: " + pref.getEType());
			// create String that will match with 'like' in query
			filterWord = "%" + filterWord + "%";
			mUtil.queryGiftDataList(
					queryDataType.QUERY_GIFTBYTITLE, 
					pref.getEType(),
					new String[] { filterWord });
		} catch (Exception e) {
			Log.d(tag,
					"Error connecting to Content Provider" + e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.ListFragment#onListItemClick(android.widget.ListView
	 * , android.view.View, int, long)
	 */
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.i(tag, "onListItemClick");
		Log.i(tag,	"position: " + position + " id = " + (giftData.get(position)).getId());

		Intent intent = new Intent();
		intent.setClass(getActivity().getApplicationContext(), DisplayGift.class);
		intent.putExtra("gift", (Serializable) giftData.get(position));
		intent.putExtra("userId", ((ShowGift)getActivity()).getUserId());
		startActivityForResult(intent, 0);
		
	}


}
