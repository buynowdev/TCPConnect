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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import cn.zhaoyuening.tcpconnect.adapter.ShowMsgListAdapter;
import cn.zhaoyuening.tcpconnect.domain.Msg;
import cn.zhaoyuening.tcpconnect.runnable.SendContentRunnable;
import cn.zhaoyuening.tcpconnect.runnable.SocketClientRunnable;

public class MainActivity extends AppCompatActivity {

    public static final int SET_SOCKET = 1;
    public static final int CONNECT_FAIL = 2;

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
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SET_SOCKET:
                    mSocket = (Socket) msg.obj;
                    bt_connect.setText("断  开");
                    isConnected=true;
                    Log.d(TAG,"传递socket成功");
                    break;
                case CONNECT_FAIL:
                    Toast.makeText(MainActivity.this, "连接失败："+(String)msg.obj, Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"链接失败");
                    break;
            }
        }
    };
    private ArrayAdapter<Msg> mAdapter;

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

        lv_msgList.setAdapter(mAdapter);
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
        try {
            msg = new Msg(mSocket.getInetAddress().getLocalHost().getHostAddress(),mSocket.getLocalPort(),Msg.SEND,content);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        addMsg(msg);
        mExecutor.execute(new SendContentRunnable(content,mSocket));
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
            if (mSocket!=null){
                try {
                    //关闭socket并赋值null
                    mSocket.close();
                    mSocket=null;
                    isConnected=false;
                    bt_connect.setText("连  接");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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

        mExecutor.execute(new SocketClientRunnable(ip,Integer.parseInt(port),handler));
    }

    //作为服务端 监听端口
    private void connect_server() {

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
        mAdapter = new ShowMsgListAdapter(MainActivity.this,
                R.layout.item_getmsg,R.layout.item_sendmsg,MainActivity.msgList);
    }
}
