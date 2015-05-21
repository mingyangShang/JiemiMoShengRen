package com.levelup.jiemimoshengren.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.SparseIntArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.levelup.jiemimoshengren.R;
import com.levelup.jiemimoshengren.base.BaseActivity;
import com.levelup.jiemimoshengren.model.FindUser;
import com.levelup.jiemimoshengren.model.User;

public class GameActivity extends BaseActivity implements OnClickListener{
	
	public static final int ANIM_DURATION = 500; //动画播放的时间
	public static final int COUNT_DURATION = 4000; //progress倒计时的总时间
	public static final int COUNT_INTERVAL = 10; //progress的刷新间隔
	public static final int MAX_PROGRESS = 100; //progress的最大值
	public static final int GAME_PASS_NUM = 5; //通过游戏最小答对的个数
	
	private int currCount = 1; //当前点击的数字
	private int[] randomNumbers; //随机数字
	private ImageView[] numberButtons; //数字Button
	private SparseIntArray btnNumMap; //button和数字的map 
	
	private int[] numNormalRes = { //数字图片正常
		R.drawable.game_num_1,R.drawable.game_num_2,R.drawable.game_num_3,
		R.drawable.game_num_4,R.drawable.game_num_5,R.drawable.game_num_6,
		R.drawable.game_num_7,R.drawable.game_num_8,R.drawable.game_num_9
	};
	private int[] numPressedRes = { //数字图片按下
			R.drawable.game_num_press_1,R.drawable.game_num_press_2,R.drawable.game_num_press_3,
			R.drawable.game_num_press_4,R.drawable.game_num_press_5,R.drawable.game_num_press_6,
			R.drawable.game_num_press_7,R.drawable.game_num_press_8,R.drawable.game_num_press_9
	};
	private int[] countDownNumRes = {R.drawable.game_timer_1,R.drawable.game_timer_2,R.drawable.game_timer_3};
	private ImageView countdownImg;
	private View startView;
	private ProgressBar pbCountdown; //倒计时的progressbar
	
	private User toAddFriend;
	
	private CountDownTimer countDownTimer = new CountDownTimer(3999, 500) {
		@Override
		public void onTick(long millisUntilFinished) {
			if(millisUntilFinished<1000){
				countdownImg.setVisibility(View.GONE);
				return ;
			}
			int imgRes = countDownNumRes[(int) (millisUntilFinished/1000)-1];
			countdownImg.setImageResource(imgRes);
		}
		@Override
		public void onFinish() {
			countdownImg.setVisibility(View.GONE);
			countdownImg = null;
			findViewById(R.id.iv_cardio).setVisibility(View.VISIBLE); //心电图可见
			for(int i=0;i<numberButtons.length;++i){ //倒计时结束后让数字不可见并可点击
				final ImageView view = numberButtons[i];
				view.setImageResource(R.drawable.game_num_back);
				view.setClickable(true);
			}
			pbCountDownTimer.start();
		}
	};
	private CountDownTimer pbCountDownTimer = new CountDownTimer(COUNT_DURATION,COUNT_INTERVAL) {
		@Override
		public void onTick(long millisUntilFinished) {
			final int progress = (int) (MAX_PROGRESS*millisUntilFinished/(float)COUNT_DURATION);
			pbCountdown.setProgress(progress);
		}
		@Override
		public void onFinish() {
			pbCountdown.setProgress(0);
			for(int i=0;i<numberButtons.length;++i){ //倒计时结束后让数字不可见并可点击
				final ImageView view = numberButtons[i];
				final int numText = btnNumMap.get(view.getId()); //显示的数字
				if(numText>=currCount){ //还未点击
					view.setImageResource(numNormalRes[numText-1]);
				}
				view.setClickable(false);
			}
			if(currCount-1>=GAME_PASS_NUM){
				showMsg("恭喜您答对了"+(currCount-1)+"个，成功完成解密");
				onPassedGame();
			}else{
				showMsg("您答对了"+(currCount-1)+"个，很遗憾，为完成解密");
				onNotPassGame();
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState,R.layout.activity_game);
	}
	
	@Override
	protected void initData() {
		toAddFriend = getIntent().getParcelableExtra("finduser");
		randomNumbers = makeRandomButtonNumbers();
		btnNumMap = new SparseIntArray(9);
	}
	@Override
	protected void initView() {
		countdownImg = (ImageView) findViewById(R.id.timer);
		startView = findViewById(R.id.bt_start);
		startView.setOnClickListener(this);
		pbCountdown = (ProgressBar) findViewById(R.id.pb);
		pbCountdown.setProgress(MAX_PROGRESS);
		TextView tvFriendName = (TextView) findViewById(R.id.friend_name);
		if(toAddFriend!=null){
			tvFriendName.setText(toAddFriend.getNick());
		}
		
		ImageView button1 = (ImageView)findViewById(R.id.Button1Id);
		ImageView button2 = (ImageView)findViewById(R.id.Button2Id);
		ImageView button3 = (ImageView)findViewById(R.id.Button3Id);
		ImageView button4 = (ImageView)findViewById(R.id.Button4Id);
		ImageView button5 = (ImageView)findViewById(R.id.Button5Id);
		ImageView button6 = (ImageView)findViewById(R.id.Button6Id);
		ImageView button7 = (ImageView)findViewById(R.id.Button7Id);
		ImageView button8 = (ImageView)findViewById(R.id.Button8Id);
		ImageView button9 = (ImageView)findViewById(R.id.Button9Id);
		numberButtons = new ImageView[]{button1, button2, button3,
				button4, button5, button6,
				button7, button8, button9};
		//设置监听器和数字值,构建map
		List<Animator> animators = new ArrayList<Animator>(9);
		for(int i=0;i<numberButtons.length;++i){
			ImageView currButton = numberButtons[i];
			btnNumMap.put(currButton.getId(), randomNumbers[i]); //构建button和显示数字的映射
			currButton.setOnClickListener(new OnClickListener() {
				public void onClick(View view) {
					onNumberButtonClick((ImageView)view);
				}
			});
		}
		//执行翻转动画
		/*AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.setDuration(1000);
		animatorSet.playTogether(animators);
		animatorSet.start();*/
	}

	/***/
	private void startGame() {
		System.err.println("开始游戏");
		pbCountDownTimer.start();
	}
	
	/**显示数字*/
	private void showNums(){
		for(int i=0;i<numberButtons.length;++i){
			final ImageView img = numberButtons[i];
			final int res = numPressedRes[btnNumMap.get(img.getId())-1];
			img.setImageResource(res);
			img.setClickable(false);
		}
	}
	
	/**当数字button被点击*/
	private void onNumberButtonClick(ImageView numBtn){
		//设置点击后button的外观
		numBtn.setClickable(false);
		int num = btnNumMap.get(numBtn.getId());
		if(num != currCount){
			onErrorClick(numBtn);
			currCount = 1;
		}else{
			onRightClick(numBtn);
			++currCount;
		}
	}
	
	/**点击button数字错误*/
	private void onErrorClick(ImageView btn){
		Toast.makeText(this, "错误", 1000).show();	
		pbCountDownTimer.cancel(); //停止倒计时
		for(int i=0;i<numberButtons.length;++i){
			final ImageView currBtn = numberButtons[i];
			final int numText = btnNumMap.get(currBtn.getId()); //显示的数字
			//全部设置上数字
			if(currBtn==btn){ //这个错误,在currCount-1上画错
				currBtn.setBackgroundResource(numNormalRes[numText-1]);
				currBtn.setImageResource(R.drawable.miss);
			}else if(numText>=currCount){ //还未点击
				currBtn.setImageResource(numNormalRes[numText-1]);
			}
			//全部设置不可点击
			currBtn.setClickable(false);
		}
		if(currCount-1>=GAME_PASS_NUM){
			showMsg("恭喜您答对了"+(currCount-1)+"个，成功完成解密");
			onPassedGame();
		}else{
			showMsg("您答对了"+(currCount-1)+"个，很遗憾，为完成解密");
			onNotPassGame();
		}
	}
	/**点击button数字正确*/
	private void onRightClick(ImageView btn){
		btn.setImageResource(numPressedRes[currCount-1]);
		if(currCount == 9){ //全部猜对
			pbCountDownTimer.cancel();
			showMsg("你太腻害了，全都记对了，解密成功");
			onPassedGame();
		}
	}

	/**当游戏通过的时候*/
	private void onPassedGame(){
		Intent intent = new Intent(this,UserInfoActivity.class);
		intent.putExtra("finduser", toAddFriend);
		intent.putExtra("chat", false); //表示userinfoactivity不是来自于聊天界面
		startActivity(intent);
	}
	
	/**完成游戏的解密*/
	private void onNotPassGame(){
		this.finish();
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
	private Animator makeFirstAnim(final ImageView numBtn){
		Animator animator = ObjectAnimator
		.ofFloat(numBtn, "rotationX", 0.0F, 180.0F)
		.setDuration(1000);
		animator.addListener(new AnimatorListener() {
			
			public void onAnimationStart(Animator animation) {}
			
			public void onAnimationRepeat(Animator animation) {}
			
			public void onAnimationEnd(Animator animation) {
//				numBtn.setText("");
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

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_start:
			startView.setVisibility(View.GONE);
			startView = null;
			showNums();
			countdownImg.setVisibility(View.VISIBLE);
			countDownTimer.start();
			break;
		default:
			break;
		}
	}
}
