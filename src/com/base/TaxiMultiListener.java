package com.base;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

/**
 * ��������Ϣ������
 * 
 * @author Administrator
 * 
 */
public class TaxiMultiListener implements PacketListener {

	@Override
	public void processPacket(Packet packet) {
		Message message = (Message) packet;
		String body = message.getBody();
	}
	
}
