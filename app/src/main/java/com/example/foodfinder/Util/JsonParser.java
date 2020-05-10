package com.example.foodfinder.Util;

import android.util.JsonReader;

import com.example.foodfinder.Models.VersionApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class JsonParser {
    public VersionApi getVersion(InputStream in) throws JSONException, IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        int versionNumber = 0;
        int releaseNumber = 0;

        try {
            reader.beginObject();
            while (reader.hasNext()) {
                switch (reader.nextName()){
                    case "versionNumber":{
                        versionNumber = reader.nextInt();
                        break;
                    }
                    case "releaseNumber":{
                        releaseNumber = reader.nextInt();
                        break;
                    }
                }
            }
            reader.endObject();
        } finally {

            reader.close();
        }

        return new VersionApi(versionNumber, releaseNumber);
    }

}
