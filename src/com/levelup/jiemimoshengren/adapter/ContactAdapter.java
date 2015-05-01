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
import com.levelup.jiemimoshengren.model.MyContact;
import com.levelup.jiemimoshengren.utils.PingYinUtil;

/**
 * Created by smy on 2015/3/4.
 */
public class ContactAdapter extends BaseAdapter implements SectionIndexer{
    private Context mContext;
    private List<MyContact> contacts;

    private static final Comparator pinyinComparator= new PinyinComparator();

    private static final String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @SuppressWarnings("unchecked")
    public ContactAdapter(Context mContext,List<MyContact> contacts){
        this.mContext = mContext;
        this.contacts = contacts;
        //鎺掑簭(瀹炵幇浜嗕腑鑻辨枃娣锋帓)
        Arrays.sort(contacts.toArray(), new PinyinComparator());
    }
    
    public int getCount() {
        return contacts.size();
    }

    public Object getItem(int position) {
        return contacts.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final MyContact contact = contacts.get(position);
        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_contact, null);
            viewHolder = new ViewHolder();
            viewHolder.tvCatalog = (TextView)convertView.findViewById(R.id.tv_contact_index);
            viewHolder.ivAvatar = (ImageView)convertView.findViewById(R.id.img_head);
            viewHolder.tvNick = (TextView)convertView.findViewById(R.id.tv_name);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        String catalog = PingYinUtil.converterToFirstSpell(contact.name).substring(0, 1);
        if(position == 0){
            viewHolder.tvCatalog.setVisibility(View.VISIBLE);
            viewHolder.tvCatalog.setText(catalog);
        }else{
            final MyContact lastContact = contacts.get(position - 1);
            String lastCatalog = PingYinUtil.converterToFirstSpell(lastContact.name.substring(0, 1));
            if(catalog.equals(lastCatalog)){
                viewHolder.tvCatalog.setVisibility(View.GONE);
            }else{
                viewHolder.tvCatalog.setVisibility(View.VISIBLE);
                viewHolder.tvCatalog.setText(catalog);
            }
        }

        viewHolder.ivAvatar.setImageResource(R.drawable.icon);
        viewHolder.tvNick.setText(contact.name);
        return convertView;
    }

    static class ViewHolder{
        TextView tvCatalog;//鐩綍
        ImageView ivAvatar;//澶村儚
        TextView tvNick;//鏄电О
    }

    public int getPositionForSection(int section) {
        for (int i = 0,size=contacts.size(); i < size; i++) {
            String l = PingYinUtil.converterToFirstSpell(contacts.get(i).name).substring(0, 1);
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

    public static class PinyinComparator implements Comparator{
        public int compare(Object o1, Object o2) {
            final MyContact contact1 = (MyContact)o1;
            final MyContact contact2 = (MyContact)o2;
            String str1 = PingYinUtil.getPingYin(contact1.name);
            String str2 = PingYinUtil.getPingYin(contact2.name);
            return str1.compareTo(str2);
        }
    }
}
