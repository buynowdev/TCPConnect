package cn.zhaoyuening.tcpconnect.runnable;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import cn.zhaoyuening.tcpconnect.MainActivity;

/**
 * Created by Zhao on 2016/7/10.
 */
public class SocketClientRunnable implements Runnable {
    private String mIP;
    private int mPort;
    private Handler mMainThreadHandler;

    /**
     *
     * @param IP 访问的服务端 ip地址
     * @param port 访问的服务端 端口号
     * @param mainThreadHandler 主线程的handler
     */
    public SocketClientRunnable(String IP, int port, Handler mainThreadHandler) {
        this.mIP = IP;
        this.mPort = port;
        this.mMainThreadHandler = mainThreadHandler;
    }

    @Override
    public void run() {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(InetAddress.getByName(mIP), mPort));
            //链接成功
            Message msg = Message.obtain();
            msg.what= MainActivity.SET_SOCKET;
            msg.obj=socket;
            //传递socket给主线程
            mMainThreadHandler.sendMessage(msg);

        } catch (IOException e) {
            //连接失败
            Message msg = Message.obtain();
            msg.obj="无法连接服务端";
            mMainThreadHandler.sendMessage(msg);
            e.printStackTrace();
        }
    }
}
