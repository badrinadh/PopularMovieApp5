package newapp.test.test.popularmovieapp1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class MainActivity extends AppCompatActivity {

    boolean mDualPane;
    String mLastSinglePaneFragment;

    Context context;
    Loader loader;
    View view;
    GridView gridview;
    String moviesJsonStr=null;
    String orderBy;
    ArrayList<String> movieIdArray = new ArrayList<String>();
    ArrayList<String> imageArray = new ArrayList<String>();
    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    public boolean mTwoPane=false;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    SharedPreferences prefs;
    private MainActivityFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InternetConnectivity i=new InternetConnectivity();
        context=this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.container_movies) != null) {
            mTwoPane = true;
            mFragment = new MainActivityFragment();
        }

        gridview = (GridView) findViewById(R.id.gridView);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                if (mTwoPane) {
                    Bundle args = new Bundle();
                    args.putString("id",movieIdArray.get(position));
                    MovieInfoFragment fragment = new MovieInfoFragment();
                    fragment.setArguments(args);

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container_movies, fragment, DETAILFRAGMENT_TAG)
                            .commit();
                }else{
                    Bundle args = new Bundle();
                    args.putString("id",movieIdArray.get(position));
                    Intent i = new Intent(getBaseContext(), MovieInfo.class);
                    i.putExtras(args);
                    startActivity(i);
                }
            }
        });


        DisplayMetrics metrics = this.getApplicationContext().getResources().getDisplayMetrics();
        int x = metrics.widthPixels;

        if(x<1100){
            gridview.setNumColumns(2);
        }else if(x>=1100 && x<=2000 ){
            gridview.setNumColumns(3);
        }else{
            gridview.setNumColumns(4);
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        orderBy=prefs.getString("order_by_title","popular");

        loader=new Loader();


        if(orderBy.equals("favourite")){
            dbHelper = new DatabaseHelper(getApplicationContext());
            db = dbHelper.getWritableDatabase();

            Cursor c = db.rawQuery("SELECT "+ DatabaseContract.Table1.POSTER_IMAGE_URL+","+DatabaseContract.Table1.MOVIE_ID+" FROM " + DatabaseContract.Table1.TABLE_NAME, null);
            if(c.moveToFirst()){
                do{
                    imageArray.add(c.getString(c.getColumnIndex(DatabaseContract.Table1.POSTER_IMAGE_URL)));
                    movieIdArray.add(c.getString(c.getColumnIndex(DatabaseContract.Table1.MOVIE_ID)));
                }while(c.moveToNext());
            }
            c.close();
            gridview.setAdapter(new ImageAdapter(this, imageArray));
        }else{
            if (i.isNetworkAvailable(this)) {
                new LoadImages().execute();
            }
        }

    }

    private class LoadImages extends AsyncTask<Void, Void, String> {

        protected void onPreExecute(){
            loader.startLoader(context,"Loading","please Wait...!");
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
            Log.e("URL", builtUri.toString());
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
                imageArray=getImageUrl(str);
                gridview.setAdapter(new ImageAdapter(getApplicationContext(),imageArray));
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

    protected void onResume() {
        super.onResume();
        String sortOrder=prefs.getString("order_by_title","popular");
        if(sortOrder != null && !sortOrder.equals(orderBy)) {
            orderBy=sortOrder;
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        if(moviesJsonStr!=null){
            savedInstanceState.putString("AsyncData",moviesJsonStr);
        }else{
            savedInstanceState.putString("AsyncData",null);
        }

        // etc.
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        String str = savedInstanceState.getString("AsyncData");

        if(str!=null){
            try {
                imageArray=getImageUrl(str);
                gridview.setAdapter(new ImageAdapter(getApplicationContext(),imageArray));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i=new Intent(this,SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
