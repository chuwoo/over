package com.test.chuwoo.luxin;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by zhuqunwu on 2016/4/20.
 */
public class UdpSend extends  Thread {
    private DatagramPacket dp;
    private  DatagramSocket ds;
    private InetAddress ip;
    private String data;
    private  int port;
    public UdpSend(DatagramSocket ds,String data) throws IOException {
        this.ds=ds;
        this.data=data;
        this.ip=InetAddress.getByName("162.254.7.164");
        this.port= 8124;

    }
    //public static String resule;
    public  void run(){

        try{
            //ds=new DatagramSocket(5555);

            dp=new DatagramPacket(data.getBytes(),data.getBytes().length,ip,port);
            ds.send(dp);

        }catch (SocketException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        //ds.close();

    }

    //InetAddress ip=InetAddress.getByName(CommonConfig.SERVER_IP_ADDRESS);
    //int port=CommonConfig.SERVER_PORT;
    //byte[] sendBuffer=useSting.getBytes();
    //DatagramSocket udpSocket=new DatagramSocket(4567) ;
    //DatagramPacket packet=new DatagramPacket(sendBuffer, sendBuffer.length, ip, port);

    //udpSocket.send(packet);


}
