package com.example.apporganizer.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apporganizer.R;
import com.example.apporganizer.model.CategoryItem;

import java.util.ArrayList;
import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryVH> {

    public interface OnCategoryClickListener {
        void onCategoryClick(String category);
    }

    private final List<CategoryItem> items = new ArrayList<>();
    private final OnCategoryClickListener listener;

    public CategoriesAdapter(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<CategoryItem> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryVH holder, int position) {
        CategoryItem item = items.get(position);
        holder.txtName.setText(item.getName());
        holder.txtCount.setText(item.getCount() + " application(s)");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onCategoryClick(item.getName());
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class CategoryVH extends RecyclerView.ViewHolder {
        final TextView txtName;
        final TextView txtCount;

        CategoryVH(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtCategoryName);
            txtCount = itemView.findViewById(R.id.txtCategoryCount);
        }
    }
}
