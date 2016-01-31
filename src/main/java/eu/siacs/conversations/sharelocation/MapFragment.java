package eu.siacs.conversations.sharelocation;

import android.app.Fragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.fragment_map)
public class MapFragment extends Fragment {
    @ViewById
    protected MapView map;

    private MyLocationNewOverlay locationOverlay;
    private ItemizedIconOverlay<OverlayItem> markerOverlay;

    private boolean showLocation;
    private List<OverlayItem> overlayItems = new ArrayList<>();
    private GpsMyLocationProvider locationProvider;

    @AfterViews
    protected void init() {
        locationProvider = new GpsMyLocationProvider(getActivity());

        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        locationOverlay = new MyLocationNewOverlay(getActivity(), new GpsMyLocationProvider(getActivity()), map);
        markerOverlay = new ItemizedIconOverlay<>(getActivity(), overlayItems, null);

        map.getOverlays().add(markerOverlay);
    }

    public MapFragment moveTo(double lat, double lon, int zoom) {
        map.getController().setZoom(zoom);
        map.getController().setCenter(new GeoPoint(lat, lon));

        return this;
    }

    public MapFragment showLocation(boolean enable) {
        if(enable && !showLocation) {
            map.getOverlays().add(locationOverlay);

            if(isResumed()) {
                locationOverlay.enableMyLocation(locationProvider);
            }
        } else if (!enable && showLocation) {
            map.getOverlays().remove(locationOverlay);
            locationOverlay.disableMyLocation();
        }

        showLocation = enable;

        return this;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(showLocation) {
            locationOverlay.enableMyLocation(locationProvider);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        locationOverlay.disableMyLocation();
    }

    public MapFragment clearMarkers() {
        markerOverlay.removeAllItems();

        return this;
    }

    public MapFragment addMarker(Marker marker) {
        markerOverlay.addItem(new OverlayItem(marker.title(), marker.description(), new GeoPoint(marker.latitude(), marker.longitude())));

        return this;
    }
}
