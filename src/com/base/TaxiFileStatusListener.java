package com.base;

public interface TaxiFileStatusListener {
	final int WAIT = 1;
	final int SENDING = 2;
	final int COMPLETE = 3;
	final int INITIAL = 4;
	final int REFUSED = 5;

	/**
	 * 
	 * @param statusCode
	 *            状态码
	 * @param sendbyte
	 *            发送的字节数
	 * @param present
	 *            发送的进度
	 * @param filesize
	 *            文件字节数
	 * 
	 * */
	public void OnStatus(int statusCode, long sendbyte, double present,
			long filesize, String filename);
}
