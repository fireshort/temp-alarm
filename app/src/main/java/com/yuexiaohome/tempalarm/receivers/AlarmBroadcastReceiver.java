package com.yuexiaohome.tempalarm.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.yuexiaohome.tempalarm.services.AlarmService;
import com.yuexiaohome.tempalarm.utils.SysUtil;

public class AlarmBroadcastReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context,Intent intent)
    {

        SysUtil sysUtil=new SysUtil(context);
        boolean isServiceRunning=sysUtil.isServiceRunning("com.yuexiaohome.tempalarm.services.AlarmService");
        if(!isServiceRunning)
        {
            Intent i=new Intent(context,AlarmService.class);
            System.out.println("id at AlarmBroadcastReceiver:"+intent.getIntExtra("id",0));
            i.putExtra("id",intent.getIntExtra("id",0));
            context.startService(i);
        }
    }
}
