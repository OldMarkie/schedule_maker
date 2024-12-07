package com.mobdeve.s21.mco.schedule_maker;

import android.graphics.Color;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * The `ColorUtils` class provides utility methods for working with colors,
 * including loading color data from a JSON file and finding the nearest
 * color name for a given color.
 */

public class ColorUtils {
    private Map<String, Integer> colorMap = new HashMap<>();

    /**
     * Constructor for the `ColorUtils` class.
     * Loads color data from a JSON file provided as an input stream.
     * The JSON file should contain an array of color objects, where each
     * object has "name" and "hex" properties representing the color name
     * and hexadecimal color code, respectively.
     *
     * @param inputStream The input stream containing the JSON color data.
     */

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

    /**
     * Finds the nearest color name for a given color.
     * This method calculates the Euclidean distance between the given color
     * and each color in the color map, and returns the name of the color
     * with the minimum distance.
     *
     * @param selectedColor The color to find the nearest name for.
     * @return The name of the nearest color, or null if the color map is empty.
     */

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

    /**
     * Calculates the Euclidean distance between two colors.
     *
     * @param color1 The first color.
     * @param color2 The second color.
     * @return The Euclidean distance between the two colors.
     */

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
