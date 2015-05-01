package com.levelup.jiemimoshengren.adapter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.levelup.jiemimoshengren.R;
import com.levelup.jiemimoshengren.utils.PingYinUtil;

/**
 * Created by smy on 2015/3/4.
 */
public class AlphaIndexAdapter extends BaseAdapter implements SectionIndexer{
    private Context mContext;
    private List<String> mNicks;

    private static final Comparator pinyinComparator= new PinyinComparator();

    private static final String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @SuppressWarnings("unchecked")
    public AlphaIndexAdapter(Context mContext,List<String> nicks){
        this.mContext = mContext;
        this.mNicks = nicks;
        //排序(实现了中英文混排)
        Arrays.sort(mNicks.toArray(), new PinyinComparator());
    }
    public int getCount() {
        return mNicks.size();
    }

    public Object getItem(int position) {
        return mNicks.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final String nickName = mNicks.get(position);
        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_contact_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tvCatalog = (TextView)convertView.findViewById(R.id.contactitem_catalog);
            viewHolder.ivAvatar = (ImageView)convertView.findViewById(R.id.contactitem_avatar_iv);
            viewHolder.tvNick = (TextView)convertView.findViewById(R.id.contactitem_nick);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        String catalog = PingYinUtil.converterToFirstSpell(nickName).substring(0, 1);
        if(position == 0){
            viewHolder.tvCatalog.setVisibility(View.VISIBLE);
            viewHolder.tvCatalog.setText(catalog);
        }else{
            String lastCatalog = PingYinUtil.converterToFirstSpell(mNicks.get(position - 1).substring(0, 1));
            if(catalog.equals(lastCatalog)){
                viewHolder.tvCatalog.setVisibility(View.GONE);
            }else{
                viewHolder.tvCatalog.setVisibility(View.VISIBLE);
                viewHolder.tvCatalog.setText(catalog);
            }
        }

        viewHolder.ivAvatar.setImageResource(R.drawable.icon);
        viewHolder.tvNick.setText(nickName);
        return convertView;
    }

    static class ViewHolder{
        TextView tvCatalog;//目录
        ImageView ivAvatar;//头像
        TextView tvNick;//昵称
    }

    public int getPositionForSection(int section) {
        for (int i = 0,size=mNicks.size(); i < size; i++) {
            String l = PingYinUtil.converterToFirstSpell(mNicks.get(i)).substring(0, 1);
            char firstChar = l.toUpperCase().charAt(0);
            if (firstChar == mSections.charAt(section)) {
                return i;
            }
        }
        return 0;
    }

    public Object[] getSections() {
        String[] sections = new String[mSections.length()];
        for (int i = 0; i < mSections.length(); i++)
            sections[i] = String.valueOf(mSections.charAt(i));
        return sections;
    }

    public int getSectionForPosition(int position) {
        return 0;
    }

    /*中文拼音比较*/
    public static class PinyinComparator implements Comparator{
        public int compare(Object o1, Object o2) {
            String str1 = PingYinUtil.getPingYin((String) o1);
            String str2 = PingYinUtil.getPingYin((String) o2);
            return str1.compareTo(str2);
        }
    }
}
