package com.yuexiaohome.tempalarm.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.yuexiaohome.tempalarm.R;
import com.yuexiaohome.tempalarm.utils.SysUtil;

public class AboutActivity extends ActionBarActivity
{
    @Bind(R.id.about_temp_alarm)
    TextView textAboutAlarm;

    @Bind(R.id.text_remark)
    TextView textRemark;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SysUtil sysUtil=new SysUtil(this);
        textAboutAlarm.setText(getString(R.string.app_name)+" v"+sysUtil.getVersionName());
        textRemark.setText(Html.fromHtml(getString(R.string.remark)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_about,menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
