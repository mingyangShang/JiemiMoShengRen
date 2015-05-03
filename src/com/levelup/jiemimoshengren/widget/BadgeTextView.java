package com.levelup.jiemimoshengren.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class BadgeTextView extends TextView {

	private int badgeCount; //提示消息的显示数字
	public static final int MAX_BADGE_COUNT = 99,MIN_BADGE_COUNT = 0;
	
	public BadgeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	public BadgeTextView(Context context,AttributeSet attrs){
		this(context,attrs,0);
	}
	public BadgeTextView(Context context){
		this(context, null);
	}
	public int getBadgeCount() {
		return badgeCount;
	}
	public void setBadgeCount(int badgeCount) {
		if(badgeCount<MIN_BADGE_COUNT){
			this.badgeCount = MIN_BADGE_COUNT;
		}else if(badgeCount>MAX_BADGE_COUNT){
			this.badgeCount = MAX_BADGE_COUNT;
		}else{
			this.badgeCount = badgeCount;
		}
		
	}
}
