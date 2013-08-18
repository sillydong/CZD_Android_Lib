package czd.lib.view.tab;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class TabBarHelper {
	public static int handleClick(ViewGroup parent, View view, int current, OnTabClickListener listener) {
		int index = ((Integer) view.getTag()).intValue();
		if (index != current) {
			listener.onTabClick(index);
			resetTabs(parent, index);
		}
		else {
			listener.onTabReClick(index);
		}
		return index;
	}

	public static void initTabs(ViewGroup parent, int index, OnClickListener listener) {
		int k = 0;
		int len = parent.getChildCount();
		for (int i = 0; i < len; i++) {
			View tab = parent.getChildAt(i);
			if (tab.getClass() == TabButton.class) {
				if (k == index) {
					((TabButton) tab).setChecked(true);
				}
				else if (((TabButton) tab)._checked) {
					((TabButton) tab).setChecked(false);
				}
				tab.setTag(Integer.valueOf(k));
				tab.setOnClickListener(listener);
				k++;
			}
		}
	}

	public static void resetTabs(ViewGroup parent, int index) {
		int k = 0;
		int len = parent.getChildCount();
		for (int i = 0; i < len; i++) {
			View tab = parent.getChildAt(i);
			if (tab != null && tab.getClass() == TabButton.class) {
				if (k == index) {
					((TabButton) tab).setChecked(true);
				}
				else if (((TabButton) tab)._checked) {
					((TabButton) tab).setChecked(false);
				}
				k++;
			}
		}
	}

	public static int setCurrentTab(ViewGroup parent, int index, int current) {
		if (index != current) {
			resetTabs(parent, index);
		}
		return index;
	}

}
