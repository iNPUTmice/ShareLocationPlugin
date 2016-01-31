package eu.siacs.conversations.sharelocation;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.share_locaction_activity)
public class ShareLocationActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, LocationListener {

	private GoogleApiClient mGoogleApiClient;
	private Location mLastLocation;
	@ViewById(R.id.share_button)
	Button mShareButton;
	@ViewById(R.id.snackbar)
	RelativeLayout mSnackbar;

	@FragmentById(R.id.map_fragment)
	MapFragment mapFragment;

	@AfterViews
	void init() {
		mapFragment.showLocation(true);

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addApi(LocationServices.API)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();
	}

	@Click(R.id.cancel_button)
	void cancel() {
		setResult(RESULT_CANCELED);
		finish();
	}

	@Click(R.id.share_button)
	void share() {
		if (mLastLocation != null) {
			Intent result = new Intent();
			result.putExtra("latitude",mLastLocation.getLatitude());
			result.putExtra("longitude",mLastLocation.getLongitude());
			result.putExtra("altitude",mLastLocation.getAltitude());
			result.putExtra("accuracy",(int) mLastLocation.getAccuracy());
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
		this.mLastLocation = null;
		if (LocationUtil.isLocationEnabled(this)) {
			this.mSnackbar.setVisibility(View.GONE);
		} else {
			this.mSnackbar.setVisibility(View.VISIBLE);
		}
		mShareButton.setEnabled(false);
		mShareButton.setTextColor(0x8a000000);
		mShareButton.setText(R.string.locating);
		mGoogleApiClient.connect();
	}

	@Override
	protected void onPause() {
		mGoogleApiClient.disconnect();
		super.onPause();
	}

	private void centerOnLocation(Location location) {
		mapFragment.moveTo(location.getLatitude(), location.getLongitude(), Config.DEFAULT_ZOOM);
	}

	@Override
	public void onConnected(Bundle bundle) {
		LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient,
				LocationRequest.create()
						.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
						.setInterval(1000),
				this
		);
	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

	}

	@Override
	public void onLocationChanged(Location location) {
		if (this.mLastLocation == null) {
			centerOnLocation(location);
			this.mShareButton.setEnabled(true);
			this.mShareButton.setTextColor(0xde000000);
			this.mShareButton.setText(R.string.share);
		}
		this.mLastLocation = location;
	}
}
