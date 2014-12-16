package com.potlatchClient.provider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class dataStorage extends ContentProvider {

	private static String tag = dataStorage.class.getCanonicalName();
	private SQLiteDatabase db;
	

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		Log.i(tag , "insert to " + uri.getLastPathSegment());
		Long id = values.getAsLong(dataContract.Col._ID);
		Uri dataUri = Uri.parse(values.getAsString(dataContract.Col._SRC_URL));

		Uri insertedID = ContentUris.withAppendedId(uri,  id);

		OutputStream stream = null;
		InputStream in = null;
		try {
			stream = getContext().getContentResolver().openOutputStream(insertedID);				

			Bitmap bitmap = null;

			in = getContext().getContentResolver().openInputStream(dataUri);			
			bitmap = BitmapFactory.decodeStream(in);
			
			if (bitmap != null)
			{
				if (uri.equals(dataContract.THUMBNAIL_CONTENT_URI))
					bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth()/4,  bitmap.getHeight()/4);
				bitmap.compress(CompressFormat.JPEG, 70, stream);
			}		
			in.close();
			stream.close();
		}
		catch(FileNotFoundException e)
		{
			Log.d(tag, e.getMessage());
		}
		
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        getContext().getContentResolver().notifyChange(insertedID, null);
		return insertedID;
	}
	
	@Override
	synchronized public boolean onCreate() {
		// TODO Auto-generated method stub
		Log.i(tag , "onCreate");
		Context context = this.getContext();
		storageDBHelper dbHelper = new storageDBHelper(context);
		db = dbHelper.getWritableDatabase();
		if (db != null)
		{
			return true;
		}
		return false;
	}

	@Override
	public Cursor query(Uri table, String[] columns, String selection, String[] selectionArgs,
			String orderBy) {
		// TODO Auto-generated method stub
		Log.i(tag , "query");
		db.query(table.toString(), columns, selection, selectionArgs, null, null, orderBy);
		return null;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		Log.i(tag , "update");
		
		return 0;
	}
	

	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode)
	        throws FileNotFoundException {


	    // path to /data/data/yourapp/app_data/dir
	    String dir = dataContract.BASE_PATH;
	    if (uri != null && uri.getPathSegments().size() > 0)
	    {
	    	dir += "/" + uri.getPathSegments().get(0);
	    }

	    File root = new File(Environment.getExternalStorageDirectory(), 
	    		dir);
	    root.mkdirs();

	    long id = ContentUris.parseId(uri);
	    File path = new File(root, String.valueOf(id));

	    int imode = 0;
	    if (mode.contains("w")) {
	        imode |= ParcelFileDescriptor.MODE_WRITE_ONLY;
	        if (!path.exists()) {
	            try {
	                path.createNewFile();
	            } catch (IOException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }
	    }
	    if (mode.contains("r"))
	        imode |= ParcelFileDescriptor.MODE_READ_ONLY;
	    if (mode.contains("+"))
	        imode |= ParcelFileDescriptor.MODE_APPEND;

	    return ParcelFileDescriptor.open(path, imode);
	}

}
