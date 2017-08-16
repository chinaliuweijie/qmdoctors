package com.qingmiao.qmdoctor.widget;

import android.text.TextUtils;

import com.qingmiao.qmdoctor.bean.ContactModel;
import com.qingmiao.qmdoctor.global.KeyOrValueGlobal;
import com.qingmiao.qmdoctor.utils.LogUtil;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 *
 * @author xiaanming
 *
 */
public class PinyinComparator implements Comparator<ContactModel> {

	public int compare(ContactModel o1, ContactModel o2) {
		if(o1.sortLetters.equals("#")&& o2.sortLetters.equals("#")){
			return o1.pinyinName.compareTo(o2.pinyinName);
		}else if (o1.sortLetters.equals("@")
				|| o2.sortLetters.equals("#")) {
			return -1;
		} else if (o1.sortLetters.equals("#")
				|| o2.sortLetters.equals("@")) {
			return 1;
		}else if(o1.sortLetters.equals(KeyOrValueGlobal.KEY_BIAOXINHUANZHE)||o2.sortLetters.equals(KeyOrValueGlobal.KEY_BIAOXINHUANZHE)){
			if(o1.sortLetters.equals(KeyOrValueGlobal.KEY_BIAOXINHUANZHE) && o2.sortLetters.equals(KeyOrValueGlobal.KEY_BIAOXINHUANZHE)){
				LogUtil.LogShitou(o1.sortLetters);
				if(Character.isLetter(o1.pinyinName.substring(0, 1).toUpperCase().charAt(0)) && Character.isLetter(o2.pinyinName.substring(0, 1).toUpperCase().charAt(0))){
					return o1.pinyinName.substring(0, 1).toUpperCase().compareTo(o2.pinyinName.substring(0, 1).toUpperCase());
				}else if(Character.isLetter(o1.pinyinName.substring(0, 1).toUpperCase().charAt(0)) && !Character.isLetter(o2.pinyinName.substring(0, 1).toUpperCase().charAt(0))){
					return -1;
				}else if(!Character.isLetter(o1.pinyinName.substring(0, 1).toUpperCase().charAt(0)) && Character.isLetter(o2.pinyinName.substring(0, 1).toUpperCase().charAt(0))){
					return 1;
				}else{
					return o1.pinyinName.compareTo(o2.pinyinName);
				}
			}
			return Integer.MAX_VALUE;
		}
		else {
			return o1.sortLetters.compareTo(o2.sortLetters);
		}
	}
}
