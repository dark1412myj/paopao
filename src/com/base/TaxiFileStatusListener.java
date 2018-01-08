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
	 *            ״̬��
	 * @param sendbyte
	 *            ���͵��ֽ���
	 * @param present
	 *            ���͵Ľ���
	 * @param filesize
	 *            �ļ��ֽ���
	 * 
	 * */
	public void OnStatus(int statusCode, long sendbyte, double present,
			long filesize, String filename);
}
