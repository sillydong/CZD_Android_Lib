package czd.lib.adapter;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: chenzhidong
 * Date: 13-12-28
 * Time: 上午10:35
 */
public abstract class BaseArrayAdapter extends android.widget.BaseAdapter {
	protected Context context;
	protected ArrayList<Object> datas;

	public BaseArrayAdapter(Context context) {
		this.context = context;
		this.datas = new ArrayList<Object>();
	}

	public BaseArrayAdapter(Context context, ArrayList<Object> datas) {
		this.context = context;
		this.datas = datas;
	}

	public void setDatas(ArrayList<Object> datas) {
		this.datas = datas;
		notifyDataSetChanged();
	}

	public void appendDatas(ArrayList<Object> datas) {
		this.datas.addAll(datas);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return this.datas == null ? 0 : this.datas.size();
	}

	@Override
	public Object getItem(int position) {
		if (this.datas != null && this.datas.size() > 0 && position >= 0 && position <= this.datas.size())
			return datas.get(position);
		return null;
	}
}
