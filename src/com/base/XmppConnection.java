package com.base;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.OfflineMessageManager;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * XmppConnection 工具类
 * 
 * @user 穆永嘉修改
 * 
 */
public class XmppConnection {
	private int SERVER_PORT = 5222;
	private String SERVER_HOST = "58.155.102.117";
	// private String SERVER_HOST = "192.168.1.147";
	private XMPPConnection connection = null;
	private String SERVER_NAME = "ubuntuserver4java";
	private static XmppConnection xmppConnection = new XmppConnection();
	private String useraccount = null;

	private Context tx;

	public static void Init(Context t) {
		getInstance().tx = t;
	}

	public String getLocalUserAccount() {
		return useraccount;
	}

	// private TaxiConnectionListener connectionListener;
	/**
	 * 单例模式
	 * 
	 * @return
	 */
	synchronized public static XmppConnection getInstance() {
		return xmppConnection;
	}

	/**
	 * 创建连接
	 */
	public XMPPConnection getConnection() {
		if (connection == null) {
			openConnection();
		}
		return connection;
	}

	/**
	 * 打开连接
	 */
	public boolean openConnection() {
		try {
			if (null == connection || !connection.isAuthenticated()) {
				// Log.v("regist","???");
				Connection.DEBUG_ENABLED = true;// 开启DEBUG模式
				// 配置连接
				ConnectionConfiguration config = new ConnectionConfiguration(
						SERVER_HOST, SERVER_PORT, SERVER_NAME);
				config.setReconnectionAllowed(true);
				config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
				config.setSendPresence(true); // 状态设为离线，目的为了取离线消息
				config.setSASLAuthenticationEnabled(false); // 是否启用安全验证
				config.setTruststorePath("/system/etc/security/cacerts.bks");
				config.setTruststorePassword("changeit");
				config.setTruststoreType("bks");
				connection = new XMPPConnection(config);
				Log.v("regist", "will successful");
				connection.connect();// 连接到服务器
				Log.v("regist", "connect successful");
				// 配置各种Provider，如果不配置，则会无法解析数据
				configureConnection(ProviderManager.getInstance());
				return true;
			}
		} catch (XMPPException xe) {
			xe.printStackTrace();
			connection = null;
		}
		return false;
	}

	/**
	 * 关闭连接
	 */
	public void closeConnection() {
		if (connection != null) {
			// 移除連接監聽
			// connection.removeConnectionListener(connectionListener);
			if (connection.isConnected())
				connection.disconnect();
			connection = null;
		}
		Log.i("XmppConnection", "關閉連接");
	}

	/**
	 * 登录
	 * 
	 * @param account
	 *            登录帐号
	 * @param password
	 *            登录密码
	 * @return
	 */
	public boolean login(String account, String password) {
		try {
			if (getConnection() == null)
				return false;
			getConnection().login(account, password);

			useraccount = account;/* 局部变量记录当前登陆用户 */

			// 更改在綫狀態
			Presence presence = new Presence(Presence.Type.available);
			getConnection().sendPacket(presence);
			// 添加連接監聽
			TaxiConnectionListener connectionListener = new TaxiConnectionListener();
			getConnection().addConnectionListener(connectionListener);
			return true;
		} catch (XMPPException xe) {
			xe.printStackTrace();
		}
		return false;
	}

	/**
	 * 注册
	 * 
	 * @param account
	 *            注册帐号
	 * @param password
	 *            注册密码
	 * @return 1、注册成功 0、服务器没有返回结果2、这个账号已经存在3、注册失败
	 */
	public String regist(String account, String password) {
		if (getConnection() == null)
			return "0";
		Registration reg = new Registration();
		reg.setType(IQ.Type.SET);
		reg.setTo(getConnection().getServiceName());
		// 注意这里createAccount注册时，参数是UserName，不是jid，是"@"前面的部分。
		reg.setUsername(account);
		reg.setPassword(password);
		// 这边addAttribute不能为空，否则出错。所以做个标志是android手机创建的吧！！！！！
		reg.addAttribute("android", "geolo_createUser_android");
		PacketFilter filter = new AndFilter(new PacketIDFilter(
				reg.getPacketID()), new PacketTypeFilter(IQ.class));
		PacketCollector collector = getConnection().createPacketCollector(
				filter);
		getConnection().sendPacket(reg);
		IQ result = (IQ) collector.nextResult(SmackConfiguration
				.getPacketReplyTimeout());
		// Stop queuing results停止请求results（是否成功的结果）
		collector.cancel();
		if (result == null) {
			Log.e("regist", "No response from server.");
			return "0";
		} else if (result.getType() == IQ.Type.RESULT) {
			Log.v("regist", "regist success.");
			return "1";
		} else { // if (result.getType() == IQ.Type.ERROR)
			if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
				Log.e("regist", "IQ.Type.ERROR: "
						+ result.getError().toString());
				return "2";
			} else {
				Log.e("regist", "IQ.Type.ERROR: "
						+ result.getError().toString());
				return "3";
			}
		}
	}

	/**
	 * 更改用户状态
	 */
	public void setPresence(int code) {
		XMPPConnection con = getConnection();
		if (con == null)
			return;
		Presence presence;
		switch (code) {
		case 0:
			presence = new Presence(Presence.Type.available);
			con.sendPacket(presence);
			Log.v("state", "设置在线");
			break;
		case 1:
			presence = new Presence(Presence.Type.available);
			presence.setMode(Presence.Mode.chat);
			con.sendPacket(presence);
			Log.v("state", "设置Q我吧");
			break;
		case 2:
			presence = new Presence(Presence.Type.available);
			presence.setMode(Presence.Mode.dnd);
			con.sendPacket(presence);
			Log.v("state", "设置忙碌");
			break;
		case 3:
			presence = new Presence(Presence.Type.available);
			presence.setMode(Presence.Mode.away);
			con.sendPacket(presence);
			Log.v("state", "设置离开");
			break;
		case 4:
			Roster roster = con.getRoster();
			Collection<RosterEntry> entries = roster.getEntries();
			for (RosterEntry entry : entries) {
				presence = new Presence(Presence.Type.unavailable);
				presence.setPacketID(Packet.ID_NOT_AVAILABLE);
				presence.setFrom(con.getUser());
				presence.setTo(entry.getUser());
				con.sendPacket(presence);
				Log.v("state", presence.toXML());
			}
			// 向同一用户的其他客户端发送隐身状态
			presence = new Presence(Presence.Type.unavailable);
			presence.setPacketID(Packet.ID_NOT_AVAILABLE);
			presence.setFrom(con.getUser());
			presence.setTo(StringUtils.parseBareAddress(con.getUser()));
			con.sendPacket(presence);
			Log.v("state", "设置隐身");
			break;
		case 5:
			presence = new Presence(Presence.Type.unavailable);
			con.sendPacket(presence);
			Log.v("state", "设置离线");
			break;
		default:
			break;
		}
	}

	/**
	 * 获取所有组
	 * 
	 * @return 所有组集合
	 */
	public List<RosterGroup> getGroups() {
		if (getConnection() == null)
			return null;
		List<RosterGroup> grouplist = new ArrayList<RosterGroup>();
		Collection<RosterGroup> rosterGroup = getConnection().getRoster()
				.getGroups();
		Iterator<RosterGroup> i = rosterGroup.iterator();
		while (i.hasNext()) {
			grouplist.add(i.next());
		}
		return grouplist;
	}

	/**
	 * 获取某个组里面的所有好友
	 * 
	 * @param roster
	 * @param groupName
	 *            组名
	 * @return
	 */
	public List<RosterEntry> getEntriesByGroup(String groupName) {
		if (getConnection() == null)
			return null;
		List<RosterEntry> Entrieslist = new ArrayList<RosterEntry>();
		RosterGroup rosterGroup = getConnection().getRoster().getGroup(
				groupName);
		Collection<RosterEntry> rosterEntry = rosterGroup.getEntries();
		Iterator<RosterEntry> i = rosterEntry.iterator();
		while (i.hasNext()) {
			Entrieslist.add(i.next());
		}
		return Entrieslist;
	}

	/**
	 * 获取所有好友信息
	 * 
	 * @return
	 */
	public List<RosterEntry> getAllEntries() {
		if (getConnection() == null)
			return null;
		List<RosterEntry> Entrieslist = new ArrayList<RosterEntry>();
		Collection<RosterEntry> rosterEntry = getConnection().getRoster()
				.getEntries();
		Iterator<RosterEntry> i = rosterEntry.iterator();
		while (i.hasNext()) {
			Entrieslist.add(i.next());
		}
		return Entrieslist;
	}

	/**
	 * 获取用户VCard信息
	 * 
	 * @param connection
	 * @param user
	 * @return
	 * @throws XMPPException
	 */
	public VCard getUserVCard(String user) {
		if (getConnection() == null)
			return null;
		VCard vcard = new VCard();
		try {
			vcard.load(getConnection(), user + "@"
					+ getConnection().getServiceName());
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		return vcard;
	}

	/**
	 * 获取用户头像信息
	 * 
	 * @param connection
	 * @param user
	 * @return
	 */

	public Drawable getUserImage(String user) {
		if (getConnection() == null)
			return null;
		ByteArrayInputStream bais = null;
		try {
			VCard vcard = new VCard();
			// 加入这句代码，解决No VCard for
			ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp",
					new org.jivesoftware.smackx.provider.VCardProvider());
			if (user == "" || user == null || user.trim().length() <= 0) {
				return null;
			}
			vcard.load(getConnection(), user + "@"
					+ getConnection().getServiceName());

			if (vcard == null || vcard.getAvatar() == null)
				return null;
			bais = new ByteArrayInputStream(vcard.getAvatar());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		// Bitmap bm =BitmapFactory.decodeStream(bais);
		return FormatTools.getInstance().InputStream2Drawable(bais);
	}

	/**
	 * 添加一个分组
	 * 
	 * @param groupName
	 * @return
	 */
	public boolean addGroup(String groupName) {
		if (getConnection() == null)
			return false;
		try {
			getConnection().getRoster().createGroup(groupName);
			Log.v("addGroup", groupName + "創建成功");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 删除分组
	 * 
	 * @param groupName
	 * @return
	 */
	public boolean removeGroup(String groupName) {
		if (getConnection() == null)
			return false;
		try {

		} catch (Exception e) {

		}
		return true;
	}

	/**
	 * 添加好友 无分组
	 * 
	 * @param userName
	 * @param name
	 * @return
	 */
	public boolean addUser(String userName, String name) {
		if (getConnection() == null)
			return false;
		try {
			getConnection().getRoster().createEntry(userName, name, null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 添加好友 有分组
	 * 
	 * @param userName
	 * @param name
	 * @param groupName
	 * @return
	 */
	public boolean addUser(String userName, String name, String groupName) {
		if (getConnection() == null)
			return false;
		try {
			Presence subscription = new Presence(Presence.Type.subscribed);
			subscription.setTo(userName);
			userName += "@" + getConnection().getServiceName();
			getConnection().sendPacket(subscription);
			getConnection().getRoster().createEntry(userName, name,
					new String[] { groupName });
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 删除好友
	 * 
	 * @param userName
	 * @return
	 */
	public boolean removeUser(String userName) {
		if (getConnection() == null)
			return false;
		try {
			RosterEntry entry = null;
			if (userName.contains("@"))
				entry = getConnection().getRoster().getEntry(userName);
			else
				entry = getConnection().getRoster().getEntry(
						userName + "@" + getConnection().getServiceName());
			if (entry == null)
				entry = getConnection().getRoster().getEntry(userName);
			getConnection().getRoster().removeEntry(entry);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 设置好友状态等的一些监听
	 * **/

	public void addStatusListen() {
		if (getConnection() == null)
			return;
		// final String loginuser = getConnection().getUser().substring(0,
		// getConnection().getUser().lastIndexOf("@"));

		// 理解为条件过滤器 过滤出Presence包
		PacketFilter filter = new AndFilter(
				new PacketTypeFilter(Presence.class));

		PacketListener listener = new PacketListener() {

			@Override
			public void processPacket(Packet packet) {
				Log.i("Presence", "PresenceService------" + packet.toXML());
				// 看API可知道 Presence是Packet的子类
				if (packet instanceof Presence) {
					Log.i("Presence", packet.toXML());
					Presence presence = (Presence) packet;
					// Presence还有很多方法，可查看API
					String from = presence.getFrom();// 发送方

					String to = presence.getTo();// 接收方
					// Presence.Type有7中状态
					if (presence.getType().equals(Presence.Type.subscribe)) {// 好友申请

						Log.e("friend", "新的好友申请");
						presence.setType(Presence.Type.unsubscribed);
						// Presence ans = new
						// Presence(Presence.Type.unsubscribed);// 同意是
						// ans.setTo(from);// 接收方jid
						// ans.setFrom(to);// 发送方jid
						// connection.sendPacket(ans);//
						// connection是你自己的XMPPConnection链接

						// Toast.makeText(MainActivity.context, "好友申请" ,
						// 0).show();

					} else if (presence.getType().equals(
							Presence.Type.subscribed)) {// 同意添加好友

					} else if (presence.getType().equals(
							Presence.Type.unsubscribe)) {// 拒绝添加好友 和 删除好友

					} else if (presence.getType().equals(
							Presence.Type.unsubscribed)) {// 这个我没用到
					} else if (presence.getType().equals(
							Presence.Type.unavailable)) {// 好友下线
															// 要更新好友列表，可以在这收到包后，发广播到指定页面
															// 更新列表

					} else if (presence.getType().equals(
							Presence.Type.available)) {// 好友上线

					} else {
						// 错误处理
					}
				}
			}
		};
		getConnection().addPacketListener(listener, filter);
	}

	/**
	 * 查询用户
	 * 
	 * @param userName
	 * @return
	 * @throws XMPPException
	 */

	/**
	 * 搜索用户
	 */
	public ArrayList<String> searchUsers(String user) {
		ArrayList<String> users = new ArrayList<String>();
		UserSearchManager usm = new UserSearchManager(getConnection());
		Form searchForm = null;
		try {
			searchForm = usm.getSearchForm("search."
					+ getConnection().getServiceName());
			Form answerForm = searchForm.createAnswerForm();
			answerForm.setAnswer("Username", true);
			// answerForm.setAnswer("Email", true);
			answerForm.setAnswer("search", user);
			ReportedData data = usm.getSearchResults(answerForm, "search."
					+ getConnection().getServiceName());
			// column:jid,Username,Name,Email
			Iterator<Row> it = data.getRows();
			Row row = null;
			while (it.hasNext()) {
				row = it.next();
				// Log.d("UserName",
				// row.getValues("Username").next().toString());
				// Log.d("Name", row.getValues("Name").next().toString());
				// Log.d("Email", row.getValues("Email").next().toString());
				// 若存在，则有返回,UserName一定非空，其他两个若是有设，一定非空
				users.add(row.getValues("Username").next().toString());
			}
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return users;
	}

	// public List<HashMap<String, String>> searchUsers(String userName) {
	// if (getConnection() == null)
	// return null;
	// HashMap<String, String> user = null;
	// List<HashMap<String, String>> results = new ArrayList<HashMap<String,
	// String>>();
	// try {
	// // new ServiceDiscoveryManager(getConnection());
	//
	// UserSearchManager usm = new UserSearchManager(getConnection());
	//
	// Log.e("Search", "" + getConnection().getServiceName());
	// Form searchForm = usm.getSearchForm("search."
	// + getConnection().getServiceName());
	// // Form searchForm = usm.getSearchForm(getConnection()
	// // .getServiceName());
	// Log.e("Search", "?" + searchForm);
	//
	// Form answerForm = searchForm.createAnswerForm();
	//
	// Log.e("Search", "answerFrom =" + answerForm);
	//
	// // answerForm.setAnswer("userAccount", true);
	// answerForm.setAnswer("Username", true);
	// // answerForm.setAnswer("NickName", true);
	// // answerForm.setAnswer("search", userName);
	// answerForm.setAnswer("search", userName);
	//
	// // answerForm.setAnswer("userPhote", userName);
	//
	// ReportedData data = usm.getSearchResults(answerForm, "search."
	// + getConnection().getServiceName());
	// // Log.e("", msg)
	//
	// Log.e("Search", "data = " + data);
	//
	// Iterator<Row> it = data.getRows();
	// Row row = null;
	// String str;
	// while (it.hasNext()) {
	// user = new HashMap<String, String>();
	// row = it.next();
	//
	// user.put("userAccount", str = row.getValues("Username").next()
	// .toString());
	// Log.e("Search", "usrname = " + str);
	// // Log.e("Search", "password = "
	// // + row.getValues("password").next());
	// // Log.e("Search", "nickname = " +
	// // row.getValues("nickname").next());
	// // user.put("userAccount", row.getValues("userAccount").next()
	// // .toString());
	//
	// if (row.getValues("NickName")!=null&&row.getValues("NickName").next() !=
	// null)
	// user.put("userPhote", row.getValues("NickName").next()
	// .toString());
	// results.add(user);
	// // 若存在，则有返回,UserName一定非空，其他两个若是有设，一定非空
	// }
	// } catch (XMPPException e) {
	// e.printStackTrace();
	// }
	// return results;
	// }

	/**
	 * 修改心情
	 * 
	 * @param connection
	 * @param status
	 */
	public void changeStateMessage(String status) {
		if (getConnection() == null)
			return;
		Presence presence = new Presence(Presence.Type.available);
		presence.setStatus(status);
		getConnection().sendPacket(presence);
	}

	/**
	 * 修改用户头像
	 * 
	 * @param file
	 */
	public boolean changeImage(File file) {
		if (getConnection() == null)
			return false;
		try {
			VCard vcard = new VCard();
			vcard.load(getConnection());

			byte[] bytes;

			bytes = getFileBytes(file);
			String encodedImage = StringUtils.encodeBase64(bytes);
			vcard.setAvatar(bytes, encodedImage);
			vcard.setEncodedImage(encodedImage);
			vcard.setField("PHOTO", "<TYPE>image/jpg</TYPE><BINVAL>"
					+ encodedImage + "</BINVAL>", true);

			ByteArrayInputStream bais = new ByteArrayInputStream(
					vcard.getAvatar());
			FormatTools.getInstance().InputStream2Bitmap(bais);
			// vcard.seta
			vcard.save(getConnection());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 文件转字节
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private byte[] getFileBytes(File file) throws IOException {
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(file));
			int bytes = (int) file.length();
			byte[] buffer = new byte[bytes];
			int readBytes = bis.read(buffer);
			if (readBytes != buffer.length) {
				throw new IOException("Entire file not read");
			}
			return buffer;
		} finally {
			if (bis != null) {
				bis.close();
			}
		}
	}

	/**
	 * 删除当前用户
	 * 
	 * @return
	 */
	public boolean deleteAccount() {
		if (getConnection() == null)
			return false;
		try {
			getConnection().getAccountManager().deleteAccount();
			return true;
		} catch (XMPPException e) {
			return false;
		}
	}

	/**
	 * 修改密码
	 * 
	 * @return
	 */
	public boolean changePassword(String pwd) {
		if (getConnection() == null)
			return false;
		try {
			getConnection().getAccountManager().changePassword(pwd);
			return true;
		} catch (XMPPException e) {
			return false;
		}
	}

	private Map<String, Chat> chatManage = new HashMap<String, Chat>();// 聊天窗口管理map集合

	/**
	 * 获取或创建聊天窗口
	 * 
	 * @param friend
	 *            好友名
	 * @param listenter
	 *            聊天監聽器
	 * @return
	 */
	public Chat getFriendChat(String friend, MessageListener listenter) {
		if (getConnection() == null)
			return null;
		/** 判断是否创建聊天窗口 */
		for (String fristr : chatManage.keySet()) {
			if (fristr.equals(friend)) {
				// 存在聊天窗口，则返回对应聊天窗口
				return chatManage.get(fristr);
			}
		}
		/** 创建聊天窗口 */
		Chat chat = getConnection().getChatManager().createChat(
				friend + "@" + getConnection().getServiceName(), listenter);
		/** 添加聊天窗口到chatManage */
		chatManage.put(friend, chat);
		return chat;
	}

	/**
	 * 给好有发送字符串
	 * 
	 * @param friend
	 *            好友名
	 * @param str
	 *            发送的字符串
	 * */
	public boolean sendToFriendChat(String friend, String str) {
		Chat chat = getFriendChat(friend, null);
		try {
			// String msgjson =
			// "{\"messageType\":\""+messageType+"\",\"chanId\":\""+chanId+"\",\"chanName\":\""+chanName+"\"}";
			// chat.sendMessage(msgjson);
			chat.sendMessage(str);
			return true;
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 添加监听，最好是放在登录方法中，在关闭连接方法中移除监听， 原因是为了避免重复添加监听，接受重复消息 退出程序应该关闭连接，移除监听。
	 * 该监听可以接受所有好友的消息
	 * */
	public void addChatListen() {
		TaxiChatManagerListener chatManagerListener = new TaxiChatManagerListener();
		getConnection().getChatManager().addChatListener(chatManagerListener);
	}

	/**
	 * 移除监听
	 * */
	public void removeChatListen() {
		Collection<ChatManagerListener> cl = getConnection().getChatManager()
				.getChatListeners();
		Iterator<ChatManagerListener> it = cl.iterator();
		while (it.hasNext()) {
			getConnection().getChatManager().removeChatListener(it.next());
		}
	}

	/**
	 * 初始化会议室列表
	 */
	public List<HostedRoom> getHostRooms() {
		if (getConnection() == null)
			return null;
		Collection<HostedRoom> hostrooms = null;
		List<HostedRoom> roominfos = new ArrayList<HostedRoom>();
		try {
			new ServiceDiscoveryManager(getConnection());
			hostrooms = MultiUserChat.getHostedRooms(getConnection(),
					getConnection().getServiceName());
			for (HostedRoom entry : hostrooms) {
				roominfos.add(entry);
				Log.i("room",
						"名字：" + entry.getName() + " - ID:" + entry.getJid());
			}
			Log.i("room", "服务会议数量:" + roominfos.size());
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		return roominfos;
	}

	/**
	 * 创建房间
	 * 
	 * @param roomName
	 *            房间名称
	 */
	public MultiUserChat createRoom(String user, String roomName,
			String password) {
		if (getConnection() == null)
			return null;

		MultiUserChat muc = null;
		try {
			// 创建一个MultiUserChat
			muc = new MultiUserChat(getConnection(), roomName + "@conference."
					+ getConnection().getServiceName());
			// 创建聊天室
			muc.create(roomName);
			// 获得聊天室的配置表单
			Form form = muc.getConfigurationForm();
			// 根据原始表单创建一个要提交的新表单。
			Form submitForm = form.createAnswerForm();
			// 向要提交的表单添加默认答复
			for (Iterator<FormField> fields = form.getFields(); fields
					.hasNext();) {
				FormField field = fields.next();
				if (!FormField.TYPE_HIDDEN.equals(field.getType())
						&& field.getVariable() != null) {
					// 设置默认值作为答复
					submitForm.setDefaultAnswer(field.getVariable());
				}
			}
			// 设置聊天室的新拥有者
			List<String> owners = new ArrayList<String>();
			owners.add(getConnection().getUser());// 用户JID
			submitForm.setAnswer("muc#roomconfig_roomowners", owners);
			// 设置聊天室是持久聊天室，即将要被保存下来
			submitForm.setAnswer("muc#roomconfig_persistentroom", true);
			// 房间仅对成员开放
			submitForm.setAnswer("muc#roomconfig_membersonly", false);
			// 允许占有者邀请其他人
			submitForm.setAnswer("muc#roomconfig_allowinvites", true);
			if (!password.equals("")) {
				// 进入是否需要密码
				submitForm.setAnswer("muc#roomconfig_passwordprotectedroom",
						true);
				// 设置进入密码
				submitForm.setAnswer("muc#roomconfig_roomsecret", password);
			}
			// 能够发现占有者真实 JID 的角色
			// submitForm.setAnswer("muc#roomconfig_whois", "anyone");
			// 登录房间对话
			submitForm.setAnswer("muc#roomconfig_enablelogging", true);
			// 仅允许注册的昵称登录
			submitForm.setAnswer("x-muc#roomconfig_reservednick", true);
			// 允许使用者修改昵称
			submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);
			// 允许用户注册房间
			submitForm.setAnswer("x-muc#roomconfig_registration", false);
			// 发送已完成的表单（有默认值）到服务器来配置聊天室
			muc.sendConfigurationForm(submitForm);
		} catch (XMPPException e) {
			e.printStackTrace();
			return null;
		}
		return muc;
	}

	/**
	 * 加入会议室
	 * 
	 * @param user
	 *            昵称
	 * @param password
	 *            会议室密码
	 * @param roomsName
	 *            会议室名
	 */
	public MultiUserChat joinMultiUserChat(String user, String roomsName,
			String password) {
		if (getConnection() == null)
			return null;
		try {
			// 使用XMPPConnection创建一个MultiUserChat窗口
			MultiUserChat muc = new MultiUserChat(getConnection(), roomsName
					+ "@conference." + getConnection().getServiceName());
			// 聊天室服务将会决定要接受的历史记录数量
			DiscussionHistory history = new DiscussionHistory();
			history.setMaxChars(0);
			// history.setSince(new Date());
			// 用户加入聊天室
			muc.join(user, password, history,
					SmackConfiguration.getPacketReplyTimeout());
			Log.i("MultiUserChat", "会议室【" + roomsName + "】加入成功........");
			return muc;
		} catch (XMPPException e) {
			e.printStackTrace();
			Log.i("MultiUserChat", "会议室【" + roomsName + "】加入失败........");
			return null;
		}
	}

	/**
	 * 查询会议室成员名字
	 * 
	 * @param muc
	 */
	public List<String> findMulitUser(MultiUserChat muc) {
		if (getConnection() == null)
			return null;
		List<String> listUser = new ArrayList<String>();
		Iterator<String> it = muc.getOccupants();
		// 遍历出聊天室人员名称
		while (it.hasNext()) {
			// 聊天室成员名字
			String name = StringUtils.parseResource(it.next());
			listUser.add(name);
		}
		return listUser;
	}

	/**
	 * 添加会议室监听，用法同聊天监听
	 * */
	public void addMulitChatListen() {
		// getConnection().g
	}

	/**
	 * 初始化文件传输
	 * */
	FileTransferManager m_fileManager = null;

	public void initFileTrans() {
		// 创建文件传输管理器
		m_fileManager = new FileTransferManager(getConnection());
		m_fileManager.addFileTransferListener(new ChatFileTransferListener());
	}

	/**
	 * 
	 * 回调函数的调用
	 * 
	 * */
	private void fileCallBack(OutgoingFileTransfer transfer,
			TaxiFileStatusListener listener, String filename) {
		if (listener == null)
			return;
		if (transfer == null)
			return;
		switch (transfer.getStatus().ordinal()) {
		case 1:
			listener.OnStatus(TaxiFileStatusListener.INITIAL,
					transfer.getBytesSent(), transfer.getProgress(),
					transfer.getFileSize(), filename);
			break;
		case 2:
			listener.OnStatus(TaxiFileStatusListener.WAIT,
					transfer.getBytesSent(), transfer.getProgress(),
					transfer.getFileSize(), filename);
			break;
		case 3:
			listener.OnStatus(TaxiFileStatusListener.REFUSED,
					transfer.getBytesSent(), transfer.getProgress(),
					transfer.getFileSize(), filename);
			break;
		case 6:
			listener.OnStatus(TaxiFileStatusListener.SENDING,
					transfer.getBytesSent(), transfer.getProgress(),
					transfer.getFileSize(), filename);
			break;
		case 7:
			listener.OnStatus(TaxiFileStatusListener.COMPLETE,
					transfer.getBytesSent(), transfer.getProgress(),
					transfer.getFileSize(), filename);
			break;
		}
		// if (transfer.getStatus().name().equals("initial")) {
		// listener.OnStatus(TaxiFileStatusListener.INITIAL,
		// transfer.getBytesSent(), transfer.getProgress(),
		// transfer.getFileSize());
		// } else if (transfer.getStatus().name().equals("refused")) {
		// listener.OnStatus(TaxiFileStatusListener.REFUSED,
		// transfer.getBytesSent(), transfer.getProgress(),
		// transfer.getFileSize());
		// } else if
		// (transfer.getStatus().name().equals("negotiating_transfer")) {
		// listener.OnStatus(TaxiFileStatusListener.WAIT,
		// transfer.getBytesSent(), transfer.getProgress(),
		// transfer.getFileSize());
		// } else if (transfer.getStatus().name().equals("in_progress")) {
		// listener.OnStatus(TaxiFileStatusListener.SENDING,
		// transfer.getBytesSent(), transfer.getProgress(),
		// transfer.getFileSize());
		// } else if (transfer.getStatus().name().equals("complete")) {
		// listener.OnStatus(TaxiFileStatusListener.COMPLETE,
		// transfer.getBytesSent(), transfer.getProgress(),
		// transfer.getFileSize());
		// }
	}

	/**
	 * 发送文件
	 * 
	 * @param user
	 * @param filePath
	 */
	public void sendFile(String user, String filePath,
			TaxiFileStatusListener listener) {
		if (getConnection() == null)
			return;

		// 创建输出的文件传输
		OutgoingFileTransfer transfer = m_fileManager
				.createOutgoingFileTransfer(user);

		File file = new File(filePath);
		// 发送文件
		try {

			transfer.sendFile(file, "a file");

			while (!transfer.isDone()) {
				// Log.e("file", "" + transfer.getStatus().ordinal() + "+"
				// + transfer.getStatus().name());
				fileCallBack(transfer, listener, file.getName());
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			fileCallBack(transfer, listener, file.getName());
			// Log.e("file", "" + transfer.getStatus().ordinal() + "+"
			// + transfer.getStatus().name());
		} catch (XMPPException e) {
			e.printStackTrace();
			// Log.e("file", "ex:" + e.getMessage());
		}
	}

	/**
	 * 获取离线消息
	 * 
	 * @return
	 */
	public Map<String, List<HashMap<String, String>>> getHisMessage() {
		if (getConnection() == null)
			return null;
		Map<String, List<HashMap<String, String>>> offlineMsgs = null;

		try {
			OfflineMessageManager offlineManager = new OfflineMessageManager(
					getConnection());
			Iterator<Message> it = offlineManager.getMessages();

			int count = offlineManager.getMessageCount();
			if (count <= 0)
				return null;
			offlineMsgs = new HashMap<String, List<HashMap<String, String>>>();

			while (it.hasNext()) {
				Message message = it.next();
				String fromUser = StringUtils.parseName(message.getFrom());
				;
				HashMap<String, String> histrory = new HashMap<String, String>();
				histrory.put("useraccount",
						StringUtils.parseName(getConnection().getUser()));
				histrory.put("friendaccount", fromUser);
				histrory.put("info", message.getBody());
				histrory.put("type", "left");
				if (offlineMsgs.containsKey(fromUser)) {
					offlineMsgs.get(fromUser).add(histrory);
				} else {
					List<HashMap<String, String>> temp = new ArrayList<HashMap<String, String>>();
					temp.add(histrory);
					offlineMsgs.put(fromUser, temp);
				}
			}
			offlineManager.deleteMessages();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return offlineMsgs;
	}

	/**
	 * 加入providers的函数 ASmack在/META-INF缺少一个smack.providers 文件
	 * 
	 * @param pm
	 */
	public void configureConnection(ProviderManager pm) {

		// Private Data Storage
		pm.addIQProvider("query", "jabber:iq:private",
				new PrivateDataManager.PrivateDataIQProvider());

		// Time
		try {
			pm.addIQProvider("query", "jabber:iq:time",
					Class.forName("org.jivesoftware.smackx.packet.Time"));
		} catch (ClassNotFoundException e) {
			Log.w("TestClient",
					"Can't load class for org.jivesoftware.smackx.packet.Time");
		}

		// Roster Exchange
		pm.addExtensionProvider("x", "jabber:x:roster",
				new RosterExchangeProvider());

		// Message Events
		pm.addExtensionProvider("x", "jabber:x:event",
				new MessageEventProvider());

		// Chat State
		pm.addExtensionProvider("active",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("composing",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("paused",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("inactive",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("gone",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());

		// XHTML
		pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",
				new XHTMLExtensionProvider());

		// Group Chat Invitations
		pm.addExtensionProvider("x", "jabber:x:conference",
				new GroupChatInvitation.Provider());

		// Service Discovery # Items
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",
				new DiscoverItemsProvider());

		// Service Discovery # Info
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",
				new DiscoverInfoProvider());

		// Data Forms
		pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());

		// MUC User
		pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user",
				new MUCUserProvider());

		// MUC Admin
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin",
				new MUCAdminProvider());

		// MUC Owner
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner",
				new MUCOwnerProvider());

		// Delayed Delivery
		pm.addExtensionProvider("x", "jabber:x:delay",
				new DelayInformationProvider());

		// Version
		try {
			pm.addIQProvider("query", "jabber:iq:version",
					Class.forName("org.jivesoftware.smackx.packet.Version"));
		} catch (ClassNotFoundException e) {
			// Not sure what's happening here.
		}

		// VCard
		pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());

		// Offline Message Requests
		pm.addIQProvider("offline", "http://jabber.org/protocol/offline",
				new OfflineMessageRequest.Provider());

		// Offline Message Indicator
		pm.addExtensionProvider("offline",
				"http://jabber.org/protocol/offline",
				new OfflineMessageInfo.Provider());

		// Last Activity
		pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());

		// User Search
		pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());

		// SharedGroupsInfo
		pm.addIQProvider("sharedgroup",
				"http://www.jivesoftware.org/protocol/sharedgroup",
				new SharedGroupsInfo.Provider());

		// JEP-33: Extended Stanza Addressing
		pm.addExtensionProvider("addresses",
				"http://jabber.org/protocol/address",
				new MultipleAddressesProvider());

		// FileTransfer
		pm.addIQProvider("si", "http://jabber.org/protocol/si",
				new StreamInitiationProvider());

		pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams",
				new BytestreamsProvider());

		// Privacy
		pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
		pm.addIQProvider("command", "http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider());
		pm.addExtensionProvider("malformed-action",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.MalformedActionError());
		pm.addExtensionProvider("bad-locale",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadLocaleError());
		pm.addExtensionProvider("bad-payload",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadPayloadError());
		pm.addExtensionProvider("bad-sessionid",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadSessionIDError());
		pm.addExtensionProvider("session-expired",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.SessionExpiredError());
	}

	/**
	 * 判断OpenFire用户的状态 strUrl : url格式 -
	 * http://my.openfire.com:9090/plugins/presence
	 * /status?jid=user1@SERVER_NAME&type=xml 返回值 : 0 - 用户不存在; 1 - 用户在线; 2 -
	 * 用户离线 说明 ：必须要求 OpenFire加载 presence 插件，同时设置任何人都可以访问
	 */
	public int IsUserOnLine(String user) {
		String url = "http://" + SERVER_HOST + ":9090/plugins/presence/status?"
				+ "jid=" + user + "@" + SERVER_NAME + "&type=xml";
		int shOnLineState = 0; // 不存在
		try {
			URL oUrl = new URL(url);
			URLConnection oConn = oUrl.openConnection();
			if (oConn != null) {
				BufferedReader oIn = new BufferedReader(new InputStreamReader(
						oConn.getInputStream()));
				if (null != oIn) {
					String strFlag = oIn.readLine();
					oIn.close();
					System.out.println("strFlag" + strFlag);
					if (strFlag.indexOf("type=\"unavailable\"") >= 0) {
						shOnLineState = 2;
					}
					if (strFlag.indexOf("type=\"error\"") >= 0) {
						shOnLineState = 0;
					} else if (strFlag.indexOf("priority") >= 0
							|| strFlag.indexOf("id=\"") >= 0) {
						shOnLineState = 1;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return shOnLineState;
	}
}