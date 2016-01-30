package com.aerosyx.thesys.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.aerosyx.thesys.ActivityRecipeDetails;
import com.aerosyx.thesys.R;
import com.aerosyx.thesys.data.Constant;
import com.aerosyx.thesys.model.Recipe;
import com.balysv.materialripple.MaterialRippleLayout;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class RecipeGridAdapter extends RecyclerView.Adapter<RecipeGridAdapter.ViewHolder> implements Filterable {

    private final int mBackground;
    private List<Recipe> original_items = new ArrayList<>();
    private List<Recipe> filtered_items = new ArrayList<>();
    private ItemFilter mFilter = new ItemFilter();
    private final TypedValue mTypedValue = new TypedValue();
    private Context ctx;
    private ImageLoader imgloader = ImageLoader.getInstance();

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name;
        public TextView duration;
        public ImageView image;
        public MaterialRippleLayout lyt_parent;

        public ViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.name);
            duration = (TextView) v.findViewById(R.id.duration);
            image = (ImageView) v.findViewById(R.id.image);
            lyt_parent = (MaterialRippleLayout) v.findViewById(R.id.lyt_parent);
        }
    }

    public Filter getFilter() {
        return mFilter;
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public RecipeGridAdapter(Context ctx, List<Recipe> items) {
        this.ctx = ctx;
        original_items = items;
        filtered_items = items;
        ctx.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
    }

    @Override
    public RecipeGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        v.setBackgroundResource(mBackground);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Recipe p = filtered_items.get(position);
        holder.name.setText(p.name);
        holder.duration.setText(p.duration + " ");
        imgloader.displayImage(Constant.getURLimgRecipe(p.image), holder.image);
        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ctx, ActivityRecipeDetails.class);
                i.putExtra(ActivityRecipeDetails.EXTRA_OBJCT, p);
                ctx.startActivity(i);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return filtered_items.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String query = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            final List<Recipe> list = original_items;
            final List<Recipe> result_list = new ArrayList<>(list.size());
            for (int i = 0; i < list.size(); i++) {
                String str_title = list.get(i).name;
                String str_cat = list.get(i).category_name;
                if (str_title.toLowerCase().contains(query) || str_cat.toLowerCase().contains(query)) {
                    result_list.add(list.get(i));
                }
            }
            results.values = result_list;
            results.count = result_list.size();
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filtered_items = (List<Recipe>) results.values;
            notifyDataSetChanged();
        }

    }
}