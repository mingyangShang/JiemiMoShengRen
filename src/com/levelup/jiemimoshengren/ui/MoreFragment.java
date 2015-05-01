package com.levelup.jiemimoshengren.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.levelup.jiemimoshengren.R;

/**
 * Created by smy on 2015/3/4.
 */
public class MoreFragment extends Fragment{
    View moreView;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        moreView = inflater.inflate(R.layout.fragment_more,container,false);
        return moreView;
    }
}
