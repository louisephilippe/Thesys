package com.aerosyx.thesys.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
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
import com.aerosyx.thesys.adapter.CategoryListAdapter;
import com.aerosyx.thesys.data.CategoryLoader;
import com.aerosyx.thesys.data.DatabaseHandler;
import com.aerosyx.thesys.model.Category;
import com.aerosyx.thesys.utils.Tools;
import com.google.gson.Gson;

import java.util.List;


public class CategoryFragment extends Fragment {

    public RecyclerView recyclerView;
    public CategoryListAdapter mAdapter;
    private View view;
    private ProgressBar progressBar;
    private SearchView searchView;
    private LinearLayout lyt_not_found;

    private Gson gson = new Gson();
    private DatabaseHandler db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_category, null);
        // activate fragment menu
        setHasOptionsMenu(true);
        db =  new DatabaseHandler(getActivity());

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        lyt_not_found = (LinearLayout) view.findViewById(R.id.lyt_not_found);
        progressBar     = (ProgressBar) view.findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // specify an adapter (see also next example)
        List<Category> list = db.getAllCategory();
        displayData(list);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_category, menu);
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

    private void displayData(List<Category> list){
        mAdapter = new CategoryListAdapter(getActivity(), list);
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
        CategoryLoader task = new CategoryLoader(new CategoryLoader.TaskListener() {
            @Override
            public void onFinished(List<Category> result) {
                onProcess = false;
                showProgress(onProcess);
                if(result != null){
                    List<Category> list = result;
                    list = db.addListCategory(list);
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
