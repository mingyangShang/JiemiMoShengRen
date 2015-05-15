package com.levelup.jiemimoshengren.ui;

import java.io.File;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.levelup.jiemimoshengren.R;
import com.levelup.jiemimoshengren.base.BaseActivity;
import com.levelup.jiemimoshengren.utils.SystemInfoUtil;

public class SelectImgPopupActivity extends BaseActivity implements OnClickListener{

	private Button btn_take_photo, btn_pick_photo, btn_cancel;  
	private LinearLayout layout;
	
	/* 头像文件 */
    private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";
 
    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    public static final int CODE_IMG_RESULT = 0xa3;
 
    // 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。
    private static int output_X = 240;
    private static int output_Y = 240;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_select_pic);
	}
	
	@Override
	protected void initData() {
		
	}

	@Override
	protected void initView() {
		btn_take_photo = (Button) this.findViewById(R.id.btn_take_photo);  
		btn_pick_photo = (Button) this.findViewById(R.id.btn_pick_photo);  
		btn_cancel = (Button) this.findViewById(R.id.btn_cancel);  
		
		layout=(LinearLayout)findViewById(R.id.pop_layout);  
		
		//添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity  
		layout.setOnClickListener(new OnClickListener() {  
			
			public void onClick(View v) {  
				Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！",   
						Toast.LENGTH_SHORT).show();   
				}  
			});  
		//添加按钮监听  
		btn_cancel.setOnClickListener(this);  
		btn_pick_photo.setOnClickListener(this);
		btn_take_photo.setOnClickListener(this);
	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		finish();
		return true;
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_take_photo:
			choseHeadImageFromCameraCapture();
			break;
		case R.id.btn_pick_photo:
			choseHeadImageFromGallery();
			break;
		case R.id.btn_cancel:
			finish();
			break;
		default: 
			break;
		} 
	}
	
	// 从本地相册选取图片作为头像
    private void choseHeadImageFromGallery() {
        Intent intentFromGallery = new Intent();
        // 设置文件类型
        intentFromGallery.setType("image/*");
        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
    }
    
    // 启动手机相机拍摄照片作为头像
    private void choseHeadImageFromCameraCapture() {
        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断存储卡是否可用，存储照片文件
        if (SystemInfoUtil.isSDExists()) {
            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                    .fromFile(new File(Environment
                            .getExternalStorageDirectory(), IMAGE_FILE_NAME)));
        }
        startActivityForResult(intentFromCapture, CODE_CAMERA_REQUEST);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent intent) {
        // 用户没有进行有效的设置操作，返回
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        switch (requestCode) {
        case CODE_GALLERY_REQUEST:
            cropRawPhoto(intent.getData());
            break;
        case CODE_CAMERA_REQUEST:
            if (SystemInfoUtil.isSDExists()) {
                File tempFile = new File(Environment.getExternalStorageDirectory(),IMAGE_FILE_NAME);
                cropRawPhoto(Uri.fromFile(tempFile));
            } else {
            	showMsgFromRes(R.string.no_sdcard);
            }
            break;
        case CODE_RESULT_REQUEST:
            if (intent != null) {
                backToRegi(intent);
            }
            break;
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }
    
    /**
     * 裁剪原始的图片
     */
    public void cropRawPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", output_X);
        intent.putExtra("outputY", output_Y);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CODE_RESULT_REQUEST);
    }
    
    /**
     * 提取保存裁剪之后的图片数据，并设置头像部分的View
     */
    private void backToRegi(Intent intent) {
    	setResult(CODE_IMG_RESULT, intent);
    	finish();
    }
}
