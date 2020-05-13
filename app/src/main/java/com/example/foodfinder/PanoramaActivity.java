package com.example.foodfinder;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.foodfinder.R;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.places.PlacesFactory;
import com.yandex.mapkit.places.panorama.NotFoundError;
import com.yandex.mapkit.places.panorama.PanoramaService;
import com.yandex.mapkit.places.panorama.PanoramaView;
import com.yandex.runtime.Error;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

public class PanoramaActivity extends Activity implements PanoramaService.SearchListener {
    private final String MAPKIT_API_KEY = "e1454e95-9e09-4fcd-99b0-906ca676b547";
    private Point place;

    private PanoramaView panoramaView;
    private PanoramaService panoramaService;
    private PanoramaService.SearchSession searchSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
        PlacesFactory.initialize(this);
        setContentView(R.layout.activity_panorama);
        super.onCreate(savedInstanceState);
        panoramaView = (PanoramaView)findViewById(R.id.panoview);

        panoramaService = PlacesFactory.getInstance().createPanoramaService();
        place = new Point(
                getIntent().getDoubleExtra("latitude", 0),
                getIntent().getDoubleExtra("longitude", 0));
        searchSession = panoramaService.findNearest(place, this);
    }

    @Override
    protected void onStop() {
        panoramaView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        panoramaView.onStart();
    }

    @Override
    public void onPanoramaSearchResult(String panoramaId) {
        panoramaView.getPlayer().openPanorama(panoramaId);
        panoramaView.getPlayer().enableMove();
        panoramaView.getPlayer().enableRotation();
        panoramaView.getPlayer().enableZoom();
        panoramaView.getPlayer().enableMarkers();
    }

    @Override
    public void onPanoramaSearchError(Error error) {
        String errorMessage = getString(R.string.unknown_error_message);
        if (error instanceof NotFoundError) {
            errorMessage = getString(R.string.not_found_error_message);
        } else if (error instanceof RemoteError) {
            errorMessage = getString(R.string.remote_error_message);
        } else if (error instanceof NetworkError) {
            errorMessage = getString(R.string.network_error_message);
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
