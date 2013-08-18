package czd.lib.location;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 
 * @author Chen.Zhidong
 *         2012-1-5
 * 
 */
public class BasicLocationLoader implements LocationListener {

	private Criteria cr;
	private LocationManager locationManager;
	private static final int HANDLER_COMPLETE = 0;
	private static final int HANDLER_CONNECT = 1;
	private static final int HANDLER_ERROR = 2;
	private static final int HANDLER_EMPTY = 3;
	private Context mContext;
	private Handler locationhandler;
	private Location result = null;
	private String provider;

	public BasicLocationLoader(final Context context, final LocationCallback _callback) {
		mContext = context;
		locationhandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case HANDLER_COMPLETE:
					locationManager.removeUpdates(BasicLocationLoader.this);
					_callback.LocationLoadComplete((Location) msg.obj);
					break;
				case HANDLER_CONNECT:
					_callback.LocationLoadConnect();
					break;
				case HANDLER_ERROR:
					locationManager.removeUpdates(BasicLocationLoader.this);
					_callback.LocationLoadError((String) msg.obj);
					break;
				case HANDLER_EMPTY:
					_callback.LocationLoadEmptyProvider();
					break;
				default:
					break;
				}
			}

		};

		locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			locationhandler.sendEmptyMessage(HANDLER_EMPTY);
		}
		else {
			locationhandler.sendEmptyMessage(HANDLER_CONNECT);
			cr = new Criteria();
			cr.setAccuracy(Criteria.ACCURACY_FINE);
			cr.setAltitudeRequired(false);
			cr.setBearingRequired(false);
			cr.setCostAllowed(false);
			cr.setPowerRequirement(Criteria.POWER_MEDIUM);
			provider = locationManager.getBestProvider(cr, true);
			result = locationManager.getLastKnownLocation(provider);
			if (result == null) {
				locationManager.requestLocationUpdates(provider, 0, 0, this);
			}
			else {
				locationhandler.sendMessage(locationhandler.obtainMessage(HANDLER_COMPLETE, result));
			}
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		if (location == null) {
			if (provider.equals(LocationManager.GPS_PROVIDER) && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				provider = LocationManager.NETWORK_PROVIDER;
				result = locationManager.getLastKnownLocation(provider);
			}
			else if (provider.equals(LocationManager.NETWORK_PROVIDER) && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				provider = LocationManager.GPS_PROVIDER;
				result = locationManager.getLastKnownLocation(provider);
			}
			if (result == null) {
				locationhandler.sendMessage(locationhandler.obtainMessage(HANDLER_ERROR, "无法获得当前位置！"));
			}
			else {
				locationhandler.sendMessage(locationhandler.obtainMessage(HANDLER_COMPLETE, result));
			}
		}
		else {
			locationhandler.sendMessage(locationhandler.obtainMessage(HANDLER_COMPLETE, location));
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.v("Pull", "on Status Changed");
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.v("Pull", "on Provider Enabled");
	}

	@Override
	public void onProviderDisabled(String provider) {
		locationhandler.sendMessage(locationhandler.obtainMessage(HANDLER_ERROR, "地点源关闭，无法获取地点！"));
	}

	public interface LocationCallback {
		public void LocationLoadComplete(Location location);

		public void LocationLoadConnect();

		public void LocationLoadError(String error_msg);

		public void LocationLoadEmptyProvider();
	}
}
