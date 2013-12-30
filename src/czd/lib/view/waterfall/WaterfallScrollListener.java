package czd.lib.view.waterfall;

import czd.lib.view.absinterface.PositionListener;

public abstract interface WaterfallScrollListener extends PositionListener {
	public void onRefresh();

}
