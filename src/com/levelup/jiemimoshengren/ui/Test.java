package com.levelup.jiemimoshengren.ui;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;

import com.levelup.jiemimoshengren.R;
import com.levelup.jiemimoshengren.widget.ScaleButton;

public class Test extends Activity implements AnimatorListener,OnClickListener{
	
	private ScaleButton scaleButton ;
	private Button button;
	
	private ProgressBar progressBar;
	@Override
	protected void onCreate(Bundle bundle){
		super.onCreate(bundle);
		setContentView(R.layout.test);
		/*button = (Button) findViewById(R.id.bt_login2);
		progressBar = (ProgressBar) findViewById(R.id.bar);
		scaleButton = (ScaleButton) findViewById(R.id.bt_login);
		scaleButton.setAnimatorListener(this);
		scaleButton.setOnClickListener(this);*/
	}



	public void onAnimationStart(Animator animation) {
		// TODO Auto-generated method stub
		
	}



	public void onAnimationEnd(Animator animation) {
		scaleButton.setText("");
		progressBar.setVisibility(View.VISIBLE);
	}



	public void onAnimationCancel(Animator animation) {
		// TODO Auto-generated method stub
		
	}



	public void onAnimationRepeat(Animator animation) {
		// TODO Auto-generated method stub
		
	}



	public void onClick(View v) {
		scaleButton.startAnimation();
	}
}
