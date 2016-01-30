package com.aerosyx.thesys;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.aerosyx.thesys.data.Constant;
import com.aerosyx.thesys.data.DatabaseHandler;
import com.aerosyx.thesys.model.Recipe;
import com.aerosyx.thesys.utils.Tools;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ActivityRecipeDetails extends AppCompatActivity {

    public static final String EXTRA_OBJCT = "com.app.sample.recipe.OBJ";

    //ads
    private AdView mAdView;

    private Recipe recipe;
    private FloatingActionButton fab;
    private View parent_view;
    private ImageLoader imgloader = ImageLoader.getInstance();
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        parent_view = findViewById(android.R.id.content);

        prepareAds();

        db = new DatabaseHandler(getApplicationContext());
        Tools.initImageLoader(getApplicationContext());

        recipe = (Recipe) getIntent().getSerializableExtra(EXTRA_OBJCT);
        setupToolbar(recipe.name);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fabToggle();

        ((TextView) findViewById(R.id.duration)).setText(recipe.duration + " ");
        ((TextView) findViewById(R.id.category)).setText(recipe.category_name);
        ((WebView) findViewById(R.id.instructions)).loadData(recipe.instruction, "text/html", "UTF-8");
        ImageView image = (ImageView) findViewById(R.id.image);
        imgloader.displayImage(Constant.getURLimgRecipe(recipe.image), image);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (db.isExist(DatabaseHandler.TABLE_FAVORITES, recipe.id+"")) {
                    db.deleteFavorites(recipe);
                    Snackbar.make(parent_view, recipe.name + " removed from favorites", Snackbar.LENGTH_SHORT).show();
                } else {
                    db.addOneFavorite(recipe);
                    Snackbar.make(parent_view, recipe.name + " added to favorites", Snackbar.LENGTH_SHORT).show();
                }
                fabToggle();
            }
        });

        // for system bar in lollipop
        Tools.systemBarLolipop(this);
    }

    private void setupToolbar(String name) {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(name);
    }

    private void fabToggle() {
        if (db.isExist(DatabaseHandler.TABLE_FAVORITES, recipe.id + "")) {
            fab.setImageResource(R.drawable.ic_nav_favorites);
        } else {
            fab.setImageResource(R.drawable.ic_nav_favorites_outline);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_recipe_details, menu);
        return true;
    }

    private void prepareAds(){
        // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
        // values/strings.xml.
        mAdView = (AdView) findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);
    }
}
