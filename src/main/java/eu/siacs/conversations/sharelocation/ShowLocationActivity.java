package eu.siacs.conversations.sharelocation;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsItem;

@EActivity(R.layout.show_locaction_activity)
public class ShowLocationActivity extends Activity implements OnMapReadyCallback {

	private GoogleMap mGoogleMap;
	private LatLng mLocation;
	private String mLocationName;

	@FragmentById(R.id.map_fragment)
	MapFragment mapFragment;

	@AfterViews
	void init() {
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		mapFragment.getMapAsync(this);
	}

	 @Override
	 @OptionsItem(android.R.id.home)
	 public void finish() {
		 super.finish();
	 }

	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();

		this.mLocationName = intent != null ? intent.getStringExtra("name") : null;

		if (intent != null && intent.hasExtra("longitude") && intent.hasExtra("latitude")) {
			double longitude = intent.getDoubleExtra("longitude",0);
			double latitude = intent.getDoubleExtra("latitude",0);
			this.mLocation = new LatLng(latitude,longitude);
			if (this.mGoogleMap != null) {
				markAndCenterOnLocation(this.mLocation, this.mLocationName);
			}
		}
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		this.mGoogleMap = googleMap;
		this.mGoogleMap.setMyLocationEnabled(true);
		if (this.mLocation != null) {
			this.markAndCenterOnLocation(this.mLocation,this.mLocationName);
		}
	}

	private void markAndCenterOnLocation(LatLng location, String name) {
		this.mGoogleMap.clear();
		MarkerOptions options = new MarkerOptions();
		options.position(location);
		if (name != null) {
			options.title(name);
		}
		this.mGoogleMap.addMarker(options);
		this.mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, Config.DEFAULT_ZOOM));
	}

}
