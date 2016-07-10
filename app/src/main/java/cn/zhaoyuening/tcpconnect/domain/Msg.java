package cn.zhaoyuening.tcpconnect.domain;

/**
 * Created by Zhao on 2016/7/11.
 */
public class Msg {
    public final static int GET = 1;
    public final static int SEND = 2;
    private String ip;
    private int port;
    private int type;
    private String content;

    public Msg(String ip, int port, int type,String content) {
        this.ip = ip;
        this.port = port;
        this.type = type;
        this.content = content;
    }

    public String getIp() {
        return ip;
    }


    public int getPort() {
        return port;
    }


    public int getType() {
        return type;
    }

    public String getContent() {
        return content;
    }
}
