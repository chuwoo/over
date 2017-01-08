package com.test.chuwoo.luxin;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Created by zhuqunwu on 2016/4/20.
 */

public  class UdpSend extends Thread{
    private DatagramPacket dp;
    private  DatagramSocket socket;
    //private JSONObject obj;
    private InetAddress ip;
    private String data;
    private  int port;
    public   boolean sock=false;
    //private String msg;
    public UdpSend(String data,DatagramSocket socket)  {


            //this.ip=InetAddress.getByName("54.200.165.24");
            InetSocketAddress isa=new InetSocketAddress("54.200.165.24",8124);
         ip=isa.getAddress();
        this.data=data;
        this.socket=socket;

        port= 8124;
        //obj=new JSONObject();
        //this.msg=msg;

    }
    //public static String resule;
    public  void run(){
        //this.data=msg;



        dp=new DatagramPacket(data.getBytes(),data.getBytes().length,ip,port);
        try {
            socket.send(dp);
            //Log.d("udpsend","error");
        } catch (IOException e) {
            e.printStackTrace();
        }

        sock=false;
        Log.d("socket","ok");

    }

    //InetAddress ip=InetAddress.getByName(CommonConfig.SERVER_IP_ADDRESS);
    //int port=CommonConfig.SERVER_PORT;
    //byte[] sendBuffer=useSting.getBytes();
    //DatagramSocket udpSocket=new DatagramSocket(4567) ;
    //DatagramPacket packet=new DatagramPacket(sendBuffer, sendBuffer.length, ip, port);

    //udpSocket.send(packet);


}
