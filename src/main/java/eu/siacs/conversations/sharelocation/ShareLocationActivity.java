package eu.siacs.conversations.sharelocation;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ViewSwitcher;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;

@EActivity(R.layout.share_locaction_activity)
public class ShareLocationActivity extends BaseLocationActivity {
	private static final int LOCATING = 0;
	private static final int SHARE = 1;

	@ViewById(R.id.snackbar)
	RelativeLayout mSnackbar;
	@ViewById
	ViewSwitcher shareSwitcher;

	@FragmentById(R.id.map_fragment)
	MapFragment mapFragment;

	@AfterViews
	void init() {
		requirePermissions(new String[]{
				Manifest.permission.ACCESS_FINE_LOCATION,
				Manifest.permission.WRITE_EXTERNAL_STORAGE
		});

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

		shareSwitcher.setDisplayedChild(LOCATING);
	}

	private void centerOnLocation(Location location) {
		mapFragment.moveTo(location.getLatitude(), location.getLongitude(), Config.DEFAULT_ZOOM);
	}

	@Override
	@UiThread
	public void onLocationChanged(Location location, IMyLocationProvider locationProvider) {
		super.onLocationChanged(location, locationProvider);

		centerOnLocation(location);
		shareSwitcher.setDisplayedChild(SHARE);
	}
}
