package newapp.test.test.popularmovieapp1;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by badarinadh on 3/30/2016.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    public ArrayList<String> movieArray=new ArrayList<String>();
    public int width=0;
    public ImageAdapter(Context c,ArrayList movieArray) {
        mContext = c;
        this.movieArray=movieArray;
        DisplayMetrics metrics = c.getResources().getDisplayMetrics();
        int x = metrics.widthPixels;
        if(x<1100){
            width=Math.round(x/2);
        }else if(x>=1100 && x<=2000 ){
            width=Math.round(x/3);
        }else{
            width=Math.round(x/4);
        }
    }

    public int getCount() {
        return movieArray.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(width,700));
        } else {
            imageView = (ImageView) convertView;
        }
        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185/"+movieArray.get(position))
                .resize(width, 700)
                .centerCrop()
                .placeholder(R.drawable.loading)
                .into(imageView);
        //imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }
}