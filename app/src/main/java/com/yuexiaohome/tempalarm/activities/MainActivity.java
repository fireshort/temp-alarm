package com.yuexiaohome.tempalarm.activities;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.yuexiaohome.tempalarm.R;
import com.yuexiaohome.tempalarm.fragments.InputMinuteFragment;
import com.yuexiaohome.tempalarm.receivers.AlarmBroadcastReceiver;
import com.yuexiaohome.tempalarm.utils.ImageUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends ActionBarActivity implements InputMinuteFragment.InputMinuteFragmentListener
{
    private AlarmManager alarmManager;

    private NotificationManager nm;

    private long lastSetAlarm=0;

    private TextDrawable.IBuilder builder;

    private String[] minutes={"5","8","10","15","20","30","C"};

    private int[] ids={R.id.text_set_alarm5,R.id.text_set_alarm8,R.id.text_set_alarm10,R.id.text_set_alarm15,
            R.id.text_set_alarm20,R.id.text_set_alarm30,R.id.text_set_alarm_custom};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
        nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        ColorGenerator generator=ColorGenerator.MATERIAL; // or use DEFAULT
// declare the builder object once.
        builder=TextDrawable.builder()
                .beginConfig()
                //.withBorder(4)
                .width(120)  // width in px
                .height(120) // height in px
                .endConfig()
                .round();
        for(int i=0; i<minutes.length; i++)
        {
            TextDrawable textDrawable=builder.build(minutes[i],generator.getColor(minutes[i]));

            textDrawable.setBounds(0,0,textDrawable.getMinimumWidth(),textDrawable.getMinimumHeight());
            ((TextView)ButterKnife.findById(this,ids[i])).setCompoundDrawables(textDrawable,null,null,null);
        }


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

            //Intent intent=new Intent(MainActivity.this,AlarmActivity.class);
            Intent intent=new Intent(MainActivity.this,AlarmBroadcastReceiver.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK); // 看是否有效果，在其他地方
            SimpleDateFormat dateformat=new SimpleDateFormat("HHmmssSSS");
            int id=Integer.valueOf(dateformat.format(calendar.getTime()));
            System.out.println("id at hellomini:"+id);
            intent.putExtra("id",id);

            //PendingIntent pi=PendingIntent.getActivity(MainActivity.this,id,intent,0);
            PendingIntent pi=PendingIntent.getBroadcast(MainActivity.this,id,intent,0);

            alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pi);
            Toast.makeText(MainActivity.this,"闹钟设置成功。",Toast.LENGTH_SHORT).show();

            Intent i=new Intent(this,CancelAlarmActivity.class);
            i.putExtra("id",id);
            PendingIntent pd=PendingIntent.getActivity(this,id,i,PendingIntent.FLAG_UPDATE_CURRENT);


            TextDrawable textDrawable=builder.build(String.valueOf(minute),ColorGenerator.MATERIAL.getColor(String.valueOf(minute)));

            dateformat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
            // 通知时在状态栏显示的内容
            String contentText="已设闹钟："+dateformat.format(calendar.getTime())+" 点击取消。";
            NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(MainActivity.this);
            mBuilder.setTicker(contentText)//通知首次出现在通知栏，带上升动画效果的
                    .setContentTitle(getResources().getString(R.string.app_name))//设置通知栏标题
                    .setContentText(contentText) //设置通知栏显示内容
                    .setContentIntent(pd) //设置通知栏点击意图
//  .setNumber(number) //设置通知集合的数量
                            //.setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                    .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级 Notification.PRIORITY_MIN
//  .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                    .setOngoing(true)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                    .setDefaults(Notification.DEFAULT_SOUND)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                            //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                    .setLargeIcon(ImageUtil.drawableToBitmap(textDrawable))
                    .setSmallIcon(R.drawable.ic_alarm_white_24dp);//设置通知小ICON
            Notification notification=mBuilder.build();
            notification.flags|=Notification.FLAG_AUTO_CANCEL;
            notification.flags |= Notification.FLAG_NO_CLEAR;// 点击'Clear'时，不清除该通知

            // 发出状态栏通知
            // The first parameter is the unique ID for the Notification and the second is the Notification object.
            nm.notify(id, notification);

        }else
        {

        }

    }

    @OnClick({R.id.text_set_alarm5,R.id.text_set_alarm8,R.id.text_set_alarm10,R.id.text_set_alarm15,
            R.id.text_set_alarm20,R.id.text_set_alarm30,R.id.text_set_alarm_custom})
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
        case R.id.text_set_alarm_custom:
            DialogFragment fragment = new InputMinuteFragment();
            //fragment.setArguments(bundle);
            fragment.show(getSupportFragmentManager(),null);
            break;
        }

    }

    @Override
    public void onFinishInputMinute(String inputText) {
        //Toast.makeText(this, "Hi, " + inputText, Toast.LENGTH_SHORT).show();
        setAlarm(Integer.valueOf(inputText));
    }
}