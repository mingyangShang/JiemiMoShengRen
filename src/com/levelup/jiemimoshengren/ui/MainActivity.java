package com.levelup.jiemimoshengren.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.levelup.jiemimoshengren.R;
import com.levelup.jiemimoshengren.base.BaseActivity;
import com.levelup.jiemimoshengren.widget.PagerSlidingTabStrip;

/**
 * Created by smy on 2015/3/4.
 * 解密陌生人 主界面
 */
public class MainActivity extends BaseActivity {

    /*聊天*/
    private MsgFragment chatFragment;
    /*通讯录*/
    private ContactFragment contactFragment;
    /*更多*/
    private MoreFragment moreFragment;

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;

    private static final String TABS_COLOR = "#45c01a";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState,R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        pager = (ViewPager)getView(R.id.pager);
        tabs = (PagerSlidingTabStrip)getView(R.id.tabs);
        pager.setAdapter(new SlidingPagerAdapter(getSupportFragmentManager()));
        tabs.setViewPager(pager);
        setTabsValue();
    }

    private void setTabsValue(){
        tabs.setShouldExpand(true);
        tabs.setDividerColor(Color.TRANSPARENT);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        tabs.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, dm));
        tabs.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, dm));
        tabs.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, dm));
        tabs.setIndicatorColor(Color.parseColor(TABS_COLOR));
        tabs.setSelectedTextColor(Color.parseColor(TABS_COLOR));
        tabs.setTabBackground(0);
    }

    public class SlidingPagerAdapter extends FragmentPagerAdapter {
        public SlidingPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        private final String[] titles = { "聊天", "通讯录", "更多" };

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (chatFragment == null) {
                        chatFragment = new MsgFragment();
                    }
                    return chatFragment;
                case 1:
                    if (contactFragment == null) {
                        contactFragment = new ContactFragment();
                    }
                    return contactFragment;
                case 2:
                    if (moreFragment == null) {
                        moreFragment = new MoreFragment();
                    }
                    return moreFragment;
                default:
                    return null;
            }
        }
    }



}
