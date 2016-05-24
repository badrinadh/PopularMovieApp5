package newapp.test.test.popularmovieapp1;

import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

/**
 * Created by badarinadh on 4/4/2016.
 */
public class InternetConnectivity {

    public boolean isNetworkAvailable(final Context context) {
        boolean returnStatus=false;
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        returnStatus=connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
        if(!returnStatus){
            Toast.makeText(context,"No internet Connection",Toast.LENGTH_LONG).show();
        }
        return returnStatus;
    }
}
