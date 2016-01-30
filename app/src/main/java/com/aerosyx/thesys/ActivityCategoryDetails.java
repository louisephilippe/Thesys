package com.aerosyx.thesys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aerosyx.thesys.adapter.RecipeGridAdapter;
import com.aerosyx.thesys.data.Constant;
import com.aerosyx.thesys.data.DatabaseHandler;
import com.aerosyx.thesys.model.Category;
import com.aerosyx.thesys.model.Recipe;
import com.aerosyx.thesys.utils.Tools;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class ActivityCategoryDetails extends AppCompatActivity {

    public static final String EXTRA_OBJCT = "com.app.materialrecipe.OBJ";

    private RecyclerView recyclerView;
    private RecipeGridAdapter mAdapter;
    private Category category;
    private SearchView searchView;
    private View parent_view;
    private DatabaseHandler db;
    private ImageLoader imgloader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_details);
        parent_view = findViewById(android.R.id.content);
        db = new DatabaseHandler(getApplicationContext());
        Tools.initImageLoader(getApplicationContext());

        category = (Category) getIntent().getSerializableExtra(EXTRA_OBJCT);

        setupToolbar();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //((ImageView) findViewById(R.id.image)).setImageResource(category.getPhoto());
        ((TextView) findViewById(R.id.name)).setText(category.name);
        ((TextView) findViewById(R.id.description)).setText(category.description);
        ImageView image = (ImageView) findViewById(R.id.image);
        imgloader.displayImage(Constant.getURLimgCategory(category.banner), image);

        List<Recipe> recipes = db.getRecipesByCategoryId(category);
        mAdapter = new RecipeGridAdapter(this, recipes);
        recyclerView.setAdapter(mAdapter);

        // for system bar in lollipop
        Tools.systemBarLolipop(this);
    }

    private void setupToolbar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_category_details, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                try {
                    mAdapter.getFilter().filter(s);
                } catch (Exception e) {
                }
                return true;
            }
        });
        // Detect SearchView icon clicks
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemsVisibility(menu, searchItem, false);
            }
        });

        // Detect SearchView close
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                setItemsVisibility(menu, searchItem, true);
                return false;
            }
        });
        searchView.onActionViewCollapsed();
        return true;
    }
    private void setItemsVisibility(Menu menu, MenuItem exception, boolean visible) {
        for (int i=0; i<menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != exception) item.setVisible(visible);
        }
    }
}
