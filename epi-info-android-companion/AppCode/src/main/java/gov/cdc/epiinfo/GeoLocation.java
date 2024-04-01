package gov.cdc.epiinfo;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationRequest.Builder;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.data.Geometry;
import com.google.maps.android.data.MultiGeometry;
import com.google.maps.android.data.kml.KmlMultiGeometry;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPolygon;

import java.util.Calendar;
import java.util.List;

public class GeoLocation implements LocationListener {

	private static Location CurrentLocation;
	private static String CurrentGeography;
	private static long GeographyTime;
	private static FusedLocationProviderClient googleApiClient;
	private static LocationRequest locationRequest;

	@SuppressLint("MissingPermission")
	public static Location GetCurrentLocation() {
		try {
			CurrentLocation = getLastLocationIfAvailable().getResult();
		} catch (Exception ex) {

		}
		return CurrentLocation;
	}

	public static String GetCurrentGeography() {
		if (CurrentGeography == null)
			return "";
		else
			return CurrentGeography;
	}

	public void StopListening() {
		try {

			googleApiClient.removeLocationUpdates(this);
		} catch (Exception ex) {

		}
	}

	public void BeginListening(Activity activity) {

		try {
			if (googleApiClient == null) {
				googleApiClient = LocationServices.getFusedLocationProviderClient(activity);

			}

			if (locationRequest == null) {
				locationRequest = CreateLocationRequestBuilder().build();
			}

			if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return;
			}
			googleApiClient.requestLocationUpdates(locationRequest, this, Looper.getMainLooper());
			CurrentLocation = getLastLocationIfAvailable().getResult();
		} catch (Exception ex) {

		}
	}

	private Builder CreateLocationRequestBuilder() {
		Builder locationBuilder = new Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000);
		locationBuilder.setMinUpdateIntervalMillis(5000);
		return locationBuilder;
	}

	@SuppressLint("MissingPermission")
	public static Task<Location> getLastLocationIfAvailable() {
		return GoogleApiAvailability.getInstance()
				.checkApiAvailability(googleApiClient)
				.onSuccessTask(unused -> googleApiClient.getLastLocation())
				.addOnFailureListener(e -> Log.d(TAG, "Location Unavailable"));
	}

	@Override
	public void onLocationChanged(Location location) {
		CurrentLocation = location;

		if (CurrentGeography == null || CurrentGeography == "") {
			GeographyTime = Calendar.getInstance().getTimeInMillis();
			new GeoSearchTask().execute();
		} else {
			if (Calendar.getInstance().getTimeInMillis() > GeographyTime + 300000) {
				GeographyTime = Calendar.getInstance().getTimeInMillis();
				new GeoSearchTask().execute();
			}
		}
	}

	public String GetLocationName(double latitude, double longitude) {
		String name = "";

		if (AppManager.GetPlacemarks() != null) {
			LatLng location = new LatLng(latitude, longitude);

			KmlPlacemark match = liesOnPlacemark(AppManager.GetPlacemarks(), location);
			if (match != null) {
				try {
					name = match.getProperty("name").replace("<at><openparen>", "").replace("<closeparen>", "");
				} catch (Exception ex) {
					int x = 5;
					x++;
				}
			}
		}
		return name;
	}

	private class GeoSearchTask extends AsyncTask<Void, Void, String> {

		protected String doInBackground(Void... voids) {
			return GetLocationName(CurrentLocation.getLatitude(), CurrentLocation.getLongitude());
		}

		protected void onPostExecute(String result) {
			if (result != null) {
				CurrentGeography = result;
			} else {
				CurrentGeography = "";
			}
		}
	}


	private KmlPlacemark liesOnPlacemark(List<KmlPlacemark> placemarks, LatLng test) {


		if (placemarks == null || test == null) {
			return null;
		}

		for (KmlPlacemark placemark : placemarks) {
			if (placemark.getGeometry() instanceof KmlPolygon) {
				if (liesOnPolygon((KmlPolygon) placemark.getGeometry(), test)) {
					return placemark;
				}
			} else if (placemark.getGeometry() instanceof MultiGeometry) {
				if (liesOnMultigeometry((MultiGeometry) placemark.getGeometry(), test)) {
					return placemark;
				}
			}
		}

		return null;
	}

	private boolean liesOnMultigeometry(MultiGeometry multiGeometry, LatLng test) {
		for (Geometry geometry : multiGeometry.getGeometryObject()) {
			if (geometry instanceof KmlPolygon) {
				return liesOnPolygon((KmlPolygon) geometry, test);
			} else if (geometry instanceof KmlMultiGeometry) {
				return liesOnMultigeometry((KmlMultiGeometry) geometry, test);
			}
		}
		return false;
	}

	private boolean liesOnPolygon(KmlPolygon polygon, LatLng test) {
		boolean lies = false;

		if (polygon == null || test == null) {
			return lies;
		}
		List<LatLng> outerBoundary = polygon.getOuterBoundaryCoordinates();
		lies = PolyUtil.containsLocation(test, outerBoundary, true);

		if (lies) {
			List<List<LatLng>> innerBoundaries = polygon.getInnerBoundaryCoordinates();
			if (innerBoundaries != null) {
				for (List<LatLng> innerBoundary : innerBoundaries) {
					if (PolyUtil.containsLocation(test, innerBoundary, true)) {
						lies = false;
						break;
					}
				}
			}
		}

		return lies;
	}

}