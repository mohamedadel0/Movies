package com.example.muhammad_adel.final_project_phase1_version2;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class FragFavourite extends Fragment {
    FilmData filmData;
    /*****************Films info**************/
    ArrayList<String> arrayListPosters = new ArrayList<>();
    ArrayList<String> arrayListTitles = new ArrayList<>();
    ArrayList<String> arrayListReleaseData = new ArrayList<>();
    ArrayList<String> arrayListVoteAverage = new ArrayList<>();
    ArrayList<String> arrayListOverview = new ArrayList<>();
    ArrayList<String> arrayListTrailers = new ArrayList<>();
    ArrayList<String> arrayListAuthorsForReviews = new ArrayList<>();
    ArrayList<String> arrayListReviews = new ArrayList<>();
    GridView gridView;
    Cursor cursor;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_favourite, container, false);
        gridView = (GridView)v;
        filmData = new FilmData(getActivity());
        cursor = filmData.getAllData();
        if(cursor != null) {
            int index1 = cursor.getColumnIndex("posters");
            int index2 = cursor.getColumnIndex("title");
            int index3 = cursor.getColumnIndex("releaseData");
            int index4 = cursor.getColumnIndex("voteAverage");
            int index5 = cursor.getColumnIndex("overview");
            int index6 = cursor.getColumnIndex("trailers");
            int index7 = cursor.getColumnIndex("authorsForReviews");
            int index8 = cursor.getColumnIndex("reviews");
            while(cursor.moveToNext()) {
                /**********Get films information from database to add to favourite*****/
                arrayListPosters.add(cursor.getString(index1));
                arrayListTitles.add(cursor.getString(index2));
                arrayListReleaseData.add(cursor.getString(index3));
                arrayListVoteAverage.add(cursor.getString(index4));
                arrayListOverview.add(cursor.getString(index5));
                arrayListTrailers.add(cursor.getString(index6));
                arrayListAuthorsForReviews.add(cursor.getString(index7));
                arrayListReviews.add(cursor.getString(index8));
            }
        }
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PostersAdapter posterAdapter = new PostersAdapter(arrayListPosters);
        gridView.setAdapter(posterAdapter);
        final FrameLayout frameLayout = (FrameLayout) getActivity().findViewById(R.id.add_frag_details_from_favourite);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String posters = arrayListPosters.get(i);
                String titles = arrayListTitles.get(i);
                String releaseData = arrayListReleaseData.get(i);
                String voteAverage = arrayListVoteAverage.get(i);
                String overView = arrayListOverview.get(i);
                String[] trailers = ConvertTypes.convertStringToArray(arrayListTrailers.get(i));
                String[] authorsForReviews = ConvertTypes.convertStringToArray(arrayListAuthorsForReviews.get(i));
                String[] reviews = ConvertTypes.convertStringToArray(arrayListReviews.get(i));
                /****************Open details Activity offline********************/
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FilmDataToDetails", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("poster_path", posters);
                editor.putString("title", titles);
                editor.putString("release_date", releaseData);
                editor.putString("vote_average", voteAverage);
                editor.putString("overview", overView);
                editor.putString("trailers", ConvertTypes.convertArrayToString(trailers));
                editor.putString("authorsForReviews", ConvertTypes.convertArrayToString(authorsForReviews));
                editor.putString("reviews", ConvertTypes.convertArrayToString(reviews));
                editor.putBoolean("fromFavourite", true);
                editor.commit();
                if (frameLayout != null) {
                    FragmentManager manager = getActivity().getFragmentManager();
                    FragmentTransaction fragmentTransaction = manager.beginTransaction();
                    //fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.add_frag_details_from_favourite, new FragDetails()).commit();
                } else {
                    FragmentManager manager = getActivity().getFragmentManager();
                    FragmentTransaction fragmentTransaction = manager.beginTransaction().addToBackStack(null);
                    fragmentTransaction.replace(R.id.relativeFromFavouriteActivity, new FragDetails());
                    fragmentTransaction.commit();
                }


            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().finish();
    }

    public class PostersAdapter extends BaseAdapter {
        ArrayList<String> posters;
        PostersAdapter(ArrayList<String> arrayListP) {
         this.posters = arrayListP;
        }
        class ViewHolder2 {
            ImageView imageView;
            ViewHolder2(View v) {
                imageView = (ImageView) v.findViewById(R.id.imageViewPoster);
            }
        }
        @Override
        public int getCount() {
         return this.posters.size();
     }

        @Override
         public Object getItem(int i) {
         return this.posters.get(i);
     }

        @Override
         public long getItemId(int i) {
         return i;
     }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder2 viewHolder2 = null;
            View v;
            v = view;
            if (v == null) {
                 LayoutInflater inflater = getActivity().getLayoutInflater();
                 v = inflater.inflate(R.layout.poster_list_row, viewGroup, false);
                 viewHolder2 = new ViewHolder2(v);
                v.setTag(viewHolder2);
            } else {
               viewHolder2 = (ViewHolder2) v.getTag();
            }
            Picasso.with(getActivity()).load(this.posters.get(i)).placeholder(R.drawable.placeholder).error(R.drawable.error).into(viewHolder2.imageView);
            return v;
     }
 }
}
