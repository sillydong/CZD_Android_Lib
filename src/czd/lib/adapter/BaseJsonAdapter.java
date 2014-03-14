package czd.lib.adapter;

import android.content.Context;
import czd.lib.data.JSONUtil;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created with IntelliJ IDEA.
 * User: chenzhidong
 * Date: 13-12-28
 * Time: 上午10:35
 */
public abstract class BaseJsonAdapter extends android.widget.BaseAdapter {
	protected Context context;
	protected JSONArray datas;

	public BaseJsonAdapter(Context context) {
		this.context = context;
		this.datas = new JSONArray();
	}

	public BaseJsonAdapter(Context context, JSONArray datas) {
		this.context = context;
		this.datas = datas;
	}

	public void setDatas(JSONArray datas) {
		this.datas = datas;
		notifyDataSetChanged();
	}

	public void appendDatas(JSONArray datas) {
		this.datas = JSONUtil.combineArrays(this.datas, datas);
		notifyDataSetChanged();
	}
	
	public void removeItem(int position){
		this.datas=JSONUtil.removeAtIndex(this.datas,position);
		notifyDataSetChanged();
	}
	
	public void cleanAll(){
		this.datas=new JSONArray();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return this.datas == null ? 0 : this.datas.length();
	}

	@Override
	public Object getItem(int position) {
		try
		{
			if (this.datas != null && this.datas.length() > 0 && position >= 0 && position <= this.datas.length())
				return datas.getJSONObject(position);
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
