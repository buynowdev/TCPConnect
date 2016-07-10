package cn.zhaoyuening.tcpconnect.runnable;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Zhao on 2016/7/10.
 */
public class SendContentRunnable implements Runnable {

    private String mContent;
    private Socket mSocket;

    public SendContentRunnable(String mContent, Socket mSocket) {
        this.mContent = mContent;
        this.mSocket = mSocket;
    }

    @Override
    public void run() {
        try {
            OutputStream out = mSocket.getOutputStream();
            out.write(mContent.getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
