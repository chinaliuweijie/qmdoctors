package com.qingmiao.qmdoctor.bean;

import java.io.Serializable;

public class ContactModel implements Serializable{

	public UserFriendBean friend;
	// 首字母显示
	public String sortLetters;
	//TYPE
	public int type;

	public boolean isCheck;

	// 2 代表不是标新好友   1代表是标新好友
	public int friendLibeType = 1;

}
