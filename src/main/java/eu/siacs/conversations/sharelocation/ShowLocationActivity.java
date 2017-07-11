package eu.siacs.conversations.sharelocation;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShowLocationActivity extends Activity implements OnMapReadyCallback {

	private GoogleMap mGoogleMap;
	private LatLng mLocation;
	private String mLocationName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		setContentView(R.layout.show_locaction_activity);
		MapFragment fragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);
		fragment.getMapAsync(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
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
	protected void onPause() {
		super.onPause();
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
			this.mGoogleMap.addMarker(options).showInfoWindow();
		} else {
			this.mGoogleMap.addMarker(options);
		}
		this.mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, Config.DEFAULT_ZOOM));
	}

}
