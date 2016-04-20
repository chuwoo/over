package com.test.chuwoo.luxin;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MyActivity extends Activity {
    public final static String EXTRA_MESSAGE = "com.test.zhuqunwu.duijiangji";
    private MyService myService;
    private AudEncoder amrEncoder;
    private MyService msgServer;
    private boolean flag = true;

    /***
     * private ServiceConnection conn=new ServiceConnection() {
     *
     * @Override public void onServiceConnected(ComponentName name, IBinder service) {
     * msgServer=((MyService.MyUdp)service).getService();
     * //msgServer.senduser();
     * }
     * @Override public void onServiceDisconnected(ComponentName name) {
     * <p/>
     * }
     * };
     ****/
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        Intent intent = new Intent(this, MyService.class);    //构建一个intent
        bindService(intent, conn, Context.BIND_AUTO_CREATE);

        //intent.putExtra(EXTRA_MESSAGE, message);             //把用户内容传给intert
        //startService(intent);
        //Log.d(TAG, "exe");

        //ap.init(new byte[640]);


    }

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            msgServer = ((MyService.MyUdp) service).getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendMessage(View view) {         //按钮按下的执行函数
        if (flag) {
            amrEncoder = AudEncoder.getAudEncoderInstance();
            //Log.d("mail","audiostart");
            amrEncoder.start();
            flag = false;
        } else if (amrEncoder != null) {
            Log.d("mail", "audiostop");
            //amrEncoder.stop();
            flag = true;
        }
    }
}