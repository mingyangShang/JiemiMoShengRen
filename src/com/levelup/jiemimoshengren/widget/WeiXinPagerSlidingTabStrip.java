package com.levelup.jiemimoshengren.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class WeiXinPagerSlidingTabStrip extends PagerSlidingTabStrip {
	
	private BadgeView msgBadgeView,contactBadgeView; //未读消息提醒和联系人提醒
	public static final int TAB_POS_MSG = 0,TAB_POS_CONTACT = 1; //所在Tab的位置

	public WeiXinPagerSlidingTabStrip(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}
	public WeiXinPagerSlidingTabStrip(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public WeiXinPagerSlidingTabStrip(Context context){
		this(context,null,0);
	}
	
	/**刷新未读消息提示数*/
	public void updateMsgTab(int unreadMsgCount){
		msgBadgeView.setBadgeCount(unreadMsgCount);
	}
	/**刷新未读联系人变化提示数*/
	public void updateContactTab(int unreadContactCount){
		contactBadgeView.setBadgeCount(unreadContactCount);
	}
	
	@Override
	protected void addTab(final int position, View tab){
		super.addTab(position, tab);
		BadgeView badgeView = new BadgeView(getContext());
		badgeView.setTargetView(tab);
		if(position == TAB_POS_MSG){
			msgBadgeView = badgeView;
		}else if(position == TAB_POS_CONTACT){
			contactBadgeView = badgeView;
		}
	}

}
