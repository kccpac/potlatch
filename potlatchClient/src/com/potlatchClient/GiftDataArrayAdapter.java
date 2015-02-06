package com.potlatchClient;

import java.util.List;

import com.potlatchClient.provider.GiftInClient;
import com.potlatchClient.server.Gift;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class GiftDataArrayAdapter extends ArrayAdapter<Gift> {

    private static final String LOG_TAG = GiftDataArrayAdapter.class
            .getCanonicalName();

    int resource;

    public GiftDataArrayAdapter(Context _context, int _resource,
            List<Gift> _items) {
        super(_context, _resource, _items);
        Log.d(LOG_TAG, "constructor()");
        resource = _resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(LOG_TAG, "getView()");
        LinearLayout todoView = null;
        try {
            Gift item = getItem(position);

            long id = item.getId();
            String title = item.getTitle();

            if (convertView == null) {
                todoView = new LinearLayout(getContext());
                String inflater = Context.LAYOUT_INFLATER_SERVICE;
                LayoutInflater vi = (LayoutInflater) getContext()
                        .getSystemService(inflater);
                vi.inflate(resource, todoView, true);
            } else {
                todoView = (LinearLayout) convertView;
            }

            TextView KEY_IDTV = (TextView) todoView
            		.findViewById(R.id.gift_listview_custom_row_KEY_ID_textView);
            
            TextView titleTV = (TextView) todoView
                    .findViewById(R.id.gift_listview_custom_row_title_textView);

            KEY_IDTV.setText("" + id);
            titleTV.setText("" + title);
        
        } catch (Exception e) {
            Toast.makeText(getContext(),
                    "exception in ArrayAdpter: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
        return todoView;
    }

}

