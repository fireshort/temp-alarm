package com.yuexiaohome.tempalarm.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

public class CancelAlarmActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent=getIntent();
        int id=intent.getIntExtra("id",0);
        System.out.println("id at the CancelAlarmActivity beginning:"+id);
        if(id>0)
        {
            NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            nm.cancel(id);

            System.out.println("id2 CancelAlarmActivity:"+id);
            AlarmManager alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
            Intent i=new Intent(CancelAlarmActivity.this,AlarmActivity.class);
            PendingIntent pi=PendingIntent.getActivity(this,id,i,0);
            alarmManager.cancel(pi);
            this.finish();
        }
    }
}