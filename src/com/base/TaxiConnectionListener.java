package com.base;

import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.ConnectionListener;

import android.content.Context;
import android.util.Log;

import com.mypro.paopao.MainActivity;
//import com.techrare.utils.Utils;
//import com.techrare.utils.XmppConnection;

/**
 * 连接监听类
 * 
 * @author Administrator
 * 
 */
public class TaxiConnectionListener implements ConnectionListener {
	private Timer tExit;
	private String username;
	private String password;
	private int logintime = 2000;

	@Override
	public void connectionClosed() {
		Log.i("TaxiConnectionListener", "連接關閉");
		// 關閉連接
		XmppConnection.getInstance().closeConnection();
		// 重连服务器
		tExit = new Timer();
		tExit.schedule(new timetask(), logintime);
	}

	@Override
	public void connectionClosedOnError(Exception e) {
		Log.i("TaxiConnectionListener", "連接關閉異常");
		// 判斷為帳號已被登錄
		boolean error = e.getMessage().equals("stream:error (conflict)");
		if (!error) {
			// 關閉連接
			XmppConnection.getInstance().closeConnection();
			// 重连服务器
			tExit = new Timer();
			tExit.schedule(new timetask(), logintime);
		}
	}

	Context tx = null;
	
	class timetask extends TimerTask {
		@Override
		public void run() {
			username = Utils.getInstance().getSharedPreferences("taxicall",
					"account", tx);
			password = Utils.getInstance().getSharedPreferences("taxicall",
					"password", tx);
			if (username != null && password != null) {
				Log.i("TaxiConnectionListener", "嘗試登錄");
				// 连接服务器
				if (XmppConnection.getInstance().login(username, password)) {
					Log.i("TaxiConnectionListener", "登錄成功");
				} else {
					Log.i("TaxiConnectionListener", "重新登錄");
					tExit.schedule(new timetask(), logintime);
				}
			}
		}
	}

	@Override
	public void reconnectingIn(int arg0) {
	}

	@Override
	public void reconnectionFailed(Exception arg0) {
	}

	@Override
	public void reconnectionSuccessful() {
	}

}
