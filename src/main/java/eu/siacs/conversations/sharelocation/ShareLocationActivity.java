package eu.siacs.conversations.sharelocation;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import java.util.List;
import java.util.Locale;

public class ShareLocationActivity extends Activity implements OnMapReadyCallback,
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener,
		LocationListener{

	private GoogleMap mGoogleMap;
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	private Location mLastLocation;
	private Button mCancelButton;
	private Button mShareButton;
	private RelativeLayout mSnackbar;
    private RelativeLayout mLocationInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_locaction_activity);
		MapFragment fragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);
		fragment.getMapAsync(this);
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addApi(LocationServices.API)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();
		mCancelButton = (Button) findViewById(R.id.cancel_button);
		mCancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		mShareButton = (Button) findViewById(R.id.share_button);
		mShareButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
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
		});
		mSnackbar = (RelativeLayout) findViewById(R.id.snackbar);
		TextView snackbarAction = (TextView) findViewById(R.id.snackbar_action);
		snackbarAction.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.mLastLocation = null;
		if (isLocationEnabled()) {
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

	@Override
	public void onMapReady(GoogleMap googleMap) {
		this.mGoogleMap = googleMap;
		this.mGoogleMap.setMyLocationEnabled(true);
	}

	private void centerOnLocation(LatLng location) {
		this.mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, Config.DEFAULT_ZOOM));
	}

	@Override
	public void onConnected(Bundle bundle) {
		mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

	}

	@Override
	public void onLocationChanged(Location location) {
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        if (latitude != 0 && longitude != 0) {
            Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
            try {
                List<Address> addresses = geoCoder.getFromLocation(latitude, longitude, 1);

                String address = "";
                if (addresses != null) {
                    Address Address = addresses.get(0);
                    StringBuilder strAddress = new StringBuilder("");

                    for (int i = 0; i < Address.getMaxAddressLineIndex(); i++) {
                        strAddress.append(Address.getAddressLine(i)).append("\n");
                    }
                    address = strAddress.toString();
                    address = address.substring(0, address.length()-1); //trim last \n
                    mLocationInfo = (RelativeLayout) findViewById(R.id.location);
                    TextView snackbarLocation = (TextView) findViewById(R.id.snackbar_message);
                    snackbarLocation.setText(address);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
		if (this.mLastLocation == null) {
			centerOnLocation(new LatLng(location.getLatitude(), location.getLongitude()));
			this.mShareButton.setEnabled(true);
			this.mShareButton.setTextColor(0xde000000);
			this.mShareButton.setText(R.string.share);
            mLocationInfo.setVisibility(View.VISIBLE);
		}
		this.mLastLocation = location;
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	private boolean isLocationEnabledKitkat() {
		try {
			int locationMode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
			return locationMode != Settings.Secure.LOCATION_MODE_OFF;
		} catch (Settings.SettingNotFoundException e) {
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	private boolean isLocationEnabledLegacy() {
		String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		return !TextUtils.isEmpty(locationProviders);
	}

	private boolean isLocationEnabled() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
			return isLocationEnabledKitkat();
		}else{
			return isLocationEnabledLegacy();
		}
	}
}
