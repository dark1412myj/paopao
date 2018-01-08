package com.base;

import java.util.Date;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.packet.DelayInformation;
import org.json.JSONException;
import org.json.JSONObject;

import com.mypro.mymsg.MessageData;
import com.mypro.mymsg.MessageDispatcher;

import android.util.Log;

/**
 * 单人聊天信息监听类
 * 
 * @author Administrator
 * 
 */
public class TaxiChatManagerListener implements ChatManagerListener {

	@Override
	public void chatCreated(Chat chat, boolean arg1) {
		chat.addMessageListener(new MessageListener() {
			@Override
			public void processMessage(Chat arg0, Message msg) {
				// 登录用户
				StringUtils.parseName(XmppConnection.getInstance()
						.getConnection().getUser());
				// 发送消息用户
				msg.getFrom();
				// 消息内容
				String body = msg.getBody();

				DelayInformation info = (DelayInformation) msg.getExtension(
						"x", "jabber:x:delay");
				Date date = null;
				String tm = null;
				if (info != null) {
					date = info.getStamp();
					tm = SystemUtils.transTime(date);
					Log.e("离线消息", "收到离线消息, 时间：" + date.getTime());
				} else {
					date = new Date();
					tm = SystemUtils.getLocalTime();
				}

				// MessageData data = new MessageData(msg.getFrom(), "", msg
				// .getBody(), MessageData.RECEIVE, null, date);
				String from = msg.getFrom();
				from = from.substring(0, from.lastIndexOf('@'));
				MessageData data = new MessageData(from, XmppConnection
						.getInstance().getLocalUserAccount(), msg.getBody(),
						MessageData.RECEIVE, null, tm, -1);

				MessageDispatcher.getInstance().dispatchMessage(data);

				Log.e("message", "from" + msg.getFrom() + "value:" + body);
				boolean left = body.substring(0, 1).equals("{");
				boolean right = body
						.substring(body.length() - 1, body.length())
						.equals("}");
				if (left && right) {
					try {
						JSONObject obj = new JSONObject(body);
						String type = obj.getString("messageType");
						String chanId = obj.getString("chanId");
						String chanName = obj.getString("chanName");
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			}
		});
	}
}
