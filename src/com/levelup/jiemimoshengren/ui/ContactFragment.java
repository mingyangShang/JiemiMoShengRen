package com.levelup.jiemimoshengren.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.levelup.jiemimoshengren.R;
import com.levelup.jiemimoshengren.adapter.ContactAdapter;
import com.levelup.jiemimoshengren.model.MyContact;
import com.levelup.jiemimoshengren.widget.IndexableListView;

/**
 * Created by smy on 2015/3/4.
 */
public class ContactFragment extends Fragment {
    private View contactView;
    private IndexableListView contactLv;

    private List<MyContact> myContacts = new ArrayList<MyContact>(); //联系人列表
    private BaseAdapter contactAdapter; //联系人列表项适配器

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        contactView = inflater.inflate(R.layout.fragment_contact,container,false);
        contactLv = (IndexableListView) contactView.findViewById(R.id.lv_contact);
        contactLv.setAdapter(contactAdapter);
        return contactView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        queryContacts();
        contactAdapter = new ContactAdapter(activity,myContacts);
    }

    /*查询联系人信息*/
    private void queryContacts() {
        MyContact contact = new MyContact("shang","商明阳","shang");
        myContacts.add(contact);
    }
}
