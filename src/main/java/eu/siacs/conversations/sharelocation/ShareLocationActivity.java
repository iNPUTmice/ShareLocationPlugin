package eu.siacs.conversations.sharelocation;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Button;
import android.widget.ViewSwitcher;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;

@EActivity(R.layout.share_locaction_activity)
public class ShareLocationActivity extends BaseLocationActivity {
	private static final int SEARCHING = 0;
	private static final int MAP = 1;

	@ViewById
	FloatingActionButton share;

	@FragmentById(R.id.map_fragment)
	MapFragment mapFragment;

	@ViewById
	IconicsImageView icon;

	@ViewById
	Button enable;

	@ViewById
	ViewSwitcher switcher;

	@AfterViews
	void init() {
		requirePermissions(new String[]{
				Manifest.permission.ACCESS_FINE_LOCATION,
				Manifest.permission.WRITE_EXTERNAL_STORAGE
		});

		mapFragment.showLocation(true);

		share.setImageDrawable(
				new IconicsDrawable(this)
						.icon(GoogleMaterial.Icon.gmd_send)
						.color(Color.WHITE)
						.sizeDp(24)
		);
	}

	@Click
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

	@Click
	void enable() {
		startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	}

	@Override
	protected void onResume() {
		super.onResume();

		icon.setIcon(LocationUtil.isLocationEnabled(this) ? GoogleMaterial.Icon.gmd_location_searching : GoogleMaterial.Icon.gmd_location_disabled);
		enable.setVisibility(LocationUtil.isLocationEnabled(this) ? View.GONE : View.VISIBLE);
		switcher.setDisplayedChild(SEARCHING);
	}

	private void centerOnLocation(Location location) {
		mapFragment.moveTo(location.getLatitude(), location.getLongitude(), Config.DEFAULT_ZOOM);
	}

	@Override
	@UiThread
	public void onLocationChanged(Location location, IMyLocationProvider locationProvider) {
		super.onLocationChanged(location, locationProvider);

		centerOnLocation(location);

		if(Permissions.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
			// only show the map if we got the required permission
			switcher.setDisplayedChild(MAP);
		}
	}
}
