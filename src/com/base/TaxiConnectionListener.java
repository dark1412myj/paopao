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
 * ���Ӽ�����
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
		Log.i("TaxiConnectionListener", "�B���P�]");
		// �P�]�B��
		XmppConnection.getInstance().closeConnection();
		// ����������
		tExit = new Timer();
		tExit.schedule(new timetask(), logintime);
	}

	@Override
	public void connectionClosedOnError(Exception e) {
		Log.i("TaxiConnectionListener", "�B���P�]����");
		// �Д��鎤̖�ѱ����
		boolean error = e.getMessage().equals("stream:error (conflict)");
		if (!error) {
			// �P�]�B��
			XmppConnection.getInstance().closeConnection();
			// ����������
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
				Log.i("TaxiConnectionListener", "�Lԇ���");
				// ���ӷ�����
				if (XmppConnection.getInstance().login(username, password)) {
					Log.i("TaxiConnectionListener", "��䛳ɹ�");
				} else {
					Log.i("TaxiConnectionListener", "���µ��");
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
