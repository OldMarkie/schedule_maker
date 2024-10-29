package com.mobdeve.s21.mco.schedule_maker;

import android.graphics.Color;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ColorUtils {
    private Map<String, Integer> colorMap = new HashMap<>();

    public ColorUtils(InputStream inputStream) {
        try {
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            String json = new String(buffer);
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject colorObject = jsonArray.getJSONObject(i);
                String name = colorObject.getString("name");
                String hex = colorObject.getString("hex");
                colorMap.put(name, Color.parseColor(hex));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getNearestColorName(int selectedColor) {
        String nearestColor = null;
        double minDistance = Double.MAX_VALUE;

        for (Map.Entry<String, Integer> entry : colorMap.entrySet()) {
            int color = entry.getValue();
            double distance = calculateDistance(selectedColor, color);
            if (distance < minDistance) {
                minDistance = distance;
                nearestColor = entry.getKey();
            }
        }
        return nearestColor;
    }

    private double calculateDistance(int color1, int color2) {
        int r1 = Color.red(color1);
        int g1 = Color.green(color1);
        int b1 = Color.blue(color1);
        int r2 = Color.red(color2);
        int g2 = Color.green(color2);
        int b2 = Color.blue(color2);
        return Math.sqrt(Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2) + Math.pow(b1 - b2, 2));
    }
}
