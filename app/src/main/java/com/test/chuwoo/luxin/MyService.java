package com.test.chuwoo.luxin;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by zhuqunwu on 2016/4/20.
 */
public class MyService extends Service {
    String LOG="MyService";
    private DatagramSocket socketUDP=null;
    private JSONObject obj;
    //public  AudPlay ap =null;
    @Override
    public void onCreate() {

        obj=new JSONObject();

        try {
            socketUDP=new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        new AudPlay(socketUDP).start();
        //start();
        super.onCreate();
        Log.d(LOG,"onCreate ");
    }

    //private DatagramSocket socketUDP=null;
    //private JSONObject obj;
    //public  AudPlay ap =null;
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d(LOG,"Command exe");

        String message=intent.getStringExtra(MyActivity.EXTRA_MESSAGE);

        try {
            obj.put("name", message);
            obj.put("content", "");
            obj.put("action", "online");
        } catch (JSONException e) {
            e.printStackTrace();
        }//创建json对象
        try {
            new UdpSend(socketUDP,obj.toString()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }



        return  super.onStartCommand(intent,flags,startId);

    }

    @Override
    public IBinder onBind(Intent intent) {


        Log.d(LOG, "Bind exe");
        return  new MyUdp();
    }

    public  void senduse(String str){
        try {
            obj.put("name", str);
            obj.put("content", "");
            obj.put("action", "online");
        } catch (JSONException e) {
            e.printStackTrace();
        }//创建json对象
        try {
            new UdpSend(socketUDP,obj.toString()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void sendmsg(String str) {
        try {
            //obj.put("name", "test");
            obj.put("content",str );
            obj.put("action", "chat");
        } catch (JSONException e) {
            e.printStackTrace();
        }//创建json对象
        try {
            new UdpSend(socketUDP,obj.toString()).start();        //把json用udp发送到网络
        } catch (IOException e) {
            e.printStackTrace();
        }
        //TextView tv=(TextView)findViewById(R.id.textView);
        //tv.setText("这是一个测试");
        //System.out.println("这是一个测试");
    }


    public class MyUdp extends Binder {
        public MyService getService(){
            return MyService.this;
        }
    }
}

