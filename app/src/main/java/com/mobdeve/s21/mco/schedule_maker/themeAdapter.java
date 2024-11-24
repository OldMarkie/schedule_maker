package com.mobdeve.s21.mco.schedule_maker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import com.mobdeve.s21.mco.schedule_maker.Themes;

public class themeAdapter extends BaseAdapter {
    private Context context;
    private List<Themes> themesList;

    public themeAdapter(Context context, List<Themes> themesList){
        this.context = context;
        this.themesList = themesList;
    }

    @Override
    public int getCount() {
        return themesList != null ? themesList.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rootView = LayoutInflater.from(context)
                .inflate(R.layout.item_theme, viewGroup, false);

        TextView txtName = rootView.findViewById(R.id.name);
        ImageView image = rootView.findViewById(R.id.image);

        txtName.setText(themesList.get(i).getName());
        image.setImageResource(themesList.get(i).getImage());

        return rootView;

    }
}
