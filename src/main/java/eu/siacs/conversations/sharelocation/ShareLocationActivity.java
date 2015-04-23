package eu.siacs.conversations.sharelocation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class ShareLocationActivity extends Activity implements LocationListener {
    private Location mLastLocation;
    private LocationManager locationManager;
    private String provider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            Log.d(Config.LOGTAG, "Selected " + provider + "as best provider");
            onLocationChanged(location);
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                // TODO do your thing
                if (mLastLocation != null && mLastLocation.getAccuracy() < 100){
                    Intent result = new Intent();
                    result.putExtra("latitude",mLastLocation.getLatitude());
                    result.putExtra("longitude",mLastLocation.getLongitude());
                    result.putExtra("altitude",mLastLocation.getAltitude());
                    result.putExtra("accuracy",(int) mLastLocation.getAccuracy());
                    setResult(RESULT_OK, result);
                    finish();
                }
            }
        }, 0, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.mLastLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
    
	}

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Please enable Location",
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }
}