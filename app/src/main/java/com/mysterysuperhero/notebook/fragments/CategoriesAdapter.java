package com.mysterysuperhero.notebook.fragments;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mysterysuperhero.notebook.R;
import com.mysterysuperhero.notebook.utils.Category;
import com.mysterysuperhero.notebook.utils.Note;

import java.util.ArrayList;

/**
 * Created by dmitri on 21.06.16.
 */
public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>   {

    Context context;
    ArrayList<Category> categories;
    CategoriesFragment fragment;

    public CategoriesAdapter(Context context, ArrayList<Category> categories, CategoriesFragment fragment) {
        this.context = context;
        this.categories = categories;
        this.fragment = fragment;
    }

    @Override
    public CategoriesAdapter.CategoriesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.categories_item, viewGroup, false);
        return new CategoriesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CategoriesViewHolder holder, final int position) {
        TextView nameTextView = (TextView) holder.cardView.findViewById(R.id.nameTextView);
        nameTextView.setText(categories.get(position).getName());

        holder.cardView.setCardBackgroundColor(Color.parseColor(
                categories.get(position).getColor()
        ));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                fragment.buildChangeNoteDialog(categories.get(position), CategoriesAdapter.this);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.categories.size();
    }

    public class CategoriesViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        TextView name;
        String _id;

        CategoriesViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            name = (TextView)itemView.findViewById(R.id.nameTextView);
        }
    }

    public void addToCategories(ArrayList<Category> newNotes) {
        this.categories.addAll(newNotes);
    }

}
