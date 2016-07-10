package cn.zhaoyuening.tcpconnect.runnable;

import android.os.Handler;

import java.net.Socket;

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
        String port = mSocket.getPort()+"";

    }
}
