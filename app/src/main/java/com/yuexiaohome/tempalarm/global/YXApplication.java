package com.yuexiaohome.tempalarm.global;

import android.app.Application;

public class YXApplication extends Application
{
    private static Application instance;

    public static Application getInstance()
    {
        return instance;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        instance=this;

    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
    }
}

