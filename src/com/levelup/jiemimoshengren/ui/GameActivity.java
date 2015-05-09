package com.levelup.jiemimoshengren.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.levelup.jiemimoshengren.R;
import com.levelup.jiemimoshengren.base.BaseActivity;

public class GameActivity extends BaseActivity{
	
	public static final int ANIM_DURATION = 500; //动画播放的时间
	
	private int currCount = 1; //当前点击的数字
	private int[] randomNumbers; //随机数字
	private Button[] numberButtons; //数字Button
	private SparseIntArray btnNumMap; //button和数字的map 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState,R.layout.activity_main);
	}
	
	@Override
	protected void initData() {
		randomNumbers = makeRandomButtonNumbers();
		btnNumMap = new SparseIntArray(9);
	}
	@Override
	protected void initView() {
		Button button1 = (Button)findViewById(R.id.Button1Id);
		Button button2 = (Button)findViewById(R.id.Button2Id);
		Button button3 = (Button)findViewById(R.id.Button3Id);
		Button button4 = (Button)findViewById(R.id.Button4Id);
		Button button5 = (Button)findViewById(R.id.Button5Id);
		Button button6 = (Button)findViewById(R.id.Button6Id);
		Button button7 = (Button)findViewById(R.id.Button7Id);
		Button button8 = (Button)findViewById(R.id.Button8Id);
		Button button9 = (Button)findViewById(R.id.Button9Id);
		numberButtons = new Button[]{button1, button2, button3,
				button4, button5, button6,
				button7, button8, button9};
		//设置监听器和数字值,构建map
		List<Animator> animators = new ArrayList<Animator>(9);
		for(int i=0;i<numberButtons.length;++i){
			Button currButton = numberButtons[i];
			currButton.setText(""+randomNumbers[i]);
			animators.add(makeFirstAnim(currButton));
			btnNumMap.put(currButton.getId(), randomNumbers[i]); //构建button和数字的映射
			currButton.setOnClickListener(new OnClickListener() {
				public void onClick(View view) {
					onNumberButtonClick((Button)view);
				}
			});
		}
		//执行翻转动画
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.setDuration(1000);
		animatorSet.playTogether(animators);
		animatorSet.start();
	}
	
	/**当数字button被点击*/
	private void onNumberButtonClick(Button numBtn){
		//设置点击后button的外观
		numBtn.setClickable(false);
		int num = btnNumMap.get(numBtn.getId());
		System.out.println(""+numBtn.getX());
		if(num != currCount){
			currCount = 1;
			onErrorClick(numBtn);
		}else{
			++currCount;
			onRightClick(numBtn);
		}
		
	}
	
	/**点击button数字错误*/
	private void onErrorClick(Button btn){
		btn.setBackgroundColor(Color.RED);
		Toast.makeText(this, "错误", 1000).show();	
		List<Animator> animators = new ArrayList<Animator>();
		for(int i=0;i<numberButtons.length;++i){
			Button currBtn = numberButtons[i];
			//全部翻转过来
			if(TextUtils.isEmpty(currBtn.getText())){
				animators.add(makeSecondAnim(currBtn));
			}
			//全部设置上数字
			currBtn.setText(""+btnNumMap.get(currBtn.getId()));
			//全部设置不可点击
			currBtn.setClickable(false);
		}
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.setDuration(ANIM_DURATION);
		animatorSet.playTogether(animators);
		animatorSet.start();
	}
	/**点击button数字正确*/
	private void onRightClick(Button btn){
		makeSecondAnim(btn).start();
		btn.setText(""+btnNumMap.get(btn.getId()));
		btn.setBackgroundColor(Color.GREEN);
		
	}

	/**产生1-9的随机数字数组*/
	public int[] makeRandomButtonNumbers(){
		//构造顺序排列的数字
		List<Integer> numbers = new LinkedList<Integer>();
		for(int i=1;i<10;++i){
			numbers.add(i);
		}
		//产生随机数字
		int[] randomNumbers = new int[9];
		for(int len=numbers.size(),j=0;len>0;--len,++j){
			int i = (int)(Math.random()*len);
			randomNumbers[j] = numbers.get(i);
			numbers.remove(i);
		}
		return randomNumbers;
	}
	
	/**创建正向翻转的animator*/
	private Animator makeFirstAnim(final Button numBtn){
		Animator animator = ObjectAnimator
		.ofFloat(numBtn, "rotationX", 0.0F, 180.0F)
		.setDuration(1000);
		animator.addListener(new AnimatorListener() {
			
			public void onAnimationStart(Animator animation) {}
			
			public void onAnimationRepeat(Animator animation) {}
			
			public void onAnimationEnd(Animator animation) {
				numBtn.setText("");
			}
			
			public void onAnimationCancel(Animator animation) {}
		});
		return animator;
	}
	/**创建反向翻转的animator*/
	private Animator makeSecondAnim(View view){
		return ObjectAnimator
				.ofFloat(view, "rotationX", 180.0F, 0.0F)
				.setDuration(ANIM_DURATION);
	}
}
