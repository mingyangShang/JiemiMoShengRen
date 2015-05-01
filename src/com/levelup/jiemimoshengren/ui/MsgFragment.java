package com.levelup.jiemimoshengren.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.levelup.jiemimoshengren.R;
import com.levelup.jiemimoshengren.adapter.MsgAdapter;
import com.levelup.jiemimoshengren.model.ChatMsg;

/**
 * Created by smy on 2015/3/4.
 */
public class MsgFragment extends Fragment implements AdapterView.OnItemClickListener{

    private View chatView; //整个聊天界面布局的view
    private ListView pullToRefreshListView; //下拉刷新Listview

    private BaseAdapter chatAdapter;

    private List<ChatMsg> chatMsgs = new ArrayList<ChatMsg>(); //聊天内容集合


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        queryChatMsgData();
        chatAdapter = new MsgAdapter(activity,chatMsgs);
    }

    /*查询聊天信息数据*/
    private void queryChatMsgData() {
        ChatMsg msg1 = new ChatMsg(ChatMsg.DEFAULT_HEAD_URL,"dasd","dasd","dasd");
        if(chatMsgs==null){
            System.out.println("null");
        }
        chatMsgs.add(msg1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        chatView = inflater.inflate(R.layout.fragment_msg,container,false);
        pullToRefreshListView = (ListView) chatView.findViewById(R.id.list_chats);
        pullToRefreshListView.setAdapter(chatAdapter);
        pullToRefreshListView.setOnItemClickListener(this);
        return chatView;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(),"click"+position,Toast.LENGTH_LONG).show();
    }
}
