package com.levelup.jiemimoshengren.ui;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dd.circularprogressbutton.CircularProgressButton;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.levelup.jiemimoshengren.R;
import com.levelup.jiemimoshengren.base.DefaultActivity;
import com.levelup.jiemimoshengren.config.Constant;
import com.levelup.jiemimoshengren.model.FindUser;
import com.levelup.jiemimoshengren.model.User;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.smy.volley.extend.EasyJsonObject;

/** 个人信息界面(其他好友的) */
public class UserInfoActivity extends DefaultActivity {

	private FindUser user; // user对象

	private TextView tvUserName;
	private TextView tvSex;
	private ImageView ivHead;
	private TextView tvSign;

	private CircularProgressButton btAddContact, btChat;

	private ImageView label;
	private boolean comeFromChat; // 是否从聊天界面来

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_user_info);
	}

	@Override
	protected void initData() {
		super.initData();
		Intent intent = getIntent();
		if (intent != null) {
			user = intent.getParcelableExtra("finduser");
			comeFromChat = intent.getBooleanExtra("chat", true);
			if (user == null) { // 上面没有传具体信息过来,暂时认为不可能

			}
		}
	}

	@Override
	protected void initView() {
		super.initView();
		label = (ImageView) findViewById(R.id.labeiId);
		tvUserName = (TextView) findViewById(R.id.usernameId);
		ivHead = (ImageView) findViewById(R.id.avatarId);
		tvSex = (TextView) findViewById(R.id.tv_sex);
		tvSign = (TextView) findViewById(R.id.signatureId);
		btAddContact = (CircularProgressButton) findViewById(R.id.add_contact);
		btChat = (CircularProgressButton) findViewById(R.id.chat);

		setHeight(tvSign, 0.283f, 0.07f);
		label.setImageBitmap(setPictureSize(R.drawable.cardiograph, 0.83f,
				0.16f));

		// 利用user的信息填写界面内容
		if (user != null) {
			System.err.println("user:"+user);
			tvUserName.setText(user.getNick());
			tvSign.setText(user.getSign());
			tvSex.setText(user.isFemale() ? getString(R.string.hint_sex_female)
					: getString(R.string.hint_sex_male));
			ImageLoader.getInstance().displayImage(user.getImgUrl(), ivHead);
			if (user.getLongitude() == 0) { // 当为0时认为是已经为好友了
				btChat.setVisibility(View.VISIBLE);
			} else {
				btAddContact.setVisibility(View.VISIBLE);
			}
		}

	}

	private void setHeight(View view, float sx, float sy) {
		int screen_width = getResources().getDisplayMetrics().widthPixels;
		int screen_height = getResources().getDisplayMetrics().heightPixels;
		android.view.ViewGroup.LayoutParams params = view.getLayoutParams();
		params.width = (int) (screen_width * sx);
		params.height = (int) (screen_height * sy);
		view.setLayoutParams(params);
		System.out.println(params);
	}

	private Bitmap setPictureSize(int id, float sx, float sy) {
		int screen_width = getResources().getDisplayMetrics().widthPixels;
		int screen_height = getResources().getDisplayMetrics().heightPixels;
		Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(id))
				.getBitmap();
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		System.out.println(width + "" + height);
		System.out.println(getResources().getDisplayMetrics().density);
		float scaleX = (float) (screen_width * sx) / width;
		float scaleY = (float) (screen_height * sy) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleX, scaleY);
		Bitmap bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		Canvas canvas = new Canvas();
		canvas.drawBitmap(bitmap2, matrix, null);
		return bitmap2;
	}

	/** 添加好友 */
	public void addContact(View v) {
		if (user != null) {
			addContact(user.getUsername(),
					getString(R.string.default_add_friend_reason));
		}
	}

	/** 删除好友 */
	public void removeContact(View v) {
	}

	/** 去聊天 */
	public void goToChat(View v) {
		if (!comeFromChat) {
			Intent intent = new Intent(UserInfoActivity.this,ChatActivity.class);
			intent.putExtra("userId", user.getUsername());
			startActivity(intent);
		}
		this.finish();
	}

	/** 获取某人的信息 */
	private void getUserInfo(final String uid) {
		JSONObject queryUserJson = makeQueryUserJson(uid);
		requestQueue.add(new JsonObjectRequest(Constant.URL_USER_INFO,
				queryUserJson, new Listener<JSONObject>() {
					public void onResponse(JSONObject response) {
						EasyJsonObject userJson = new EasyJsonObject(response);
						if (userJson.getBoolean("success")) {
							User user = new User();
							userJson = userJson.getStringAsJSONObject("msg");
							user.setUsername(userJson.getString("uid"));
							user.setNick(userJson.getString("nick"));
							user.setFemale(userJson.getString("sex").equals(
									User.SEX_FEMALE));
							user.setSign(userJson.getString("sign"));
							user.setImgUrl(userJson.getString("head"));
							refreshUI();
						} else {
							showMsg(userJson.getString("error"));
						}
					}
				}, this));
	}

	/** 创建查询用户信息的json */
	private JSONObject makeQueryUserJson(final String uid) {
		EasyJsonObject queryUserJson = new EasyJsonObject();
		queryUserJson.put("uid", uid);
		return queryUserJson;
	}

	/** 刷新UI */
	private void refreshUI() {

	}

	/** 添加好友 */
	private void addContact(final String uid, final String reason) {
		new Thread(new Runnable() {
			public void run() {
				try {
					EMContactManager.getInstance().addContact(uid, reason);
					sendFirstMsg(uid); // 发送第一条消息
					runOnUiThread(new Runnable() {
						public void run() {
							showMsgFromRes(R.string.add_successful);
							//跳转到chatActivity中去
							Intent intent = new Intent(UserInfoActivity.this,ChatActivity.class);
							intent.putExtra("userId", uid);
							startActivity(intent);
						}
					});
					// 发送请求后默认对方已同意，将对方加入自己的好友
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							showMsg(getString(R.string.Request_add_buddy_failure)
									+ e.getMessage());
						}
					});
				}
			}
		}).start();
	}

	/** 好友添加成功后发送一条消息模拟系统通知 */
	private void sendFirstMsg(final String toChatUsername) {
		sendFirstMsg(toChatUsername, Constant.SYSTEM_INFO + "hello");
	}

	private void sendFirstMsg(final String toChatUsername, final String content) {
		EMConversation conversation = EMChatManager.getInstance()
				.getConversation(toChatUsername);
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
		// 如果是群聊，设置chattype,默认是单聊
		TextMessageBody txtBody = new TextMessageBody(content);
		// 设置消息body
		message.addBody(txtBody);
		// 设置要发给谁,用户username或者群聊groupid
		message.setReceipt(toChatUsername);
		// 把messgage加到conversation中
		conversation.addMessage(message);
		try {
			EMChatManager.getInstance().sendMessage(message);
		} catch (EaseMobException e) {
			e.printStackTrace();
		}
	}

}
