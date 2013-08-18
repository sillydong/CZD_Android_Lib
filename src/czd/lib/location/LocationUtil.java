package czd.lib.location;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationUtil {

	public static String getAddress(Context context, Location loc) {
		String result = "";
		Geocoder gc = new Geocoder(context, Locale.getDefault());
		try {
			List<Address> addresses = gc.getFromLocation(loc.getLatitude(), loc.getLongitude(), 10);
			if (addresses != null && addresses.size() > 0) {
				Address address = addresses.get(0);
				int maxLine = address.getMaxAddressLineIndex();
				if (maxLine >= 2) {
					result = address.getAddressLine(1) + address.getAddressLine(2);
				}
				else {
					result = address.getAddressLine(1);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Address searchLocationByName(Context context, String name) {
		Geocoder gc = new Geocoder(context, Locale.getDefault());
		try {
			List<Address> addresses = gc.getFromLocationName(name, 1);
			if (addresses != null) {
				Address address_send = null;
				for (Address address : addresses) {
					address.getAddressLine(1);
					address_send = address;
				}
				return address_send;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static double lonToPx(double lng, int zoom) {
		return (lng + 180) * (256L << zoom) / 360;
	}

	public static double pxToLon(double pixelX, int zoom) {
		return pixelX * 360 / (256L << zoom) - 180;
	}

	public static double latToPx(double lat, int zoom) {
		double siny = Math.sin(lat * Math.PI / 180);
		double y = Math.log((1 + siny) / (1 - siny));
		return (128 << zoom) * (1 - y / (2 * Math.PI));
	}

	public static double pxToLat(double pixelY, int zoom) {
		double y = 2 * Math.PI * (1 - pixelY / (128 << zoom));
		double z = Math.pow(Math.E, y);
		double siny = (z - 1) / (z + 1);
		return Math.asin(siny) * 180 / Math.PI;
	}

}
