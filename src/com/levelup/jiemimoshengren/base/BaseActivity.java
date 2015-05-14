package com.levelup.jiemimoshengren.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.easemod.chat.SmyHXSDKHelper;
import com.levelup.jiemimoshengren.R;
import com.levelup.jiemimoshengren.ui.LoginActivity;

public abstract class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题栏
    }

    /**sub class can override this func not old onCreate to simpleify manipulation*/
    protected void onCreate(Bundle savedInstanceState,int resId){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
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

    protected void goTo(Class<? extends Activity> destActivity,Bundle bundle,boolean finish) {
        Intent intent = new Intent(this,destActivity);
        if(bundle!=null){
        	intent.putExtras(bundle);
        }
        startActivity(intent);
        if(finish){
        	finish();
        }
    }
    protected void goTo(Class<? extends Activity> destActivity,boolean finish){
        goTo(destActivity,null,finish);
    }
    protected void goTo(Class<? extends Activity> destActivity){
    	goTo(destActivity,false);
    }
    protected void goToWithFinish(Class<? extends Activity> destActivity){
    	goTo(destActivity,true);
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
    protected void showMsgFromRes(int resId){
    	showMsg(getString(resId));
    }
    public void back(View v){
    	this.finish();
    }

	/**生成对话框*/
	protected ProgressDialog makeProgressDialog(final Context context,final String msg,final OnCancelListener onCancelListener){
		final ProgressDialog pd = new ProgressDialog(context);
		pd.setCanceledOnTouchOutside(false);
		pd.setOnCancelListener(onCancelListener);
		pd.setMessage(msg);
		return pd;
	}
}
