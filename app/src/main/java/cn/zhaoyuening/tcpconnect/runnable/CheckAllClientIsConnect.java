package cn.zhaoyuening.tcpconnect.runnable;

import java.net.Socket;
import java.util.List;

/**
 * Created by Zhao on 2016/7/11.
 */
public class CheckAllClientIsConnect implements Runnable{
    private List<Socket> mSocketList;

    public CheckAllClientIsConnect(List<Socket> mSocketList) {
        this.mSocketList = mSocketList;
    }

    @Override
    public void run() {
        while(true){
            for (Socket socket :
                    mSocketList) {
                if (socket==null||socket.isClosed()){
                    mSocketList.remove(socket);
                }
            }
        }
    }
}
