package com.mobdeve.s21.mco.schedule_maker;

import java.util.ArrayList;
import java.util.List;

public class themesData {

    // Returns a list of Themes
    public static List<Themes> getThemesList() {
        List<Themes> themesList = new ArrayList<>();
        themesList.add(new Themes("Mario", R.drawable.marioicon));  // Add your actual drawable names
        themesList.add(new Themes("Pokemon", R.drawable.pokemonicon));
        themesList.add(new Themes("Minecraft", R.drawable.minecrafticon));
        themesList.add(new Themes("Legend of Zelda", R.drawable.zeldaicon));

        return themesList;
    }
}
