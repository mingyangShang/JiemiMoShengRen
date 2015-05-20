/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.levelup.jiemimoshengren.model;

import com.easemob.chat.EMContact;
/**用户模型类*/
public class User extends EMContact {
	protected int unreadMsgCount;
	protected String header;
	protected String avatar;

	protected String sign;
	protected boolean isFemale;
	protected String imgUrl;
	protected String pwd;
	
	public static final String SEX_FEMALE = "F",SEX_MALE = "M";

	public User() {}


	public boolean isFemale() {
		return isFemale;
	}

	public void setFemale(boolean isFemale) {
		this.isFemale = isFemale;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		if(sign == null){
			this.sign = "";
		}else{
			this.sign = sign;
		}
	}

	public User(String username) {
		this.username = username;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public int getUnreadMsgCount() {
		return unreadMsgCount;
	}

	public void setUnreadMsgCount(int unreadMsgCount) {
		this.unreadMsgCount = unreadMsgCount;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
	public void setNick(String newNick){
		System.err.println("setnick");
		System.err.println("newNick:"+newNick);
		System.err.println("currusername:"+username);
		if(!newNick.equals(username)){
			super.setNick(newNick);
		}else{
			System.err.println("不同意修改昵称");
		}
	}

	@Override
	public int hashCode() {
		return 17 * getUsername().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof User)) {
			return false;
		}
		return getUsername().equals(((User) o).getUsername());
	}

	@Override
	public String toString() {
		return "username:"+username+",url:"+imgUrl+",usernick:"+(nick==null?"null":nick+",female:"+isFemale);
	}


	public String getPwd() {
		return pwd;
	}


	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
}
