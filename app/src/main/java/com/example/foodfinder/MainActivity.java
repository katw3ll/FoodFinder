package com.example.foodfinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;


import android.util.DisplayMetrics;
import android.util.JsonReader;

import android.view.MenuItem;
import android.view.WindowManager;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.foodfinder.Model.PlacePoint;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.ismaeldivita.chipnavigation.ChipNavigationBar.OnItemSelectedListener;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;

import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polygon;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CircleMapObject;
import com.yandex.mapkit.map.Cluster;
import com.yandex.mapkit.map.ClusterListener;
import com.yandex.mapkit.map.ClusterTapListener;
import com.yandex.mapkit.map.ClusterizedPlacemarkCollection;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.PolygonMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.search.search_layer.PlacemarkListener;
import com.yandex.mapkit.search.search_layer.SearchResultItem;
import com.yandex.runtime.image.ImageProvider;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements ClusterListener {

    private final String MAPKIT_API_KEY = "e1454e95-9e09-4fcd-99b0-906ca676b547";

    private MapView mapview;
    private MapObjectCollection mapObjects;
    private ClusterizedPlacemarkCollection clusterizedCollection;

    private List<PlacePoint> cafeList = new ArrayList<>();
    private List<PlacePoint> canteenList = new ArrayList<>();
    private List<PlacePoint> barList = new ArrayList<>();
    private List<PlacePoint> restaurantList = new ArrayList<>();
    private List<PlacePoint> buffetList = new ArrayList<>();

    int iconId = R.drawable.ic_burger;

    private String apiUrl = "https://apidata.mos.ru/v1/datasets/1903/rows?$top=5000";
    private String versionUrl = "https://apidata.mos.ru/v1/datasets/1903/version";
    private String apiKey = "api_key=dc1e343a94767732736a858f05dd4a98";

    private RequestQueue mQueue;

    private String versionPathName = "version.json";
    private String dataFileName = "data.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mQueue = Volley.newRequestQueue(this);
        try {
            checkVersion();
        } catch (IOException e) {
            e.printStackTrace();
        }

        getPointsFromFile();

        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);

        // Укажите имя activity вместо map.
        setContentView(R.layout.activity_main);
        mapview = (MapView)findViewById(R.id.mapview);
        mapview.getMap().move(
                new CameraPosition(new Point(55.7522, 37.6156), 11.6f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 2f),
                null);

        mapObjects = mapview.getMap().getMapObjects().addCollection();
        clusterizedCollection =
                mapview.getMap().getMapObjects().addClusterizedPlacemarkCollection(this);

        ChipNavigationBar menu = findViewById(R.id.navigation);
        menu.setOnItemSelectedListener(mOnNavigationItemSelectedListener);
        menu.setItemSelected(R.id.navigation_restaurant, true);
    }

    private void getPointsFromFile(){
        try {
            JsonReader reader = new JsonReader(
                    new InputStreamReader(openFileInput("data.json"), "UTF-8")
            );
            reader.beginArray();
            while (reader.hasNext()) {
                reader.beginObject();
                while (reader.hasNext()) {
                    switch (reader.nextName()){
                        case "Cells":{
                            PlacePoint placePoint = new PlacePoint();
                            reader.beginObject();
                            while (reader.hasNext()) {
                                switch (reader.nextName()){
                                    case "Name":{
                                        placePoint.setName(reader.nextString());
                                        break;
                                    }
                                    case "TypeObject":{
                                        placePoint.setType(reader.nextString());
                                        break;
                                    }
                                    case "geoData":{
                                        reader.beginObject();
                                        reader.nextName();
                                        reader.skipValue();
                                        reader.nextName();
                                        reader.beginArray();
                                        placePoint.setLongitude(reader.nextDouble());
                                        placePoint.setLatitude(reader.nextDouble());
                                        reader.endArray();
                                        reader.endObject();
                                        break;
                                    }
                                    default:{
                                        reader.skipValue();
                                        break;
                                    }
                                }
                            }
                            reader.endObject();
                            switch (placePoint.getType()){
                                case "кафе":{
                                    cafeList.add(placePoint);
                                    break;
                                }
                                case "столовая":{
                                    canteenList.add(placePoint);
                                    break;
                                }
                                case "бар":{
                                    barList.add(placePoint);
                                    break;
                                }
                                case "ресторан":{
                                    restaurantList.add(placePoint);
                                    break;
                                }
                                case "буфет":{
                                    buffetList.add(placePoint);
                                    break;
                                }
                                default:{
                                    System.out.println(placePoint.getType());
                                    break;
                                }
                            }
                            break;
                        }
                        default:{
                            reader.skipValue();
                            break;
                        }
                    }
                }
                reader.endObject();
            }
            reader.endArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setPointsOnMap(List<PlacePoint> list, int imgId){
        clusterizedCollection.clear();
        iconId = imgId;
        for (PlacePoint point : list) {
            PlacemarkMapObject mark = clusterizedCollection.addPlacemark(new Point(point.getLatitude(), point.getLongitude()));
            mark.setIcon(ImageProvider.fromResource(this, imgId));
            mark.setIconStyle(new IconStyle().setScale(0.6f));
            mark.addTapListener(listener);
        }
        clusterizedCollection.clusterPlacemarks(60, 15);
    }

    @Override
    public void onClusterAdded(Cluster cluster) {
        cluster.getAppearance().setIcon(
                new TextImageProvider(Integer.toString(cluster.getSize()), iconId));
    }

    private MapObjectTapListener listener = new MapObjectTapListener() {
        @Override
        public boolean onMapObjectTap(MapObject mapObject, Point point) {
            Intent intent = new Intent(MainActivity.this, PanoramaActivity.class);
            intent.putExtra("latitude", point.getLatitude());
            intent.putExtra("longitude", point.getLongitude());
            startActivity(intent);
            return true;
        }
    };
    
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

    private ChipNavigationBar.OnItemSelectedListener  mOnNavigationItemSelectedListener
            = new ChipNavigationBar.OnItemSelectedListener() {

        @Override
        public void onItemSelected(int id) {
            switch (id) {
                case R.id.navigation_cafe:
                    System.out.println("cafe");
                    setPointsOnMap(cafeList, R.drawable.ic_cafe);
                    break;
                case R.id.navigation_restaurant:
                    System.out.println("restaurant");
                    setPointsOnMap(restaurantList, R.drawable.ic_restaurant);
                    break;
                case R.id.navigation_bar:
                    System.out.println("bar");
                    setPointsOnMap(barList, R.drawable.ic_bar);
                    break;
                case R.id.navigation_buffet:
                    System.out.println("buffet");
                    setPointsOnMap(buffetList, R.drawable.ic_buffet);
                    break;
                case R.id.navigation_canteen:
                    System.out.println("canteen");
                    setPointsOnMap(canteenList, R.drawable.ic_canteen);
                    break;
            }
        }

    };

    public class TextImageProvider extends ImageProvider {

        private static final float FONT_SIZE = 15;
        private static final float MARGIN_SIZE = 3;
        private static final float SIZE = 2;
        private static final float STROKE_SIZE = 3;

        @Override
        public String getId() {
            return "text_" + text;
        }

        private final String text;
        private final int imgId;
        @Override
        public Bitmap getImage() {
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager manager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
            manager.getDefaultDisplay().getMetrics(metrics);

            Paint textPaint = new Paint();
            textPaint.setTextSize(FONT_SIZE * metrics.density);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setStyle(Paint.Style.FILL);
            textPaint.setAntiAlias(true);

            float widthF = textPaint.measureText(text);
            Paint.FontMetrics textMetrics = textPaint.getFontMetrics();
            float heightF = Math.abs(textMetrics.bottom) + Math.abs(textMetrics.top);
            float textRadius = (float)Math.sqrt(widthF * widthF + heightF * heightF) / 2;
            float internalRadius = textRadius + MARGIN_SIZE * metrics.density;
            float externalRadius = internalRadius + STROKE_SIZE * metrics.density;

            int width = (int) (2 * externalRadius + 0.5);

//            Bitmap icon = Bitmap.createScaledBitmap(
//                    BitmapFactory.decodeResource(getResources(), imgId),
//                    width, width, false);

            Bitmap bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            Paint backgroundPaint = new Paint();
//            canvas.drawBitmap(icon, 0, 0, backgroundPaint);
            backgroundPaint.setAntiAlias(true);
//            float radius = textRadius-15 + metrics.density;
            backgroundPaint.setColor(Color.BLUE);
            canvas.drawCircle(width / 2, width / 2, externalRadius, backgroundPaint);
            backgroundPaint.setColor(Color.WHITE);
            canvas.drawCircle(width / 2, width / 2, internalRadius, backgroundPaint);

            canvas.drawText(
                    text,
                    width / 2,
                    width / 2 - (textMetrics.ascent + textMetrics.descent) / 2,
                    textPaint);

            return bitmap;
        }

        public TextImageProvider(String text, int imgId) {
            this.text = text;
            this.imgId = imgId;
        }
    }


    private void checkVersion() throws IOException {
        String url = versionUrl + "?" + apiKey;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String currentVersion = response.toString();
                        String version = "";
                        try {
                            BufferedReader br = new BufferedReader(new InputStreamReader(
                                    openFileInput(versionPathName)));
                            version = br.readLine();
                        } catch (IOException e) {
                            saveVersion(currentVersion);
                            e.printStackTrace();
                        }
                        System.out.println(currentVersion);
                        System.out.println(version);
                        if (!currentVersion.equals(version)){
                            saveVersion(currentVersion);
                            getNewData();
                        }else{
                            System.out.println("Файл не обновлен");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);

    }

    private void saveVersion(String version) {
        FileOutputStream outputStream = null;
        try {
            outputStream = openFileOutput(versionPathName, Context.MODE_PRIVATE);
            outputStream.write(version.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getNewData() {
        System.out.println("Получаем файл");
        String url = apiUrl + "&" + apiKey;

        StringRequest request = new StringRequest (Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            BufferedWriter bw =  new BufferedWriter(new OutputStreamWriter(
                                    openFileOutput(dataFileName, MODE_PRIVATE)));
                            bw.write(response);
                            System.out.println("Записан файл");
                            // закрываем поток
                            bw.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // пишем данные

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                999999999,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(request);
    }

}
