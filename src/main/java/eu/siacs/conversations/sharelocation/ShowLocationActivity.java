package eu.siacs.conversations.sharelocation;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;

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
	private ShareActionProvider mShareActionProvider;

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

	public static String createGeoUri(final LatLng location) {
		return "geo:" + location.latitude + "," + location.longitude;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.show_location, menu);

		final MenuItem item = menu.findItem(R.id.action_share_location);
		if (item.getActionProvider() != null) {
			this.mShareActionProvider = (ShareActionProvider) item.getActionProvider();

			final Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_TEXT, createGeoUri(mLocation));
			shareIntent.setType("text/plain");

			mShareActionProvider.setShareIntent(shareIntent);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			case R.id.action_copy_location:
				ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				ClipData clip = ClipData.newPlainText("location", createGeoUri(mLocation));
				clipboard.setPrimaryClip(clip);
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
