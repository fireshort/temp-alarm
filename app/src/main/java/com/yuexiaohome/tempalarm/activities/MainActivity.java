package com.yuexiaohome.tempalarm.activities;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.yuexiaohome.tempalarm.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends ActionBarActivity
{
    private AlarmManager alarmManager;

    private NotificationManager nm;

    private long lastSetAlarm=0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
        nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id=item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id==R.id.action_settings)
        {
            Intent intent=new Intent(this,SettingsActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setAlarm(int minute)
    {
        Calendar calendar=Calendar.getInstance();
        long currentMilliseconds=System.currentTimeMillis();
        if(currentMilliseconds-lastSetAlarm>4000)
        {
            lastSetAlarm=currentMilliseconds;

            calendar.setTimeInMillis(currentMilliseconds);
            calendar.add(Calendar.MINUTE,minute);

            Intent intent=new Intent(MainActivity.this,AlarmActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK); // 看是否有效果，在其他地方
            SimpleDateFormat dateformat=new SimpleDateFormat("HHmmssSSS");
            int id=Integer.valueOf(dateformat.format(calendar.getTime()));
            System.out.println("id at hellomini:"+id);
            intent.putExtra("id",id);
            PendingIntent pi=PendingIntent.getActivity(MainActivity.this,id,intent,0);
            alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pi);
            Toast.makeText(MainActivity.this,"闹钟设置成功。",Toast.LENGTH_SHORT).show();

            Notification baseNF=new Notification();
            // 设置通知在状态栏显示的图标
            baseNF.icon=R.drawable.ic_alarm_white_24dp;
            dateformat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
            // 通知时在状态栏显示的内容
            String contentText="已设闹钟："+dateformat.format(calendar.getTime())+" 点击取消。";
            baseNF.tickerText=contentText;
            // 通知的默认参数 DEFAULT_SOUND, DEFAULT_VIBRATE, DEFAULT_LIGHTS.
            baseNF.defaults=Notification.DEFAULT_SOUND;
            baseNF.flags|=Notification.FLAG_AUTO_CANCEL;
            baseNF.flags|=Notification.FLAG_NO_CLEAR;// 点击'Clear'时，不清除该通知
            // 第二个参数 ：下拉状态栏时显示的消息标题 expanded message title
            // 第三个参数：下拉状态栏时显示的消息内容 expanded message text
            // 第四个参数：点击该通知时执行页面跳转
            Intent i=new Intent(this,CancelAlarmActivity.class);
            i.putExtra("id",id);

            PendingIntent pd=PendingIntent.getActivity(this,id,i,PendingIntent.FLAG_UPDATE_CURRENT);
            baseNF.setLatestEventInfo(this,"临时闹钟",contentText,pd);
            // 发出状态栏通知
            // The first parameter is the unique ID for the Notification
            // and the second is the Notification object.
            nm.notify(id,baseNF);
        }else
        {

        }

    }

    @OnClick({R.id.text_set_alarm5,R.id.text_set_alarm8,R.id.text_set_alarm10,R.id.text_set_alarm15,
            R.id.text_set_alarm20,R.id.text_set_alarm30,R.id.text_set_alarm60,R.id.text_set_alarm90,
            R.id.text_set_alarm120})
    public void onClick(View v)
    {
        switch(v.getId())
        {
        case R.id.text_set_alarm5:
            setAlarm(5);
            break;
        case R.id.text_set_alarm8:
            setAlarm(8);
            break;
        case R.id.text_set_alarm10:
            setAlarm(10);
            break;
        case R.id.text_set_alarm15:
            setAlarm(15);
            break;
        case R.id.text_set_alarm20:
            setAlarm(20);
            break;
        case R.id.text_set_alarm30:
            setAlarm(30);
            break;
        case R.id.text_set_alarm60:
            setAlarm(60);
            break;
        case R.id.text_set_alarm90:
            setAlarm(90);
            break;
        case R.id.text_set_alarm120:
            setAlarm(120);
            break;
        }

    }
}