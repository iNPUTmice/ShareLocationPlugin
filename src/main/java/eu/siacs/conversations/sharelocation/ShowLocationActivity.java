package eu.siacs.conversations.sharelocation;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsItem;

@EActivity(R.layout.show_locaction_activity)
public class ShowLocationActivity extends Activity {
	@FragmentById(R.id.map_fragment)
	MapFragment mapFragment;

	@Extra("name")
	String locationName;

	@Extra
	double latitude;

	@Extra
	double longitude;

	@AfterViews
	void init() {
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		mapFragment.showLocation(true);
		updateMapMarker();
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
