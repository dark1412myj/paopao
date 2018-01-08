package com.mypro.myfile;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;

public class MyFileHelper {
	public static MyFileHelper helper = null;

	static Context context = null;

	public static void InitContext(Context tx) {
		if (context == null)
			context = tx;
	}

	public static synchronized MyFileHelper getInstance() {
		if (helper == null)
			helper = new MyFileHelper();
		return helper;
	}

	public boolean writeFile(String filename, byte[] data) {
		FileOutputStream outStream = null;
		try {
			outStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
			outStream.write(data);
			outStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			if (outStream != null)
				try {
					outStream.close();
				} catch (IOException e1) {
				}
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			if (outStream != null)
				try {
					outStream.close();
				} catch (IOException e1) {
				}
			return false;
		}
		return true;
	}

	public byte[] readFile(String filename) {
		try {
			FileInputStream inStream = context.openFileInput(filename);
			// inStream.
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length = -1;
			while ((length = inStream.read(buffer)) != -1) {
				stream.write(buffer, 0, length);
			}
			byte[] ans = stream.toByteArray();
			stream.close();
			inStream.close();
			return ans;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			return null;
		}
		return null;
	}
}
