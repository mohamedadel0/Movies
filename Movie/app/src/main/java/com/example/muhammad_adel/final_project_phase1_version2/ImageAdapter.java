package com.example.muhammad_adel.final_project_phase1_version2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ImageAdapter extends BaseAdapter {
    Context c;
    ArrayList<Films> films = new ArrayList<>();
    public ImageAdapter(Context c, ArrayList<Films> films) {
        this.c = c;
        this.films = films;
    }
    class ViewHolder1 {
        ImageView imageView;
        ViewHolder1(View v) {
            imageView = (ImageView) v.findViewById(R.id.img_main_grid);
        }
    }
    @Override
    public int getCount() {
        return films.size();
    }

    @Override
    public Object getItem(int i) {
        return films.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        ViewHolder1 viewHolder1 = null;
       /* ImageView img;
        img = (ImageView) view;
        //if (img == null) {
           img = new ImageView(c);
        //} inflate*/
        if (v == null) {
            LayoutInflater layoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = layoutInflater.inflate(R.layout.image_for_main_grid, viewGroup, false);
            viewHolder1 = new ViewHolder1(v);
            v.setTag(viewHolder1);
        } else {
            viewHolder1 = (ViewHolder1) v.getTag();
        }
        Picasso.with(c).load(films.get(i).poster_path).placeholder(R.drawable.placeholder).error(R.drawable.error).fit().centerCrop().into(viewHolder1.imageView);
        return v;
    }
}
