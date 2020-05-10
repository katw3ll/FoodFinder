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
import com.yandex.mapkit.map.MapObjectCollection;
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

    private String versionPathName = "version.json";
    private String dataFileName = "data.json";


//    private APIHandler api;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_menu1:

                    return true;
                case R.id.navigation_menu2:

                    return true;
                case R.id.navigation_menu3:

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Test TAG", "Это мое сообщение для записи в журнале");

        try {
            String FILENAME = "user_details";
            String name = "suresh";

            String pathJson = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/AlarmHouses.json";
            File fileJson = new File(pathJson);

            fileJson.createNewFile();
            PrintWriter out = new PrintWriter(fileJson);
            out.close();



//            api = new APIHandler(this);
            Log.i("Test", "Api создано =================");
        } catch (IOException e) {
            Log.i("Test","Oops, Something wrong with URL...");
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



        mapview.getMap().getMapObjects()
                .addPlacemark(
                        new Point(55.751574, 37.573856),
                        ImageProvider.fromResource(this, R.drawable.ic_burger)
                );
        mapview.getMap().getMapObjects()
                .addPlacemark(
                        new Point(55.772699, 37.681056),
                        ImageProvider.fromResource(this, R.drawable.ic_burger)
                );
//        mapview.getMap().getMapObjects()
//                .addPlacemark(
//                        new Point(55.532966, 37.527265),
//                        ImageProvider.fromResource(this, R.drawable.ic_my)
//                );


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
