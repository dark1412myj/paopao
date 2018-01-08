package com.mypro.paopao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smackx.packet.VCard;

import com.base.XmppConnection;
import com.mypro.db.ChatMsgDatabaseHelper;
import com.mypro.emoj.FaceConversionUtil;
import com.mypro.myfile.MyFileHelper;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	EditText login_name;
	EditText login_password;

	@Override
	protected void onRestart() {
		super.onRestart();
		// XmppConnection.getInstance().
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		/** 初始化单例模式的变量 */
		ChatMsgDatabaseHelper.init(getApplicationContext());
		MyFileHelper.InitContext(getApplicationContext());
		XmppConnection.Init(getApplicationContext());
		new Thread(new Runnable() {
			@Override
			public void run() {
				FaceConversionUtil.getInstace().getFileText(getApplication());
			}
		}).start();
		// FaceConversionUtil.getInstace().getFileText(getApplication());

		Button registrantion = (Button) findViewById(R.id.login_registration);
		Button login = (Button) findViewById(R.id.login_login);
		Button forget = (Button) findViewById(R.id.login_forgot);
		login_name = (EditText) findViewById(R.id.login_name);
		login_password = (EditText) findViewById(R.id.login_password);
		login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String a = login_name.getText().toString();
				final String b = login_password.getText().toString();

				new Thread(new Runnable() {
					public void run() {
						XmppConnection.getInstance().openConnection();
						final boolean flag = XmppConnection.getInstance()
								.login(a, b);
						/**
						 * XMPP各种监听
						 * */
						XmppConnection.getInstance().addChatListen();
						XmppConnection.getInstance().initFileTrans();
						XmppConnection.getInstance().setPresence(0);
						XmppConnection.getInstance().addStatusListen();

						runOnUiThread(new Runnable() {
							public void run() {
								if (flag == false) {
									Toast.makeText(getApplicationContext(),
											"登陆失败请检查用户名密码", 0).show();
									return;
								}
								Intent it = new Intent(MainActivity.this,
										FriendsListActivity.class);
								startActivity(it);
							}
						});
					}
				}).start();
			}
		});
	}

	// public static Context context = null;
	// Button ok;
	// Button cancle;
	// EditText name;
	// EditText password;
	// TextView tx;
	//

	// Handler handle = new Handler() {
	// @Override
	// public void handleMessage(android.os.Message msg) {
	// Intent it = new Intent(MainActivity.this, NomalChatActivity.class);
	// switch (msg.what) {
	// case 1:
	// it.putExtra("oppsite", "bbb");
	// break;
	// case 2:
	// it.putExtra("oppsite", "ccc");
	// break;
	// }
	// startActivity(it);
	// }
	// };
	//
	// public void chatbbb(View view) {
	// Message msg = new Message();
	// msg.what = 1;
	// handle.sendMessage(msg);
	// }
	//
	// public void chatccc(View view) {
	// Message msg = new Message();
	// msg.what = 2;
	// handle.sendMessage(msg);
	// }
	//
	// public void showDlg(String s) {
	// Toast.makeText(getApplicationContext(), s, 0).show();
	// }
	//
	// public void addfriend(View view) {
	// new Thread() {
	// @Override
	// public void run() {
	// final boolean flag = XmppConnection.getInstance().addUser(
	// "bbb@127.0.0.1", "bbb天才");
	// runOnUiThread(new Runnable() {
	// @Override
	// public void run() {
	// Toast.makeText(getApplicationContext(), "" + flag, 0)
	// .show();
	// }
	// });
	// }
	// }.start();
	// }
	//
	// // @127.0.0.1/Spark 2.6.3
	// public void findfriend(View view) {
	// new Thread() {
	// @Override
	// public void run() {
	// final ArrayList<String> l = XmppConnection.getInstance()
	// .searchUsers("b");
	// final VCard vc = XmppConnection.getInstance().getUserVCard(
	// "bbb");
	// runOnUiThread(new Runnable() {
	//
	// @Override
	// public void run() {
	// Toast.makeText(getApplicationContext(),
	// "" + l.size() + "  " + vc.getFirstName(), 1000)
	// .show();
	// for (final String h : l) {
	//
	// Toast.makeText(getApplicationContext(), "" + h, 0)
	// .show();
	// // Toast.makeText(getApplicationContext(),
	// // "" + h.get("userPhote"), 0).show();
	// }
	//
	// }
	// });
	//
	// }
	// }.start();
	//
	// }
	//
	// public void sendfile(View view) {
	// // Toast.makeText(getApplicationContext(),
	// // ""+Environment.getExternalStorageDirectory(), 0).show();
	// new Thread() {
	// @Override
	// public void run() {
	// XmppConnection.getInstance().initFileTrans();
	// XmppConnection.getInstance().sendFile(
	// "bbb@127.0.0.1/Spark 2.6.3",
	// Environment.getExternalStorageDirectory()
	// + "/Flash.exe", null);
	// }
	// }.start();
	// //
	// XmppConnection.getInstance().sendFile("bbb",Environment.getExternalStorageDirectory()+"");
	// }
	//
	// public void submit(View view) {
	// final String s = name.getText().toString();
	// final String n = password.getText().toString();
	// new Thread() {
	// @Override
	// public void run() {
	// List<RosterGroup> lr = XmppConnection.getInstance().getGroups();
	// for (int i = 0; i < lr.size(); ++i) {
	// RosterGroup rp = lr.get(i);
	// Collection<RosterEntry> cl = rp.getEntries();
	// Iterator<RosterEntry> tp = cl.iterator();
	// while (tp.hasNext()) {
	// RosterEntry ppp = tp.next();
	// Log.e("name:", ppp.getName());
	// }
	// }
	//
	// // Chat chat =
	// XmppConnection.getInstance().sendToFriendChat("bbb", "123");
	// };
	// }.start();
	//
	// }
	//
	// public void findfriend22(View view) {
	// Intent it = new Intent(MainActivity.this, FindFriend.class);
	// startActivity(it);
	// }
	//
	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.main, menu);
	// return true;
	// }
	//
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// // Handle action bar item clicks here. The action bar will
	// // automatically handle clicks on the Home/Up button, so long
	// // as you specify a parent activity in AndroidManifest.xml.
	// int id = item.getItemId();
	// if (id == R.id.action_settings) {
	// return true;
	// }
	// return super.onOptionsItemSelected(item);
	// }
}
