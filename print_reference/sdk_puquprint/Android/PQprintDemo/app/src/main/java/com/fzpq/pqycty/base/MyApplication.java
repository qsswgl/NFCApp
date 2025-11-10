package com.fzpq.pqycty.base;

import android.app.Application;
import android.content.Context;

import com.puqu.sdk.PuQuPrintManager;


/**
 * Created by bw on 2018-02-17.
 * 公共全局变量
 */

public class MyApplication extends Application {
	private static MyApplication instance;
	private static Context appContext;
	private PuQuPrintManager printDeviceManager;


	@Override
	public void onCreate() {
		super.onCreate();
		printDeviceManager = new PuQuPrintManager(this);
		appContext =this.getApplicationContext();
		instance = this;
	}



	public static Context getAppContext() {
		return appContext;
	}
	public static MyApplication getInstance() {
		return instance;
	}

	public PuQuPrintManager getPrintDeviceManager() {
		return printDeviceManager;
	}

}
