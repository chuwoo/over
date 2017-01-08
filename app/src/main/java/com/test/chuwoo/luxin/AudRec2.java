package com.test.chuwoo.luxin;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.LinkedList;


/**
 * Created by zhuqunwu on 2016/4/24.
 */
public class AudRec2 extends Thread{
    private AudioRecord m_in_rec ;
    private LinkedList m_in_q ;
    private int         m_in_buf_size ;
    private short []     m_in_bytes ;
    private byte []     m_in_bytes1 ;
    private boolean     m_keep_running ;
    private  String ipAdd;
    private int dk;
    private JSONObject obj;
    //private Socket s = null;
    private DatagramSocket ds;
    //private JSONObject obj;
    //ObjectOutputStream oos =null;
    private UdpSend us;
    AudRec2(DatagramSocket ds) {
        //System.out.println("测试1");
        this.ds=ds;



        //us=new UdpSend(ds);
        m_keep_running = true ;
        //this.ipAdd="162.254.7.164";
        //this.dk =8214;

        //System.out.println("s1");
       /* try {
			//s = new Socket(ipAdd,dk,null,4777);
			oos = new ObjectOutputStream(new BufferedOutputStream(s.getOutputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    }

    public void init(LinkedList data_q) {
        //m_in_q = data_q ;
        m_in_buf_size = android.media.AudioRecord.getMinBufferSize(8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        m_in_rec = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                m_in_buf_size) ;

        //m_in_bytes = new short [m_in_buf_size] ;
        m_in_bytes1 = new byte[m_in_buf_size];
        //System.out.println(data_q);
        //m_keep_running = true ;
        System.out.println("s2");
    }




    public void run() {
        int bytes_read ;
        //int i ;
        //String str=null;
        m_in_rec.startRecording() ;
        while(MyService.index==0) {
            bytes_read = m_in_rec.read(m_in_bytes1, 0, m_in_buf_size) ;
            Log.d("rec",""+m_in_buf_size);
            Log.d("rec2",""+m_in_bytes1.length);
            try {


                InetSocketAddress isa = new InetSocketAddress("54.200.165.24",8124);
                InetAddress add=isa.getAddress();
                DatagramPacket dp=new DatagramPacket(m_in_bytes1,m_in_bytes1.length,add,8124);
                ds.send(dp);
            }catch (Exception e){
                Log.d("socket","error");
            }


        }
        m_in_rec.stop() ;
        m_in_rec.release();
        m_in_rec = null ;
        m_in_bytes = null ;
    }
    public void logd(String s) {
        Log.d("AudRec", s) ;
    }
}
