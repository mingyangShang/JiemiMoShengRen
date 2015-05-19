package com.levelup.jiemimoshengren.utils;

import android.content.Context;
import android.widget.ImageView;

import com.levelup.jiemimoshengren.R;
import com.levelup.jiemimoshengren.base.SmyApplication;
import com.levelup.jiemimoshengren.model.User;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

public class UserUtils {
    /**
     * 根据username获取相应user，由于demo没有真实的用户数据，这里给的模拟的数据；
     * @param username
     * @return
     */
    public static User getUserInfo(String username){
        User user = SmyApplication.getSingleton().getContacts().get(username);
        if(user == null){
            user = new User(username);
        }
            
        if(user != null){
            //demo没有这些数据，临时填充
            user.setNick(username);
//            user.setAvatar("http://downloads.easemob.com/downloads/57.png");
        }
        return user;
    }
    
    /**
     * 设置用户头像
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView){
        User user = getUserInfo(username);
        setUserAvatar(context, user, imageView);
    }
    public static void setUserAvatar(Context context,User user,ImageView imageView){
         if(user != null && user.getImgUrl()!=null && user.getImgUrl().startsWith("http")){
         	ImageLoader.getInstance().displayImage(user.getImgUrl(), imageView);
//             Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
         }else{
//             Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
         	ImageLoader.getInstance().displayImage("drawable://"+R.drawable.default_avatar, imageView);
         }
    }
    
}
