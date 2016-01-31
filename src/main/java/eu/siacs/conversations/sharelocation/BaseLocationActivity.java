package eu.siacs.conversations.sharelocation;

import android.location.Location;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;

@EActivity
public abstract class BaseLocationActivity extends BaseActivity implements IMyLocationConsumer {
    protected GpsMyLocationProvider locationProvider;

    @AfterViews
    protected void initBaseLocationActivity() {
        locationProvider = new GpsMyLocationProvider(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationProvider.startLocationProvider(this);
    }

    @Override
    protected void onPause() {
        locationProvider.stopLocationProvider();
        super.onPause();
    }

    @Override
    public void onLocationChanged(Location location, IMyLocationProvider provider) {
        // ignore
    }
}
