package newapp.test.test.popularmovieapp1;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import newapp.test.test.popularmovieapp1.db.DatabaseContract;
import newapp.test.test.popularmovieapp1.db.DatabaseHelper;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieInfoFragment extends Fragment {

    String movieId,trailer,review;
    View view;
    Loader loader;
    String moviesInfoJsonStr;
    ImageView img1,img2,fav;
    TextView title,rating,release,votes,description,reviewTV;
    int width,oneThird;
    boolean loadStatus=true;
    boolean favorite=false;
    Button loadmore;
    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    String dMovieName,dMoviePoster,dMovieBackdrop,dMovieReview,dMovieRatting,dMovieTrailer,dMovieVotes,dReleaseDate,dDescription;
    public MovieInfoFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        InternetConnectivity i= new InternetConnectivity();
        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        width = metrics.widthPixels;
        oneThird=Math.round(width / 3);

        Bundle arguments = getArguments();
        if(arguments!= null){
            movieId=arguments.getString("id");
        }else {
            movieId = getActivity().getIntent().getExtras().getString("id");
        }
        dbHelper = new DatabaseHelper(getActivity().getApplicationContext());
        db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM " + DatabaseContract.Table1.TABLE_NAME + " WHERE " + DatabaseContract.Table1.MOVIE_ID + "='" + movieId + "'", null);

        if(c.moveToFirst()){
            do{
                //,,,dDescription;
                dMovieName = c.getString(c.getColumnIndex(DatabaseContract.Table1.MOVIE_NAME));
                dMoviePoster = c.getString(c.getColumnIndex(DatabaseContract.Table1.POSTER_IMAGE_URL));
                dMovieBackdrop = c.getString(c.getColumnIndex(DatabaseContract.Table1.BACKDROP_IMAGE_URL));
                dMovieReview = c.getString(c.getColumnIndex(DatabaseContract.Table1.REVIEW));
                dMovieRatting = c.getString(c.getColumnIndex(DatabaseContract.Table1.RATTING));
                dMovieTrailer = c.getString(c.getColumnIndex(DatabaseContract.Table1.TRAILER_URL));
                dMovieVotes = c.getString(c.getColumnIndex(DatabaseContract.Table1.VOTES));
                dReleaseDate = c.getString(c.getColumnIndex(DatabaseContract.Table1.RELEASE_DATE));
                dDescription = c.getString(c.getColumnIndex(DatabaseContract.Table1.DESCRIPTION));

            }while(c.moveToNext());
        }
        c.close();

        view=inflater.inflate(R.layout.fragment_movie_info, container, false);
        img1=(ImageView) view.findViewById(R.id.imageView);
        img2=(ImageView) view.findViewById(R.id.poster);
        title=(TextView) view.findViewById(R.id.title);
        rating=(TextView) view.findViewById(R.id.ratting);
        release=(TextView) view.findViewById(R.id.release);
        votes=(TextView) view.findViewById(R.id.votes);
        description=(TextView) view.findViewById(R.id.description);
        trailer="Hxy8BZGQ5Jo";
        LinearLayout pTrailer=(LinearLayout) view.findViewById(R.id.pTrailer);
        reviewTV=(TextView) view.findViewById(R.id.review);
        loadmore=(Button) view.findViewById(R.id.loadMore);
        fav=(ImageView) view.findViewById(R.id.fav);

        if(dbHelper.CheckIsDataAlreadyInDBorNot(DatabaseContract.Table1.TABLE_NAME,DatabaseContract.Table1.MOVIE_ID,movieId,db)){
            favorite=true;

            setFav();
        }else{
            setUnFav();
        }

        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favorite) {
                    favorite = false;
                    setUnFav();
                    db.execSQL("DELETE FROM " + DatabaseContract.Table1.TABLE_NAME + " WHERE " + DatabaseContract.Table1.MOVIE_ID + "='" + movieId + "'");
                    Toast.makeText(getActivity(),"Removed from Fav",Toast.LENGTH_LONG).show();
                } else {
                    setFav();
                    ContentValues values = new ContentValues();
                    values.put(DatabaseContract.Table1.MOVIE_ID, movieId);
                    values.put(DatabaseContract.Table1.MOVIE_NAME, dMovieName);
                    values.put(DatabaseContract.Table1.POSTER_IMAGE_URL, dMoviePoster);
                    values.put(DatabaseContract.Table1.BACKDROP_IMAGE_URL, dMovieBackdrop);
                    values.put(DatabaseContract.Table1.RATTING, dMovieRatting);
                    values.put(DatabaseContract.Table1.RELEASE_DATE, dReleaseDate);
                    values.put(DatabaseContract.Table1.VOTES, dMovieVotes);
                    values.put(DatabaseContract.Table1.TRAILER_URL, dMovieTrailer);
                    values.put(DatabaseContract.Table1.DESCRIPTION, dDescription);
                    values.put(DatabaseContract.Table1.REVIEW, dMovieReview);

                    db.insert(DatabaseContract.Table1.TABLE_NAME, null, values);
                    favorite = true;
                    Toast.makeText(getActivity(),"Added to Fav",Toast.LENGTH_LONG).show();
                }

            }
        });

        loadmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loadStatus){
                    //maximise
                    loadStatus=false;
                    reviewTV.setMaxLines(Integer.MAX_VALUE);
                    loadmore.setText("minimize");
                }else{
                    //minimize
                    loadStatus=true;
                    reviewTV.setLines(2);
                    loadmore.setText("Load more");
                }
            }
        });

        pTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + dMovieTrailer)));
            }
        });

        loader=new Loader();
        if(!favorite){
            if (i.isNetworkAvailable(getActivity())) {
                new LoadMovieInfo().execute();
            }
        }else{
            Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w500"+dMovieBackdrop)
                    .resize(width,600)
                    .centerCrop()
                    .placeholder(R.drawable.loading)
                    .into(img1);
            reviewTV.setText(dMovieReview);
            title.setText(dMovieName);
            rating.setText(dMovieRatting+"/10");
            votes.setText("Votes : "+dMovieVotes);
            release.setText("Released : "+dReleaseDate);
            description.setText(dDescription);
            Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w500" + dMoviePoster)
                    .placeholder(R.drawable.loading)
                    .into(img2);
        }

        return view;
    }

    private class LoadMovieInfo extends AsyncTask<Void, Void, String> {

        protected void onPreExecute(){
            loader.startLoader(getActivity(),"Loading","please Wait...!");
        }
        @Override
        protected String doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            final String FORECAST_BASE_URL =
                    "https://api.themoviedb.org/3/movie/"+movieId+"?";
            final String APPID_PARAM = "api_key";
            final String OPEN_MOVIE_API_KEY="bbd22e5e355e2bbf74bf36675374c355";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(APPID_PARAM, OPEN_MOVIE_API_KEY)
                    .appendQueryParameter("append_to_response","releases,trailers,reviews")
                    .build();
            try {
                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }else{
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() == 0) {
                        return null;
                    }
                    return moviesInfoJsonStr= buffer.toString();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String str){
            try {
                getMovieInfoJson(str);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            loader.stopLoader();
        }
    }

    public void getMovieInfoJson(String str) throws JSONException, IOException {
        JSONObject object1=new JSONObject(str);
        dMovieBackdrop=object1.getString("backdrop_path");
        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w500"+dMovieBackdrop)
                .resize(width,600)
                .centerCrop()
                .placeholder(R.drawable.loading)
                .into(img1);

        JSONObject object2=object1.getJSONObject("trailers");
        JSONArray arr1=object2.getJSONArray("youtube");
        JSONObject object3=arr1.getJSONObject(0);
        dMovieTrailer=trailer=object3.getString("source");

        JSONObject object4=object1.getJSONObject("reviews");
        JSONArray arr2=object4.getJSONArray("results");
        JSONObject object5=arr2.getJSONObject(0);

        dMovieReview=review=object5.getString("content");
        reviewTV.setText(review);

        dMovieName=object1.getString("original_title");
        title.setText(dMovieName);
        dMovieRatting=object1.getString("vote_average");
        rating.setText(dMovieRatting+"/10");
        dMovieVotes=object1.getString("vote_count");
        votes.setText("Votes : "+dMovieVotes);
        dReleaseDate=object1.getString("release_date");
        release.setText("Released : "+dReleaseDate);
        dDescription=object1.getString("overview");
        description.setText(dDescription);
        dMoviePoster=object1.getString("poster_path");
        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w500" + dMoviePoster)
                .placeholder(R.drawable.loading)
                .into(img2);
    }

    public void setFav(){
        fav.setImageDrawable(getResources().getDrawable(R.drawable.start_fav));
    }

    public void setUnFav(){
        fav.setImageDrawable(getResources().getDrawable(R.drawable.star_default));
    }

}