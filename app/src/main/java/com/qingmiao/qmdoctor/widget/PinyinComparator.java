package com.qingmiao.qmdoctor.widget;

import com.qingmiao.qmdoctor.bean.ContactModel;
import com.qingmiao.qmdoctor.global.KeyOrValueGlobal;
import com.qingmiao.qmdoctor.utils.LogUtil;

import java.util.Comparator;

/**
 *
 * @author xiaanming
 *
 */
public class PinyinComparator implements Comparator<ContactModel> {

	public int compare(ContactModel o1, ContactModel o2) {
		if (o1.sortLetters.equals("@")
				|| o2.sortLetters.equals("#")) {
			return -1;
		} else if (o1.sortLetters.equals("#")
				|| o2.sortLetters.equals("@")) {
			return 1;
		}else if(o1.sortLetters.equals(KeyOrValueGlobal.KEY_BIAOXINHUANZHE)||o2.sortLetters.equals(KeyOrValueGlobal.KEY_BIAOXINHUANZHE)){
			if(o1.sortLetters.equals(KeyOrValueGlobal.KEY_BIAOXINHUANZHE) && o2.sortLetters.equals(KeyOrValueGlobal.KEY_BIAOXINHUANZHE)){
				LogUtil.LogShitou(o1.sortLetters);
				return o1.sortLetters.compareTo(o2.sortLetters);
			}
			return 100;
		}
		else {
			return o1.sortLetters.compareTo(o2.sortLetters);
		}
	}

}
