package com.example.muhammad_adel.final_project_phase1_version2;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class FragDetails extends Fragment {
    Cursor cursor;
    CheckBox checkBox;
    boolean fromFavouriteToPause;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_details, container, false);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FilmDataToDetails", Context.MODE_PRIVATE);
        final String poster = sharedPreferences.getString("poster_path", "");
        final String title = sharedPreferences.getString("title", "");
        final String releaseData = sharedPreferences.getString("release_date", "");
        final String voteAverage = sharedPreferences.getString("vote_average", "");
        final String overview = sharedPreferences.getString("overview", "");
        final String[] trailers = ConvertTypes.convertStringToArray(sharedPreferences.getString("trailers", ""));
        final String[] authorsForReviews = ConvertTypes.convertStringToArray(sharedPreferences.getString("authorsForReviews", ""));
        final String[] reviews = ConvertTypes.convertStringToArray(sharedPreferences.getString("reviews", ""));
        final boolean fromFavourite = sharedPreferences.getBoolean("fromFavourite", false);
        fromFavouriteToPause = fromFavourite;

        ImageView imageView = (ImageView) getActivity().findViewById(R.id.imageView);
        Picasso.with(getActivity()).load(poster).into(imageView);
        TextView filmTitle = (TextView) getActivity().findViewById(R.id.title);
        TextView date = (TextView) getActivity().findViewById(R.id.date);
        TextView filmVoteAverage = (TextView) getActivity().findViewById(R.id.vote_average);
        TextView filmOverview = (TextView) getActivity().findViewById(R.id.overview);
        ListView listViewForTrailers = (ListView) getActivity().findViewById(R.id.listView);
        ListView listViewForReviews = (ListView) getActivity().findViewById(R.id.listView2);
        filmTitle.setText(title);
        date.setText(releaseData);
        filmVoteAverage.setText(voteAverage);
        filmOverview.setText(overview);
        filmOverview.setMovementMethod(new ScrollingMovementMethod());
        String array_for_list_item_row[] = new String[trailers.length];
        /**********If one of movies has more than one trailer*************/

        for (int number_of_trailers = 0; number_of_trailers < trailers.length; number_of_trailers++) {
            array_for_list_item_row[number_of_trailers] = "Trailer " + (number_of_trailers + 1);
        }
        /********************Add Trailers to listView************************/
        ArrayAdapter list_items = new ArrayAdapter(getActivity(), R.layout.list_row_for_trailers, R.id.text_trailer, array_for_list_item_row);
        listViewForTrailers.setAdapter(list_items);
        /*******************Open Trailers on Youtube************************/
       listViewForTrailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               Intent open_trailer_from_youtube = new Intent(Intent.ACTION_VIEW, Uri.parse(trailers[i]));
               startActivity(open_trailer_from_youtube);
           }
       });

        /**********************Start Add Reviews *****************************/
        listViewForReviews.setAdapter(new ReviewsAdapter(authorsForReviews, reviews));
        /*********************Start Add To Favourite***************************/
        final FilmData filmData = new FilmData(getActivity());
        checkBox = (CheckBox) getActivity().findViewById(R.id.checkBox);
         cursor = filmData.getAllData();
        if (cursor != null) {
            int index = cursor.getColumnIndex("title");
            while (cursor.moveToNext()) {
                String titleFromDataBase = cursor.getString(index);

                if (title.equals(titleFromDataBase)) { // why (==) doesn't work ?!

                    checkBox.setChecked(true);
                }
            }
        }


       checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
               if (b == true) {
                   long row = filmData.insertData(poster, title, releaseData, voteAverage, overview, trailers, authorsForReviews, reviews);
                    if(row > 0) {
                       MessageForDB.message(getActivity(), "Added to favourite");
                   }
               } else {
                   filmData.deleteFilmFormFavourite(title);
                   MessageForDB.message(getActivity(), "Removed from favourite");
                   /********To Set Changes if user remove film from detail activity*********/
                   if(fromFavourite) {
                       getActivity().finish();
                      // getActivity().onBackPressed();
                   }



               }
           }
       });


    }

    @Override
    public void onPause() {
        super.onPause();
        final FrameLayout frameLayout = (FrameLayout) getActivity().findViewById(R.id.add_frag_details);

        if (fromFavouriteToPause && !checkBox.isChecked()) {
            Intent refreshFavouriteActivity = new Intent(getActivity(), Favourite.class);
            startActivity(refreshFavouriteActivity);
        } else if (frameLayout == null){
            getFragmentManager().popBackStack();
        }
    }


    public class ReviewsAdapter extends BaseAdapter{
        String[] authorsForReviews;
        String[] reviews;
        public ReviewsAdapter (String[] authorsForReviews, String[] reviews) {
            this.authorsForReviews = authorsForReviews;
            this.reviews = reviews;
        }
        @Override
        public int getCount() {
            return reviews.length;
        }

        @Override
        public Object getItem(int i) {
            return reviews[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater layoutInflater = getActivity().getLayoutInflater();
            View root = layoutInflater.inflate(R.layout.list_row_for_reviews, viewGroup, false);
            TextView authorName = (TextView) root.findViewById(R.id.authorName);
            TextView review = (TextView) root.findViewById(R.id.review);
            authorName.setText(authorsForReviews[i]);
            /*To make Scroll reviews more readable*/
            if (reviews.length >= 2) {
                if (reviews[1] != null && reviews[0] != null) {
                    if (reviews[1].length() > reviews[0].length()) {
                        String temp = reviews[1];
                        reviews[1] = reviews[0];
                        reviews[0] = temp;
                        String temp2 = authorsForReviews[1];
                        authorsForReviews[1] = authorsForReviews[0];
                        authorsForReviews[0] = temp2;
                    }
                }
            }
            review.setText(reviews[i]);
            return root;
        }
    }
}