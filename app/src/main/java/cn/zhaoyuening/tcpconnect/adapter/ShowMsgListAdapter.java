package cn.zhaoyuening.tcpconnect.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cn.zhaoyuening.tcpconnect.R;
import cn.zhaoyuening.tcpconnect.domain.Msg;

/**
 * Created by Zhao on 2016/7/11.
 */
public class ShowMsgListAdapter extends ArrayAdapter<Msg> {
    private Context mContext;
    //获取消息使用的item
    private int mResourceGet;
    //发送消息使用的item
    private int mResourceSend;
    private List<Msg> mMsgList;

    public ShowMsgListAdapter(Context context, int resource_get,int resource_send, List<Msg> msgList) {
        super(context, resource_get,  msgList);
        this.mContext = context;
        this.mResourceGet = resource_get;
        this.mResourceSend = resource_send;
        this.mMsgList = msgList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Msg msg = mMsgList.get(position);
        int resource ;
        //根据发送 与 接收使用不同的布局文件
        if(msg.getType()==Msg.GET){
            resource = mResourceGet;
        }else{
            resource = mResourceSend;
        }
        //获取视图
        View view = View.inflate(mContext,resource,null);
        TextView  tv_ip = (TextView) view.findViewById(R.id.tv_ip);
        TextView  tv_port = (TextView) view.findViewById(R.id.tv_port);
        TextView tv_content = (TextView) view.findViewById(R.id.tv_msgContent);

        tv_ip.setText(msg.getIp());
        tv_port.setText(msg.getPort()+"");
        tv_content.setText(msg.getContent());

        return view;
    }
}
