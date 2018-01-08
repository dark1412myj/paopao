package com.mypro.db;

import java.util.ArrayList;
import java.util.List;

import com.base.XmppConnection;
import com.mypro.mymsg.FileData;
import com.mypro.mymsg.MessageData;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ChatMsgDatabaseHelper extends SQLiteOpenHelper {

	static final String DATABASE_NAME = "DB";
	static final int DATABASE_VERSION = 2;

	public ChatMsgDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	private static ChatMsgDatabaseHelper ins = null;

	public static void init(Context context) {
		if (ins == null)
			ins = new ChatMsgDatabaseHelper(context);
	}

	public static ChatMsgDatabaseHelper getInstance() {
		return ins;
	}

	/*************************************************************************************
	 * 处理聊天对话信息 *
	 ************************************************************************************* 
	 **/

	/**
	 * 查询从eid开始向前的n条记录，不包括eid这条
	 * */
	public ArrayList<MessageData> findNMsgBegin(int num, int eid, String opp) {
		SQLiteDatabase db = getReadableDatabase();
		ArrayList<MessageData> ans = new ArrayList<MessageData>();
		String sql = "SELECT * FROM message where _user = '"
				+ XmppConnection.getInstance().getLocalUserAccount()
				+ "' and ( _from = '" + opp + "' or _to = '" + opp
				+ "' ) and _id >" + eid + " ORDER BY _id desc limit " + num;

		Cursor cursor = db.rawQuery(sql, null);

		if (false == cursor.moveToFirst()) {
			return ans;
		}
		do {
			int id = cursor.getInt(cursor.getColumnIndex("_id"));
			// String user = cursor.getString(cursor.getColumnIndex("_user"));
			String from = cursor.getString(cursor.getColumnIndex("_from"));
			String to = cursor.getString(cursor.getColumnIndex("_to"));
			String date = cursor.getString(cursor.getColumnIndex("_date"));
			String item = cursor.getString(cursor.getColumnIndex("_detail"));
			int type = cursor.getInt(cursor.getColumnIndex("_type"));

			MessageData tmp = new MessageData(from, to, item, type, null, date,
					id);

			if (type == MessageData.REQUIRE)
				tmp.setFileParam(findFileDataFromId(id));

			ans.add(tmp);
		} while (cursor.moveToNext());

		db.close();
		return ans;
	}

	/**
	 * 查找消息id大于id的信息
	 * */
	public ArrayList<MessageData> findMsgUntil(int eid, String opp) {
		SQLiteDatabase db = getReadableDatabase();
		ArrayList<MessageData> ans = new ArrayList<MessageData>();
		String sql = "SELECT * FROM message where _user = '"
				+ XmppConnection.getInstance().getLocalUserAccount()
				+ "' and ( _from = '" + opp + "' or _to = '" + opp
				+ "' ) and _id >" + eid;

		Cursor cursor = db.rawQuery(sql, null);

		if (false == cursor.moveToFirst()) {
			return ans;
		}
		do {
			int id = cursor.getInt(cursor.getColumnIndex("_id"));
			// String user = cursor.getString(cursor.getColumnIndex("_user"));
			String from = cursor.getString(cursor.getColumnIndex("_from"));
			String to = cursor.getString(cursor.getColumnIndex("_to"));
			String date = cursor.getString(cursor.getColumnIndex("_date"));
			String item = cursor.getString(cursor.getColumnIndex("_detail"));
			int type = cursor.getInt(cursor.getColumnIndex("_type"));
			MessageData tmp = new MessageData(from, to, item, type, null, date,
					id);

			if (type == MessageData.REQUIRE)
				tmp.setFileParam(findFileDataFromId(id));

			ans.add(tmp);
		} while (cursor.moveToNext());

		db.close();

		return ans;
	}

	/**
	 * 查找最后n条属于该账号的记录
	 * */

	public ArrayList<MessageData> findLastNMsg(int n, String opp) {
		SQLiteDatabase db = getReadableDatabase();
		ArrayList<MessageData> ans = new ArrayList<MessageData>();
		String sql = "SELECT * FROM message where _user = '"
				+ XmppConnection.getInstance().getLocalUserAccount()
				+ "' and ( _from = '" + opp + "' or _to = '" + opp
				+ "' ) ORDER BY _id desc limit " + n;
		Cursor cursor = db.rawQuery(sql, null);

		if (false == cursor.moveToFirst()) {
			return ans;
		}
		do {
			int id = cursor.getInt(cursor.getColumnIndex("_id"));
			// String user = cursor.getString(cursor.getColumnIndex("_user"));
			String from = cursor.getString(cursor.getColumnIndex("_from"));
			String to = cursor.getString(cursor.getColumnIndex("_to"));
			String date = cursor.getString(cursor.getColumnIndex("_date"));
			String item = cursor.getString(cursor.getColumnIndex("_detail"));
			int type = cursor.getInt(cursor.getColumnIndex("_type"));

			MessageData tmp = new MessageData(from, to, item, type, null, date,
					id);
			if (type == MessageData.REQUIRE)
				tmp.setFileParam(findFileDataFromId(id));

			ans.add(tmp);
		} while (cursor.moveToNext());

		db.close();
		return ans;
	}

	public MessageData findMsgFromId(int fid) {
		SQLiteDatabase db = getReadableDatabase();
		String sql = "select * from message where _id=" + fid;
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst() == false) {
			// 为空的Cursor
			return null;
		}

		int id = cursor.getInt(cursor.getColumnIndex("_id"));
		// String user = cursor.getString(cursor.getColumnIndex("_user"));
		String from = cursor.getString(cursor.getColumnIndex("_from"));
		String to = cursor.getString(cursor.getColumnIndex("_to"));
		String date = cursor.getString(cursor.getColumnIndex("_date"));
		String item = cursor.getString(cursor.getColumnIndex("_detail"));
		int type = cursor.getInt(cursor.getColumnIndex("_type"));
		MessageData tmp = new MessageData(from, to, item, type, null, date, id);
		if (type == MessageData.REQUIRE)
			tmp.setFileParam(findFileDataFromId(id));
		db.close();
		return tmp;
	}

	/**
	 * 查找最后插入的消息的id
	 * */
	public int findLastMsgId() {
		SQLiteDatabase db = getReadableDatabase();
		String sql = "select * from message where _id in (select max(_id) from message where _user='"
				+ XmppConnection.getInstance().getLocalUserAccount() + "')";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst() == false) {
			// 为空的Cursor
			return -1;
		}
		int id = cursor.getInt(cursor.getColumnIndex("_id"));
		cursor.close();
		db.close();
		return id;
	}

	/**
	 * 查找最后一条与opp的消息
	 * */
	public MessageData findLastMsg(String opp) {
		SQLiteDatabase db = getReadableDatabase();
		String sql = "select * from message where _id in ( select max(_id) from message where _user='"
				+ XmppConnection.getInstance().getLocalUserAccount()
				+ "' and ( _from = '" + opp + "' or _to = '" + opp + "' ) )";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst() == false) {
			// 为空的Cursor
			return null;
		}
		int id = cursor.getInt(cursor.getColumnIndex("_id"));
		// String user = cursor.getString(cursor.getColumnIndex("_user"));
		String from = cursor.getString(cursor.getColumnIndex("_from"));
		String to = cursor.getString(cursor.getColumnIndex("_to"));
		String date = cursor.getString(cursor.getColumnIndex("_date"));
		String item = cursor.getString(cursor.getColumnIndex("_detail"));
		int type = cursor.getInt(cursor.getColumnIndex("_type"));
		cursor.close();
		db.close();
		MessageData ans = new MessageData(from, to, item, type, null, date, id);
		if (type == MessageData.REQUIRE)
			ans.setFileParam(findFileDataFromId(id));
		return ans;
	}

	public void deleteMsg(int msgid) {
		SQLiteDatabase db = getWritableDatabase();
		String sql = "delete from message where _id =" + msgid;
		db.execSQL(sql);
		db.close();
		deleteFileMsg(msgid);
	}

	public void deleteMsg(String msgid) {
		SQLiteDatabase db = getWritableDatabase();
		String sql = "delete from message where _id =" + msgid;
		db.execSQL(sql);
		db.close();
		deleteFileMsg(msgid);
	}

	public void addMsg(MessageData msg) {
		String user = XmppConnection.getInstance().getLocalUserAccount();
		SQLiteDatabase sqlbase = getWritableDatabase();
		String sql = "insert into message values(NULL,'" + user + "','"
				+ msg.getSender() + "','" + msg.getAccepter() + "','"
				+ msg.getStrTime() + "','" + msg.getChatItem() + "',"
				+ msg.getChatType() + ")";
		sqlbase.execSQL(sql);
		sqlbase.close();
		if (msg.getChatType() == MessageData.REQUIRE) {
			MessageData last = findLastMsg(msg.getSender());
			addFileMsg(last, last.getChatItem(), "", 0);
		}

	}

	/*****************
	 * 处理用户头像信息 *
	 ***************** 
	 **/
	public void addAvtorPath(String user, String filename) {
		SQLiteDatabase db = getWritableDatabase();
		String sql = "insert into user values (NULL,'" + user + "','"
				+ filename + "')";
		db.execSQL(sql);
		db.close();
	}

	public void insertOrReplacePath(String user, String filename) {
		SQLiteDatabase db = getWritableDatabase();
		String sql = "insert or REPLACE INTO user(_name,_path) VALUES('" + user
				+ "','" + filename + "')";
		db.execSQL(sql);
		db.close();
	}

	public String findAvtorPath(String user) {
		String filename = null;
		SQLiteDatabase db = getReadableDatabase();
		String sql = "select * from user where _name='" + user + "'";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst() == false) {
			// 为空的Cursor
			return null;
		}
		filename = cursor.getString(cursor.getColumnIndex("_name"));
		return filename;
	}

	/**
	 * 操作file表
	 * */
	public void addFileMsg(MessageData msg, String filename, String savePath,
			int status) {
		// addMsg(msg);
		// MessageData last = findLastMsg(msg.getSender());
		String user = XmppConnection.getInstance().getLocalUserAccount();
		SQLiteDatabase db = getWritableDatabase();
		String sql = "insert into file values( '" + user + "' , '"
				+ msg.getSender() + "' , " + msg.getIndex() + " , '" + filename
				+ "', '" + savePath + "'," + status + ")";
		db.execSQL(sql);
		db.close();
	}

	public void deleteFileMsg(int fid) {
		SQLiteDatabase db = getWritableDatabase();
		String sql = "delete from file where _id = " + fid;
		db.execSQL(sql);
		db.close();
	}

	public void deleteFileMsg(String fid) {
		SQLiteDatabase db = getWritableDatabase();
		String sql = "delete from file where _id = " + fid;
		db.execSQL(sql);
		db.close();
	}

	public void updateFileMsg(int fid, String savepath, int status) {
		SQLiteDatabase db = getWritableDatabase();
		String sql = "update file set _path='" + savepath + "' , _status ="
				+ status + " where _id =" + fid;
		db.execSQL(sql);
		db.close();
	}

	public void updateFileMsgStr(int fid, String item) {
		SQLiteDatabase db = getWritableDatabase();
		String sql = "update message set _detail = '" + item + "' where _id = "
				+ fid;
		db.execSQL(sql);
		db.close();
	}

	public FileData findFileDataFromId(int qid) {
		SQLiteDatabase db = getReadableDatabase();
		String sql = "select * from file where _id = " + qid;
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst() == false) {
			// 为空的Cursor
			return null;
		}

		String user = cursor.getString(cursor.getColumnIndex("_user"));
		String from = cursor.getString(cursor.getColumnIndex("_from"));
		// int id = cursor.getInt(cursor.getColumnIndex("_id"));
		String name = cursor.getString(cursor.getColumnIndex("_name"));
		String path = cursor.getString(cursor.getColumnIndex("_path"));
		int status = cursor.getInt(cursor.getColumnIndex("_status"));

		return new FileData(user, from, name, path, status);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			String sql1 = "CREATE TABLE message ( _id INTEGER PRIMARY KEY AUTOINCREMENT, _user TEXT NOT NULL ,_from TEXT NOT NULL ,_to TEXT NOT NULL, _date TEXT NOT NULL ,_detail TEXT NOT NULL ,_type INTEGER)";
			db.execSQL(sql1);// 需要异常捕获
		} catch (Exception e) {

		}

		try {
			String sql2 = "CREATE TABLE user( _id INTEGER PRIMARY KEY AUTOINCREMENT ,_name TEXT NOT NULL unique,_path TEXT)";
			db.execSQL(sql2);// 需要异常捕获
		} catch (Exception e) {

		}
		try {
			String sql3 = "CREATE TABLE file ( _user TEXT NOT NULL , _from TEXT NOT NULL , _id INTEGER PRIMARY KEY , _name TEXT NOT NULL, _path TEXT NOT NULL, _status INTEGER)";
			db.execSQL(sql3);// 需要异常捕获
		} catch (Exception e) {

		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion != newVersion) {
			onCreate(db);
		}
	}

}
