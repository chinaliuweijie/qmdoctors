package com.qingmiao.qmdoctor.factory;

import android.support.v4.app.Fragment;

import com.qingmiao.qmdoctor.fragment.ConversationListFragment;
import com.qingmiao.qmdoctor.fragment.DocuMentFragment;
import com.qingmiao.qmdoctor.fragment.HomeFragment;
import com.qingmiao.qmdoctor.fragment.MeFragment;
import com.qingmiao.qmdoctor.fragment.PatientFragment;

import java.util.HashMap;

/**
 * company : 青苗
 * Created by 姬鹏杰 on 2017/3/9.
 */

public class FragmentFactory {

	private static HashMap<Integer, Fragment> sSavedFragment = new HashMap<>();

	public static Fragment getFragment(int position) {
		Fragment fragment = sSavedFragment.get(position);
		if (fragment == null) {
			switch (position) {
				case 0:
					fragment = new HomeFragment();
					break;
				case 1:
					fragment = new ConversationListFragment();
					break;
				case 2:
					fragment = new PatientFragment();
					break;
				case 3:
					fragment = new MeFragment();
					break;
			}
			sSavedFragment.put(position, fragment);
		}
		return fragment;
	}

}
