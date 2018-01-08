package com.mypro.mymsg;

import java.io.File;
import java.util.Date;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;

import android.graphics.drawable.Drawable;
import android.os.Environment;

public class MessageData {
	public static final int RECEIVE = 0;
	public static final int SENDER = 1;
	public static final int WARN = 2;
	public static final int REQUIRE = 3;
	private String sender;
	private String accepter;
	private String chatItem;
	// private Date time;
	private String strTime;
	private int chatType;
	private Drawable avtor;
	private int index;

	private FileData fileParam;

	public void startMessageLoop(FileTransferRequest request) {
		while (fileParam.getStatus() == 0) {
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(0);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}

		if (fileParam.getStatus() == -1)
			request.reject();
		else {
			File insFile = new File(Environment.getExternalStorageDirectory()
					+ "/" + request.getFileName());
			IncomingFileTransfer infiletransfer = request.accept();
			try {
				infiletransfer.recieveFile(insFile);
			} catch (XMPPException e) {
				e.printStackTrace();
			}
		}

	}

	public FileData getFileParam() {
		return fileParam;
	}

	public void setFileParam(FileData fileParam) {
		this.fileParam = fileParam;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getStrTime() {
		return strTime;
	}

	public void setStrTime(String strTime) {
		this.strTime = strTime;
	}

	public MessageData(String sender, String accepter, String chatItem,
			int chatType, Drawable avtor, String tm, int id) {
		this.sender = sender;
		this.accepter = accepter;
		this.chatItem = chatItem;
		this.chatType = chatType;
		this.avtor = avtor;
		this.strTime = tm;
		this.index = id;
	}

	// public Date getTime() {
	// return time;
	// }
	//
	// public void setTime(Date time) {
	// this.time = time;
	// }

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getAccepter() {
		return accepter;
	}

	public void setAccepter(String accepter) {
		this.accepter = accepter;
	}

	public String getChatItem() {
		return chatItem;
	}

	public void setChatItem(String chatItem) {
		this.chatItem = chatItem;
	}

	public int getChatType() {
		return chatType;
	}

	public void setChatType(int chatType) {
		this.chatType = chatType;
	}

	public Drawable getAvtor() {
		return avtor;
	}

	public void setAvtor(Drawable avtor) {
		this.avtor = avtor;
	}

}
