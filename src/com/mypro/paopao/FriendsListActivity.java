package com.mypro.paopao;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.RosterEntry;

import com.base.XmppConnection;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FriendsListActivity extends Activity {
	ListView list = null;
	PullToRefreshListView mPullRefreshListView = null;

	public void addMenu() {
		SlidingMenu menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		menu.setMenu(R.layout.menu_friend);
		//menu.setSecondaryMenu(R.layout.menu_friend);
		menu.setFadeEnabled(false);
		menu.setBehindScrollScale(0.25f);
		menu.setFadeDegree(0.25f);
		menu.setBackgroundColor(Color.parseColor("#B0E2FF"));

		menu.setAboveCanvasTransformer(new SlidingMenu.CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (1 - percentOpen * 0.25);
				canvas.scale(scale, scale, 0, canvas.getHeight() / 2);
			}
		});
		menu.setBehindCanvasTransformer(new SlidingMenu.CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (percentOpen * 0.25 + 0.75);
				canvas.scale(scale, scale, -canvas.getWidth() / 2,
						canvas.getHeight() / 2);
			}
		});
		Button bt = (Button) menu.findViewById(R.id.menu_findfriend);
		//Log.e("bt", "" + bt);
		bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Toast.makeText(getApplicationContext(), "vv", 0).show();
				Intent it = new Intent(FriendsListActivity.this,
						FindFriend.class);
				startActivity(it);
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend);
		//addMenu();
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.friend_listview);
		list = mPullRefreshListView.getRefreshableView();
		// registerForContextMenu(list);
		// addMenu();
		// registerForContextMenu(list);
		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(
								getApplicationContext(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);

						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);

						refreshView.onRefreshComplete();
						// Do work to refresh the list here.
						// new GetDataTask().execute();
						// mPullRefreshListView.onRefreshComplete();
						// mPullRefreshListView.
					}
				});

		// list = (ListView) findViewById(R.id.friend_listview);
		getFriendList();
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				FriendData m = m_friendData.get(position - 1);
				String w = null;
				if (m.name.indexOf("@") == -1)
					w = m.name;
				else {
					w = m.name.substring(0, m.name.indexOf("@"));
				}
				Intent it = new Intent(FriendsListActivity.this,
						NomalChatActivity.class);
				it.putExtra("oppsite", w);
				startActivity(it);
			}
		});
		addMenu();
	}

	ArrayList<FriendData> m_friendData = new ArrayList<FriendsListActivity.FriendData>();

	class FriendData {
		String name;
		Drawable avtor;
		String title;
	}

	public void getFriendList() {
		m_friendData.clear();
		new Thread() {
			@Override
			public void run() {
				// ArrayList<FriendData> f = new
				// ArrayList<FriendsListActivity.FriendData>();
				List<RosterEntry> x = XmppConnection.getInstance()
						.getAllEntries();
				for (RosterEntry i : x) {
					FriendData data = new FriendData();
					String w = i.getName();
					if (w != null && w.length() != 0)
						data.name = i.getName();
					else
						data.name = i.getUser();
					m_friendData.add(data);
				}
				final MyApapter adapter = new MyApapter();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						list.setAdapter(adapter);
					}
				});
			}
		}.start();
		// XmppConnection.get
	}

	class ViewHolder {
		ImageView avtor;
		TextView name;
		TextView descripe;

		ViewHolder(View view) {
			avtor = (ImageView) view.findViewById(R.id.friend_item_avtor);
			name = (TextView) view.findViewById(R.id.friend_item_nackname);
			descripe = (TextView) view.findViewById(R.id.friend_item_descripte);
		}
	}

	class MyApapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public MyApapter() {
			mInflater = getLayoutInflater();
		}

		@Override
		public int getCount() {
			return m_friendData.size();
		}

		@Override
		public FriendData getItem(int position) {
			return m_friendData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			View v = convertView;

			if (null == v) {
				v = mInflater.inflate(R.layout.friend_item, null);
				holder = new ViewHolder(v);
				v.setTag(holder);
			} else {
				holder = (ViewHolder) v.getTag();
			}

			FriendData dat = getItem(position);
			if (dat.avtor != null)
				holder.avtor.setImageDrawable(dat.avtor);
			if (dat.name != null)
				holder.name.setText(dat.name);
			if (dat.title != null)
				holder.descripe.setText(dat.title);

			return v;
		}

	}
}
