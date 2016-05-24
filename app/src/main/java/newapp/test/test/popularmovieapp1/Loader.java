package newapp.test.test.popularmovieapp1;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by badarinadh on 4/1/2016.
 */
public class Loader {

    ProgressDialog progress;

    public void startLoader(Context context,String title,String message){
        progress = ProgressDialog.show(context, title,
                message, true);
    }

    public void stopLoader(){
        if(progress.isShowing()){
            progress.dismiss();
        }
    }
}
