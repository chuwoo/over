package com.test.chuwoo.luxin;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedList;

/**
 * Created by zhuqunwu on 2016/4/20.
 */
public class AudPlay extends Thread {
    private  AudioTrack m_out_trk;
    private  int m_out_buf_size;
    private  boolean m_keep_running;
    //private  DatagramPacket dp;
    private  DatagramSocket ds;
    //private InetAddress ip;
    private String data;
    //private  int port;
    //public static boolean Sendf=false;
    public static String resule=null;
    private LinkedList m_out_q;
    private byte []    m_out_bytes ;
    byte [] bytes_pkg;
    //private MyActivity handler;

    public AudPlay(DatagramSocket ds)  {
        this.ds=ds;
        m_keep_running=true;
        m_out_buf_size = android.media.AudioTrack.getMinBufferSize(8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        m_out_trk = new AudioTrack(AudioManager.STREAM_VOICE_CALL, 8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                m_out_buf_size,
                AudioTrack.MODE_STREAM);

        m_keep_running = true ;
        //this.port= 8124;
        //this.Sendf=false;

    }

    public  void run() {
        try {
            m_out_trk.play();
            //byte[] bta = new byte[1024];
            byte[] bte=new byte[640];
            DatagramPacket dp=new DatagramPacket(bte,640);
        //ds=new DatagramSocket(5555);
            Log.d("audplay","ok");
            while (true) {

                ds.receive(dp);


//                resule = new String(dp.getData(), dp.getOffset(), dp.getLength());
//                //int len=(""+resule.length());
//
//                try {
//                    JSONObject obj = new JSONObject(resule);
//                    bte=obj.getString("content").getBytes("ISO-8859-1");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

               // Log.d("udp rcv", resule);
                if(m_out_q==null){
                    m_out_q=new LinkedList();
                }
                m_out_q.add(bte);
                synchronized (m_out_q){
                    if(!m_out_q.isEmpty()){
                        m_out_bytes=(byte[])(m_out_q.removeFirst());
                        bytes_pkg=m_out_bytes.clone();
                         m_out_bytes=null;
                    }
                }
                if(bytes_pkg!=null){
                    m_out_trk.write(bytes_pkg,0,bytes_pkg.length);
            }
            bytes_pkg=null;
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //m_out_trk.stop();
        //m_out_trk.release();
        //m_out_trk=null;

        //System.out.println(resule);


    }


}
