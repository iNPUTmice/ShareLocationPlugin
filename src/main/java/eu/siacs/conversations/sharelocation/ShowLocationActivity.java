package eu.siacs.conversations.sharelocation;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.show_locaction_activity)
public class ShowLocationActivity extends BaseLocationActivity {
	@FragmentById(R.id.map_fragment)
	MapFragment mapFragment;

	@Extra("name")
	String locationName;

	@Extra
	double latitude;

	@Extra
	double longitude;

	@ViewById
	Button showOwnLocation;

	@AfterViews
	void init() {
		requirePermissions(new String[]{
				Manifest.permission.WRITE_EXTERNAL_STORAGE
		});

		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		mapFragment.showLocation(true);
		updateMapMarker();
	}

	@Override
	protected void onResume() {
		super.onResume();

		showOwnLocation.setVisibility(Permissions.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ? View.GONE : View.VISIBLE);
	}

	@Click
	void showOwnLocation() {
		requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
	}

	@Override
	@OptionsItem(android.R.id.home)
	public void finish() {
		super.finish();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);	// inject new values to the variables
		updateMapMarker();
	}

	private void updateMapMarker() {
		markAndCenterOnLocation(latitude, longitude, locationName);
	}

	private void markAndCenterOnLocation(double latitude, double longitude, String name) {
		mapFragment.moveTo(latitude, longitude, Config.DEFAULT_ZOOM)
				.clearMarkers()
				.addMarker(
						Marker.builder()
								.latitude(latitude)
								.longitude(longitude)
								.title(name)
								.build()
				);
	}
}
