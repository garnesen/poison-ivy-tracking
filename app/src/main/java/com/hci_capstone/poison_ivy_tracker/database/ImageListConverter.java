package com.hci_capstone.poison_ivy_tracker.database;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ImageListConverter {

    @TypeConverter
    public List<String> fromJson(String json) {
        if (json == null) {
            return null;
        }

        Type listType = new TypeToken<List<String>>() {}.getType();
        return new Gson().fromJson(json, listType);
    }

    @TypeConverter
    public String listToJson(List<String> imageLocations) {
        if (imageLocations == null) {
            return null;
        }

        Gson gson = new Gson();
        String json = gson.toJson(imageLocations);
        return json;
    }
}
