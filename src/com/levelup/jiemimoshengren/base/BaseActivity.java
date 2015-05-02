package com.levelup.jiemimoshengren.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.eashmod.chat.SmyHXSDKHelper;

public abstract class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    /**sub class can override this func not old onCreate to simpleify manipulation*/
    protected void onCreate(Bundle savedInstanceState,int resId){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题栏
      /*  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        WindowManager.LayoutParams.FLAG_FULLSCREEN);*/ //设置全屏
        setContentView(resId);
        init();
    }
    protected void onCreate(Bundle savedInstanceState,View view){
        super.onCreate(savedInstanceState);
        setContentView(view);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //当界面可见时加入可见界面列表中
        ((SmyHXSDKHelper)SmyHXSDKHelper.getInstance()).pushActivity(this);
        
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }
    
    @Override
    protected void onStop(){
    	super.onStop();
    	//将当前界面从可见界面列表中删除
    	((SmyHXSDKHelper)SmyHXSDKHelper.getInstance()).popActivity(this);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    protected abstract void initData(); //初始化数据
    protected abstract void initView(); //初始化视图
    protected void init() {
        initData();
        initView();
    }
    // ==findViewById()
    protected View getView(int resId){
        return findViewById(resId);
    }

    protected void goTo(Class<? extends Activity> destActivity,Bundle bundle) {
        Intent intent = new Intent(this,destActivity);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    protected void goTo(Class<? extends Activity> destActivity){
        goTo(destActivity,null);
    }
    protected Bundle getBundleFromUp(){
        return getIntent().getExtras();
    }
    
    /**弹出Toast，Toast的样式今后可以自定义，暂时使用默认*/
    protected void showMsg(String msg) {
    	Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();	
	}
    protected void showMsg(String msg,int duration){
    	Toast.makeText(this, msg, duration).show();
    }

}
