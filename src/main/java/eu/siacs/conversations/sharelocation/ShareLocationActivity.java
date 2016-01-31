package eu.siacs.conversations.sharelocation;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.ViewById;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;

@EActivity(R.layout.share_locaction_activity)
public class ShareLocationActivity extends Activity implements IMyLocationConsumer {

	@ViewById(R.id.share_button)
	Button mShareButton;
	@ViewById(R.id.snackbar)
	RelativeLayout mSnackbar;

	@FragmentById(R.id.map_fragment)
	MapFragment mapFragment;

	GpsMyLocationProvider locationProvider;

	@AfterViews
	void init() {
		locationProvider = new GpsMyLocationProvider(this);
		mapFragment.showLocation(true);
	}

	@Click(R.id.cancel_button)
	void cancel() {
		setResult(RESULT_CANCELED);
		finish();
	}

	@Click(R.id.share_button)
	void share() {
		Location lastLocation = locationProvider.getLastKnownLocation();

		if (lastLocation != null) {
			Intent result = new Intent();
			result.putExtra("latitude", lastLocation.getLatitude());
			result.putExtra("longitude", lastLocation.getLongitude());
			result.putExtra("altitude", lastLocation.getAltitude());
			result.putExtra("accuracy", (int) lastLocation.getAccuracy());
			setResult(RESULT_OK, result);
			finish();
		}
	}

	@Click(R.id.snackbar_action)
	void locationSettings() {
		startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (LocationUtil.isLocationEnabled(this)) {
			this.mSnackbar.setVisibility(View.GONE);
		} else {
			this.mSnackbar.setVisibility(View.VISIBLE);
		}

		mShareButton.setEnabled(false);
		mShareButton.setTextColor(0x8a000000);
		mShareButton.setText(R.string.locating);

		locationProvider.startLocationProvider(this);
	}

	@Override
	protected void onPause() {
		locationProvider.stopLocationProvider();

		super.onPause();
	}

	private void centerOnLocation(Location location) {
		mapFragment.moveTo(location.getLatitude(), location.getLongitude(), Config.DEFAULT_ZOOM);
	}

	@Override
	public void onLocationChanged(Location location, IMyLocationProvider locationProvider) {
		centerOnLocation(location);
		this.mShareButton.setEnabled(true);
		this.mShareButton.setTextColor(0xde000000);
		this.mShareButton.setText(R.string.share);
	}
}
