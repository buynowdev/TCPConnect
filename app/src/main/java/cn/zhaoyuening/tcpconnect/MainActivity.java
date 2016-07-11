package cn.zhaoyuening.tcpconnect;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import cn.zhaoyuening.tcpconnect.adapter.ShowMsgListAdapter;
import cn.zhaoyuening.tcpconnect.domain.Msg;
import cn.zhaoyuening.tcpconnect.runnable.CheckAllClientIsConnect;
import cn.zhaoyuening.tcpconnect.runnable.GetMsgRunnable;
import cn.zhaoyuening.tcpconnect.runnable.SendContentRunnable;
import cn.zhaoyuening.tcpconnect.runnable.SocketClientRunnable;
import cn.zhaoyuening.tcpconnect.runnable.SocketServerRunnable;

public class MainActivity extends AppCompatActivity {

    public static final int SET_SOCKET = 1;
    public static final int CONNECT_FAIL = 2;
    public static final int GET_MSG = 3;
    public static final int SET_SERVERSOCKET = 4;
    public static final int ADD_CLIENTSOCKET = 5;

    private static String TAG = "TAG_MAINACTIVITY";

    //存放msg列表
    private static List<Msg> msgList = new ArrayList<Msg>();
    private EditText et_remoteIP;
    private EditText et_remoteProt;
    private CheckBox cb_isServer;
    private Button bt_connect;
    private ListView lv_msgList;
    private Button bt_send;
    private EditText et_msgContent;
    private Executor mExecutor;
    private Socket mSocket;
    private boolean isConnected=false;
    private ArrayAdapter<Msg> mAdapter;
    private ServerSocket mServerSocket;
    private List<Socket> socketList;
    private Handler mHandler = new Handler(){


        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SET_SOCKET:
                    mSocket = (Socket) msg.obj;
                    bt_connect.setText("断  开");
                    isConnected=true;
                    Log.d(TAG,"传递socket成功");
                    //开始监听消息
                    mExecutor.execute(new GetMsgRunnable(mSocket,this));
                    break;
                case CONNECT_FAIL:
                    Toast.makeText(MainActivity.this, "连接失败："+(String)msg.obj, Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"链接失败");
                    break;
                case GET_MSG:
                    Msg m = (Msg) msg.obj;
                    addMsg(m);
                    Log.d(TAG,"获取消息："+m.getContent());
                    break;
                case SET_SERVERSOCKET:
                    mServerSocket = (ServerSocket) msg.obj;
                    Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                    isConnected = true;
                    bt_connect.setText("断  开");
                    break;
                case ADD_CLIENTSOCKET:
                    Socket socket = (Socket)msg.obj;
                    addClientSocket(socket);
                    Toast.makeText(MainActivity.this, "有新设备连接:"+
                            socket.getInetAddress().getHostAddress(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void addClientSocket(Socket socket) {
        mSocketList.add(socket);
        //监听该客户端消息
        mExecutor.execute(new GetMsgRunnable(socket, mHandler));
    }

    private List<Socket> mSocketList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initEvent();
    }

    private void initEvent() {
        //连接
        bt_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }
        });
        //发送消息
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsg();
            }
        });
        //循环检查服务端所有连接 是否还在连接
        //将未连接socket删除
        mExecutor.execute(new CheckAllClientIsConnect(mSocketList));
        lv_msgList.setAdapter(mAdapter);
        cb_isServer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //服务端不需要远程ip 将et隐藏
                    et_remoteIP.setVisibility(View.INVISIBLE);
                }else{
                    et_remoteIP.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    //发送消息
    private void sendMsg() {
        //判断是否连接成功
        if (!isConnected){
            Toast.makeText(MainActivity.this, "无连接", Toast.LENGTH_SHORT).show();
            return;
        }
        //获取消息内容
        String content = et_msgContent.getText().toString();
        Msg msg = null;

        if (cb_isServer.isChecked()){
            //服务端 发送给每个客户端消息
            for (Socket socket :
                    mSocketList) {
                msg = new Msg(socket.getLocalAddress().toString(),socket.getLocalPort(),Msg.SEND,content);
                mExecutor.execute(new SendContentRunnable(content,socket));
                addMsg(msg);
            }
        }else{
            msg = new Msg(mSocket.getLocalAddress().toString(),mSocket.getLocalPort(),Msg.SEND,content);
            //客户端给服务端发送消息
            mExecutor.execute(new SendContentRunnable(content,mSocket));
            addMsg(msg);
        }
    }

    //将msg添加到列表 并更新UI 显示出消息
    private void addMsg(Msg msg) {
        MainActivity.msgList.add(msg);
        mAdapter.notifyDataSetChanged();
        lv_msgList.setAdapter(mAdapter);
        lv_msgList.setSelection(MainActivity.msgList.size());
    }

    private void connect() {
        //判断是否已经连接 如果已经连接则断开连接
        if (isConnected){
            if (cb_isServer.isChecked()){
                try {
                    mServerSocket.close();
                    mServerSocket = null;
                    for (Socket socket: mSocketList) {
                        socket.close();
                    }
                    socketList = new LinkedList<Socket>();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else{
                if (mSocket!=null){
                    try {
                        //关闭socket并赋值null
                        mSocket.close();
                        mSocket=null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            isConnected=false;
            bt_connect.setText("连  接");
            return;
        }
        //区分客户端方式 与 服务端方式
        if (cb_isServer.isChecked()){
            connect_server();
        }else {
            connect_client();
        }

    }

    //作为客户端连接到 指定的地址和端口
    private void connect_client() {
        //获取ip 与 port
        String ip = et_remoteIP.getText().toString();
        String port = et_remoteProt.getText().toString();

        mExecutor.execute(new SocketClientRunnable(ip,Integer.parseInt(port), mHandler));
    }

    //作为服务端 监听端口
    private void connect_server() {
        int port = Integer.parseInt(et_remoteProt.getText().toString());
        mExecutor.execute(new SocketServerRunnable(port,mHandler));
    }

    private void initData() {
        mExecutor = Executors.newCachedThreadPool();
        et_remoteIP = (EditText) findViewById(R.id.et_remoteIP);
        et_remoteProt = (EditText) findViewById(R.id.et_remotePort);
        cb_isServer = (CheckBox) findViewById(R.id.cb_isServer);
        bt_connect = (Button) findViewById(R.id.bt_connect);
        lv_msgList = (ListView) findViewById(R.id.lv_msgList);
        bt_send = (Button) findViewById(R.id.bt_send);
        et_msgContent = (EditText) findViewById(R.id.et_msgContent);
        mSocketList = new LinkedList<Socket>();
        mAdapter = new ShowMsgListAdapter(MainActivity.this,
                R.layout.item_getmsg,R.layout.item_sendmsg,MainActivity.msgList);
    }
}
