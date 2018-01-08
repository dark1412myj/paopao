package com.mypro.paopao;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.List;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;

import com.base.SystemUtils;
import com.base.TaxiFileStatusListener;
import com.base.XmppConnection;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.mypro.db.ChatMsgDatabaseHelper;
import com.mypro.emoj.ChatMsgEntity;
import com.mypro.emoj.FaceConversionUtil;
import com.mypro.emoj.FaceRelativeLayout;
import com.mypro.emoj.FaceRelativeLayout.FunctionClickListener;
import com.mypro.emoj.MessagePlusEndity;
import com.mypro.mymsg.FileData;
import com.mypro.mymsg.MessageData;
import com.mypro.mymsg.MessageDispatcher;
import com.mypro.paopao.FindFriend.FriendData;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NomalChatActivity extends Activity {

	public void test() {
	}

	MyAdapter m_adapter = null;
	PullToRefreshListView mPullRefreshListView = null;
	ListView m_listview = null;
	public String oppositeName;
	TextView opp;
	Button btn_send;
	EditText chatIn;
	LinearLayout ly;
	Drawable my = null, you = null;
	FaceRelativeLayout faceRelativeLayout = null;

	void InitAvtor() {
		new Thread() {
			@Override
			public void run() {
				my = XmppConnection.getInstance().getUserImage(
						XmppConnection.getInstance().getLocalUserAccount());
				you = XmppConnection.getInstance().getUserImage(oppositeName);
			}
		}.start();
	}

	// ArrayDeque<MessageData> m_data2 = new ArrayDeque<MessageData>() ;
	ArrayList<MessageData> m_data = new ArrayList<MessageData>();

	public String getOppositeName() {
		return oppositeName;
	}

	/**
	 * 自己发送信息同时调用
	 * */
	public void sendMessage(final String msg) {
		new Thread() {
			@Override
			public void run() {
				XmppConnection.getInstance()
						.sendToFriendChat(oppositeName, msg);
			}
		}.start();

		MessageData my = new MessageData(XmppConnection.getInstance()
				.getLocalUserAccount(), oppositeName, msg, MessageData.SENDER,
				null, SystemUtils.getLocalTime(), -1);
		// Log.e("xxx", "fff" + my.getSender());

		/* 派发消息 */
		MessageDispatcher.getInstance().dispatchMessage(my);

	}

	/*
	 * 信息有改变，重新读取信息 *
	 */
	public void messageChange() {
		if (m_data == null)
			return;
		int eid = 0;
		if (m_data.size() != 0)
			eid = m_data.get(m_data.size() - 1).getIndex();
		ArrayList<MessageData> add = ChatMsgDatabaseHelper.getInstance()
				.findMsgUntil(eid, oppositeName);
		m_data.addAll(add);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				m_adapter.notifyDataSetChanged();
				m_listview.setSelection(m_data.size() - 1);
			}
		});
	}

	public void initPrevious(String sender) {
		messageChange();
	}

	// int i_recvParam;
	// String s_recvParam;

	/**
	 * 
	 * 接受文件请求
	 * 
	 * */
	public void OnReceiveFile(final FileTransferRequest request) {
		// i_recvParam = 0;
		String usr = XmppConnection.getInstance().getLocalUserAccount();
		MessageData msg = new MessageData(oppositeName, usr, ""
				+ request.getFileName(), MessageData.REQUIRE, null,
				SystemUtils.getLocalTime(), -1);
		final int id = MessageDispatcher.getInstance().dispatchMessage(msg);

		/**
		 **
		 **
		 **
		 **/
		new Thread() {
			public void run() {
				boolean flag = true;
				while (flag) {
					for (MessageData i : m_data) {
						if (i.getIndex() == id) {
							i.startMessageLoop(request);
							flag = false;
							break;
						}
					}
				}
				Log.e("mythread", "end");
			}
		}.start();
		// while (i_recvParam == 0)
		// try {
		// Thread.sleep(0);
		// } catch (InterruptedException e1) {
		// e1.printStackTrace();
		// }
		// if (i_recvParam == -1)
		// request.reject();
		// else {
		// File insFile = new File(Environment.getExternalStorageDirectory()
		// + "/" + request.getFileName());
		// IncomingFileTransfer infiletransfer = request.accept();
		// try {
		// infiletransfer.recieveFile(insFile);
		// } catch (XMPPException e) {
		// e.printStackTrace();
		// }
		// }
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nomalchat);

		/* 绑定各空间与变量 */
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.chat_listview);
		m_listview = mPullRefreshListView.getRefreshableView();
		registerForContextMenu(m_listview);
		opp = (TextView) findViewById(R.id.chat_oppname);
		btn_send = (Button) findViewById(R.id.btn_send);
		chatIn = (EditText) findViewById(R.id.et_sendmessage);
		faceRelativeLayout = (FaceRelativeLayout) findViewById(R.id.FaceRelativeLayout);

		faceRelativeLayout
				.setFunctionClickListener(new FunctionClickListener() {
					@Override
					public void onClick(MessagePlusEndity item) {
						switch (item.position) {
						case 0:

							break;
						case 1:
							Intent getContentIntent = FileUtils
									.createGetContentIntent();
							Intent intent = Intent.createChooser(
									getContentIntent, "Select a file");
							startActivityForResult(intent, REQUEST_CHOOSER);
							break;
						case 2:
							break;
						case 3:
							break;
						}
						// Toast.makeText(getApplicationContext(),
						// ""+item.position+item.name,0).show();
						// item.name
					}
				});
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

						// Do work to refresh the list here.
						// new GetDataTask().execute();
						mPullRefreshListView.onRefreshComplete();
					}
				});

		ly = (LinearLayout) findViewById(R.id.linner_bottom);

		/* 添加发送监听 */
		btn_send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendMessage("" + chatIn.getText());
				chatIn.setText("");
			}
		});

		/* 设置适配器 */
		m_adapter = new MyAdapter();
		m_listview.setAdapter(m_adapter);

		/* 设置窗口标题 */
		Intent it = getIntent();
		oppositeName = it.getStringExtra("oppsite");
		opp.setText(oppositeName);
		InitAvtor();
		initPrevious(oppositeName);

		MessageDispatcher.getInstance().setActivity(this);

	}

	/**
	 * 正在发送的文件的状态的监听器
	 * */
	class A implements TaxiFileStatusListener {
		boolean flag;
		boolean flag2;
		boolean flag3;
		boolean flag4;

		A() {
			flag = true;
			flag2 = true;
			flag3 = true;
			flag4 = true;
		}

		@Override
		public void OnStatus(final int statusCode, long sendbyte,
				double present, long filesize, final String filename) {
			/**
			 * 该函数会导致更新UI在主线程执行
			 * */
			runOnUiThread(new Runnable() {
				@Override
				public void run() {

					switch (statusCode) {
					case TaxiFileStatusListener.WAIT:
						if (flag) {
							flag = false;
							String str = XmppConnection.getInstance()
									.getLocalUserAccount();
							MessageData mg = new MessageData(str, oppositeName,
									"等待确认:" + filename, MessageData.WARN, null,
									SystemUtils.getLocalTime(), -1);
							MessageDispatcher.getInstance().dispatchMessage(mg);
						}
						break;
					case TaxiFileStatusListener.REFUSED:
						if (flag2) {
							flag2 = false;
							String str = XmppConnection.getInstance()
									.getLocalUserAccount();
							MessageData mg = new MessageData(str, oppositeName,
									"对方拒绝" + filename + "的发送",
									MessageData.WARN, null,
									SystemUtils.getLocalTime(), -1);
							MessageDispatcher.getInstance().dispatchMessage(mg);
						}
						break;
					case TaxiFileStatusListener.SENDING:
						if (flag3) {
							flag3 = false;
							String str = XmppConnection.getInstance()
									.getLocalUserAccount();
							MessageData mg = new MessageData(str, oppositeName,
									"对方同意,正在发送" + filename, MessageData.WARN,
									null, SystemUtils.getLocalTime(), -1);
							MessageDispatcher.getInstance().dispatchMessage(mg);
						}
						break;
					case TaxiFileStatusListener.COMPLETE:
						if (flag4) {
							flag4 = false;
							String str = XmppConnection.getInstance()
									.getLocalUserAccount();
							MessageData mg = new MessageData(str, oppositeName,
									"" + filename + "发送完毕", MessageData.WARN,
									null, SystemUtils.getLocalTime(), -1);
							MessageDispatcher.getInstance().dispatchMessage(mg);
						}
					}

				}
			});

		}
	}

	private static final int REQUEST_CHOOSER = 1234;

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CHOOSER:
			if (resultCode == RESULT_OK) {
				final Uri uri = data.getData();
				// Get the File path from the Uri
				final String path = FileUtils.getPath(this, uri);
				// Alternatively, use FileUtils.getFile(Context, Uri)
				if (path != null && FileUtils.isLocal(path)) {
					// File file = new File(path);
					new Thread() {
						public void run() {
							XmppConnection.getInstance().initFileTrans();
							XmppConnection.getInstance().sendFile(
									oppositeName + "@127.0.0.1/Spark 2.6.3",
									path, new A());
						}
					}.start();
				}
			}
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MessageDispatcher.getInstance().setActivity(null);
	}

	class MyViewHolder {

		TextView time;
		TextView datail;
		ImageView avtor;

		Button accept;
		Button reject;
		LinearLayout ly;

		public MyViewHolder() {
		}

		public MyViewHolder(int x, View view) {
			switch (x) {
			case 1:
				time = (TextView) view.findViewById(R.id.chat1_time);
				datail = (TextView) view.findViewById(R.id.chat1_datail);
				avtor = (ImageView) view.findViewById(R.id.chat1_avtor);
				break;
			case 2:
				time = (TextView) view.findViewById(R.id.chat2_time);
				datail = (TextView) view.findViewById(R.id.chat2_datail);
				avtor = (ImageView) view.findViewById(R.id.chat2_avtor);
				break;
			case 3:
				datail = (TextView) view.findViewById(R.id.chat3_warn);
				break;
			case 4:
				datail = (TextView) view.findViewById(R.id.chat4_filename);
				ly = (LinearLayout) view.findViewById(R.id.chat4_linear);
				accept = (Button) view.findViewById(R.id.chat4_accept);
				reject = (Button) view.findViewById(R.id.chat4_reject);
				break;
			}
		}
	}

	class MyAdapter extends BaseAdapter {
		private static final int TYPE_REVEIVE = 0;
		private static final int TYPE_SEND = 1;
		private static final int TYPE_WARN = 2;
		private static final int TYPE_REQUIRE = 3;
		private static final int TYPE_MAX_COUNT = 4;
		private LayoutInflater mInflater;

		public MyAdapter() {
			mInflater = getLayoutInflater();
		}

		@Override
		public int getViewTypeCount() {
			return TYPE_MAX_COUNT;
		}

		@Override
		public int getItemViewType(int position) {
			return m_data.get(position).getChatType();
		}

		@Override
		public int getCount() {
			return m_data == null ? 0 : m_data.size();
		}

		@Override
		public MessageData getItem(int position) {
			return m_data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			MyViewHolder holder = null;
			int type = getItemViewType(position);
			final MessageData data = getItem(position);
			Log.e("list", "" + type + " data=" + data.getChatItem());
			if (null == convertView) {
				switch (type) {
				case TYPE_REVEIVE:
					convertView = mInflater.inflate(R.layout.nomalchat_item1,
							null);
					holder = new MyViewHolder(1, convertView);
					convertView.setTag(holder);
					break;
				case TYPE_SEND:
					convertView = mInflater.inflate(R.layout.nomalchat_item2,
							null);
					holder = new MyViewHolder(2, convertView);
					convertView.setTag(holder);
					break;
				case TYPE_WARN:
					convertView = mInflater.inflate(R.layout.nomalchat_item3,
							null);
					holder = new MyViewHolder(3, convertView);
					convertView.setTag(holder);
					break;
				case TYPE_REQUIRE:
					convertView = mInflater.inflate(R.layout.nomalchat_item4,
							null);
					holder = new MyViewHolder(4, convertView);
					convertView.setTag(holder);
					break;
				}
			} else {
				holder = (MyViewHolder) convertView.getTag();
			}
			switch (type) {
			case TYPE_REVEIVE:
				if (data.getStrTime() != null)
					holder.time.setText(data.getStrTime());
				holder.datail.setText(data.getChatItem());
				if (you != null)
					holder.avtor.setImageDrawable(you);
				break;
			case TYPE_SEND:
				if (data.getStrTime() != null)
					holder.time.setText(data.getStrTime());
				holder.datail.setText(data.getChatItem());
				if (my != null)
					holder.avtor.setImageDrawable(my);
				break;
			case TYPE_WARN:
				if (data.getChatItem() != null)
					holder.datail.setText(data.getChatItem());
				break;
			case TYPE_REQUIRE:
				holder.datail.setText(data.getChatItem());
				// if()
				// if(data.getObjtmp())
				FileData filedata = data.getFileParam();
				if (filedata != null && filedata.getStatus() == 0)
					holder.ly.setVisibility(View.VISIBLE);
				else
					holder.ly.setVisibility(View.GONE);
				final MyViewHolder my = holder;
				holder.accept.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						my.ly.setVisibility(View.GONE);
						// i_recvParam = 1;
						/* 更改数据库中的表的接受文件状态 */
						ChatMsgDatabaseHelper.getInstance().updateFileMsg(
								data.getIndex(), "", 1);

						/* 更改数据库中的表的接受文件的item字段 */
						ChatMsgDatabaseHelper.getInstance().updateFileMsgStr(
								data.getIndex(),
								"您已同意接受文件:" + data.getChatItem());
						// m_data.get(position)=null;
						/* 更新当前数据 */
						m_data.get(position).setFileParam(
								ChatMsgDatabaseHelper.getInstance()
										.findFileDataFromId(data.getIndex()));
						m_data.get(position).setChatItem(
								"您已同意接受文件:" + data.getChatItem());
						// m_data.set(position, ChatMsgDatabaseHelper
						// .getInstance().findMsgFromId(data.getIndex()));
						/* 刷新显示 */
						m_adapter.notifyDataSetChanged();

					}
				});
				holder.reject.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						my.ly.setVisibility(View.GONE);
						// i_recvParam = -1;
						/* 更改数据库中的表的接受文件状态 */
						ChatMsgDatabaseHelper.getInstance().updateFileMsg(
								data.getIndex(), "", -1);

						/* 更改数据库中的表的接受文件的item字段 */
						ChatMsgDatabaseHelper.getInstance().updateFileMsgStr(
								data.getIndex(),
								"您拒绝接受文件:" + data.getChatItem());
						/* 更新当前数据 */
						// m_data.set(position, ChatMsgDatabaseHelper
						// .getInstance().findMsgFromId(data.getIndex()));
						m_data.get(position).setFileParam(
								ChatMsgDatabaseHelper.getInstance()
										.findFileDataFromId(data.getIndex()));
						m_data.get(position).setChatItem(
								"您拒绝接受文件:" + data.getChatItem());
						/* 刷新显示 */
						m_adapter.notifyDataSetChanged();

					}
				});
				break;
			}
			return convertView;
		}
	}

}
