package com.example.foodfinder;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class APIHandler {
    private String apiUrl = "https://apidata.mos.ru/v1/datasets/1903/rows?$top=5000";
    private String versionUrl = "https://apidata.mos.ru/v1/datasets/1903/version";
    private String apiKey = "api_key=dc1e343a94767732736a858f05dd4a98";

    private RequestQueue mQueue;

    private String versionPathName = "version.json";
    private String dataFileName = "data.json";

    final private String LOG_TAG = "DebugTest";

    private Context context;


    public APIHandler(Context context) throws IOException {
        mQueue = Volley.newRequestQueue(context);
        this.context = context;
        checkVersion();
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
                                    context.openFileInput(versionPathName)));
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
            outputStream = context.openFileOutput(versionPathName, Context.MODE_PRIVATE);
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
                                    context.openFileOutput(dataFileName, context.MODE_PRIVATE)));
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
