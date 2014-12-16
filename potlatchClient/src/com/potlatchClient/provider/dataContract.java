package com.potlatchClient.provider;

import android.content.ContentResolver;
import android.net.Uri;

public class dataContract {

	public static final String TABLE_GIFT = "gift";
	public static final String TABLE_TOUCHCOUNT = "touchcount";
	public static final String TABLE_USER = "user";
	
	public static final String SOURCE_PATH = "data";
	public static final String THUMBNAIL_PATH = "thumbnail";
	
	
	private static final Uri BASE_URI = Uri
			.parse("content://com.potlatchClient.provider.dataStorage/");

	public static final String BASE_PATH="/Android/data/" + BASE_URI.getHost();
	
	// URI for all content stored as story entity
	public static final Uri SOURCE_CONTENT_URI = BASE_URI.buildUpon()
			.appendPath(SOURCE_PATH).build();
	
	public static final Uri THUMBNAIL_CONTENT_URI = BASE_URI.buildUpon()
			.appendPath(THUMBNAIL_PATH).build();
	
	// The URI for this table.
	public static final Uri GIFT_URI = Uri.withAppendedPath(BASE_URI, TABLE_GIFT);

	public static final Uri TOUCHCOUNT_URI = Uri.withAppendedPath(BASE_URI, TABLE_TOUCHCOUNT);

	public static final Uri USER_URI = Uri.withAppendedPath(BASE_URI, TABLE_USER);

	// Mime type for a directory of data items
	public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/dataStorage.data.text";

	// Mime type for a single data item
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/dataStorage.data.text";

	// All columns of this table
	public static class Col {
		public static final String _ID = "id"; 
		public static final String _OWNERID = "ownerid";
		public static final String _TITLE = "title";
		public static final String _DESCRIPTION = "description";
		public static final String _TYPE = "type"; 
		public static final String _SRC_URL ="src_url"; 
		public static final String _THUMB_URL ="thumb_url";
		public static final String _COUNTER_TOUCHED= "counter_touched";
		public static final String _COUNTER_INPROP = "counter_inprop";
		public static final String _COUNTER_OBSCENE = "counter_obscene";
		public static final String _TOUCHED = "touched";
		public static final String _INPROP = "inappropriate";
		public static final String _OBSCENE = "obscene";
	}

	public static final String[] GIFT_COLUMNS = {
											Col._OWNERID,
											Col._ID, 
											Col._TITLE, 
											Col._DESCRIPTION,
											Col._TYPE,
											Col._COUNTER_TOUCHED,
											Col._COUNTER_INPROP,
											Col._COUNTER_OBSCENE,
											Col._SRC_URL,
											Col._THUMB_URL 
											};
	
	public static final String[] TITLESEARCH_COLUMNS = {Col._TITLE, Col._ID};
	
	public static final String[] TOUCHCOUNT_COLUMNS = { Col._ID,  Col._TITLE, Col._COUNTER_TOUCHED};

	public static final String[] USER_COLUMNS = { 
											Col._ID,
											Col._OWNERID,
											Col._TOUCHED,
											Col._INPROP,
											Col._OBSCENE
											};	

}
