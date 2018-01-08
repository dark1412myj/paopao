package com.base;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.provider.Settings;

public class SystemUtils {
	/**
	 * ��ȡϵͳ��ǰʱ��
	 * */
	public static String getLocalTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		return df.format(date.getTime());
	}

	/**
	 * ��Date��ʽ�����ַ���
	 * */
	public static String transTime(Date date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(date.getTime());
	}

	/**
	 * ��ȡϵͳ����APPӦ��
	 * 
	 * @param context
	 */
	public static ArrayList<AppInfo> getAllApp(Context context) {
		PackageManager manager = context.getPackageManager();
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
		// ����ȡ����APP����Ϣ�����ֽ�������
		Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));
		ArrayList<AppInfo> appList = new ArrayList<AppInfo>();
		for (ResolveInfo info : apps) {
			AppInfo appInfo = new AppInfo();

			appInfo.setAppLable(info.loadLabel(manager) + "");
			appInfo.setAppIcon(info.loadIcon(manager));
			appInfo.setAppPackage(info.activityInfo.packageName);
			appInfo.setAppClass(info.activityInfo.name);
			appList.add(appInfo);
			System.out.println("info.activityInfo.packageName="
					+ info.activityInfo.packageName);
			System.out.println("info.activityInfo.name="
					+ info.activityInfo.name);
		}

		return appList;
	}

	/**
	 * ��ȡ�û���װ��APPӦ��
	 * 
	 * @param context
	 */
	public static ArrayList<AppInfo> getUserApp(Context context) {
		PackageManager manager = context.getPackageManager();
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
		// ����ȡ����APP����Ϣ�����ֽ�������
		Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));
		ArrayList<AppInfo> appList = new ArrayList<AppInfo>();
		for (ResolveInfo info : apps) {
			AppInfo appInfo = new AppInfo();
			ApplicationInfo ainfo = info.activityInfo.applicationInfo;
			if ((ainfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
				appInfo.setAppLable(info.loadLabel(manager) + "");
				appInfo.setAppIcon(info.loadIcon(manager));
				appInfo.setAppPackage(info.activityInfo.packageName);
				appInfo.setAppClass(info.activityInfo.name);
				appList.add(appInfo);
			}
		}

		return appList;
	}

	/**
	 * ���ݰ�����Activity�������ѯӦ����Ϣ
	 * 
	 * @param cls
	 * @param pkg
	 * @return
	 */
	public static AppInfo getAppByClsPkg(Context context, String pkg, String cls) {
		AppInfo appInfo = new AppInfo();

		PackageManager pm = context.getPackageManager();
		Drawable icon;
		CharSequence label = "";
		ComponentName comp = new ComponentName(pkg, cls);
		try {
			ActivityInfo info = pm.getActivityInfo(comp, 0);
			icon = pm.getApplicationIcon(info.applicationInfo);
			label = pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0));
		} catch (NameNotFoundException e) {
			icon = pm.getDefaultActivityIcon();
		}
		appInfo.setAppClass(cls);
		appInfo.setAppIcon(icon);
		appInfo.setAppLable(label + "");
		appInfo.setAppPackage(pkg);

		return appInfo;
	}

	/**
	 * ��ת��WIFI����
	 * 
	 * @param context
	 */
	public static void intentWifiSetting(Context context) {
		if (android.os.Build.VERSION.SDK_INT > 10) {
			// 3.0���ϴ����ý��棬Ҳ����ֱ����ACTION_WIRELESS_SETTINGS�򿪵�wifi����
			context.startActivity(new Intent(
					android.provider.Settings.ACTION_SETTINGS));
		} else {
			context.startActivity(new Intent(
					android.provider.Settings.ACTION_WIRELESS_SETTINGS));
		}
	}

	/**
	 * WIFI���翪��
	 * 
	 */
	public static void toggleWiFi(Context context, boolean enabled) {
		WifiManager wm = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		wm.setWifiEnabled(enabled);
	}

	/**
	 * �ƶ����翪��
	 */
	public static void toggleMobileData(Context context, boolean enabled) {
		ConnectivityManager conMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		Class<?> conMgrClass = null; // ConnectivityManager��
		Field iConMgrField = null; // ConnectivityManager���е��ֶ�
		Object iConMgr = null; // IConnectivityManager�������
		Class<?> iConMgrClass = null; // IConnectivityManager��
		Method setMobileDataEnabledMethod = null; // setMobileDataEnabled����
		try {
			// ȡ��ConnectivityManager��
			conMgrClass = Class.forName(conMgr.getClass().getName());
			// ȡ��ConnectivityManager���еĶ���mService
			iConMgrField = conMgrClass.getDeclaredField("mService");
			// ����mService�ɷ���
			iConMgrField.setAccessible(true);
			// ȡ��mService��ʵ������IConnectivityManager
			iConMgr = iConMgrField.get(conMgr);
			// ȡ��IConnectivityManager��
			iConMgrClass = Class.forName(iConMgr.getClass().getName());
			// ȡ��IConnectivityManager���е�setMobileDataEnabled(boolean)����
			setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod(
					"setMobileDataEnabled", Boolean.TYPE);
			// ����setMobileDataEnabled�����ɷ���
			setMobileDataEnabledMethod.setAccessible(true);
			// ����setMobileDataEnabled����
			setMobileDataEnabledMethod.invoke(iConMgr, enabled);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	/**
	 * GPS���� ��ǰ������� ��ǰ������ر�
	 */
	public static void toggleGPS(Context context) {
		Intent gpsIntent = new Intent();
		gpsIntent.setClassName("com.android.settings",
				"com.android.settings.widget.SettingsAppWidgetProvider");
		gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
		gpsIntent.setData(Uri.parse("custom:3"));
		try {
			PendingIntent.getBroadcast(context, 0, gpsIntent, 0).send();
		} catch (CanceledException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ����ϵͳ����
	 * 
	 * @param context
	 */
	public static void holdSystemAudio(Context context) {
		AudioManager audiomanage = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		// ��ȡϵͳ�������
		// int maxVolume =
		// audiomanage.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		// ��ȡ��ǰ����
		// int currentVolume =
		// audiomanage.getStreamVolume(AudioManager.STREAM_RING);
		// ��������
		// audiomanage.setStreamVolume(AudioManager.STREAM_SYSTEM,
		// currentVolume, AudioManager.FLAG_PLAY_SOUND);

		// ��������
		// ADJUST_RAISE ������������������������ͬ
		// ADJUST_LOWER ��������
		audiomanage.adjustStreamVolume(AudioManager.STREAM_SYSTEM,
				AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);

	}

	/**
	 * �������ȣ�ÿ30������
	 * 
	 * @param resolver
	 * @param brightness
	 */
	public static void setBrightness(Activity activity) {
		ContentResolver resolver = activity.getContentResolver();
		Uri uri = android.provider.Settings.System
				.getUriFor("screen_brightness");
		int nowScreenBri = getScreenBrightness(activity);
		nowScreenBri = nowScreenBri <= 225 ? nowScreenBri + 30 : 30;
		System.out.println("nowScreenBri==" + nowScreenBri);
		android.provider.Settings.System.putInt(resolver, "screen_brightness",
				nowScreenBri);
		resolver.notifyChange(uri, null);
	}

	/**
	 * ��ȡ��Ļ������
	 * 
	 * @param activity
	 * @return
	 */
	public static int getScreenBrightness(Activity activity) {
		int nowBrightnessValue = 0;
		ContentResolver resolver = activity.getContentResolver();
		try {
			nowBrightnessValue = android.provider.Settings.System.getInt(
					resolver, Settings.System.SCREEN_BRIGHTNESS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nowBrightnessValue;
	}

	/**
	 * ��ת��ϵͳ����
	 * 
	 * @param context
	 */
	public static void intentSetting(Context context) {
		String pkg = "com.android.settings";
		String cls = "com.android.settings.Settings";

		ComponentName component = new ComponentName(pkg, cls);
		Intent intent = new Intent();
		intent.setComponent(component);

		context.startActivity(intent);
	}

	/**
	 * ��ȡ�ļ����������ļ�
	 * 
	 * @param path
	 * @return
	 */
	public static ArrayList<File> getFilesArray(String path) {
		File file = new File(path);
		File files[] = file.listFiles();
		ArrayList<File> listFile = new ArrayList<File>();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {
					listFile.add(files[i]);
				}
				if (files[i].isDirectory()) {
					listFile.addAll(getFilesArray(files[i].toString()));
				}
			}
		}
		return listFile;
	}

	/**
	 * ��ȡ��Ƶ������ͼ ��ͨ��ThumbnailUtils������һ����Ƶ������ͼ��Ȼ��������ThumbnailUtils������ָ����С������ͼ��
	 * �����Ҫ������ͼ�Ŀ�͸߶�С��MICRO_KIND��������Ҫʹ��MICRO_KIND��Ϊkind��ֵ���������ʡ�ڴ档
	 * 
	 * @param videoPath
	 *            ��Ƶ��·��
	 * @param width
	 *            ָ�������Ƶ����ͼ�Ŀ��
	 * @param height
	 *            ָ�������Ƶ����ͼ�ĸ߶ȶ�
	 * @param kind
	 *            ����MediaStore.Images.Thumbnails���еĳ���MINI_KIND��MICRO_KIND��
	 *            ���У�MINI_KIND: 512 x 384��MICRO_KIND: 96 x 96
	 * @return ָ����С����Ƶ����ͼ
	 */
	public static Bitmap getVideoThumbnail(String videoPath, int width,
			int height, int kind) {
		Bitmap bitmap = null;
		// ��ȡ��Ƶ������ͼ
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		// System.out.println("w"+bitmap.getWidth());
		// System.out.println("h"+bitmap.getHeight());
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	/**
	 * ����Ƶ�ļ�
	 * 
	 * @param context
	 * @param file
	 *            ��Ƶ�ļ�
	 */
	public static void intentVideo(Context context, File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		String type = "video/*";
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, type);
		context.startActivity(intent);
	}
}
