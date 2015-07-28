package com.yuexiaohome.tempalarm.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class SysUtil
{
    private final Context context;

    public SysUtil(Context context)
    {
        this.context=context;
    }

    public boolean isServiceRunning(String serviceClassName)
    {
        //String serviceClassName="com.yuexiaohome.services.ClipboardMonitorService";
        boolean isServiceRunning=false;

        ActivityManager manager=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        for(
                ActivityManager.RunningServiceInfo service
                : manager.getRunningServices(Integer.MAX_VALUE))

        {

            if(serviceClassName.equals(service.service.getClassName()))
            {
                isServiceRunning=true;
                break;
            }

        }
        return isServiceRunning;
    }

    public String getVersionName()
    {
        String versionName="";
        PackageManager manager=context.getPackageManager();
        try
        {
            PackageInfo info=manager.getPackageInfo(context.getPackageName(),0);
            versionName=info.versionName;
        }catch(PackageManager.NameNotFoundException e)
        {
        }
        return versionName;
    }
}
