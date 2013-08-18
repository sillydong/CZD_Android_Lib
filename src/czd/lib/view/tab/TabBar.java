package czd.lib.view.tab;

import android.view.ViewGroup;

public abstract interface TabBar {
	public abstract void addTab(TabButton tab_button);

	public abstract void addTab(TabButton tab_button, ViewGroup.LayoutParams paramLayoutParams);

	public abstract void setCurrentTab(int index);
	
	public abstract void setListener(OnTabClickListener listener);
	
}