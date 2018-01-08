package com.mypro.mymsg;

public class FileData {
	private String user;
	private String from;
	private String filename;
	private String path;
	private int status;
	
	public FileData(String user, String from, String filename, String path,
			int status) {
		super();
		this.user = user;
		this.from = from;
		this.filename = filename;
		this.path = path;
		this.status = status;
	}
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

}
