package com.lp.wechat;

import android.content.Context;

import com.baidu.frontia.FrontiaApplication;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;

/**
 * Created by LP on 2017/11/2.
 */

public class WcApp extends FrontiaApplication{
    private static final String TAG = WcApp.class.getSimpleName();

    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        initEMChat();
        EMChat.getInstance().init(mContext);
        EMChat.getInstance().setDebugMode(true);
        EMChat.getInstance().setAutoLogin(true);
        EMChatManager.getInstance().getChatOptions().setUseRoster(true);
        FrontiaApplication.initFrontiaApplication(this);
        //CrashHandler crashHandler = CrashHandler.getInstance();// 全局异常捕捉
        //crashHandler.init(mContext);
    }

    private void initEMChat() {
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);

    }

    private String getAppName(int pid) {

    }
}
