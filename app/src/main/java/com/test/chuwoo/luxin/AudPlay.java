package com.test.chuwoo.luxin;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by zhuqunwu on 2016/4/20.
 */
public class AudPlay extends Thread {
    //private  DatagramPacket dp;
    private  DatagramSocket ds;
    //private InetAddress ip;
    private String data;
    //private  int port;
    //public static boolean Sendf=false;
    public static String resule=null;
    //private MyActivity handler;

    public AudPlay(DatagramSocket ds)  {
        this.ds=ds;
        //this.port= 8124;
        //this.Sendf=false;

    }

    public  void run() {

        //ds=new DatagramSocket(5555);
        Log.d("audplay","ok");
        while (true) {

            byte[] bte = new byte[1024];
            //DatagramSocket ds=new DatagramSocket(5555);


            DatagramPacket dp = new DatagramPacket(bte, bte.length);


            try {
                ds.receive(dp);
            } catch (IOException e) {
                e.printStackTrace();
            }
            resule = new String(dp.getData(), dp.getOffset(), dp.getLength());
            Log.d("udp rcv", resule);
        }

        //System.out.println(resule);


    }


}
