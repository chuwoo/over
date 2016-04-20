package com.test.chuwoo.luxin;


import android.media.MediaRecorder;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by zhuqunwu on 2016/4/1.
 */
public class AudEncoder {
    private static final String TAG = "audEncode";
    String LOG="AudEncoder";
    private static  AudEncoder audEncoder=null;
    //private Activity activity;
    private boolean isAudioRecording=false;
    private LocalServerSocket lss;
    //private LocalSocket receiver;
    private LocalSocket sender,receiver;        //基于文件的流，
    private MediaRecorder audioRecorder;
    //private static final String TAG="AudioEncoder";

    public static AudEncoder getAudEncoderInstance(){

        if(audEncoder==null){
            synchronized (AudEncoder.class){
                if(audEncoder==null){
                    audEncoder=new AudEncoder();
                    Log.d("getInstance","new Audio");
                }
            }
        }
        return audEncoder;
    }



    public  void  start(){

        if(isAudioRecording){
            Log.d(LOG,"音频已经开始编码，无需再次编码");
            return;
        }

        if(!initLocalSocket()){
            Log.d(LOG,"localSocket falt");
            releaseAll();
            return;
        }

        if(!initAudioRecorder()){
            Log.d(LOG,"initAudioRecorder falt");
            releaseAll();
            return;
        }

        this.isAudioRecording=true;
        Log.d(LOG, "strat rcv");
        startAudioRecording();//开始录音
    }


    private void startAudioRecording() {                        //录音线程
        new Thread(new AudioCaptureAndSendThread()).start();

    }
    public void stop(){
        if(isAudioRecording){
            isAudioRecording=false;
        }
        releaseAll();
    }

    private boolean initAudioRecorder() {
        if(audioRecorder !=null){
            Log.d(LOG,"audioRecord!=null");
            audioRecorder.reset();
            audioRecorder.release();
        }else {
            Log.d(LOG,"no has a proect");
            return true;
        }
        audioRecorder=new MediaRecorder();
        if(audioRecorder!=null){
            Log.d(LOG,"has a proect");
        }
        audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        final  int mono=1;
        audioRecorder.setAudioChannels(mono);               //单声道
        audioRecorder.setAudioSamplingRate(8000);           //取样频率
        audioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        audioRecorder.setOutputFile(sender.getFileDescriptor());
        Log.d(LOG,"audio setup ok");
        boolean ret=true;
        try {
            audioRecorder.prepare();
            //Log.d(LOG,"prepare ok");
            audioRecorder.start();

        }catch (Exception e){
            releaseMediaRecorder();
            Log.d(LOG,"not record");
            ret =false;
        }
        return ret;
    }

    private void releaseAll() {
        releaseMediaRecorder();
        releaseLocalSocker();
        audEncoder=null;
    }

    private void releaseMediaRecorder() {
        try{
            if(audioRecorder==null){
                return;
            }
            if(isAudioRecording){
                Log.d(LOG,"reco.stop ok");
                audioRecorder.stop();
                isAudioRecording=false;
            }
            audioRecorder.reset();
            audioRecorder.release();
            audioRecorder=null;
        }catch (Exception err){
            Log.e(TAG, "relRecoErro");
        }
    }

    private boolean initLocalSocket() {
        boolean ret=true;
        try {
            releaseLocalSocker();
            String serverName="armAudioServer";
            final int bufSize=1024;
            lss=new LocalServerSocket(serverName);
            receiver=new LocalSocket();
            receiver.connect(new LocalSocketAddress(serverName));
            receiver.setReceiveBufferSize(bufSize);
            receiver.setSendBufferSize(bufSize);
            sender=lss.accept();
            sender.setReceiveBufferSize(bufSize);
            sender.setSendBufferSize(bufSize);
        }catch (IOException e){
            ret=false;
        }
        return ret;
    }


    private void releaseLocalSocker() {
        try{
            if(sender !=null){
                sender.close();
            }
            if(receiver !=null){
                receiver.close();
            }
            if(lss !=null){
                lss.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        sender=null;
        receiver=null;
        lss=null;
    }
    //private void showToastTest(String msg) {
    //Toast.makeText(activity,msg,Toast.LENGTH_SHORT).show();
    //}


    private class AudioCaptureAndSendThread implements Runnable {       //录音线程
        public  void run(){
            try{
                sendAmrAudio();
            }catch (Exception e){
                Log.e(TAG,"sendAmrAudio() 出错");
            }
        }

        private void sendAmrAudio() throws Exception{
            //DatagramSocket udpSocket=new DatagramSocket();
            Log.d(TAG,"xian cheng streat");
            DataInputStream dataInput=new DataInputStream(receiver.getInputStream());
            skipAmrHead(dataInput);
            final int SEND_FRAME_COUNT_ONE_TIME=10;
            final int BLOCK_SIZE[]={12,13,15,17,19,20,26,31,5,0,0,0,0,0,0,0};
            byte[] sendBuffer=new byte[1024];
            Log.d(LOG,"eocode1");
            while (isAudioRecording()){
                Log.d(LOG,"encode2");
                int offset=0;
                for (int index=0;index<SEND_FRAME_COUNT_ONE_TIME;++index){
                    if(!isAudioRecording()){
                        break;
                    }
                    dataInput.read(sendBuffer,offset,1);
                    int blockIndex=(int)(sendBuffer[offset]>>3)&0x0f;
                    int frameLength=BLOCK_SIZE[blockIndex];
                    readSomeData(sendBuffer,offset+1,frameLength,dataInput);
                    offset+=frameLength+1;
                }
                Log.d(LOG, "encode end");
                //udpSend(udpSocket, sendBuffer, offset);
            }
            //udpSocket.close();
            dataInput.close();
            releaseAll();
        }
        private void skipAmrHead(DataInputStream dataInput) {
            final byte[] AMR_HEAD = new byte[] { 0x23, 0x21, 0x41, 0x4D, 0x52, 0x0A };
            int result = -1;
            int state = 0;
            try {
                while (-1 != (result = dataInput.readByte())) {
                    if (AMR_HEAD[0] == result) {
                        state = (0 == state) ? 1 : 0;
                    } else if (AMR_HEAD[1] == result) {
                        state = (1 == state) ? 2 : 0;
                    } else if (AMR_HEAD[2] == result) {
                        state = (2 == state) ? 3 : 0;
                    } else if (AMR_HEAD[3] == result) {
                        state = (3 == state) ? 4 : 0;
                    } else if (AMR_HEAD[4] == result) {
                        state = (4 == state) ? 5 : 0;
                    } else if (AMR_HEAD[5] == result) {
                        state = (5 == state) ? 6 : 0;
                    }

                    if (6 == state) {
                        break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "read mdat error...");
            }
        }
        private void readSomeData(byte[] buffer, int offset, int length, DataInputStream dataInput) {
            int numOfRead = -1;
            while (true) {
                try {
                    numOfRead = dataInput.read(buffer, offset, length);
                    if (numOfRead == -1) {
                        Log.d(TAG, "amr...no data get wait for data coming.....");
                        Thread.sleep(100);
                    } else {
                        offset += numOfRead;
                        length -= numOfRead;
                        if (length <= 0) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "amr..error readSomeData");
                    break;
                }
            }
        }
/***
 private void udpSend(DatagramSocket udpSocket, byte[] buffer, int sendLength) {
 try {
 InetAddress ip = CommonConfig.SERVER_IP_ADDRESS;
 int port = CommonConfig.SERVER_PORT;

 byte[] sendBuffer = new byte[sendLength];
 System.arraycopy(buffer, 0, sendBuffer, 0, sendLength);

 DatagramPacket packet = new DatagramPacket(sendBuffer, sendLength);
 packet.setAddress(ip);
 packet.setPort(port);
 udpSocket.send(packet);
 } catch (IOException e) {
 e.printStackTrace();
 }
 }
 ****/
    }

    private boolean isAudioRecording() {
        return isAudioRecording;

    }


}
