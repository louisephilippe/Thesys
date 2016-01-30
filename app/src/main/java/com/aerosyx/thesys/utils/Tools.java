package com.aerosyx.thesys.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.aerosyx.thesys.R;
import com.aerosyx.thesys.model.Category;
import com.aerosyx.thesys.model.Recipe;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class Tools {
    private static float getAPIVerison() {

        Float f = null;
        try {
            StringBuilder strBuild = new StringBuilder();
            strBuild.append(android.os.Build.VERSION.RELEASE.substring(0, 2));
            f = new Float(strBuild.toString());
        } catch (NumberFormatException e) {
            Log.e("", "erro ao recuperar a versão da API" + e.getMessage());
        }

        return f.floatValue();
    }

    public static void systemBarLolipop(Activity act){
        if (getAPIVerison() >= 5.0) {
            Window window = act.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(act.getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    public static boolean cekConnection(Context context, View view){
        ConnectionDetector conn = new ConnectionDetector(context);
        if(conn.isConnectingToInternet()){
            return true;
        }else{
            noConnectionSnackBar(view);
            return false;
        }
    }

    public static void noConnectionSnackBar(View view){
        final Snackbar snack = Snackbar.make(view, "No internet connection", 5000);
        snack.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snack.dismiss();
            }
        });
        snack.show();
    }

    public static String getRecipeString(Recipe recipe){
        Gson gson = new Gson();
        return gson.toJson(recipe);
    }
    public static String getCategoryString(Category category){
        Gson gson = new Gson();
        return gson.toJson(category);
    }

    public static void initImageLoader(Context context){
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(options)
                .memoryCacheSizePercentage(50)
                .build();

        ImageLoader.getInstance().init(config);
    }

    public static void rateAction(Activity activity){
        Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            activity.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + activity.getPackageName())));
        }
    }
}
