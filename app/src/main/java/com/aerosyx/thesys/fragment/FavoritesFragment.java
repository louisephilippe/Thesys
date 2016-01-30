package com.aerosyx.thesys.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.aerosyx.thesys.R;
import com.aerosyx.thesys.adapter.RecipeGridAdapter;
import com.aerosyx.thesys.data.DatabaseHandler;
import com.aerosyx.thesys.model.Recipe;

import java.util.List;

/**
 * Created by muslim on 05/01/2016.
 */
public class FavoritesFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private RecipeGridAdapter mAdapter;
    private LinearLayout lyt_not_found;
    private DatabaseHandler db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favorites, null);

        db =  new DatabaseHandler(getActivity());

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        lyt_not_found = (LinearLayout) view.findViewById(R.id.lyt_not_found);

        LinearLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        List<Recipe> list = db.getAllFavorites();
        displayData(list);
        return view;
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
}
