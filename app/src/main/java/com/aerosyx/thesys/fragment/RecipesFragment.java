package com.aerosyx.thesys.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.aerosyx.thesys.R;
import com.aerosyx.thesys.adapter.RecipeGridAdapter;
import com.aerosyx.thesys.data.Constant;
import com.aerosyx.thesys.data.DatabaseHandler;
import com.aerosyx.thesys.data.RecipeLoader;
import com.aerosyx.thesys.model.Recipe;
import com.aerosyx.thesys.utils.Tools;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by muslim on 05/01/2016.
 */
public class RecipesFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private RecipeGridAdapter mAdapter;
    private LinearLayout lyt_not_found;
    private SearchView searchView;
    private Gson gson = new Gson();
    private DatabaseHandler db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recipes, null);
        // activate fragment menu
        setHasOptionsMenu(true);
        db =  new DatabaseHandler(getActivity());

        recyclerView    = (RecyclerView) view.findViewById(R.id.recyclerView);
        lyt_not_found   = (LinearLayout) view.findViewById(R.id.lyt_not_found);
        progressBar     = (ProgressBar) view.findViewById(R.id.progressBar);

        LinearLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        List<Recipe> list = db.getAllRecipe();
        displayData(list);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_recipes, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setIconified(false);
        searchView.setQueryHint("Search Research Studies...");
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
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            boolean conn = Tools.cekConnection(getActivity().getApplicationContext(), view);
            if(conn){
                if(!onProcess){
                    onRefresh();
                }else{
                    Snackbar.make(view, "Task still running", Snackbar.LENGTH_SHORT).show();
                }
            }else {
                Tools.noConnectionSnackBar(view);
            }
        }else{
            Snackbar.make(view, item.getTitle() + " clicked", Snackbar.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setItemsVisibility(Menu menu, MenuItem exception, boolean visible) {
        for (int i=0; i<menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != exception) item.setVisible(visible);
        }
    }

    private void displayData(List<Recipe> list){
        mAdapter = new RecipeGridAdapter(getActivity(), list);
        recyclerView.setAdapter(mAdapter);
        if(mAdapter.getItemCount()==0){
            lyt_not_found.setVisibility(View.VISIBLE);
        }else{
            lyt_not_found.setVisibility(View.GONE);
        }
    }

    private boolean onProcess = false;
    private void onRefresh(){
        Snackbar.make(view, "Syncing", Snackbar.LENGTH_LONG).show();
        onProcess = true;
        showProgress(onProcess);
        String URL_recipe = Constant.getURLrecipes();
        RecipeLoader task = new RecipeLoader(new RecipeLoader.TaskListener() {
            @Override
            public void onFinished(List<Recipe> result) {
                onProcess = false;
                showProgress(onProcess);
                if(result != null){
                    List<Recipe> list = result;
                    list = db.addListRecipe(list);
                    displayData(list);
                    Snackbar.make(view, "Sync complete", Snackbar.LENGTH_LONG).show();
                }else{
                    Snackbar.make(view, "Sync failed", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        task.execute();
    }

    private void showProgress(boolean show){
        if(show){
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            lyt_not_found.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

}
