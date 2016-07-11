package cn.zhaoyuening.tcpconnect.runnable;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import cn.zhaoyuening.tcpconnect.MainActivity;

/**
 * Created by Zhao on 2016/7/11.
 */
public class SocketServerRunnable implements Runnable {
    private int mPort;
    private Handler mMainThreadHandler;

    public SocketServerRunnable(int mPort, Handler mMainThreadHandler) {
        this.mPort = mPort;
        this.mMainThreadHandler = mMainThreadHandler;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(mPort);
            //将serverSocket 传递给serverSocket
            Message m = Message.obtain();
            m.what = MainActivity.SET_SERVERSOCKET;
            m.obj=serverSocket;
            mMainThreadHandler.sendMessage(m);
            //监听客服端请求
            while(serverSocket!=null&&!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                m = Message.obtain();
                m.what = MainActivity.ADD_CLIENTSOCKET;
                m.obj = socket;
                mMainThreadHandler.sendMessage(m);
            }
            Socket socket = serverSocket.accept();
        } catch (IOException e) {
            //连接失败
            Message m = Message.obtain();
            m.what = MainActivity.CONNECT_FAIL;
            m.obj = "绑定端口失败";
            mMainThreadHandler.sendMessage(m);
            e.printStackTrace();
        }
    }
}
