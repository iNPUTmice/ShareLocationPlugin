package eu.siacs.conversations.sharelocation;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class ShowLocationActivity extends Activity implements OnMapReadyCallback {

	private GoogleMap mGoogleMap;
	private LatLng mLocation;
	private String mLocationName;

    class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View InfoWindow;

        InfoWindowAdapter() {
            InfoWindow = getLayoutInflater().inflate(R.layout.show_location_infowindow, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {

            TextView Title = ((TextView) InfoWindow.findViewById(R.id.title));
            Title.setText(marker.getTitle());
            TextView Snippet = ((TextView) InfoWindow.findViewById(R.id.snippet));
            Snippet.setText(marker.getSnippet());

            return InfoWindow;
        }
    }

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
        double longitude = mLocation.longitude;
        double latitude = mLocation.latitude;
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
                    options.snippet(address);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (name != null) {
            options.title(name);
        }
        this.mGoogleMap.setInfoWindowAdapter(new InfoWindowAdapter());
        this.mGoogleMap.addMarker(options).showInfoWindow();
        this.mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, Config.DEFAULT_ZOOM));
	}

}
