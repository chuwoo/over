package com.test.chuwoo.luxin;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.session.MediaButtonReceiver;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.LinkedList;

/**
 * Created by zhuqunwu on 2016/4/20.
 */
public class MyService extends Service {
    String LOG="MyService";
    private DatagramSocket socketUDP=null;
    private JSONObject obj;
    //private AudEncoder amrEncoder;
    private boolean audio=true;
    private AudRec2 m_recorder = null;
    private LinkedList<byte []> m_pkg_q;
    public static Integer index = 0;
    private AudioManager ar=null;
    //public static boolean trueFlers=true;
    //public UdpSend us;

    public  AudPlay ap =null;
    //private BluetoothAdapter mBluetoothAdapter;

    @Override
    public void onCreate() {
        //MyHandler handler = new MyHandler();
//初始化媒体(耳机)广播对象.
        MediaButtonReceiver mediaButtonReceiver = new MediaButtonReceiver();
//注册媒体(耳机)广播对象
        IntentFilter mediaFilter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
        mediaFilter.setPriority(100);                   //优先级
        registerReceiver(mediaButtonReceiver, mediaFilter);     //注册监听


        m_pkg_q = new LinkedList<byte[]>();
        //mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        obj=new JSONObject();
        ar=(AudioManager)MyService.this.getSystemService(Context.AUDIO_SERVICE);


        ar.setMicrophoneMute(true);             //麦克风静音



        try {
            socketUDP=new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        //ar.setSpeakerphoneOn(true);                 //打开扬声器
        new AudPlay(socketUDP).start();             //开始播放线程
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
           // new  UdpSend(socketUDP,message).start();

        //us.udprun(message);




        return  super.onStartCommand(intent,flags,startId);

    }

    @Override
    public IBinder onBind(Intent intent) {


        Log.d(LOG, "Bind exe");
        return  new MyUdp();
    }

    public  void senduse(String str){
        //new UdpSend(str).start();
        try {
            obj.put("name", str);
            obj.put("content", "");
            obj.put("action", "online");
        } catch (JSONException e) {
            e.printStackTrace();
        }//创建json对象
        new UdpSend(obj.toString(),socketUDP).start();



    }

    public void startAE(){
        if(audio){

            index=0;
            ar.setMicrophoneMute(false);        //麦克风静音为假
            m_pkg_q.clear();
            m_recorder = new AudRec2(socketUDP) ;
            m_recorder.init(m_pkg_q) ;
            if(ar.isBluetoothScoAvailableOffCall()){            //是否接了蓝牙
                ar.setBluetoothScoOn(true);
                ar.startBluetoothSco();
            }
            m_recorder.start() ;                //开始录音线程
            audio=false;
        }else{
            audio=true;
            index=1;
            ar.setMicrophoneMute(true);
            if(ar.isBluetoothScoOn()){
                ar.setBluetoothScoOn(false);
                ar.stopBluetoothSco();
            }

            }
        }






    public class MyUdp extends Binder {
        public MyService getService(){
            return MyService.this;
        }
    }
}

