package cn.zhaoyuening.tcpconnect.runnable;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import cn.zhaoyuening.tcpconnect.MainActivity;
import cn.zhaoyuening.tcpconnect.domain.Msg;

/**
 * Created by Zhao on 2016/7/10.
 */
public class GetMsgRunnable implements Runnable {
    private Socket mSocket;
    private Handler mMainThreadHandler;

    public GetMsgRunnable(Socket mSocket, Handler mMainThreadHandler) {
        this.mSocket = mSocket;
        this.mMainThreadHandler = mMainThreadHandler;
    }

    @Override
    public void run() {
        String ip = mSocket.getInetAddress().getHostAddress();
        int port = mSocket.getPort();
        try {
            InputStream in = mSocket.getInputStream();
            byte[] buf = new byte[1024];
            int len = 0;
            while((mSocket!=null&&!mSocket.isClosed())&&(len = in.read(buf))>0){
                String content = new String(buf,0,len);
                Msg msg = new Msg(ip,port,Msg.GET,content);
                Message message = Message.obtain();
                message.what = MainActivity.GET_MSG;
                message.obj = msg;
                mMainThreadHandler.sendMessage(message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
