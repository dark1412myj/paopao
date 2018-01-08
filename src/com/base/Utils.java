package com.base;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils {
	SharedPreferences sp=null;
	public static Utils util = new Utils();
	public static Utils getInstance()
	{
		return util;
	}
	Utils()
	{
		
	}
	public String getSharedPreferences(String filename,String key, Context context){
		SharedPreferences sp = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
		return sp.getString(key, "");
	}
}
