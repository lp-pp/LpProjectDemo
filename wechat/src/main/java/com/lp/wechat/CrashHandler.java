package com.lp.wechat;

import android.content.Context;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * Created by LP on 2017/11/2.
 *全局异常处理
 */

public class CrashHandler implements UncaughtExceptionHandler{
    private static final String TAG = CrashHandler.class.getSimpleName();

    private static CrashHandler crashHandler = new CrashHandler();
    private Context mCxt;

    private CrashHandler(){}

    public static CrashHandler getInstance(){
        return crashHandler；
    }

    public void init(Context mContext) {

    }
}
