package com.mypro.paopao;

import java.util.ArrayList;

import com.base.XmppConnection;
import com.mypro.paopao.FriendsListActivity.FriendData;
import com.mypro.paopao.FriendsListActivity.ViewHolder;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FindFriend extends Activity {
	ListView listview = null;
	EditText edit = null;
	MyAdapter adapter = null;

	class FriendData {
		String id;
		Drawable avtor;
	}

	ArrayList<FriendData> data = new ArrayList<FriendData>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_findfriend);
		listview = (ListView) findViewById(R.id.findfriend_listview);
		edit = (EditText) findViewById(R.id.findfriend_input);
		adapter = new MyAdapter();
		listview.setAdapter(adapter);
	}

	class ViewHolder {
		ImageView avtor;
		TextView name;

		// TextView descripe;

		ViewHolder(View view) {
			avtor = (ImageView) view.findViewById(R.id.findfriend_item_avtor);
			name = (TextView) view.findViewById(R.id.findfriend_item_nickname);
			// descripe = (TextView)
			// view.findViewById(R.id.friend_item_descripte);
		}
	}

	public void findfriend_search(View view) {
		final String f = edit.getText().toString();
		new Thread() {
			@Override
			public void run() {
				ArrayList<String> ar = XmppConnection.getInstance()
						.searchUsers(f);
				data.clear();
				for (String tm : ar) {
					FriendData fd = new FriendData();
					fd.id = tm;
					fd.avtor = XmppConnection.getInstance().getUserImage(tm);
					data.add(fd);
				}
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						adapter = new MyAdapter();
						listview.setAdapter(adapter);
						// adapter.notifyDataSetInvalidated();
						// adapter.notifyDataSetChanged();
					}
				});
			}
		}.start();
	}

	class MyAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public MyAdapter() {
			mInflater = getLayoutInflater();
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public FriendData getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ViewHolder holder;
			View v = convertView;

			if (null == v) {
				v = mInflater.inflate(R.layout.findfriend_item, null);
				holder = new ViewHolder(v);
				v.setTag(holder);
			} else {
				holder = (ViewHolder) v.getTag();
			}
			FriendData dat = getItem(position);
			if (dat.avtor != null)
				holder.avtor.setImageDrawable(dat.avtor);
			// else
			// holder.avtor.setImageDrawable();
			if (dat.id != null)
				holder.name.setText(dat.id);
			return v;
		}

	}

}
