package com.example.muhammad_adel.final_project_phase1_version2;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class FragMovie extends Fragment {

    private static String API = "ac34c9df3507bf1ea6784702591ddc82";
    ArrayList<Films> films = new ArrayList<>();
    GridView gridView;
    ImageAdapter adapter;
    public static final String topRated_API = "http://api.themoviedb.org/3/movie/top_rated?api_key=" + API;
    public static final String mostPopular_API = "http://api.themoviedb.org/3/movie/popular?api_key=" + API;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.frag_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.most_popular:
                new GetMoviesFromSite().execute(mostPopular_API);
                return true;
            case R.id.top_rated:
                new GetMoviesFromSite().execute(topRated_API);
                return true;
            case R.id.favourite:
                Intent intent = new Intent (getActivity(), Favourite.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_movie, container, false);

        new GetMoviesFromSite().execute(topRated_API);
        gridView = (GridView) rootView.findViewById(R.id.gridView);
        return rootView;

    }
    public class GetMoviesFromSite extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /**if internet connection is connected Progress activity will open
             * if internet connection is not connected Progress activity will not open so user can access favourite activity offline
             * ****/

            if (isConnected()) {
                Intent openProgress = new Intent(getActivity(), Progress.class);
                startActivity(openProgress);
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);



        }

        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection connection = null;
            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                bufferedReader = new BufferedReader (new InputStreamReader(in));
                StringBuffer mainJson = new StringBuffer();
                String line;
                while((line = bufferedReader.readLine()) != null) {
                    mainJson.append(line);
                }

                try {
                            JSONObject mainObject = new JSONObject(mainJson.toString());
                            final JSONArray result = mainObject.getJSONArray("results");
                            JSONObject film;
                            for (int i = 0; i < result.length(); i++) {
                               // final int filmNumber = i;
                                film = result.getJSONObject(i);
                                films.add(new Films());
                                int FILM_ID = film.getInt("id");
                                films.get(i).id = FILM_ID;




                                films.get(i).poster_path = "http://image.tmdb.org/t/p/w185" + film.getString("poster_path");
                                films.get(i).title = film.getString("title");
                                films.get(i).release_date = film.getString("release_date");
                                films.get(i).vote_average = film.getString("vote_average");
                                films.get(i).overview = film.getString("overview");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                publishProgress("finish");
                return mainJson.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(final String result) {

            super.onPostExecute(result);

            adapter = new ImageAdapter(getActivity(), films);
            gridView.setAdapter(adapter);
           final FrameLayout frameLayout = (FrameLayout) getActivity().findViewById(R.id.add_frag_details);


            Progress.fa.finish(); // this line to finish progress activity
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    final Films film = films.get(i);
                    /*********Start getting Trailers and reviews when user click***********/
                    String trailersLink = "http://api.themoviedb.org/3/movie/" +film.id+ "/videos?api_key=" + API;
                    RequestQueue requestQueue1 = Volley.newRequestQueue(getActivity());
                    final JsonObjectRequest jsonObjectRequestForTrailers = new JsonObjectRequest(Request.Method.GET, trailersLink, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray jsonArray = response.getJSONArray("results");
                                film.trailers = new String[jsonArray.length()];
                                for(int count = 0; count < jsonArray.length(); count++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(count);
                                    try {
                                        film.trailers[count] = "http://www.youtube.com/watch?v=" + jsonObject.getString("key");
                                    } catch (NullPointerException e) {

                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            String reviewsLink = "http://api.themoviedb.org/3/movie/" + film.id + "/reviews?api_key=" + API;
                            RequestQueue requestQueue2 = Volley.newRequestQueue(getActivity());
                            JsonObjectRequest jsonObjectRequestForReviews = new JsonObjectRequest(Request.Method.GET, reviewsLink, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        JSONArray jsonArray = response.getJSONArray("results");
                                        film.reviews = new String[jsonArray.length()];
                                        film.authorsForReviews = new String[jsonArray.length()];
                                        for (int count = 0; count < jsonArray.length(); count++) {
                                            film.authorsForReviews[count] = jsonArray.getJSONObject(count).getString("author");
                                            film.reviews[count] = jsonArray.getJSONObject(count).getString("content");
                                        }

                                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FilmDataToDetails", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("poster_path", film.poster_path);
                                        editor.putString("title", film.title);
                                        editor.putString("release_date", film.release_date);
                                        editor.putString("vote_average", film.vote_average);
                                        editor.putString("overview", film.overview);
                                        editor.putString("trailers", ConvertTypes.convertArrayToString(film.trailers));
                                        editor.putString("authorsForReviews", ConvertTypes.convertArrayToString(film.authorsForReviews));
                                        editor.putString("reviews", ConvertTypes.convertArrayToString(film.reviews));
                                        editor.putBoolean("fromFavourite", false);
                                        editor.commit();

                                        if (frameLayout != null) {
                                            FragmentManager manager = getActivity().getFragmentManager();
                                            FragmentTransaction fragmentTransaction = manager.beginTransaction();
                                            //fragmentTransaction.addToBackStack(null);
                                            fragmentTransaction.replace(R.id.add_frag_details, new FragDetails()).commit();
                                            // getFragmentManager().popBackStack();
                                        } else {
                                            FragmentManager manager = getActivity().getFragmentManager();
                                            FragmentTransaction fragmentTransaction = manager.beginTransaction();
                                            FragDetails fragDetails = new FragDetails();
                                            fragmentTransaction.replace(R.id.relativeFromMainActivity, fragDetails);
                                            fragmentTransaction.addToBackStack(null);
                                            fragmentTransaction.commit();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("VOLLEY", "onErrorResponse: " + error);
                                }
                            });

                            requestQueue2.add(jsonObjectRequestForReviews);

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("VOLLEY", "onErrorResponse: " + error);
                        }
                    });
                    requestQueue1.add(jsonObjectRequestForTrailers);

                    /*********End getting Trailers and reviews***********/


                }
                });

        }
    }


public boolean isConnected () {
    boolean connected;
    ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
    if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
        //we are connected to a network
        connected = true;
        return connected;
    }
    else
        connected = false;
        return connected;
    }
}
