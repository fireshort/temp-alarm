package com.yuexiaohome.tempalarm.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.WindowManager;

public class AlarmActivity extends Activity
{
    // 声明MediaPlayer对象
    private static MediaPlayer alarmMusic=new MediaPlayer();

    private PowerManager.WakeLock mWakelock;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent=getIntent();
        int id=intent.getIntExtra("id",0);
        System.out.println("id alarmactivity:"+id);

        NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(id);
        // 加载指定音乐，并为之创建MediaPlayer对象
        try
        {
            if(alarmMusic==null)alarmMusic=new MediaPlayer();
            if(!alarmMusic.isPlaying())
            {
                //alarmMusic.stop();
                alarmMusic.setDataSource(this,RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
                alarmMusic.prepare();
                alarmMusic.setLooping(true); // 设置为循环播放
                alarmMusic.start(); // 播放音乐
            }
        }catch(Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        AlertDialog.Builder builder=new AlertDialog.Builder(AlarmActivity.this).setTitle("临时闹钟").setMessage("悦悦、笑笑，时间到了。")
                .setPositiveButton("确定",new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface arg0,int arg1)
                    {
                        // 停止音乐播放
                        alarmMusic.stop();
                        alarmMusic=null;
                        // 结束该Activity
                        AlarmActivity.this.finish();
                    }
                });
        AlertDialog alertDialog=builder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if(mWakelock!=null)
        {
            mWakelock.release();
            mWakelock=null;
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        PowerManager pm=(PowerManager)getSystemService(Context.POWER_SERVICE);
        mWakelock=pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.SCREEN_DIM_WAKE_LOCK,"SimpleTimer");
        mWakelock.acquire();
        mWakelock.release();
        mWakelock=null;
    }
}