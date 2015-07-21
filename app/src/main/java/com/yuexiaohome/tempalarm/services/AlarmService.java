package com.yuexiaohome.tempalarm.services;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.WindowManager;
import com.yuexiaohome.tempalarm.global.Setting;

public class AlarmService extends Service
{
    // 声明MediaPlayer对象
    private static MediaPlayer alarmMusic=new MediaPlayer();

    private PowerManager.WakeLock mWakelock;

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId)
    {
        PowerManager pm=(PowerManager)getSystemService(Context.POWER_SERVICE);
        mWakelock=pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,"SimpleTimer");
        mWakelock.acquire();

        int i=super.onStartCommand(intent,flags,startId);
        //Intent intent=getIntent();
        int id=intent.getIntExtra("id",0);
        System.out.println("id at Alarm Service2:"+id);

        NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(id);
        // 加载指定音乐，并为之创建MediaPlayer对象
        try
        {
            if(alarmMusic==null)
                alarmMusic=new MediaPlayer();
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

        String alarmText=Setting.getString("alerm_text","时间到了。");

        AlertDialog.Builder builder=new AlertDialog.Builder(AlarmService.this).setTitle("临时闹钟").setMessage(alarmText)
                .setPositiveButton("确定",new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface arg0,int arg1)
                    {
                        // 停止音乐播放
                        alarmMusic.stop();
                        alarmMusic=null;
                        // 结束该Activity
                        //AlarmService.this.finish();
                        AlarmService.this.stopSelf();
                    }
                });
        AlertDialog alertDialog=builder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if(mWakelock!=null)
        {
            mWakelock.release();
            mWakelock=null;
        }
    }

}
