package newapp.test.test.popularmovieapp1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import newapp.test.test.popularmovieapp1.db.DatabaseContract;
import newapp.test.test.popularmovieapp1.db.DatabaseHelper;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    Loader loader;
    View view;
    GridView gridview;
    String moviesJsonStr=null;
    String orderBy;
    ArrayList<String> movieIdArray = new ArrayList<String>();
    ArrayList<String> imageArray = new ArrayList<String>();
    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        InternetConnectivity i=new InternetConnectivity();

        view= inflater.inflate(R.layout.fragment_main, container, false);

        gridview = (GridView) view.findViewById(R.id.gridView);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent i= new Intent(getActivity(),MovieInfo.class);
                i.putExtra("id",movieIdArray.get(position));
                startActivity(i);
            }
        });


        DisplayMetrics metrics = getActivity().getApplicationContext().getResources().getDisplayMetrics();
        int x = metrics.widthPixels;

        if(x<1100){
            gridview.setNumColumns(2);
        }else if(x>=1100 && x<=2000 ){
            gridview.setNumColumns(3);
        }else{
            gridview.setNumColumns(4);
        }


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        orderBy=prefs.getString("order_by_title","popular");

        loader=new Loader();


        if(orderBy.equals("favourite")){
            dbHelper = new DatabaseHelper(getActivity().getApplicationContext());
            db = dbHelper.getWritableDatabase();

            Cursor c = db.rawQuery("SELECT "+DatabaseContract.Table1.POSTER_IMAGE_URL+","+DatabaseContract.Table1.MOVIE_ID+" FROM " + DatabaseContract.Table1.TABLE_NAME, null);
            if(c.moveToFirst()){
                do{
                    imageArray.add(c.getString(c.getColumnIndex(DatabaseContract.Table1.POSTER_IMAGE_URL)));
                    movieIdArray.add(c.getString(c.getColumnIndex(DatabaseContract.Table1.MOVIE_ID)));
                }while(c.moveToNext());
            }
            c.close();
            gridview.setAdapter(new ImageAdapter(getActivity(), imageArray));
        }else{
            if (i.isNetworkAvailable(getActivity())) {
                new LoadImages().execute();
            }
        }

        return view;
    }

    private class LoadImages extends AsyncTask<Void, Void, String> {

        protected void onPreExecute(){
            loader.startLoader(getActivity(),"Loading","please Wait...!");
        }
        @Override
        protected String doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            final String FORECAST_BASE_URL =
                    "http://api.themoviedb.org/3/movie/"+orderBy+"?";
            final String APPID_PARAM = "api_key";
            final String OPEN_MOVIE_API_KEY="bbd22e5e355e2bbf74bf36675374c355";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(APPID_PARAM, OPEN_MOVIE_API_KEY)
                    .build();
            Log.e("URL",builtUri.toString());
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
                    return moviesJsonStr= buffer.toString();
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
                ArrayList<String> imageArray=getImageUrl(str);
                gridview.setAdapter(new ImageAdapter(getActivity(),imageArray));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            loader.stopLoader();
        }
    }

    public ArrayList<String> getImageUrl(String str) throws JSONException {
        ArrayList<String> urlArray = new ArrayList<String>();
        if(str.equals("")){

        }else{
            JSONObject moviesJson = new JSONObject(str);
            JSONArray moviesArray = moviesJson.getJSONArray("results");
            for(int i = 0; i < moviesArray.length(); i++) {
                JSONObject moviesCast = moviesArray.getJSONObject(i);
                urlArray.add(moviesCast.getString("poster_path"));
                movieIdArray.add(moviesCast.getString("id"));
            }
        }
        return urlArray;
    }
}
