package com.example.foodfinder;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity {

    private final String MAPKIT_API_KEY = "e1454e95-9e09-4fcd-99b0-906ca676b547";

    private MapView mapview;

    private APIHandler api;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_menu1:
                    System.out.println("");
                    return true;
                case R.id.navigation_menu2:
                    System.out.println("SECOND SELECTED");
                    return true;
                case R.id.navigation_menu3:
                    System.out.println("THIRD SELECTED");
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            api = new APIHandler(this);
        } catch (IOException e) {
            e.printStackTrace();
        }


        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);

        // Укажите имя activity вместо map.
        setContentView(R.layout.activity_main);
        mapview = (MapView)findViewById(R.id.mapview);
        mapview.getMap().move(
                new CameraPosition(new Point(55.751574, 37.573856), 11.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);

        MapObjectTapListener listener = new MapObjectTapListener() {
            @Override
            public boolean onMapObjectTap(MapObject mapObject, Point point) {
                Log.d("TEST_TAG", "something");
                System.out.println("something");
                return false;
            }
        };

        MapObjectCollection mapObjects = mapview.getMap().getMapObjects();

        mapObjects.addPlacemark(
                new Point(55.751574, 37.573856),
                ImageProvider.fromResource(this, R.drawable.ic_burger)
        );

        mapObjects.addTapListener(listener);

//        mapview.getMap().getMapObjects()
//                .addPlacemark(
//                        new Point(55.751574, 37.573856),
//                        ImageProvider.fromResource(this, R.drawable.ic_burger)
//                );

        mapview.getMap().getMapObjects()
                .addPlacemark(
                        new Point(55.772699, 37.681056),
                        ImageProvider.fromResource(this, R.drawable.ic_burger)
                );


    }

    @Override
    protected void onStop() {
        super.onStop();
        mapview.onStop();
        MapKitFactory.getInstance().onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapview.onStart();
        MapKitFactory.getInstance().onStart();
    }

}
