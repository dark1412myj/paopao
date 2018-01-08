package com.base;

import java.io.File;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;

import com.mypro.mymsg.MessageDispatcher;

import android.os.Environment;
import android.util.Log;

public class ChatFileTransferListener implements FileTransferListener {

	@Override
	public void fileTransferRequest(FileTransferRequest request) {
		// request.getDescription();
		Log.e("file",
				"" + request.getDescription() + "??" + request.getFileName());

		MessageDispatcher.getInstance().dispatchFileRecvMessage(request);
		// request.reject();
		//
		// try {
		// Thread.sleep(60000 * 10);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// try {
		// File insFile = new File(Environment.getExternalStorageDirectory()
		// + "/" + request.getFileName());
		// IncomingFileTransfer infiletransfer = request.accept();
		// infiletransfer.recieveFile(insFile);
		//
		// // sendBroadcastFile(context, insFile.getAbsolutePath());
		// } catch (XMPPException e) {
		// e.printStackTrace();
		// }
	}
}
