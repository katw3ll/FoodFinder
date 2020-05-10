package com.example.foodfinder;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.foodfinder.Models.VersionApi;
import com.example.foodfinder.Util.JsonParser;

import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class APIHandler {
    private String apiUrl = "https://apidata.mos.ru/v1/datasets/1903/rows?$top=5000";
    private String versionUrl = "https://apidata.mos.ru/v1/datasets/1903/version";
    private String apiKey = "api_key=dc1e343a94767732736a858f05dd4a98";

    private String versionPathName = "version.json";
    private String dataFileName = "data.json";

    private final JsonParser jsonParser;

    public APIHandler(Context context) throws IOException {
        jsonParser = new JsonParser();
        Log.i("APP-CREATE", "Создание файла");
        FileOutputStream fileOutputStream = context.openFileOutput("simplefile.txt", context.MODE_PRIVATE);
        OutputStreamWriter outputWriter = new OutputStreamWriter(fileOutputStream);
        outputWriter.write("Test".toString());
        outputWriter.close();

        Log.i("APP-CREATE", "Создан");

        Log.i("Test", "Файлы созданы");

//        if(checkVersion()){
//            Log.d("Test","Oops, Something wrong with URL...");
//        }

        Log.d("Test","Oops, Something wrong with URL...");
    }


    public boolean checkVersion(){
        try {
            URL url = new URL(versionUrl + "" + apiKey);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.connect();

            InputStream in;
            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                in = connection.getErrorStream();
            } else {
                in = connection.getInputStream();
            }

            VersionApi versionGet = jsonParser.getVersion(in);
            in.close();
            in = new FileInputStream(versionPathName);
            VersionApi versionFile = jsonParser.getVersion(in);

            if (versionFile == versionGet){
                return true;
            }

        }catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            return false;
        }
        return false;
    }

}
