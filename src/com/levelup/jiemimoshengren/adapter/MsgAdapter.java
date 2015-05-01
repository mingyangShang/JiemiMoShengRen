package com.levelup.jiemimoshengren.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.levelup.jiemimoshengren.R;
import com.levelup.jiemimoshengren.model.ChatMsg;

public class MsgAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<ChatMsg> mMsgs;
	private LayoutInflater mInflater;
	
	

	public MsgAdapter(Context context, List<ChatMsg> msgs) {
		super();
		this.mContext = context;
		this.mMsgs = msgs;
		this.mInflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return mMsgs.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView==null){
			convertView = this.mInflater.inflate(R.layout.adapter_msg, null);
			holder = new ViewHolder();
			holder.imgHead = (ImageView) convertView.findViewById(R.id.img_head);
			holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tvMsg = (TextView) convertView.findViewById(R.id.tv_msg);
			holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		return convertView;
	}
	
	static class ViewHolder{
		public ImageView imgHead;
		public TextView tvName,tvMsg,tvTime;
	}

}
